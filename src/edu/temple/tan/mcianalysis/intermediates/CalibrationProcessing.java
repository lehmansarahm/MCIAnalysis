package edu.temple.tan.mcianalysis.intermediates;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.LogManager;
import edu.temple.tan.mcianalysis.utils.Constants.CALIB_FILE_COLUMN_ORDER;
import edu.temple.tan.mcianalysis.utils.Constants.INTERM_FILE_COLUMN_ORDER;
import edu.temple.tan.mcianalysis.utils.ToolkitUtils;

public class CalibrationProcessing {
	
	/**
	 * 
	 * @param userName
	 * @param inputFileList
	 * @param calibrationStep
	 */
	public static void calibrateThresholdsForUser(String userName, List<String> inputFileList, String calibrationStep) {
		String inputFilePath = "";
		String inputFilePrefix = userName + Constants.DELIMITER_FILENAME 
				+ ToolkitUtils.getFilenameCompatibleActivityName(calibrationStep);
		String linearInputFilePrefix = userName + Constants.DELIMITER_FILENAME + "Linear" + Constants.DELIMITER_FILENAME
				+ ToolkitUtils.getFilenameCompatibleActivityName(calibrationStep);
		
		for (String inputFile : inputFileList) {
	    	//LogManager.info(CalibrationProcessing.class, "Evaluating input file: " + inputFile);
			if (inputFile.contains(inputFilePrefix) || inputFile.contains(linearInputFilePrefix)) 
				inputFilePath = inputFile;
		}

		// we are assuming that an intermediate file exists for the subtask data associated 
		// with the desired calibration step ... if this file does not exist, return
		if (inputFilePath.equals("")) {
	    	LogManager.info(CalibrationProcessing.class, 
	    			"No valid input file path.  Returning from calibration processing.");
			return;
		}
		
		// else, a matching input file has been identified ... 
		// parse the data inside to determine customized user thresholds
		double calibPauseThreshold = 0.0d, calibSuddenMotionThreshold = 0.0d;
		double avgAcceleration = 0.0d, avgSpeed = 0.0d;
		
		try {
			CSVReader reader = new CSVReader(new FileReader(inputFilePath), ',', '"', 0);
			List<String[]> fileContents = reader.readAll();
			reader.close();

	    	LogManager.info(CalibrationProcessing.class, 
	    			fileContents.size() + " content lines retrieved from input file: " + inputFilePath);
			
			double calibTotalAccelMag = 0.0d;
			int calibRowCount = 0;

			// for each row in the input file, update the running calibration totals
	    	Date startTime = null, endTime = null;
			for (String[] nextLine : fileContents) {
				if (!ToolkitUtils.isHeaderLine(nextLine)) {
					String currentTime = nextLine[INTERM_FILE_COLUMN_ORDER.TIME.ordinal()];
					if (startTime == null) startTime = ToolkitUtils.getDateTime(currentTime);
					endTime = ToolkitUtils.getDateTime(currentTime);
					
					String currentSubtaskStep = nextLine[INTERM_FILE_COLUMN_ORDER.ACTIVITY.ordinal()].trim();
					if (calibrationStep.equals(currentSubtaskStep)) {
						double currAccelX = Double.parseDouble(nextLine[INTERM_FILE_COLUMN_ORDER.ACCEL_X.ordinal()]);
						double currAccelY = Double.parseDouble(nextLine[INTERM_FILE_COLUMN_ORDER.ACCEL_Y.ordinal()]);
						double currAccelZ = Double.parseDouble(nextLine[INTERM_FILE_COLUMN_ORDER.ACCEL_Z.ordinal()]);
						calibTotalAccelMag += ToolkitUtils.calculateMagnitude(currAccelX, currAccelY, currAccelZ);
						calibRowCount++;
					}
				}
			}
			
			// only proceed if we read in at least one valid row
			if (calibRowCount > 0) {
				// once input file has been read, calculate new thresholds
				avgAcceleration = (calibTotalAccelMag / calibRowCount);
				avgSpeed = (startTime == null || endTime == null) ? 0 :
						avgAcceleration * ToolkitUtils.getSecondsBetweenDates(startTime, endTime, (fileContents.size() - 1));
				calibPauseThreshold = avgAcceleration * Constants.CALIBRATION_PAUSE_THRESHOLD_PERCENTAGE;
				calibSuddenMotionThreshold = avgAcceleration * Constants.CALIBRATION_SUDDEN_MOTION_THRESHOLD_PERCENTAGE;
				
				// calculations complete ... write to intermediate output file
				dumpToOutputFile(userName, avgAcceleration, avgSpeed, calibPauseThreshold, calibSuddenMotionThreshold);
			}
		} catch (IOException e) {
	    	LogManager.error(CalibrationProcessing.class, e);
		}
	}
	
