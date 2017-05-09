package edu.temple.tan.mcianalysis.aggregates;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
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
    public static void aggregateUserComparisonCSV() {
        try {
        	List<AnalysisUserSummary> summaries = new ArrayList<>();
        	List<String> userNames = new ArrayList<>();
        	File finalDir = new File("./Final");
        	
        	File[] analysisDirs = finalDir.listFiles();
        	for (File analysisDir : analysisDirs) {
        		AnalysisUserSummary summary = new AnalysisUserSummary(analysisDir.getName());
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

            String path_to_csv = new File("").getAbsolutePath().concat("/Final/UserComparison.csv");
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
	private static List<String[]> generateOutputArrays(List<AnalysisUserSummary> summaries) {
    	List<String[]> output = new ArrayList<>();
        for (AnalysisUserSummary summary : summaries) output.addAll(summary.toOutputArray());
    	return output;
    }
    
}