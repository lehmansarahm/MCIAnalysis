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
import java.util.LinkedList;
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
        	List<String> userNames = getUniqueUserNames(analysisDirs);
        	List<LinkedList<String>> output = new ArrayList<>();
        	LinkedList<String> taskNames = new LinkedList<>();
        	
        	// iterate through again to perform first level aggregation for each user
        	boolean firstUserProcessed = false;
        	for (String userName : userNames) {
        		UserSummary summary = getUserSummary(userName, analysisDirs);
            	List<LinkedList<String>> summaryOutput = summary.toOutputList();
            	LinkedList<String> summaryTaskNames = summaryOutput.get(0);
            	LinkedList<String> summaryMetricNames = summaryOutput.get(1);
            	
        		int firstSubtaskIndex = Constants.USER_AGGREGATE_COLUMN_ORDER.values().length;
        		int subtaskHeaders = Constants.USER_AGGREGATE_SUBTASK_COLUMN_ORDER.values().length;
        		
            	if (!firstUserProcessed) {
            		output.addAll(summaryOutput);
            		firstUserProcessed = !firstUserProcessed;
            		for (int i = firstSubtaskIndex; i < summaryTaskNames.size(); i += subtaskHeaders) {
        				taskNames.add(summaryTaskNames.get(i));
            		}
            	} else {
            		// add a new output line and write the high level details
            		LinkedList<String> newSummaryOutput = new LinkedList<>();
            		LinkedList<String> summarySubtaskOutput = summaryOutput.get(2);

            		int taskIndex = Constants.USER_AGGREGATE_COLUMN_ORDER.values().length;
            		for (int i = 0; i < taskIndex; i++) {
            			newSummaryOutput.add(summarySubtaskOutput.get(i));
            		}
            		
            		// parse the matching subtasks
            		LinkedList<String> matchedTasks = new LinkedList<>();
            		for (int i = 0; i < taskNames.size(); i++) {
            			String taskName = taskNames.get(i);
            			boolean isMatchingTask = summaryTaskNames.contains(taskName);
        				for (int j = 0; j < subtaskHeaders; j++) {
                			if (isMatchingTask) {
                				int oldIndex = summaryTaskNames.indexOf(taskName);
                				int newIndex = ((i * subtaskHeaders) + firstSubtaskIndex);
                        		for (int k = 0; k < subtaskHeaders; k++) {
                        			String newOutput = summarySubtaskOutput.get(oldIndex + k);
                        			if (newSummaryOutput.size() > (newIndex + k)) {
                        				newSummaryOutput.set((newIndex + k), newOutput);
                        			} else newSummaryOutput.add(newOutput); 
                        		}
                				matchedTasks.add(taskName);
                			} else newSummaryOutput.add("-1");
        				}
            		}
            		
            		// print the remaining subtasks
            		for (int i = firstSubtaskIndex; i < summaryTaskNames.size(); i += subtaskHeaders) {
            			String taskName = summaryTaskNames.get(i);
            			if (!taskNames.contains(taskName)) {
            				taskNames.add(summaryTaskNames.get(i));
            				for (int j = i; j < (i + subtaskHeaders); j++) {
            					// print the new header values
            					LinkedList<String> firstHeaderLine = output.get(0);
            					firstHeaderLine.addLast(summaryTaskNames.get(j));
            					LinkedList<String> secondHeaderLine = output.get(1);
            					secondHeaderLine.addLast(summaryMetricNames.get(j));
            					
            					// print -1 for all previous and actual output for current
            					for (int k = 2; k < output.size(); k++) {
            						output.get(k).addLast("-1");
            					}
            					newSummaryOutput.addLast(summarySubtaskOutput.get(j));
            				}
            			}
            		}
            		
            		// add the output line to the main output list
            		output.add(newSummaryOutput);
            	}
        	}
        	
            String path_to_csv = new File("").getAbsolutePath().concat(Constants.FOLDER_NAME_FINAL 
            		+ "/" + Constants.AGGREGATE_FILE_USERS);
            CSVWriter writer = new CSVWriter(new FileWriter(path_to_csv));
            writer.writeAll(generateOutputArray(output));
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserAggregate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserAggregate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 
     * @return
     */
    private static List<String> getUniqueUserNames(File[] analysisDirs) {
    	List<String> userNames = new ArrayList<>();
    	
    	for (File analysisDir : analysisDirs) {
    		for (File userDir : analysisDir.listFiles()) {
    			String userName = userDir.getName();
    			if (!userNames.contains(userName)) userNames.add(userName);
    		}
    	}
    	
    	return userNames;
    }
    
    /**
     * 
     * @param userName
     * @param analysisDirs
     * @return
     */
    private static UserSummary getUserSummary(String userName, File[] analysisDirs) {
    	try {
			UserSummary summary = new UserSummary(userName);
	    	for (File analysisDir : analysisDirs) {
	    		String analysisName = analysisDir.getName();
	    		for (File userDir : analysisDir.listFiles()) {
	    			if (userDir.getName().equals(userName)) {
	        			String summaryFilePath = null;
	        			for (File outputFile : userDir.listFiles()) {
	        				if (Constants.AGGREGATE_FILES.contains(outputFile.getName())) {
	        					summaryFilePath = outputFile.getPath();
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
	    	return summary;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(UserAggregate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UserAggregate.class.getName()).log(Level.SEVERE, null, ex);
        }
    	return null;
    }
    
    /**
     * 
     * @param list
     * @return
     */
    private static List<String[]> generateOutputArray(List<LinkedList<String>> list) {
    	List<String[]> output = new ArrayList<>();
    	
    	for (LinkedList<String> linkedList : list) {
    		String[] array = linkedList.toArray(new String[linkedList.size()]);
    		output.add(array);
    	}
    	
    	return output;
    }
    
}