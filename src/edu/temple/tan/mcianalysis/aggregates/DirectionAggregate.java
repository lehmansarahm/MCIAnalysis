/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.tan.mcianalysis.aggregates;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.Constants.SUDDEN_MOVEMENT_AGGREGATE_COLUMN_ORDER;
import edu.temple.tan.mcianalysis.utils.Constants.SUDDEN_MOVEMENT_OUTPUT_FILE_TOTALS_COLUMN_ORDER;
import edu.temple.tan.mcianalysis.utils.ToolkitUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author philipcoulomb
 */
public class DirectionAggregate {

    public static void aggregateDirectionCSV() throws FileNotFoundException, IOException {
        String directionFolderPath = new File("").getAbsolutePath().concat(Constants.FOLDER_NAME_FINAL + Constants.FOLDER_NAME_DIRECTION);
        File directionFolder = new File(directionFolderPath);
        File[] directionFiles = directionFolder.listFiles();

        //loop through the directories found within
        for (File directionFile : directionFiles) {
            //if the file is a directory we want to go into it and aggregate the direction changes
            if (directionFile.isDirectory()) {
                File[] innerFiles = directionFile.listFiles();
                String writerPath = directionFile.getAbsolutePath().concat("/" + Constants.AGGREGATE_FILE_DIRECTION);
                CSVWriter writer = new CSVWriter(new FileWriter(writerPath));
                
                String[] totalLine = new String[] { 
                		"Task:", 
                		"Number of Direction Changes:", 
                		"Average X Acceleration Change:", 
                		"Average Y Acceleration Change:" 
            		};
                writer.writeNext(totalLine);
                
                //loop through the inner directory
                for (int j = 0; j < innerFiles.length; j++) {
                    if (!innerFiles[j].getName().equals(Constants.AGGREGATE_FILE_DIRECTION)) {
                        String innerFilePath = innerFiles[j].getAbsolutePath();
                        CSVReader reader = new CSVReader(new FileReader(innerFilePath), ',', '"', 0);
                        List<String[]> fileContents = reader.readAll();
                        reader.close();

                        int taskIndex = SUDDEN_MOVEMENT_AGGREGATE_COLUMN_ORDER.TASK.ordinal();
                        String taskName = ToolkitUtils.getActivityNameFromFileName(innerFiles[j].getName());
                        totalLine[taskIndex] =  taskName;
                        
                        int m = 0;
                        while(!fileContents.get(m)[taskIndex].equalsIgnoreCase("Configuration File Used:") && m < fileContents.size()) {
                            m++;
                        }
                        
                        totalLine[SUDDEN_MOVEMENT_AGGREGATE_COLUMN_ORDER.NUMBER_OF_DIRECTION_CHANGES.ordinal()] = 
                        		fileContents.get(m-1)[SUDDEN_MOVEMENT_OUTPUT_FILE_TOTALS_COLUMN_ORDER.NUMBER_OF_DIRECTION_CHANGES_VALUE.ordinal()];
                        totalLine[SUDDEN_MOVEMENT_AGGREGATE_COLUMN_ORDER.AXIS_1_AVERAGE_ACCEL_CHANGE.ordinal()] = 
                        		fileContents.get(m-1)[SUDDEN_MOVEMENT_OUTPUT_FILE_TOTALS_COLUMN_ORDER.AXIS_1_AVERAGE_ACCEL_CHANGE_VALUE.ordinal()];
                        totalLine[SUDDEN_MOVEMENT_AGGREGATE_COLUMN_ORDER.AXIS_2_AVERAGE_ACCEL_CHANGE.ordinal()] = 
                        		fileContents.get(m-1)[SUDDEN_MOVEMENT_OUTPUT_FILE_TOTALS_COLUMN_ORDER.AXIS_2_AVERAGE_ACCEL_CHANGE_VALUE.ordinal()];
                        writer.writeNext(totalLine);
                    }
                }
                
                writer.flush();
                writer.close();
            }
        }
    }

}