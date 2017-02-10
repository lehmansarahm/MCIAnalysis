/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.tan.mcianalysis.analyses;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.MCIAnalysis;

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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author philipcoulomb
 */
public class Direction implements Analysis {

    //strings to be written into the csv file
    String current_acceleration_axis1;
    String previous_acceleration_axis1;
    String acceleration_change_axis1;
    String average_acceleration_axis1;
    String current_acceleration_axis2;
    String previous_acceleration_axis2;
    String acceleration_change_axis2;
    String average_acceleration_axis2;

    @Override
    public void beginAnalysis(String file_path, String user_id, String param1, String param2) {
        CSVReader reader;

        try {
            reader = new CSVReader(new FileReader(file_path), ',', '"', 0);
            createDirectionAnalysisCSV(file_path, reader, user_id, param1, param2);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Acceleration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Pause.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Pause.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //--------------------------------------------------------------------------
    // The createDirectionAnalysis checks a specific reading against the previous
    // reading to see if the acceleration on a specific component changed
    // more than a certain amount to indicate a change of direction
    //--------------------------------------------------------------------------
    private void createDirectionAnalysisCSV(String file_path, CSVReader reader, 
	  String user_id, String acceleration_change_threshold, String axes_to_analyze) 
      throws IOException, ParseException {
        String path_to_csv;
        path_to_csv = initialFileSetup(file_path, user_id);

        String nextReadLine[];
        String nextWriteLine[];
        int axis1;
        int axis2;

        int loop_count = 0;
        boolean direction_change_recorded = false;
        boolean first_line_read = false;

        //variables to record the timing from each row
        SimpleDateFormat date_format = new SimpleDateFormat("HH:mm:ss");
        Date start_time = null;

        CSVWriter direction_csv_writer;

        int axes;

        //components of acceleration
        double current_x_acceleration = 0;
        double current_y_acceleration = 0;
        double previous_x_acceleration = 0;
        double previous_y_acceleration = 0;
        double change_x_acceleration = 0;
        double change_y_acceleration = 0;
        double maximum_change_threshold;

        if (acceleration_change_threshold != null) {
            maximum_change_threshold = Double.parseDouble(acceleration_change_threshold);
        } else {
            try(FileWriter fw = new FileWriter("Errors.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println("No minimum magnitude acceleration delta provided for " +
			    		"Direction analysis. Analysis could not be performed.\n");
			    //more code
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
            return;
            //throw exception and write the error to some log.
        }

        if (axes_to_analyze != null) {
            axes = Integer.parseInt(axes_to_analyze);
        } else {
            try(FileWriter fw = new FileWriter("Errors.txt", true);
			    BufferedWriter bw = new BufferedWriter(fw);
			    PrintWriter out = new PrintWriter(bw))
			{
			    out.println("No axes to analyze provided to Direction analysis. " +
			    		"Analysis could not be performed.\n");
			    //more code
			} catch (IOException e) {
			    //exception handling left as an exercise for the reader
			}
            return;
            //throw exception and write the error to some log.
        }

        //------------------------------------------
        // Direction analysis for XY
        //------------------------------------------
        if (axes == 1) {
            previous_acceleration_axis1 = "Starting X Acceleration:";
            current_acceleration_axis1 = "Next X Acceleration:";
            acceleration_change_axis1 = "X Acceleration Delta:";
            average_acceleration_axis1 = "Average X Acceleration Change";
            previous_acceleration_axis2 = "Starting Y Acceleration";
            current_acceleration_axis2 = "Next Y Acceleration:";
            acceleration_change_axis2 = "Y Acceleration Delta:";
            average_acceleration_axis2 = "Average Y Acceleration Change";
            axis1 = 5;
            axis2 = 6;

        } //-----------------------------------------------------
        // Direction analysis for XZ
        //-----------------------------------------------------
        else if (axes == 2) {
            previous_acceleration_axis1 = "Starting X Acceleration:";
            current_acceleration_axis1 = "Next X Acceleration:";
            acceleration_change_axis1 = "X Acceleration Delta:";
            average_acceleration_axis1 = "Average X Acceleration Change";
            previous_acceleration_axis2 = "Starting Z Acceleration";
            current_acceleration_axis2 = "Next Z Acceleration:";
            acceleration_change_axis2 = "Z Acceleration Delta:";
            average_acceleration_axis2 = "Average Z Acceleration Change";
            axis1 = 5;
            axis2 = 7;
        } //------------------------------------------------------
        // Direction analysis for YZ requested
        //------------------------------------------------------
        else if (axes == 3) {
            previous_acceleration_axis1 = "Starting Y Acceleration:";
            current_acceleration_axis1 = "Next Y Acceleration:";
            acceleration_change_axis1 = "Y Acceleration Delta:";
            average_acceleration_axis1 = "Average Y Acceleration Change";
            previous_acceleration_axis2 = "Starting Z Acceleration";
            current_acceleration_axis2 = "Next Z Acceleration:";
            acceleration_change_axis2 = "Z Acceleration Delta:";
            average_acceleration_axis2 = "Average Z Acceleration Change";
            axis1 = 6;
            axis2 = 7;
        } else {
            return;
        }

        reader.readNext();

        while ((nextReadLine = reader.readNext()) != null) {

            if (loop_count > 3) {

                current_x_acceleration = Double.parseDouble(nextReadLine[axis1]);
                current_y_acceleration = Double.parseDouble(nextReadLine[axis2]);

                change_x_acceleration = current_x_acceleration - previous_x_acceleration;
                change_x_acceleration = Math.abs(change_x_acceleration);

                change_y_acceleration = current_y_acceleration - previous_y_acceleration;
                change_y_acceleration = Math.abs(change_y_acceleration);

                if (first_line_read) {
                    if (change_x_acceleration > maximum_change_threshold || 
                    		change_y_acceleration > maximum_change_threshold) {
                        start_time = date_format.parse(nextReadLine[0]);
                        writeDirectionCSV(start_time, previous_x_acceleration, 
                        		current_x_acceleration, change_x_acceleration, 
                        		previous_y_acceleration, current_y_acceleration, 
                        		change_y_acceleration, path_to_csv);
                        direction_change_recorded = true;
                    }
                } else {
                    first_line_read = true;
                }
            }
            previous_x_acceleration = current_x_acceleration;
            previous_y_acceleration = current_y_acceleration;
            loop_count++;
        }

        if (!direction_change_recorded) {
            String[] total_write_line = new String[6];
            direction_csv_writer = new CSVWriter(new FileWriter(path_to_csv));

            total_write_line[0] = "Number of Direction Changes:";
            total_write_line[1] = "0";
            total_write_line[2] = average_acceleration_axis1;
            total_write_line[3] = "0";
            total_write_line[4] = average_acceleration_axis2;
            total_write_line[5] = "0";

            direction_csv_writer.writeNext(total_write_line);

            direction_csv_writer.flush();
            direction_csv_writer.close();

        }
        
        MCIAnalysis.direction_utilized = true;
    }

    //------------------------------------------------------------------
    // The writePauseCSV method is called once an identified pause
    // has ended. Each line of the csv will feature the time the pause
    // started, the time the pause ended, the duration. The final line
    // of the csv will feature totals such as the number of pauses and
    // the average duration of pauses.
    //------------------------------------------------------------------
    private void writeDirectionCSV(Date start_time, double previous_x_acceleration, 
	  double current_x_acceleration, double change_x_acceleration, double previous_y_acceleration, 
	  double current_y_acceleration, double change_y_acceleration, String file_path) throws IOException {
        CSVWriter direction_csv_writer = null;
        List<String[]> read_all = new ArrayList<String[]>();
        List<String[]> write_all = new ArrayList<String[]>();
        String total_write_line[] = new String[6];
        String next_write_line[] = new String[7];
        int number_of_direction_changes = 0;
        double total_x_acceleration_change = 0;
        double total_y_acceleration_change = 0;
        double average_x_acceleration_change = 0;
        double average_y_acceleration_change = 0;

        CSVReader direction_reader = null;

        try {
            direction_reader = new CSVReader(new FileReader(file_path), ',', '"', 0);

        } catch (FileNotFoundException ex) {

        }

        if (direction_reader != null) {

            read_all = direction_reader.readAll();

            //remove the total line because this will be recalculated
            read_all.remove(read_all.size() - 1);

            //add the components to the read_all object
            next_write_line[0] = start_time.toString();
            next_write_line[1] = String.valueOf(previous_x_acceleration);
            next_write_line[2] = String.valueOf(current_x_acceleration);
            next_write_line[3] = String.valueOf(change_x_acceleration);
            next_write_line[4] = String.valueOf(previous_y_acceleration);
            next_write_line[5] = String.valueOf(current_y_acceleration);
            next_write_line[6] = String.valueOf(change_y_acceleration);

            //add the new line to the read_all object
            read_all.add(next_write_line);

            int i = 1;

            //loop through the read_all object to recalculate the totals
            while (i < read_all.size()) {
                number_of_direction_changes++;
                total_x_acceleration_change = total_x_acceleration_change + Double.parseDouble(read_all.get(i)[3]);
                total_y_acceleration_change = total_y_acceleration_change + Double.parseDouble(read_all.get(i)[6]);
                i++;
            }

            average_x_acceleration_change = (Double) total_x_acceleration_change / number_of_direction_changes;
            average_y_acceleration_change = (Double) total_y_acceleration_change / number_of_direction_changes;

            total_write_line[0] = "Number of Direction Changes:";
            total_write_line[2] = average_acceleration_axis1;
            total_write_line[4] = average_acceleration_axis2;
            total_write_line[1] = String.valueOf(number_of_direction_changes);
            total_write_line[3] = String.valueOf(average_x_acceleration_change);
            total_write_line[5] = String.valueOf(average_y_acceleration_change);

            read_all.add(total_write_line);

            direction_csv_writer = new CSVWriter(new FileWriter(file_path));
            direction_csv_writer.writeAll(read_all);
            direction_csv_writer.close();

        } else {
            direction_csv_writer = new CSVWriter(new FileWriter(file_path));

            //this code will only run after the first direction change is recorded for the
            // activity so the first line and the total line will be equal
            next_write_line[0] = "Time of Direction Change:";
            next_write_line[1] = previous_acceleration_axis1;
            next_write_line[2] = current_acceleration_axis1;
            next_write_line[3] = acceleration_change_axis1;
            next_write_line[4] = previous_acceleration_axis2;
            next_write_line[5] = current_acceleration_axis2;
            next_write_line[6] = acceleration_change_axis2;

            direction_csv_writer.writeNext(next_write_line);

            next_write_line[0] = start_time.toString();
            next_write_line[1] = String.valueOf(previous_x_acceleration);
            next_write_line[2] = String.valueOf(current_x_acceleration);
            next_write_line[3] = String.valueOf(change_x_acceleration);
            next_write_line[4] = String.valueOf(previous_y_acceleration);
            next_write_line[5] = String.valueOf(current_y_acceleration);
            next_write_line[6] = String.valueOf(change_y_acceleration);

            direction_csv_writer.writeNext(next_write_line);

            // The totalling categories are the number of direction changes,
            // the average acceleration change at these direction changes in the
            // x and y directions
            total_write_line[0] = "Number of Direction Changes:";
            total_write_line[1] = "1";
            total_write_line[2] = average_acceleration_axis1;
            total_write_line[3] = String.valueOf(change_x_acceleration);
            total_write_line[4] = average_acceleration_axis2;
            total_write_line[5] = String.valueOf(change_y_acceleration);

            direction_csv_writer.writeNext(total_write_line);

            direction_csv_writer.flush();
            direction_csv_writer.close();
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
        absolute_path = absolute_path.concat("/Direction");
        new File(absolute_path).mkdirs();

        absolute_path = absolute_path.concat("/".concat(user_id));
        new File(absolute_path).mkdirs();

        absolute_path = absolute_path.concat("/Direction_".concat(desired_filename));

        return absolute_path;
    }
}