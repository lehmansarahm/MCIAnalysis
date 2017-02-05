/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.tan.mcianalysis;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author philipcoulomb
 */
public class AccelerationProcessing {

    static float[] gravity = new float[3];

    public static CSVReader convertToLinearAcceleration(CSVReader reader, String desired_file_name) throws IOException {
        String file_path = new File("").getAbsolutePath();
        String absolute_path = new File("").getAbsolutePath();
        absolute_path = absolute_path.concat("/Linear");
        new File(absolute_path).mkdirs();
        String given_file[] = desired_file_name.split("/");
        String[] next_write_line = new String[10];
        desired_file_name = given_file[given_file.length - 1];
        String new_file_path = file_path.concat("/Linear/".concat(desired_file_name));
        CSVWriter linear_writer = new CSVWriter(new FileWriter(new_file_path));
        String[] nextLine;
        boolean first_run_completed = false;
        List<String[]> read_all = new ArrayList<String[]>();

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

        linear_writer.writeNext(next_write_line);
        linear_writer.flush();

        while ((nextLine = reader.readNext()) != null) {
            if (!first_run_completed) {
                gravity[0] = 0;
                gravity[1] = 0;
                gravity[1] = 0;
                first_run_completed = true;
            }
            if(!nextLine[2].equalsIgnoreCase("") && !nextLine[3].equalsIgnoreCase("") && !nextLine[4].equalsIgnoreCase(""))
            writeLinearAcceleration(linear_writer, nextLine);
        }
        linear_writer.close();

        CSVReader linear_reader = new CSVReader(new FileReader(new_file_path), ',', '"', 0);

        return linear_reader;
    }

    private static void writeLinearAcceleration(CSVWriter linear_writer, String[] nextReadLine) throws IOException {
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

        linear_writer.writeNext(nextReadLine);
        linear_writer.flush();
    }

}
