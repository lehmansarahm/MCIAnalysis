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
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matt
 */
public class Acceleration implements Analysis {
    float[] gravity = new float[3];
    
    public void Acceleration()
    {
        
    }
    //-------------------------------------------------------
    // Implementing the Analysis interface to allow the
    // program to access Acceleration class
    //-------------------------------------------------------
    @Override
    public void begin_analysis(String file_path) {
        CSVReader reader;
        gravity[0]=0;
        gravity[1]=0;
        gravity[2]=0;
        
        try {
            reader = new CSVReader(new FileReader(file_path), ',' , '"' , 0);
            createAccelerationOnlyCSV(file_path,reader);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Acceleration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Acceleration.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 
       
    private void createAccelerationOnlyCSV(String file_path,CSVReader reader) throws IOException
    {
        //---------------------------------------------------------------
        // The new file should be written into the Final folder and 
        // have the Acceleration analysis added to the file name
        //---------------------------------------------------------------
        file_path = file_path.replaceFirst("Intermediate", "Final");
        file_path = file_path.substring(0, file_path.length()-4);
        String starting_acceleration_file_path = file_path.concat("_Starting Acceleration.csv");
        file_path = file_path.concat("Acceleration.csv");
        
        CSVWriter activity_csv_writer=new CSVWriter(new FileWriter(file_path));
        CSVWriter starting_acceleration = new CSVWriter(new FileWriter(starting_acceleration_file_path));
        
        String nextReadLine[];
        String[] nextWriteLine = new String[5];
        int i = 0;
        while ((nextReadLine = reader.readNext()) != null) {
            
            writeLinearAcceleration(activity_csv_writer,starting_acceleration,nextReadLine, i);
            
            i++;
        }
        starting_acceleration.close();
        activity_csv_writer.close();
    }

    private void writeLinearAcceleration(CSVWriter activity_csv_writer,CSVWriter starting_acceleration, String[] nextReadLine, int i) {
          final float alpha = (float) 0.8;
          float[] linear_acceleration = new float[3];
          
          String nextWriteLine[] = new String[5];

          gravity[0] = alpha * gravity[0] + (1 - alpha) * Float.valueOf(nextReadLine[3]);
          gravity[1] = alpha * gravity[1] + (1 - alpha) * Float.valueOf(nextReadLine[4]);
          gravity[2] = alpha * gravity[2] + (1 - alpha) * Float.valueOf(nextReadLine[2]);

          linear_acceleration[0] = Float.valueOf(nextReadLine[3]) - gravity[0];
          linear_acceleration[1] = Float.valueOf(nextReadLine[4]) - gravity[1];
          linear_acceleration[2] = Float.valueOf(nextReadLine[2]) - gravity[2];
          
          nextWriteLine[0] = nextReadLine[0];
          nextWriteLine[1] = nextReadLine[1];
          nextWriteLine[2] = String.valueOf(linear_acceleration[0]);
          nextWriteLine[3] = String.valueOf(linear_acceleration[1]);
          nextWriteLine[4] = String.valueOf(linear_acceleration[2]);
          
        if(i<20)
        {
            starting_acceleration.writeNext(nextWriteLine);
        }
        activity_csv_writer.writeNext(nextWriteLine);
    }
    
    
}
