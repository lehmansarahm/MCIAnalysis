/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcianaylsis;

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
public class Tasktime implements Analysis{

    @Override
    public void begin_analysis(String file_path) {
        CSVReader reader;
        String[] nextLine;
        
        try {
            reader = new CSVReader(new FileReader(file_path), ',' , '"' , 0);
            processTaskTime(reader);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(Acceleration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Tasktime.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(Tasktime.class.getName()).log(Level.SEVERE, null, ex);
        }
        
 
        
        
   }

    private void processTaskTime(CSVReader reader) throws IOException, ParseException {
        List<String[]> read_all = new ArrayList<String[]>();
        String path_to_directory = new File("").getAbsolutePath();
        String file_path;
        String activity_to_process = new String();
        SimpleDateFormat date_format = new SimpleDateFormat("HH:mm:ss");
        Date start_time = null; 
        Date end_time;
        long duration;
        
        path_to_directory = path_to_directory.concat("\\Final\\Task Times");
        new File(path_to_directory).mkdirs();
        
        //--------------------------------------------------------
        // Read in every line of the file
        //--------------------------------------------------------
        read_all = reader.readAll();
        
        //--------------------------------------------------------
        // Generate the total time the task took to be completed
        //--------------------------------------------------------
        start_time = date_format.parse(read_all.get(0)[0]);
        end_time = date_format.parse(read_all.get(read_all.size()-1)[0]);
        duration = end_time.getTime() - start_time.getTime();
        double diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);
       
        file_path = path_to_directory.concat("\\"+read_all.get(0)[9] + ".csv");
        
        updateTaskTime(file_path,diffInSeconds);
        
    }

    private void updateTaskTime(String file_path, double diffInSeconds) throws FileNotFoundException, IOException {
        CSVReader reader = null;
        CSVWriter writer = null;
        double total_seconds = 0;
        double average_seconds = 0;
        double total_square_distance_from_mean = 0;
        double standard_deviation = 0;
        
        List<String[]> read_all = new ArrayList<String[]>();
        String[] new_line = new String[1];
        String[] total_line = new String[3];
        new_line[0] = String.valueOf(diffInSeconds);
        
        try {
            reader = new CSVReader(new FileReader(file_path), ',' , '"' , 0);
           

        } catch (FileNotFoundException ex) {
           writer = new CSVWriter(new FileWriter(file_path));
        }
        
        if(reader!=null)
        {
            read_all = reader.readAll();
            read_all.remove(read_all.size()-1);
            read_all.add(read_all.size()-1, new_line);
            
            int i = 0;
            
            while(i< read_all.size())
            {
                total_seconds = total_seconds + Double.parseDouble(read_all.get(i)[0]); 
                i++;
            }
            reader.close();
            average_seconds = total_seconds / i;
            
            i=0;
            
            while(i<read_all.size())
            {
                total_square_distance_from_mean = total_square_distance_from_mean + (Math.pow(Double.parseDouble(read_all.get(0)[0])-average_seconds,2));
                i++;
            }
            standard_deviation = Math.sqrt(total_square_distance_from_mean / i);
            
            total_line[0] = String.valueOf(total_seconds);
            total_line[1] = String.valueOf(average_seconds);
            total_line[2] = String.valueOf(standard_deviation);
            
            read_all.add(total_line);
            
            writer = new CSVWriter(new FileWriter(file_path));
            writer.writeAll(read_all);
            writer.close();
            
        }
        else
        {
            new_line = new String[1];
            total_line = new String[2];
            
            new_line[0] = String.valueOf(diffInSeconds);
            total_line[0] = String.valueOf(diffInSeconds);
            total_line[1] = String.valueOf(diffInSeconds);
            
            writer.writeNext(new_line);
            writer.writeNext(total_line);
        
            writer.close();
        }
    }
}
