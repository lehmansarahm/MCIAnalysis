/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcianalysis;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
 * @author Matt
 */
public class Tasktime implements Analysis {

    @Override
    public void begin_analysis(String file_path, String user_id, String param1, String param2) {
        CSVReader reader;
        String[] nextLine;

        try {
            reader = new CSVReader(new FileReader(file_path), ',', '"', 0);
            processTaskTime(reader, user_id);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Acceleration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Tasktime.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Tasktime.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void processTaskTime(CSVReader reader, String user_id) throws IOException, ParseException {
        List<String[]> read_all = new ArrayList<String[]>();
        String path_to_directory = new File("").getAbsolutePath();
        String file_path;
        String activity_to_process = new String();
        SimpleDateFormat date_format = new SimpleDateFormat("HH:mm:ss");
        Date start_time = null;
        Date end_time;
        long duration;

        path_to_directory = path_to_directory.concat("/Final/Task Times");
        new File(path_to_directory).mkdirs();

        //--------------------------------------------------------
        // Read in every line of the file
        //--------------------------------------------------------
        read_all = reader.readAll();

        //--------------------------------------------------------
        // Generate the total time the task took to be completed
        //--------------------------------------------------------
        start_time = date_format.parse(read_all.get(1)[0]);
        end_time = date_format.parse(read_all.get(read_all.size() - 1)[0]);
        duration = end_time.getTime() - start_time.getTime();
        double diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);

        file_path = path_to_directory.concat("/" + read_all.get(1)[9] + ".csv");

        updateTaskTime(file_path, diffInSeconds, user_id);

    }

    private void updateTaskTime(String file_path, double diffInSeconds, String user_id) throws FileNotFoundException, IOException {
        CSVReader reader = null;
        CSVWriter writer = null;
        double total_seconds = 0;
        double average_seconds = 0;
        double total_square_distance_from_mean = 0;
        double standard_deviation = 0;

        List<String[]> read_all = new ArrayList<String[]>();
        String[] new_line = new String[2];
        String[] total_line = new String[6];
        new_line[0] = user_id;
        new_line[1] = String.valueOf(diffInSeconds);

        try {
            reader = new CSVReader(new FileReader(file_path), ',', '"', 0);

        } catch (FileNotFoundException ex) {
            writer = new CSVWriter(new FileWriter(file_path));
        }

        if (reader != null) {
            read_all = reader.readAll();
            read_all.remove(read_all.size() - 1);
            read_all.add(read_all.size() - 1, new_line);

            int i = 1;

            while (i < read_all.size()) {
                total_seconds = total_seconds + Double.parseDouble(read_all.get(i)[1]);
                i++;
            }
            reader.close();
            average_seconds = total_seconds / (i - 1);

            i = 1;

            while (i < read_all.size()) {
                total_square_distance_from_mean = total_square_distance_from_mean + (Math.pow((Double.parseDouble(read_all.get(i)[1]) - average_seconds), 2));
                i++;
            }
            standard_deviation = Math.sqrt(total_square_distance_from_mean / (i - 2));

            total_line[0] = "Total Task Time:";
            total_line[2] = "Average Task Time:";
            total_line[4] = "Task Time Sample Standard Deviation:";
            total_line[1] = String.valueOf(total_seconds);
            total_line[3] = String.valueOf(average_seconds);
            total_line[5] = String.valueOf(standard_deviation);

            read_all.add(total_line);

            writer = new CSVWriter(new FileWriter(file_path));
            writer.writeAll(read_all);
            writer.close();

        } else {
            new_line = new String[2];
            total_line = new String[6];

            new_line[0] = "User ID:";
            new_line[1] = "Task Time (Seconds):";
            writer.writeNext(new_line);

            new_line[0] = user_id;
            new_line[1] = String.valueOf(diffInSeconds);
            writer.writeNext(new_line);

            total_line[0] = "Total Task Time:";
            total_line[1] = String.valueOf(diffInSeconds);
            total_line[2] = "Average Task Time:";
            total_line[3] = String.valueOf(diffInSeconds);
            total_line[4] = "Task Time Sample Standard Deviation:";
            total_line[5] = "0";

            writer.writeNext(total_line);

            writer.close();
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
        absolute_path = absolute_path.concat("/Acceleration");
        new File(absolute_path).mkdirs();

        absolute_path = absolute_path.concat(user_id);
        new File(absolute_path).mkdirs();

        absolute_path = absolute_path.concat(desired_filename);

        return absolute_path;
    }
}
