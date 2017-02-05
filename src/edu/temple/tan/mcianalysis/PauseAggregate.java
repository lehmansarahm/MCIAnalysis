/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.tan.mcianalysis;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author philipcoulomb
 */
public class PauseAggregate {

    public static void aggregatePauseCSV() throws FileNotFoundException, IOException {
        String absolute_path = new File("").getAbsolutePath();
        absolute_path = absolute_path.concat("/Final/Pause");

        File directionFolder = new File(absolute_path);

        File[] listOfFiles = directionFolder.listFiles();

        //loop through the directories found within
        for (int i = 0; i < listOfFiles.length; i++) {
            //if the file is a directory we want to go into it and aggregate the direction changes
            if (listOfFiles[i].isDirectory()) {
                File[] innerFiles = listOfFiles[i].listFiles();
                String total_write_line[] = new String[4];
                String writer_path = listOfFiles[i].getAbsolutePath();
                writer_path = writer_path.concat("/TaskPauses.csv");
                CSVWriter writer = new CSVWriter(new FileWriter(writer_path));

                total_write_line[0] = "Task:";
                total_write_line[1] = "Number of Pauses:";
                total_write_line[2] = "Total Time Paused:";
                total_write_line[3] = "Average Pause Duration:";

                writer.writeNext(total_write_line);

                //loop through the inner directory
                for (int j = 0; j < innerFiles.length; j++) {
                    if (!innerFiles[j].getName().equals("TaskPauses.csv")) {
                        String[] name_components = innerFiles[j].getName().split("_");
                        String task_name = "";

                        if (name_components.length == 3 || name_components.length > 3) {
                            task_name = name_components[3];
                        }
                        List<String[]> read_all = new ArrayList<String[]>();

                        String full_file_path = innerFiles[j].getAbsolutePath();

                        CSVReader reader = new CSVReader(new FileReader(full_file_path), ',', '"', 0);
                        read_all = reader.readAll();
                        int m = 0;
                        while(!read_all.get(m)[0].equalsIgnoreCase("Configuration File Used:") && m<read_all.size())
                        {
                            m++;
                        }
                        total_write_line[0] = task_name;
                        total_write_line[1] = read_all.get(m-1)[1];
                        total_write_line[2] = read_all.get(m-1)[3];
                        total_write_line[3] = read_all.get(m-1)[5];
                        writer.writeNext(total_write_line);
                    }
                }
                writer.flush();
                writer.close();
            }

        }

    }

}
