package edu.temple.tan.mcianalysis.aggregates;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.utils.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserAggregate {

	/**
	 * 
	 */
    public static void aggregateUserResultsCSV() {
        try {
        	File finalDir = new File("." + Constants.FOLDER_NAME_FINAL);
        	File[] analysisDirs = finalDir.listFiles();
        	
        	// iterate through once to find all unique user names
        	List<String> userNames = new ArrayList<>();
        	for (File analysisDir : analysisDirs) {
        		for (File userDir : analysisDir.listFiles()) {
        			String userName = userDir.getName();
        			if (!userNames.contains(userName)) userNames.add(userName);
        		}
        	}
        	
        	// iterate through again to perform first level aggregation for each user
        	for (String userName : userNames) {
        		UserSummary summary = new UserSummary(userName);
            	for (File analysisDir : analysisDirs) {
            		String analysisName = analysisDir.getName();
            		for (File userDir : analysisDir.listFiles()) {
            			if (userDir.getName().equals(userName)) {
                			String summaryFilePath = null;
                			for (File output : userDir.listFiles()) {
                				if (Constants.AGGREGATE_FILES.contains(output.getName())) {
                					summaryFilePath = output.getPath();
                					break;
                				}
                			}

                            CSVReader reader = new CSVReader(new FileReader(summaryFilePath), ',', '"', 0);
                            List<String[]> contents = reader.readAll();
                            
                            boolean headersSkipped = false;
                            for (String[] line : contents) {
                            	if (headersSkipped) summary.addAggregateSummary(analysisName, line);
                            	else headersSkipped = !headersSkipped;
                            }

                            reader.close();
            			}
            		}
            	}
            	
                String outputFilePath = new File("").getAbsolutePath().concat(Constants.FOLDER_NAME_INTERMEDIATE 
                		+ "/" + userName + "_" + Constants.AGGREGATE_FILE_USERS);
                CSVWriter writer = new CSVWriter(new FileWriter(outputFilePath));
                writer.writeAll(summary.toOutputArray());
                writer.close();
        	}

        	// finally, iterate through intermediate files and stitch them all together
        	File intermDir = new File("." + Constants.FOLDER_NAME_INTERMEDIATE);
			for (File intermFile : intermDir.listFiles()) {
				if (intermFile.getName().contains(Constants.AGGREGATE_FILE_USERS)) {
					// do the thing
				}
			}
        	
            /*String path_to_csv = new File("").getAbsolutePath().concat(Constants.FOLDER_NAME_FINAL 
            		+ "/" + Constants.AGGREGATE_FILE_USERS);
            CSVWriter writer = new CSVWriter(new FileWriter(path_to_csv));
            writer.writeAll(generateOutputArrays(new ArrayList<UserSummary>(summaries.values())));
            writer.close();*/
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserAggregate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserAggregate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}