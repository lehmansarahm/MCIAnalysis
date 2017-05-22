package edu.temple.tan.mcianalysis.aggregates;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.Constants.PAUSE_AGGREGATE_COLUMN_ORDER;
import edu.temple.tan.mcianalysis.utils.Constants.PAUSE_OUTPUT_FILE_TOTALS_COLUMN_ORDER;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
                String writeLine[] = new String[PAUSE_AGGREGATE_COLUMN_ORDER.values().length];
                
                String writerFilePath = listOfFiles[i].getAbsolutePath();
                writerFilePath = writerFilePath.concat("/" + aggregateFileName);
                CSVWriter writer = new CSVWriter(new FileWriter(writerFilePath));

                writeLine[PAUSE_AGGREGATE_COLUMN_ORDER.TASK.ordinal()] = "Task:";
                writeLine[PAUSE_AGGREGATE_COLUMN_ORDER.NUM_OF_PAUSES.ordinal()] = "Number of Pauses:";
                writeLine[PAUSE_AGGREGATE_COLUMN_ORDER.TOTAL_TIME_PAUSED.ordinal()] = "Total Time Paused (ms):";
                writeLine[PAUSE_AGGREGATE_COLUMN_ORDER.AVG_PAUSE_DURATION.ordinal()] = "Average Pause Duration (ms):";
                writer.writeNext(writeLine);

                //loop through the inner directory
                for (int j = 0; j < innerFiles.length; j++) {
                    if (!innerFiles[j].getName().equals(aggregateFileName)) {
                        String[] fileNameComponents = innerFiles[j].getName().split("_");
                        String taskName = (fileNameComponents.length >= 3) ? fileNameComponents[3] : "";
                        
                        String filePath = innerFiles[j].getAbsolutePath();
                        CSVReader reader = new CSVReader(new FileReader(filePath), ',', '"', 0);
                        List<String[]> fileContents = reader.readAll();
                        
                        int m = 0;
                        while(m < fileContents.size() && !fileContents.get(m)[0].equalsIgnoreCase("Configuration File Used:")) {
                            m++;
                        }
                        
                        writeLine[PAUSE_AGGREGATE_COLUMN_ORDER.TASK.ordinal()] = taskName;
                        writeLine[PAUSE_AGGREGATE_COLUMN_ORDER.NUM_OF_PAUSES.ordinal()] = 
                        		fileContents.get(m-1)[PAUSE_OUTPUT_FILE_TOTALS_COLUMN_ORDER.PAUSE_NUM_VALUE.ordinal()];
                        writeLine[PAUSE_AGGREGATE_COLUMN_ORDER.TOTAL_TIME_PAUSED.ordinal()] = 
                        		fileContents.get(m-1)[PAUSE_OUTPUT_FILE_TOTALS_COLUMN_ORDER.TOTAL_PAUSE_TIME_VALUE.ordinal()];
                        writeLine[PAUSE_AGGREGATE_COLUMN_ORDER.AVG_PAUSE_DURATION.ordinal()] = 
                        		fileContents.get(m-1)[PAUSE_OUTPUT_FILE_TOTALS_COLUMN_ORDER.AVG_PAUSE_TIME_VALUE.ordinal()];
                        writer.writeNext(writeLine);
                        reader.close();
                    }
                }
                
                writer.flush();
                writer.close();
            }
        }
    }
}