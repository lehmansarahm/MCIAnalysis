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
        	List<UserSummary> summaries = new ArrayList<>();
        	List<String> userNames = new ArrayList<>();
        	File finalDir = new File("." + Constants.FOLDER_NAME_FINAL);
        	
        	File[] analysisDirs = finalDir.listFiles();
        	for (File analysisDir : analysisDirs) {
        		UserSummary summary = new UserSummary(analysisDir.getName());
        		File[] users = analysisDir.listFiles();
        		
        		for (File user : users) {
        			String userName = user.getName();
        			userNames.add(userName);
        			
        			String summaryFile = null;
        			File[] outputs = user.listFiles();
        			for (File output : outputs) {
        				if (summaryFile == null || output.getPath().length() < summaryFile.length()) {
        					summaryFile = output.getPath();
        				}
        			}

                    CSVReader reader = new CSVReader(new FileReader(summaryFile), ',', '"', 0);
                    List<String[]> contents = reader.readAll();
                    String[] headers = null;
                    for (String[] line : contents) {
                    	if (headers == null) headers = line;
                    	else summary.addUserSummary(userName, headers, line);
                    }
                    reader.close();
        		}
        		
        		summaries.add(summary);
        	}

    		/*
    		 * ----------------------------------------------------------------------
    		 * CURRENT SUCCESS CASE: mult. users, different names/same tasks
    		 * ----------------------------------------------------------------------
    		 * CURRENT FAILING CASES: mult. users, same names or different tasks
    		 * ----------------------------------------------------------------------
    		 */
    		
            String path_to_csv = new File("").getAbsolutePath().concat(Constants.FOLDER_NAME_FINAL 
            		+ "/" + Constants.AGGREGATE_FILE_USERS);
            CSVWriter writer = new CSVWriter(new FileWriter(path_to_csv));
            writer.writeAll(generateOutputArrays(summaries));
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserAggregate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserAggregate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     * @param comparisons
     * @param userNames
     * @return
     */
	private static List<String[]> generateOutputArrays(List<UserSummary> summaries) {
    	List<String[]> output = new ArrayList<>();
        for (UserSummary summary : summaries) output.addAll(summary.toOutputArray());
    	return output;
    }
    
}