package edu.temple.tan.mcianalysis.aggregates;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.utils.Constants;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PauseAggregate {

	/**
	 * 
	 * @throws IOException
	 */
    public static void aggregatePauseCSV() throws IOException {
        String absolute_path = new File("").getAbsolutePath();
        
        File pauseFolder = new File(absolute_path.concat(Constants.FOLDER_NAME_FINAL + Constants.FOLDER_NAME_PAUSE));
        if (pauseFolder.exists()) aggregatePauseDirectory(pauseFolder, true);
        
        pauseFolder = new File(absolute_path.concat(Constants.FOLDER_NAME_FINAL + Constants.FOLDER_NAME_PAUSE_COUNT));
        if (pauseFolder.exists()) aggregatePauseDirectory(pauseFolder, true);
        
        pauseFolder = new File(absolute_path.concat(Constants.FOLDER_NAME_FINAL + Constants.FOLDER_NAME_PAUSE_DURATION));
        if (pauseFolder.exists()) aggregatePauseDirectory(pauseFolder, false);
    }
    
    /**
     * 
     * @param folder
     * @throws IOException
     */
    public static void aggregatePauseDirectory(File folder, boolean isCount) throws IOException {
        File[] listOfFiles = folder.listFiles();
        String aggregateFileName = (isCount 
        		? Constants.AGGREGATE_FILE_PAUSE_COUNT 
				: Constants.AGGREGATE_FILE_PAUSE_DURATION);
        
        //loop through the directories found within
        for (int i = 0; i < listOfFiles.length; i++) {
            //if the file is a directory we want to go into it and aggregate the direction changes
            if (listOfFiles[i].isDirectory()) {
                File[] innerFiles = listOfFiles[i].listFiles();
                String total_write_line[] = new String[4];
                
                String writer_path = listOfFiles[i].getAbsolutePath();
                writer_path = writer_path.concat("/" + aggregateFileName);
                CSVWriter writer = new CSVWriter(new FileWriter(writer_path));

                total_write_line[Constants.PAUSE_AGGREGATE_COLUMN_ORDER.TASK.ordinal()] = "Task:";
                total_write_line[Constants.PAUSE_AGGREGATE_COLUMN_ORDER.NUM_OF_PAUSES.ordinal()] = "Number of Pauses:";
                total_write_line[Constants.PAUSE_AGGREGATE_COLUMN_ORDER.TOTAL_TIME_PAUSED.ordinal()] = "Total Time Paused:";
                total_write_line[Constants.PAUSE_AGGREGATE_COLUMN_ORDER.AVG_PAUSE_DURATION.ordinal()] = "Average Pause Duration:";

                writer.writeNext(total_write_line);

                //loop through the inner directory
                for (int j = 0; j < innerFiles.length; j++) {
                    if (!innerFiles[j].getName().equals(aggregateFileName)) {
                        String[] name_components = innerFiles[j].getName().split("_");
                        String task_name = "";

                        if (name_components.length >= 3) {
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
                        reader.close();
                    }
                }
                writer.flush();
                writer.close();
            }
        }
    }
}