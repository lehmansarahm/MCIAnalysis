/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.tan.mcianalysis.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.MCIAnalysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Matt
 */
public class ActivitySplit {

    public static String generateActivitySpecificCSV(CSVReader csv_file, String user_id, 
	  String requested_activity) throws IOException {
        String file_path = new File("").getAbsolutePath();

        String time_stamp = new SimpleDateFormat("yyyy-MM-dd_HH_mm").format(new java.util.Date());
        MCIAnalysis.run_time = time_stamp;

        MCIAnalysis.requested_activities_set.add(requested_activity);

        //----------------------------------------------------------------------
        // The new csv file name will be the user id, activity, and time stamp
        //----------------------------------------------------------------------
        String absolute_path = new File("").getAbsolutePath();
        absolute_path = absolute_path.concat("/Intermediate");
        new File(absolute_path).mkdirs();
        
        String file_name = "/Intermediate/" + user_id + "_" 
        		+ requested_activity.replace(" ", "_") + "_" + time_stamp + ".csv";
        String full_file_path = file_path.concat(file_name);

        CSVWriter activity_csv_writer = new CSVWriter(new FileWriter(full_file_path));

        String[] nextLine;

        //---------------------------------------------------
        // If the line belongs to the requested activity 
        // it is written to the new csv file in the Results
        // folder.
        //---------------------------------------------------
        while ((nextLine = csv_file.readNext()) != null) {
            if (nextLine[9].equals(requested_activity)) {
                activity_csv_writer.writeNext(nextLine);
            }
        }

        // close the csv writer
        activity_csv_writer.close();
        csv_file.close();

        return full_file_path;
    }

    public static List<String> generateCSVForAllActivities(CSVReader csv_file, String user_id) throws IOException {
        List<String> activity_csv_paths = new ArrayList<String>();
        boolean activity_started = false;
        CSVReader single_activity_csv_reader;
        String requested_activity = "NotInitialized";
        String file_path = new File("").getAbsolutePath();
        String full_file_path = null;
        CSVWriter activity_csv_writer = null;

        String time_stamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new java.util.Date());
        MCIAnalysis.run_time = time_stamp;

        String[] nextLine;
        String[] next_write_line = new String[10];

        //---------------------------------------------------
        // If the line belongs to the requested activity 
        // it is written to the new csv file in the Results
        // folder.
        //---------------------------------------------------
        while ((nextLine = csv_file.readNext()) != null) {

            if (nextLine.length > 9 && nextLine[8].equalsIgnoreCase("start")) {
                activity_started = true;
                requested_activity = nextLine[9];
                MCIAnalysis.requested_activities_set.add(requested_activity);
                String absolute_path = new File("").getAbsolutePath();
                absolute_path = absolute_path.concat("/Intermediate");
                new File(absolute_path).mkdirs();

                //----------------------------------------------------------------------
                // The new csv file name will be the user id, activity, and time stamp
                //----------------------------------------------------------------------
                String file_name = "/Intermediate/" + user_id + "_" + MCIAnalysis.acceleration_processing 
                		+ "_" + requested_activity.replace(" ", "_") + "_" + time_stamp + ".csv";
                full_file_path = file_path.concat(file_name);

                activity_csv_writer = new CSVWriter(new FileWriter(full_file_path));

                next_write_line[0] = "Time:";
                next_write_line[1] = "Reading Number:";
                next_write_line[2] = "Azimuth:";
                next_write_line[3] = "Pitch:";
                next_write_line[4] = "Roll:";
                next_write_line[5] = "Acceleration X:";
                next_write_line[6] = "Acceleration Y:";
                next_write_line[7] = "Acceleration Z:";
                next_write_line[8] = "Start/End:";
                next_write_line[9] = "Activity:";

                activity_csv_writer.writeNext(next_write_line);

            }

            if (nextLine.length > 10 && nextLine[9].equalsIgnoreCase(requested_activity) && activity_started) {
                activity_csv_writer.writeNext(nextLine);
            } else if (activity_started) {
                //------------------------------------------
                // Close current writer and add the file
                // path to the array of files
                //------------------------------------------
                activity_csv_writer.close();
                activity_csv_paths.add(full_file_path);
                String absolute_path = new File("").getAbsolutePath();
                absolute_path = absolute_path.concat("/Intermediate");
                new File(absolute_path).mkdirs();
                //-----------------------------------------------------
                // The new activity is now the requested_activity
                // create a new CSV file to store the data
                //-----------------------------------------------------
                requested_activity = nextLine[9];
                MCIAnalysis.requested_activities_set.add(requested_activity);
                String file_name = "/Intermediate/" + user_id + "_" + MCIAnalysis.acceleration_processing + "_" + requested_activity + "_" + time_stamp + ".csv";
                full_file_path = file_path.concat(file_name);

                activity_csv_writer = new CSVWriter(new FileWriter(full_file_path));

                next_write_line[0] = "Time:";
                next_write_line[1] = "Reading Number:";
                next_write_line[2] = "Azimuth:";
                next_write_line[3] = "Pitch:";
                next_write_line[4] = "Roll:";
                next_write_line[5] = "Acceleration X:";
                next_write_line[6] = "Acceleration Y:";
                next_write_line[7] = "Acceleration Z:";
                next_write_line[8] = "Start/End:";
                next_write_line[9] = "Activity:";

                activity_csv_writer.writeNext(next_write_line);

                //-----------------------------------------------
                // Write the current line into the CSV file
                //-----------------------------------------------
                activity_csv_writer.writeNext(nextLine);
            }

            if (nextLine.length > 9 && nextLine[8].equalsIgnoreCase("quit")) {
                //end the loop
                break;
            }

        }

        //------------------------------------------
        // Close current writer and add the file
        // path to the array of files
        //------------------------------------------
        if (activity_csv_writer != null) {
            activity_csv_writer.close();
            activity_csv_paths.add(full_file_path);
        }
        
        return activity_csv_paths;
    }
}
