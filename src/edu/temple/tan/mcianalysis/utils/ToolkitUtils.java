package edu.temple.tan.mcianalysis.utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;

/**
 * General purpose utility methods useful to whole toolkit
 *
 * @author Philip M. Coulomb
 * @author Sarah M. Lehman
 */
public class ToolkitUtils {
	
	/**
	 * 
	 * @param activityName
	 * @return
	 */
	public static String getFilenameCompatibleActivityName(String activityName) {
		return activityName.replace(" ", Constants.DELIMITER_SPACE);
	}
	
    /**
     * Calculates the magnitude of a vector according to formula:
     * mag=sqrt(x^2+y^2+z^2) 
     * 
     * @param accelX - the X axis component of the acceleration vector
     * @param accelY - the Y axis component of the acceleration vector
     * @param accelZ - the Z axis component of the acceleration vector
     * @return the magnitude of the provided vector
     */
    public static double calculateMagnitude(double accelX, double accelY, double accelZ) {
        double magX = Math.pow(accelX, 2);
        double magY = Math.pow(accelY, 2);
        double magZ = Math.pow(accelZ, 2);
        double magnitude = Math.sqrt(magX + magY + magZ);
        return magnitude;
    }

    /**
     * Ensures proper output directories are set up, and that the CSV Writer has 
     * a valid and clear filename to write to.
     * 
     * @param localFilePath - local file path for which to create the output directories
     * @param userID - the user ID by which to organize the output materials
     * @param analysisName - the name of the analysis operation to initialize
     * 
     * @return the absolute file path of the output directory
     */
    public static String initializeAnalysisOutputDirs(String localFilePath, String userID, 
	  String analysisName) {
        String[] pathComponents = localFilePath.split(Constants.DELIMITER_FILEPATH);
        String finalFilename = pathComponents[pathComponents.length - 1];
        String absolutePath = new File("").getAbsolutePath();
        
        String debugPath = absolutePath.concat(Constants.FOLDER_NAME_DEBUG);
        new File(debugPath).mkdirs();
        
        absolutePath = absolutePath.concat(Constants.FOLDER_NAME_FINAL);
        new File(absolutePath).mkdirs();
        
        absolutePath = absolutePath.concat(Constants.DELIMITER_FILEPATH + analysisName);
        new File(absolutePath).mkdirs();

        absolutePath = absolutePath.concat(Constants.DELIMITER_FILEPATH.concat(userID));
        new File(absolutePath).mkdirs();

        absolutePath = absolutePath.concat((Constants.DELIMITER_FILEPATH 
        		+ analysisName + Constants.DELIMITER_FILENAME).concat(finalFilename));
        return absolutePath;
    }
    
    /**
     * Support method to clean up any remaining artifacts from previous executions 
     * before proceeding with the current execution
     */
    public static void removeOldArtifacts() {
        String absolutePath = new File("").getAbsolutePath();
        removeOldDirectory(absolutePath, Constants.FOLDER_NAME_FINAL);
        removeOldDirectory(absolutePath, Constants.FOLDER_NAME_DEBUG);

        removeOldDirectory(absolutePath, Constants.FOLDER_NAME_PREPROCESSING_LINEAR);
        removeOldDirectory(absolutePath, Constants.FOLDER_NAME_PREPROCESSING_LPFILTER);
        removeOldDirectory(absolutePath, Constants.FOLDER_NAME_PREPROCESSING_NORM);

        removeOldDirectory(absolutePath, Constants.FOLDER_NAME_INTERM_ACT_SPLIT);
        removeOldDirectory(absolutePath, Constants.FOLDER_NAME_INTERM_CALIBRATIONS);
    }
    
    /**
     * 
     * @param absolutePath
     * @param folderName
     */
    private static void removeOldDirectory(String absolutePath, String folderName) {
        try {
            File folder = new File(absolutePath.concat(folderName));
			FileUtils.deleteDirectory(folder);
		} catch (IOException e) {
			// do nothing ... can't delete it if it wasn't there
		}
    }
    
	/**
	 * 
	 * @param filename
	 * @return
	 */
    public static String getActivityNameFromOutputFile(String filename) {
        String[] nameComponents = filename.split(Constants.DELIMITER_FILENAME);
        String activityName = nameComponents[nameComponents.length - 2];
    	return activityName;
    }
    
