/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcianaylsis;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
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
    
    public static String generateActivitySpecificCSV(CSVReader csv_file, String user_id, String requested_activity) throws IOException
    {
        String file_path = new File("").getAbsolutePath();
         
        String time_stamp = new SimpleDateFormat("yyyy-MM-dd_HH_mm").format(new java.util.Date());
        
        MCIAnaylsis.requested_activities_set.add(requested_activity);
        
        //----------------------------------------------------------------------
        // The new csv file name will be the user id, activity, and time stamp
        //----------------------------------------------------------------------
        String file_name = "\\Intermediate\\" +user_id +"_" + requested_activity +"_" + time_stamp + ".csv";
        String full_file_path = file_path.concat(file_name);
        
        CSVWriter activity_csv_writer=new CSVWriter(new FileWriter(full_file_path));
        
        String [] nextLine;
        
        //---------------------------------------------------
        // If the line belongs to the requested activity 
        // it is written to the new csv file in the Results
        // folder.
        //---------------------------------------------------
        while ((nextLine = csv_file.readNext()) != null) {
            if(nextLine[9].equals(requested_activity))
            {
                activity_csv_writer.writeNext(nextLine);
            }
        }
        
        // close the csv writer
        activity_csv_writer.close();
        csv_file.close();

        return full_file_path;
    }
    
    public static List<String> generateCSVForAllActivities(CSVReader csv_file,String user_id) throws IOException
    {
        List<String> activity_csv_paths = new ArrayList<String>();
        boolean activity_started = false;
        CSVReader single_activity_csv_reader;
        String requested_activity = "NotInitialized";
        String file_path = new File("").getAbsolutePath();
        String full_file_path = null;
        CSVWriter activity_csv_writer = null;
         
        String time_stamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new java.util.Date());
        

        
        String [] nextLine;
        
        //---------------------------------------------------
        // If the line belongs to the requested activity 
        // it is written to the new csv file in the Results
        // folder.
        //---------------------------------------------------
        while ((nextLine = csv_file.readNext()) != null) {
            
            if(nextLine[8].equalsIgnoreCase("start"))
            {
                activity_started = true;
                requested_activity = nextLine[9];
                MCIAnaylsis.requested_activities_set.add(requested_activity);
                
                //----------------------------------------------------------------------
                // The new csv file name will be the user id, activity, and time stamp
                //----------------------------------------------------------------------
                String file_name = "\\Intermediate\\" +user_id +" " + requested_activity +" " + time_stamp + ".csv";
                full_file_path = file_path.concat(file_name);

                activity_csv_writer=new CSVWriter(new FileWriter(full_file_path));
            }
            
            if(nextLine[9].equalsIgnoreCase(requested_activity) && activity_started)
            {
                activity_csv_writer.writeNext(nextLine);
            }
            else if(activity_started)
            {
                //------------------------------------------
                // Close current writer and add the file
                // path to the array of files
                //------------------------------------------
                activity_csv_writer.close();
                activity_csv_paths.add(full_file_path);
                
                //-----------------------------------------------------
                // The new activity is now the requested_activity
                // create a new CSV file to store the data
                //-----------------------------------------------------
                requested_activity = nextLine[9];
                MCIAnaylsis.requested_activities_set.add(requested_activity);
                String file_name = "\\Intermediate\\" +user_id +"_" + requested_activity +"_" + time_stamp + ".csv";
                full_file_path = file_path.concat(file_name);

                activity_csv_writer=new CSVWriter(new FileWriter(full_file_path));
                
                //-----------------------------------------------
                // Write the current line into the CSV file
                //-----------------------------------------------
                activity_csv_writer.writeNext(nextLine);
            }
            
            if(nextLine[8].equalsIgnoreCase("quit"))
            {
                //------------------------------------------
                // Close current writer and add the file
                // path to the array of files
                //------------------------------------------
                activity_csv_writer.close();
                activity_csv_paths.add(full_file_path);
                
                //end the loop
                break;
            }
                

        }
       
        return activity_csv_paths;
    }
}
