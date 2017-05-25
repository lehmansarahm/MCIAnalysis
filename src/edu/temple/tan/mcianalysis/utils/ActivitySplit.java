/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.tan.mcianalysis.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.MCIAnalysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ActivitySplit {

	/**
	 * 
	 * @param reader
	 * @param userID
	 * @param requestedActivityName
	 * @return
	 * @throws IOException
	 */
    public static String generateActivitySpecificCSV(CSVReader reader, String userID, 
    												String requestedActivityName) throws IOException {
        String timeStamp = getTimeStamp();
        MCIAnalysis.requestedActivitySet.add(requestedActivityName);
        List<String[]> writerContents = new ArrayList<String[]>();
        writerContents.add(ToolkitUtils.getHeaderLine());

        //----------------------------------------------------------------------
        // If the line belongs to the requested activity, it is written to the 
        // new CSV file in the Intermediate folder.
        //----------------------------------------------------------------------
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
        	String currentActivity = nextLine[Constants.INPUT_FILE_COLUMN_ORDER.ACTIVITY.ordinal()].trim();
            if (currentActivity.equalsIgnoreCase(requestedActivityName)) {
            	writerContents.add(nextLine);
            }
        }

        //----------------------------------------------------------------------
        // The new CSV file name will be the user id, activity, and time stamp
        //----------------------------------------------------------------------
        String intermFilePath = getIntermFilePath(userID, null, requestedActivityName, timeStamp);
        CSVWriter writer = new CSVWriter(new FileWriter(intermFilePath));
        writer.writeAll(writerContents);
        writer.close();

        //----------------------------------------------------------------------
        // return the path to the new intermediate file
        //----------------------------------------------------------------------
        Logger.getLogger(ActivitySplit.class.getName()).log(Level.INFO, 
        		"Finished processing intermediate input file: " + intermFilePath, "");
        return intermFilePath;
    }

    /**
     * 
     * @param reader
     * @param userID
     * @return
     * @throws IOException
     */
    public static List<String> generateCSVForAllActivities(CSVReader reader, String userID) throws IOException {
		Logger.getLogger(ActivitySplit.class.getName()).log(Level.INFO, 
        		"Splitting interm activities for user: " + userID, "");
    	
        List<String> intermFilePaths = new ArrayList<String>();
        String[] headerLine = ToolkitUtils.getHeaderLine();
        String timeStamp = getTimeStamp();
        String intermFilePath = null;
        
        int taskStartIndex = Constants.INPUT_FILE_COLUMN_ORDER.START_END.ordinal();
        int activityIndex = Constants.INPUT_FILE_COLUMN_ORDER.ACTIVITY.ordinal();

        CSVWriter writer = null;
        boolean activityStarted = false;
        String requestedActivityName = "NotInitialized";
        
        //----------------------------------------------------------------------
        // If the line belongs to the requested activity, it is written to the 
        // new CSV file in the Intermediate folder.
        //----------------------------------------------------------------------
        String[] nextLine;
        while ((nextLine = reader.readNext()) != null) {
            if (!activityStarted && matchesFlag(nextLine, taskStartIndex, Constants.FLAG_START)) {
                requestedActivityName = nextLine[activityIndex];
                MCIAnalysis.requestedActivitySet.add(requestedActivityName);
                activityStarted = true;

                //----------------------------------------------------------------------
                // The new csv file name will be the user id, activity, and time stamp
                //----------------------------------------------------------------------
                intermFilePath = getIntermFilePath(userID, MCIAnalysis.accelerationProcessing, 
                		requestedActivityName, timeStamp);
                writer = new CSVWriter(new FileWriter(intermFilePath));
                writer.writeNext(headerLine);
            }
            
            if (activityStarted) {
                if (matchesFlag(nextLine, activityIndex, requestedActivityName)) {
                	//----------------------------------------------------------------------
                	// This activity matches the expected activity ...
                	// Copy to the current intermediate file
                	//----------------------------------------------------------------------
                    writer.writeNext(nextLine);
                } else {
                	//----------------------------------------------------------------------
                	// We have encountered a new activity ...
                    // Close current writer and add the file path to the array of files
                	//----------------------------------------------------------------------
                    intermFilePaths.add(intermFilePath);
                    writer.close();

                	//----------------------------------------------------------------------
                    // The new activity is now the requested activity...
                    // Create a new CSV file to store the data.
                	//----------------------------------------------------------------------
                    requestedActivityName = nextLine[activityIndex];
                    MCIAnalysis.requestedActivitySet.add(requestedActivityName);
                    intermFilePath = getIntermFilePath(userID, MCIAnalysis.accelerationProcessing, 
                    		requestedActivityName, timeStamp);

                	//----------------------------------------------------------------------
                    // Write the expected header to the new file, followed by the new activity line
                	//----------------------------------------------------------------------
                    writer = new CSVWriter(new FileWriter(intermFilePath));
                    writer.writeNext(headerLine);
                    writer.writeNext(nextLine);
                }
            }

        	//----------------------------------------------------------------------
            // If we find the "quit" line, force the process to stop
        	//----------------------------------------------------------------------
            if (matchesFlag(nextLine, taskStartIndex, Constants.FLAG_QUIT)) {
                break;	//end the loop
            }
        }

    	//----------------------------------------------------------------------
        // Close current writer and add the file path to the array of files
    	//----------------------------------------------------------------------
        if (writer != null) {
            writer.close();
            intermFilePaths.add(intermFilePath);
        }
        
        return intermFilePaths;
    }

    /**
     * 
     * @return
     */
    private static String getTimeStamp() {
        String timeStamp = new SimpleDateFormat(Constants.SIMPLE_DATE_TIME_FORMAT).format(new java.util.Date());
        MCIAnalysis.runTime = timeStamp;
        return timeStamp;
    }
    
    /**
     * 
     * @param userID
     * @param requestedActivityName
     * @param timeStamp
     * @return
     */
    private static String getIntermFilePath(String userID, String accelProcessing, String requestedActivityName, String timeStamp) {
        String absolutePath = new File("").getAbsolutePath();
        new File(absolutePath.concat(Constants.FOLDER_NAME_INTERMEDIATE)).mkdirs();
        
        String intermFileName = Constants.FOLDER_NAME_INTERMEDIATE + "/" + userID 
        		+ (accelProcessing != null ? Constants.DELIMITER_FILENAME + accelProcessing : "")
        		+ Constants.DELIMITER_FILENAME 
        		+ requestedActivityName.replace(" ", Constants.DELIMITER_SPACE) 
        		+ Constants.DELIMITER_FILENAME + timeStamp + ".csv";
        String intermFilePath = absolutePath.concat(intermFileName);

		Logger.getLogger(ActivitySplit.class.getName()).log(Level.INFO, 
        		"New interm file path generated for activity: " + requestedActivityName, "");
        return intermFilePath;
    }
    
    /**
     * 
     * @param line
     * @param index
     * @param flag
     * @return
     */
    private static boolean matchesFlag(String[] line, int index, String flag) {
    	boolean isLineLongEnough = (line.length > index);
    	boolean isFlagPresent = isLineLongEnough && line[index].equalsIgnoreCase(flag);    	
    	return isFlagPresent;
    }
    
}