    /**
     * 
     * @param input
     * @return
     */
    public static Date getDateTime(String input) {
    	Date dateTime = null;
    	
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.SIMPLE_TIME_FORMAT_LONG);
            dateTime = dateFormat.parse(input);
        } catch (ParseException ex) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.SIMPLE_TIME_FORMAT_SHORT);
            try {
				dateTime = dateFormat.parse(input);
			} catch (ParseException e) {
				// if THAT fails ... do nothing
		    	LogManager.error(ToolkitUtils.class, ex);
			}
        }
        
        return dateTime;
    }
    
    /**
     * 
     * @param startTime
     * @param endTime
     * @param recordCount
     * @return
     */
    public static double getSecondsBetweenDates(Date startTime, Date endTime, int recordCount) {
    	long durationInMS = endTime.getTime() - startTime.getTime();
        double taskTimeInSec = (((double)durationInMS) / 1000.0);
        double durationInSec = (((taskTimeInSec == Math.floor(taskTimeInSec)) && !Double.isInfinite(taskTimeInSec))) 
        		? ((Constants.SAMPLING_PERIOD * recordCount) / 1000.0) : taskTimeInSec;
		return durationInSec;
    }

    /**
     * 
     * @param s
     * @return
     */
    public static boolean isNumeric(String... s) {  
    	boolean isNumeric = false;
    	if (s != null) {
    		isNumeric = true;
    		for (String t : s) {
    			if (t.equalsIgnoreCase("") || !t.matches("[-+]?\\d*\\.?\\d+(?:E[-+]?\\d+)?")) {
    				isNumeric = false;
    				break;
    			}
    		}
    	}
        return isNumeric;  
    }
    
    /**
     * 
     * @param nextLine
     * @return
     */
    public static boolean isHeaderLine(String[] nextLine) {
    	return (nextLine[Constants.INPUT_FILE_COLUMN_ORDER.TIME.ordinal()].equals(Constants.DATA_COLUMN_TIME));
    }

    /**
     * 
     * @return
     */
    public static String[] getInputHeaderLine() {
        String[] headerLine = new String[] {
        		Constants.DATA_COLUMN_TIME,
        		Constants.DATA_COLUMN_RECORD_NO,
        		Constants.DATA_COLUMN_AZIMUTH,
        		Constants.DATA_COLUMN_PITCH,
        		Constants.DATA_COLUMN_ROLL,
        		Constants.DATA_COLUMN_ACCEL_X,
        		Constants.DATA_COLUMN_ACCEL_Y,
        		Constants.DATA_COLUMN_ACCEL_Z,
        		Constants.DATA_COLUMN_START_END,
        		Constants.DATA_COLUMN_ACTIVITY
        };
        return headerLine;
    }

    /**
     * 
     * @return
     */
    public static String[] getIntermHeaderLine() {
        String[] headerLine = new String[] {
        		Constants.DATA_COLUMN_TIME,
        		Constants.DATA_COLUMN_RECORD_NO,
        		Constants.DATA_COLUMN_AZIMUTH,
        		Constants.DATA_COLUMN_PITCH,
        		Constants.DATA_COLUMN_ROLL,
        		Constants.DATA_COLUMN_ACCEL_X,
        		Constants.DATA_COLUMN_ACCEL_Y,
        		Constants.DATA_COLUMN_ACCEL_Z,
        		Constants.DATA_COLUMN_ACCEL_MAG,
        		Constants.DATA_COLUMN_SPEED,
        		Constants.DATA_COLUMN_START_END,
        		Constants.DATA_COLUMN_ACTIVITY
        };
        return headerLine;
    }

    /**
     * 
     * @return
     */
    public static String[] getPreprocHeaderLine() {
        String[] headerLine = new String[] {
        		Constants.DATA_COLUMN_TIME,
        		Constants.DATA_COLUMN_RECORD_NO,
        		Constants.DATA_COLUMN_AZIMUTH,
        		Constants.DATA_COLUMN_PITCH,
        		Constants.DATA_COLUMN_ROLL,
        		Constants.DATA_COLUMN_ACCEL_X,
        		Constants.DATA_COLUMN_ACCEL_Y,
        		Constants.DATA_COLUMN_ACCEL_Z,
        		Constants.DATA_COLUMN_ACCEL_MAG,
        		Constants.DATA_COLUMN_SPEED,
        		Constants.DATA_COLUMN_SPEED_EMA,
        		Constants.DATA_COLUMN_SPEED_EMA_SCALED,
        		Constants.DATA_COLUMN_START_END,
        		Constants.DATA_COLUMN_ACTIVITY
        };
        return headerLine;
    }
    
}