package edu.temple.tan.mcianalysis.analyses;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.MCIAnalysis;
import edu.temple.tan.mcianalysis.intermediates.CalibrationProcessing;
import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.Constants.INPUT_FILE_COLUMN_ORDER;
import edu.temple.tan.mcianalysis.utils.ToolkitUtils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Analysis operation for MCI Analysis Toolkit.  Intended to identify pauses 
 * within a data input file that fall beneath a certain magnitude threshold 
 * for a given number of scans.  Outputs results to a CSV file.
 * 
 * @author Philip M. Coulomb
 * @author Sarah M. Lehman
 */
public class PauseCount extends PauseBase implements Analysis {

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
            totalPauseCount = 0;
            totalPauseDuration = 0.0;
            
        	// fire off analysis operation
            createPauseAnalysisCSV(userID);
            
            // close up shop
        	reader.close();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(PauseCount.class.getName()).log(Level.SEVERE, null, ex);
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
    	// initialize the local processing properties ...
    	String startTime = "", startNo = "", endTime, endNo, nextLine[];
    	boolean currentlyPaused = false;
        double currentDuration;
    	int windowCount = 0;
    	
    	// Iterate through reader contents ...
    	while ((nextLine = reader.readNext()) != null) {
    		if (!isHeaderLine(nextLine)) {
    			if (MCIAnalysis.calibThresholdsUtilized) {
    				// only update the pause threshold if calibrations have been selected, 
    				// and a valid calibration threshold exists for the current user
    				double userThreshold = CalibrationProcessing.getCalibratedPauseThresholdForUser(userID);
    				if (userThreshold != 0.0d) pauseThreshold = userThreshold;
    			} else {
    	    		double currentMagnitude = calculateMagnitude(nextLine);
    	    		if (currentMagnitude < pauseThreshold) {
        				/*Logger.getLogger(PauseCount.class.getName()).log(Level.INFO, 
        		        		"Pause instance found!\nComparing current acceleration magnitude: " 
        		        				+ currentMagnitude + "\n...against pause threshold value: "  
        		        				+ pauseThreshold + "\n", "");*/
        				
    	    			// we've found a pause instance
    	    			if (!currentlyPaused) {
    	    				// new pause instance ... log the starting details
    	    				startTime = nextLine[INPUT_FILE_COLUMN_ORDER.TIME.ordinal()];
    	    				startNo = nextLine[INPUT_FILE_COLUMN_ORDER.RECORD_NUM.ordinal()];
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
    	    			endTime = nextLine[INPUT_FILE_COLUMN_ORDER.TIME.ordinal()];
    	    			endNo = nextLine[INPUT_FILE_COLUMN_ORDER.RECORD_NUM.ordinal()];
    	    			
    	    			// determine the current duration and add to our running total
    	    			currentDuration = windowCount * Constants.SAMPLING_PERIOD;
    	    			totalPauseDuration += currentDuration;
    	    			
    	    			// output pause details to file
    	            	addToPauseCSV(startTime, startNo, endTime, endNo, currentDuration);
    	            	totalPauseCount++;
    	            	
    	            	// reset pause details
    	    			currentlyPaused = false;
    	    			windowCount = 0;
    	    		}
    			}
    		}
    	}
    	
    	// Finalize the output file and update toolkit state
        finalizePauseCSV();
        MCIAnalysis.pauseUtilized = true;
    }
    
}