	/**
	 * 
	 * @param userID
	 * @return
	 */
	public static double getCalibratedPauseThresholdForUser(String userID) {
		return getDoubleFromCalibrationFile(userID, 
				CALIB_FILE_COLUMN_ORDER.PAUSE_THRESHOLD.ordinal());
	}
	
	/**
	 * 
	 * @param userID
	 * @return
	 */
	public static double getCalibratedSuddenMotionThresholdForUser(String userID) {
		return getDoubleFromCalibrationFile(userID, 
				CALIB_FILE_COLUMN_ORDER.SUDDEN_MOTION_THRESHOLD.ordinal());
	}
	
	/**
	 * 
	 * @param userID
	 * @param valueIndex
	 * @return
	 */
	private static double getDoubleFromCalibrationFile(String userID, int valueIndex) {
		String userCalibFileName = getCalibFileNameForUser(userID);
		File calibrationFolder = new File(getCalibrationFolderPath());
		if (calibrationFolder.exists() && calibrationFolder.isDirectory()) {
			File[] userCalibFiles = calibrationFolder.listFiles();
			for (File userCalibFile : userCalibFiles) {
				if (userCalibFile.getName().contains(userCalibFileName)) {
					try {
						CSVReader reader = new CSVReader(new FileReader(userCalibFile.getPath()), ',', '"', 0);
						List<String[]> fileContents = reader.readAll();
						reader.close();
						return (Double.parseDouble(fileContents.get(1)[valueIndex]));
					} catch (IOException ex) {
				    	LogManager.error(CalibrationProcessing.class, ex);
					}
				}
			}
		}
		return 0.0d;
	}
	
	/**
	 * 
	 * @param userName
	 * @param avgAcceleration
	 * @param avgSpeed
	 * @param calibPauseThreshold
	 * @param calibSuddenMotionThreshold
	 * @throws IOException
	 */
	private static void dumpToOutputFile(String userName, double avgAcceleration, double avgSpeed, 
					double calibPauseThreshold, double calibSuddenMotionThreshold) throws IOException {
        String[] writeLine = new String[CALIB_FILE_COLUMN_ORDER.values().length];
        writeLine[CALIB_FILE_COLUMN_ORDER.USERNAME.ordinal()] = "User Name";
        writeLine[CALIB_FILE_COLUMN_ORDER.AVERAGE_ACCELERATION.ordinal()] = "Avg Acceleration";
        writeLine[CALIB_FILE_COLUMN_ORDER.AVERAGE_SPEED.ordinal()] = "Avg Speed";
        writeLine[CALIB_FILE_COLUMN_ORDER.PAUSE_THRESHOLD.ordinal()] = "Pause Threshold";
        writeLine[CALIB_FILE_COLUMN_ORDER.SUDDEN_MOTION_THRESHOLD.ordinal()] = "Sudden Motion Threshold";

        String outputFilePath = (getCalibrationFolderPath() + "/" + getCalibFileNameForUser(userName));
    	LogManager.info(CalibrationProcessing.class, 
    			"Getting ready to dump calibration data to output file: " + outputFilePath);
		
        CSVWriter writer = new CSVWriter(new FileWriter(outputFilePath));
        writer.writeNext(writeLine);
        
        writeLine[CALIB_FILE_COLUMN_ORDER.USERNAME.ordinal()] = userName;
        writeLine[CALIB_FILE_COLUMN_ORDER.AVERAGE_ACCELERATION.ordinal()] = Double.toString(avgAcceleration);
        writeLine[CALIB_FILE_COLUMN_ORDER.AVERAGE_SPEED.ordinal()] = Double.toString(avgSpeed);
        writeLine[CALIB_FILE_COLUMN_ORDER.PAUSE_THRESHOLD.ordinal()] = Double.toString(calibPauseThreshold);
        writeLine[CALIB_FILE_COLUMN_ORDER.SUDDEN_MOTION_THRESHOLD.ordinal()] = Double.toString(calibSuddenMotionThreshold);
        
        writer.writeNext(writeLine);
        writer.close();
	}
	
	/**
	 * 
	 * @return
	 */
	private static String getCalibrationFolderPath() {
		String calibrationsFolder = (new File("").getAbsolutePath()).concat(Constants.FOLDER_NAME_INTERM_CALIBRATIONS);
		File calibFolder = new File(calibrationsFolder);
		if (!calibFolder.exists()) calibFolder.mkdirs();
		return calibrationsFolder;
	}
	
	/**
	 * 
	 * @param userName
	 * @return
	 */
	private static String getCalibFileNameForUser(String userName) {
		return userName + ".csv";
	}

}