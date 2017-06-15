package edu.temple.tan.mcianalysis.analyses;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.MCIAnalysis;
import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.LogManager;
import edu.temple.tan.mcianalysis.utils.Constants.INTERM_FILE_COLUMN_ORDER;
import edu.temple.tan.mcianalysis.utils.ToolkitUtils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Analysis operation for MCI Analysis Toolkit.  Intended to identify pauses 
 * within a data input file that fall beneath a certain magnitude threshold 
 * for a given number of scans.  Outputs results to a CSV file.
 * 
 * @author Philip M. Coulomb
 * @author Sarah M. Lehman
 */
public class PauseCount extends PauseBase implements Analysis {
	
	private double currentDuration = 0.0;

    /**
     * Initializes the analysis according to a given input data file, userID,
     * and set of operational parameters.
     * 
     * @param filePath - the path to the input data file to analyze
     * @param userID - the ID of the user for whom the data was recorded
     * @param pauseThresholdIn - the text equivalent of the acceleration magnitude 
     * 	threshold below which motion is considered "paused"
     * @param pauseWindowIn - the text equivalent of the scan window, representing the 
     * 	number of individual sequential scans which must meet the pause threshold before 
     * 	the motion is considered "paused"
     */
    @Override
    public void beginAnalysis(String filePath, String userID, String pauseThresholdIn, String pauseWindowIn) {
        try {
        	// first, make sure parameters are valid
        	boolean areValidParameters = validateParameters(pauseThresholdIn, pauseWindowIn);
        	if (!areValidParameters) return;
        	
        	// Assuming we have valid params, continue ...
        	// determine output csv path
            String csvPath = ToolkitUtils.initializeAnalysisOutputDirs(filePath, userID, 
            		Constants.ANALYSIS_PAUSE_COUNT);

        	// initialize class variables
            reader = new CSVReader(new FileReader(filePath), ',', '"', 0);
            writer = new CSVWriter(new FileWriter(csvPath));
        	pauseThreshold = Double.parseDouble(pauseThresholdIn);
        	pauseWindow = Integer.parseInt(pauseWindowIn);
            
        	// fire off analysis operation
            createPauseAnalysisCSV(userID);
            
            // close up shop
        	reader.close();
            writer.close();
        } catch (IOException ex) {
        	LogManager.error(PauseCount.class, ex);
        }
    }
    
    /**
     * Calculates the magnitude of the acceleration 
     * vector at each reading and if that magnitude is less than some 
     * defined minimum, it marks it. If x number of magnitudes are below the 
     * minimum in a row, then a pause had occurred.
     * 
     * @param userID - the ID of the user for whom the data was recorded
     * @throws IOException
     * @throws ParseException
     */
    private void createPauseAnalysisCSV(String userID) throws IOException {
    	// update pause threshold if calibrations were used
    	checkForCalibratedThreshold(userID);
    	List<String[]> readerContents = reader.readAll();
    	
    	// Iterate through reader contents ...
    	useAccel = false;
    	writer.writeNext(new String[] { "PAUSE METRIC", "SPEED" });
    	for (String[] nextLine : readerContents) {
    		if (!isHeaderLine(nextLine)) {
				double speed = Double.parseDouble(nextLine[INTERM_FILE_COLUMN_ORDER.SPEED.ordinal()]);
				evaluatePause(nextLine, speed, PAUSE_SPEED_THRESHOLD);
    		}
    	}
    	
    	// write preliminary results to output file
        finalizePauseCSV();
    	resetProcessingProps();
    	
    	useAccel = true;
    	writer.writeNext(new String[] { "PAUSE METRIC", "ACCELERATION" });
    	for (String[] nextLine : readerContents) {
    		if (!isHeaderLine(nextLine)) {
	    		double accelMag = 
	    				Double.parseDouble(nextLine[INTERM_FILE_COLUMN_ORDER.ACCEL_MAG.ordinal()]);
	    		evaluatePause(nextLine, accelMag, pauseThreshold);
    		}
    	}
    	
    	// Finalize the output file and update toolkit state
        finalizePauseCSV();
        MCIAnalysis.pauseUtilized = true;
    }
    
    /**
     * 
     * @param nextLine
     * @param value
     * @param threshold
     * @param count
     * @param duration
     */
    private void evaluatePause(String[] nextLine, double value, double threshold) {
		if (value < threshold) {
			// we've found a pause instance
			if (!currentlyPaused) {
				// new pause instance ... log the starting details
				startTime = nextLine[INTERM_FILE_COLUMN_ORDER.TIME.ordinal()];
				startNo = nextLine[INTERM_FILE_COLUMN_ORDER.RECORD_NUM.ordinal()];
				currentlyPaused = true;
			}
			// whether starting a new pause or continuing an old, bump the pause counter
			windowCount++; 
		} else {
			// measurement denotes active movement ... reset pause details
			currentlyPaused = false;
			windowCount = 0;
		}
		
		// check to see if we've completed a pause window
		if (currentlyPaused && windowCount == pauseWindow) {
			// log ending details
			endTime = nextLine[INTERM_FILE_COLUMN_ORDER.TIME.ordinal()];
			endNo = nextLine[INTERM_FILE_COLUMN_ORDER.RECORD_NUM.ordinal()];
			
			// determine the current duration and add to our running total
			currentDuration = windowCount * Constants.SAMPLING_PERIOD;
			totalPauseDurationAccel += (useAccel) ? currentDuration : 0;
			totalPauseDurationSpeed += (!useAccel) ? currentDuration : 0;
			
			// output pause details to file
        	addToPauseCSV(startTime, startNo, endTime, endNo, currentDuration);
			totalPauseCountAccel += (useAccel) ? 1 : 0;
			totalPauseCountSpeed += (!useAccel) ? 1 : 0;
        	
        	// reset pause details
			currentlyPaused = false;
			windowCount = 0;
		}
    }
    
}