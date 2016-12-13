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
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matt
 */
public class Acceleration implements Analysis {

    public void Acceleration() {

    }

    //-------------------------------------------------------
    // Implementing the Analysis interface to allow the
    // program to access Acceleration class
    //-------------------------------------------------------
    @Override
    public void begin_analysis(String file_path, String user_id, String param1, String param2) {
        CSVReader reader;

        try {
            reader = new CSVReader(new FileReader(file_path), ',', '"', 0);
            createAccelerationOnlyCSV(file_path, reader, user_id);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Acceleration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Acceleration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createAccelerationOnlyCSV(String file_path, CSVReader reader, String user_id) throws IOException {
        //---------------------------------------------------------------
        // The new file should be written into the Final folder and 
        // have the Acceleration analysis added to the file name
        //---------------------------------------------------------------
        String[] path_components = file_path.split("/");
        String desired_filename = path_components[path_components.length - 1];
        desired_filename = desired_filename.substring(0, desired_filename.length() - 4);
        String path_to_csv = initialFileSetup(file_path, user_id);
        String acceleration_file_path = path_to_csv.concat("/Acceleration");
        String starting_acceleration_file_path = path_to_csv.concat("/StartingAcceleration");
        starting_acceleration_file_path = starting_acceleration_file_path.concat("/".concat(desired_filename.concat("_StartingAcceleration.csv")));

        acceleration_file_path = acceleration_file_path.concat("/".concat(desired_filename.concat("_Acceleration.csv")));

        try (CSVWriter activity_csv_writer = new CSVWriter(new FileWriter(acceleration_file_path)); CSVWriter starting_acceleration = new CSVWriter(new FileWriter(starting_acceleration_file_path))) {

            String nextReadLine[];
            String[] nextWriteLine = new String[5];

            nextWriteLine[0] = "Time Stamp";
            nextWriteLine[1] = "Time Interval";
            nextWriteLine[2] = "X Acceleration";
            nextWriteLine[3] = "Y Acceleration";
            nextWriteLine[4] = "Z Acceleration";

            starting_acceleration.writeNext(nextWriteLine);
            activity_csv_writer.writeNext(nextWriteLine);

            int i = 0;
            reader.readNext();
            while ((nextReadLine = reader.readNext()) != null) {

                nextWriteLine[0] = nextReadLine[0];
                nextWriteLine[1] = nextReadLine[1];

                nextWriteLine[2] = nextReadLine[5];
                nextWriteLine[3] = nextReadLine[6];
                nextWriteLine[4] = nextReadLine[7];

                if (i < 20) {
                    starting_acceleration.writeNext(nextWriteLine);
                }

                activity_csv_writer.writeNext(nextWriteLine);
                i++;
            }      
        }
    }

    //-----------------------------------------------------------------------------
    // The initialFileSetup method is responsible for ensuring the proper
    // directories are setup and that the CSVWriter has a valid and clear filename
    // to write to.
    //-----------------------------------------------------------------------------
    private String initialFileSetup(String file_path, String user_id) {

        String absolute_path = new File("").getAbsolutePath();
        absolute_path = absolute_path.concat("/Final");
        new File(absolute_path).mkdirs();
        absolute_path = absolute_path.concat("/Acceleration");
        new File(absolute_path).mkdirs();

        absolute_path = absolute_path.concat("/".concat(user_id));
        new File(absolute_path).mkdirs();

        new File(absolute_path.concat("/StartingAcceleration")).mkdirs();
        new File(absolute_path.concat("/Acceleration")).mkdirs();

        return absolute_path;
    }
}
