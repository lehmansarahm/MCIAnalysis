/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.tan.mcianalysis.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author philipcoulomb
 */
public class AccelerationProcessing {

    static float[] gravity = new float[3];

    public static CSVReader convertToLinearAcceleration(CSVReader reader, String desired_file_name) throws IOException {
        String file_path = new File("").getAbsolutePath();
        String absolute_path = new File("").getAbsolutePath();
        absolute_path = absolute_path.concat(Constants.FOLDER_NAME_LINEAR);
        new File(absolute_path).mkdirs();
        String given_file[] = desired_file_name.split("/");
        String[] next_write_line = new String[10];
        desired_file_name = given_file[given_file.length - 1];
        String new_file_path = file_path.concat(Constants.FOLDER_NAME_LINEAR + "/" + desired_file_name);
        CSVWriter linear_writer = new CSVWriter(new FileWriter(new_file_path));
        String[] nextLine;
        boolean first_run_completed = false;

        next_write_line[0] = Constants.DATA_COLUMN_TIME;
        next_write_line[1] = Constants.DATA_COLUMN_RECORD_NO;
        next_write_line[2] = Constants.DATA_COLUMN_AZIMUTH;
        next_write_line[3] = Constants.DATA_COLUMN_PITCH;
        next_write_line[4] = Constants.DATA_COLUMN_ROLL;
        next_write_line[5] = Constants.DATA_COLUMN_ACCEL_X;
        next_write_line[6] = Constants.DATA_COLUMN_ACCEL_Y;
        next_write_line[7] = Constants.DATA_COLUMN_ACCEL_Z;
        next_write_line[8] = Constants.DATA_COLUMN_START_END;
        next_write_line[9] = Constants.DATA_COLUMN_ACTIVITY;

        linear_writer.writeNext(next_write_line);
        linear_writer.flush();

        while ((nextLine = reader.readNext()) != null) {
            if (!first_run_completed) {
                gravity[0] = 0;
                gravity[1] = 0;
                gravity[1] = 0;
                first_run_completed = true;
            }
            
            if (!nextLine[0].equals(Constants.DATA_COLUMN_TIME)) {
	            if (nextLine.length > 5 && !nextLine[2].equalsIgnoreCase("") && !nextLine[3].equalsIgnoreCase("") && !nextLine[4].equalsIgnoreCase(""))
	            	writeLinearAcceleration(linear_writer, nextLine);
	            if (nextLine.length > 9 && nextLine[8].equalsIgnoreCase("quit")) break;
            }
        }
        
        linear_writer.close();

        CSVReader linear_reader = new CSVReader(new FileReader(new_file_path), ',', '"', 0);
        return linear_reader;
    }

    private static void writeLinearAcceleration(CSVWriter linear_writer, String[] nextReadLine) throws IOException {
    	if (ToolkitUtils.isNumeric(nextReadLine[3]) && ToolkitUtils.isNumeric(nextReadLine[4]) && ToolkitUtils.isNumeric(nextReadLine[2])) {
	        final float alpha = (float) 0.8;
	        float[] linear_acceleration = new float[3];
	        
	        gravity[0] = alpha * gravity[0] + (1 - alpha) * Float.valueOf(nextReadLine[3]);
	        gravity[1] = alpha * gravity[1] + (1 - alpha) * Float.valueOf(nextReadLine[4]);
	        gravity[2] = alpha * gravity[2] + (1 - alpha) * Float.valueOf(nextReadLine[2]);
	
	        linear_acceleration[0] = Float.valueOf(nextReadLine[3]) - gravity[0];
	        linear_acceleration[1] = Float.valueOf(nextReadLine[4]) - gravity[1];
	        linear_acceleration[2] = Float.valueOf(nextReadLine[2]) - gravity[2];
	
	        nextReadLine[5] = String.valueOf(linear_acceleration[0]);
	        nextReadLine[6] = String.valueOf(linear_acceleration[1]);
	        nextReadLine[7] = String.valueOf(linear_acceleration[2]);
	        
	        if (nextReadLine.length >= 10) {
		        String[] activityNameComponents = nextReadLine[9].split(":");
		        nextReadLine[9] = activityNameComponents[activityNameComponents.length - 1].replaceAll("[^\\dA-Za-z ]", "");
	        }
	
	        linear_writer.writeNext(nextReadLine);
	        linear_writer.flush();
    	}
    }

}
