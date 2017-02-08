/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.tan.mcianalysis;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sarah Lehman
 */
public class PauseCount implements Analysis {

    @Override
    public void begin_analysis(String file_path, String user_id, String param1, String param2) {
        CSVReader reader;

        try {
            reader = new CSVReader(new FileReader(file_path), ',', '"', 0);
            createPauseAnalysisCSV(file_path, reader, user_id, param1, param2);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Acceleration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PauseDuration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(PauseDuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //--------------------------------------------------------------------------
    // The createPauseAnalysis calculates the magnitude of the acceleration 
    // vector at each reading and if that magnitude is less than some 
    // defined minimum it marks it. If x number of magnitudes are below the 
    // minimum in a row then a pause had occured.
    //--------------------------------------------------------------------------
    private void createPauseAnalysisCSV(String file_path, CSVReader reader, String user_id, 
    		String min_acceleration, String pause_window) throws IOException, ParseException {
        String path_to_csv;
        path_to_csv = initialFileSetup(file_path, user_id);
        CSVReader pause_reader;
        CSVWriter pause_csv_writer;
        String nextReadLine[];
        String nextWriteLine[];
        String total_write_line[] = new String[6];
        boolean previous_was_paused = false;
        int consecutive_count = 0;
        boolean pause_recorded = false;

        //variables to record the timing from each row and determine duration of pauses
        SimpleDateFormat date_format = new SimpleDateFormat("HH:mm:ss");
        Date start_time = null;
        Date end_time;
        double duration;
        double diffInSeconds;
        final double milliseconds_between_readings = 4.34782608696;	// approx 230 times per second

        //components of acceleration
        double x_acceleration = 0;
        double y_acceleration = 0;
        double z_acceleration = 0;
        //the magnitude of acceleration at a given moment
        double magnitude_of_acceleration = 0;
        double minimum_magnitude;
        double minimum_consecutive_pause = 0;

        if (min_acceleration != null) {
            minimum_magnitude = Double.parseDouble(min_acceleration);
        } else {
            try(FileWriter fw = new FileWriter("Errors.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println("No minimum magnitude provided for Pause analysis. Analysis could not be performed.\n");
			    //more code
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}

            return;
        }

        if (pause_window != null) {
            minimum_consecutive_pause = Double.parseDouble(pause_window);
        } else {
            try(FileWriter fw = new FileWriter("Errors.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println("No number of paused moments provided for Pause analysis. Analysis could not be performed.\n");
			    //more code
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
            return;
        }

        reader.readNext();

        while ((nextReadLine = reader.readNext()) != null) {
            x_acceleration = Double.parseDouble(nextReadLine[5]);
            y_acceleration = Double.parseDouble(nextReadLine[6]);
            z_acceleration = Double.parseDouble(nextReadLine[7]);

            magnitude_of_acceleration = calculateMagnitude(x_acceleration, y_acceleration, z_acceleration);

            // action to take if magnitude does not meet the minimum
            if (magnitude_of_acceleration < minimum_magnitude && consecutive_count < minimum_consecutive_pause) {
                //Need to track how many consecutive readings showed low acceleration
                if (!previous_was_paused) {
                    //record the timing of the first moment of low acceleration
                    start_time = date_format.parse(nextReadLine[0]);
                    previous_was_paused = true;
                    consecutive_count = 1;
                } else {
                    consecutive_count++;
                }
            } else if (previous_was_paused && consecutive_count == minimum_consecutive_pause) {
                end_time = date_format.parse(nextReadLine[0]);
                duration = consecutive_count * milliseconds_between_readings;

                writePauseCSV(start_time, end_time, duration, path_to_csv);

                previous_was_paused = false;
                consecutive_count = 0;
                pause_recorded = true;
            } else {
                //reset the consecutive pause checks
                previous_was_paused = false;
                consecutive_count = 0;
            }
        }

        if (!pause_recorded) {
            pause_csv_writer = new CSVWriter(new FileWriter(path_to_csv));

            total_write_line[0] = "Number of Pauses:";
            total_write_line[1] = "0";
            total_write_line[2] = "Total Time Spent Paused:";
            total_write_line[3] = "0";
            total_write_line[4] = "Average Pause Duration:";
            total_write_line[5] = "0";

            pause_csv_writer.writeNext(total_write_line);
            pause_csv_writer.close();
        }
        
        MCIAnalysis.pause_utilized = true;
    }

    //--------------------------------------------------------------------------
    // The calculateMagnitude function accepts the x,y,z components of 
    // acceleration and returns the magnitude of the acceleration vector
    //--------------------------------------------------------------------------
    private double calculateMagnitude(double x_acceleration, double y_acceleration, 
    		double z_acceleration) {
        double magnitude = 0;
        double x_squared = 0;
        double y_squared = 0;
        double z_squared = 0;

        x_squared = Math.pow(x_acceleration, 2);
        y_squared = Math.pow(y_acceleration, 2);
        z_squared = Math.pow(z_acceleration, 2);

        //-------------------------------------------------------------
        // Calculated based on formula r=sqrt(x^2+y^2+z^2) where,
        // r = magnitude of the vector.
        //-------------------------------------------------------------
        magnitude = Math.sqrt(x_squared + y_squared + z_squared);

        return magnitude;
    }

    //------------------------------------------------------------------
    // The writePauseCSV method is called once an identified pause
    // has ended. Each line of the csv will feature the time the pause
    // started, the time the pause ended, the duration. The final line
    // of the csv will feature totals such as the number of pauses and
    // the average duration of pauses.
    //------------------------------------------------------------------
    private void writePauseCSV(Date start_time, Date end_time, double duration, 
    		String file_path) throws IOException {
        CSVWriter pause_csv_writer = null;
        List<String[]> read_all = new ArrayList<String[]>();
        List<String[]> write_all = new ArrayList<String[]>();
        String total_write_line[] = new String[6];
        String next_write_line[] = new String[3];
        double total_time_paused = 0;
        int number_of_pauses = 0;
        double average_pause_duration = 0;

        CSVReader pause_reader = null;

        try {
            pause_reader = new CSVReader(new FileReader(file_path), ',', '"', 0);
        } catch (FileNotFoundException ex) {

        }

        if (pause_reader != null) {
            read_all = pause_reader.readAll();

            //remove the total line because this will be recalculated
            read_all.remove(read_all.size() - 1);

            //add the components to the read_all object
            next_write_line[0] = start_time.toString();
            next_write_line[1] = end_time.toString();
            next_write_line[2] = String.valueOf(duration);

            //add the new line to the read_all object
            read_all.add(next_write_line);

            //loop through the read_all object to recalculate the totals
            int i = 1;
            while (i < read_all.size()) {
                number_of_pauses++;
                total_time_paused = total_time_paused + Double.parseDouble(read_all.get(i)[2]);
                i++;
            }

            average_pause_duration = (Double) total_time_paused / number_of_pauses;

            total_write_line[0] = "Number of Pauses:";
            total_write_line[1] = String.valueOf(number_of_pauses);
            total_write_line[2] = "Total Time Spent Paused:";
            total_write_line[3] = String.valueOf(total_time_paused);
            total_write_line[4] = "Average Pause Duration:";
            total_write_line[5] = String.valueOf(average_pause_duration);

            read_all.add(total_write_line);

            pause_csv_writer = new CSVWriter(new FileWriter(file_path));
            pause_csv_writer.writeAll(read_all);
            pause_csv_writer.close();

        } else {
            pause_csv_writer = new CSVWriter(new FileWriter(file_path));

            //this code will only run after the first pause is recorded for the
            // activity so the first line and the total line will be equal
            next_write_line[0] = "Pause Start Time";
            next_write_line[1] = "Pause End Time";
            next_write_line[2] = "Pause Duration";
            pause_csv_writer.writeNext(next_write_line);

            next_write_line[0] = start_time.toString();
            next_write_line[1] = end_time.toString();
            next_write_line[2] = String.valueOf(duration);
            pause_csv_writer.writeNext(next_write_line);

            total_write_line[0] = "Number of Pauses:";
            total_write_line[1] = "1";
            total_write_line[2] = "Total Time Spent Paused:";
            total_write_line[3] = String.valueOf(duration);
            total_write_line[4] = "Average Pause Duration:";
            total_write_line[5] = String.valueOf(duration);

            // write the two initial lines to the csv
            pause_csv_writer.writeNext(total_write_line);
            pause_csv_writer.flush();
            pause_csv_writer.close();
        }
    }

    //-----------------------------------------------------------------------------
    // The initialFileSetup method is responsible for ensuring the proper
    // directories are setup and that the CSVWriter has a valid and clear filename
    // to write to.
    //-----------------------------------------------------------------------------
    private String initialFileSetup(String file_path, String user_id) {
        String[] path_components = file_path.split("/");
        String desired_filename = path_components[path_components.length - 1];
        String absolute_path = new File("").getAbsolutePath();
        
        absolute_path = absolute_path.concat("/Final");
        new File(absolute_path).mkdirs();
        
        absolute_path = absolute_path.concat("/Pause");
        new File(absolute_path).mkdirs();

        absolute_path = absolute_path.concat("/".concat(user_id));
        new File(absolute_path).mkdirs();

        absolute_path = absolute_path.concat("/Pause_".concat(desired_filename));
        return absolute_path;
    }
}
