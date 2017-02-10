package edu.temple.tan.mcianalysis.analyses;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.MCIAnalysis;
import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.ToolkitUtils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
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
	
	private static final String OUTPUT_DIR_NAME = "PauseCount";

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
        	// initialize class variables
            String csvPath = ToolkitUtils.initializeAnalysisOutputDirs(filePath, userID, OUTPUT_DIR_NAME);
            writer = new CSVWriter(new FileWriter(csvPath));
            totalPauseCount = 0;
            totalPauseDuration = 0.0;
            
        	// fire off analysis operation
            createPauseAnalysisCSV(filePath, pauseThresholdIn, pauseWindowIn);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(PauseDuration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Calculates the magnitude of the acceleration 
     * vector at each reading and if that magnitude is less than some 
     * defined minimum, it marks it. If x number of magnitudes are below the 
     * minimum in a row, then a pause had occurred.
     * 
     * @param filePath - the path to the input data file to analyze
     * @param pauseThresholdIn - the text equivalent of the acceleration magnitude 
     * 	threshold below which motion is considered "paused"
     * @param pauseWindowIn - the text equivalent of the scan window, representing the 
     * 	number of individual sequential scans which must meet the pause threshold before 
     * 	the motion is considered "paused"
     * @throws IOException
     * @throws ParseException
     */
    private void createPauseAnalysisCSV(String filePath, String pauseThresholdIn, 
	  String pauseWindowIn) throws IOException, ParseException {
    	// first, make sure parameters are valid
    	boolean areValidParameters = validateParameters(pauseThresholdIn, pauseWindowIn);
    	if (!areValidParameters) return;
    	
    	// Alright, assuming we have valid parameters, let's continue ... 
    	double pauseThreshold = Double.parseDouble(pauseThresholdIn);
    	int pauseWindow = Integer.parseInt(pauseWindowIn);
    	String startTime = "", startNo = "", endTime, endNo, nextLine[];
    	boolean currentlyPaused = false;
        double currentDuration;
    	int windowCount = 0;
    	
    	// Iterate through reader contents ...
    	CSVReader reader = new CSVReader(new FileReader(filePath), ',', '"', 0);
    	while ((nextLine = reader.readNext()) != null) {
    		if (!isHeaderLine(nextLine)) {
	    		double currentMagnitude = calculateMagnitude(nextLine);
	    		if (currentMagnitude < pauseThreshold) {
	    			// we've found a pause instance
	    			if (!currentlyPaused) {
	    				// new pause instance ... log the starting details
	    				startTime = nextLine[Constants.INPUT_FILE_COLUMN_ORDER.TIME.ordinal()];
	    				startNo = nextLine[Constants.INPUT_FILE_COLUMN_ORDER.RECORD_NUM.ordinal()];
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
	    			endTime = nextLine[Constants.INPUT_FILE_COLUMN_ORDER.TIME.ordinal()];
	    			endNo = nextLine[Constants.INPUT_FILE_COLUMN_ORDER.RECORD_NUM.ordinal()];
	    			
	    			// determine the current duration and add to our running total
	    			currentDuration = windowCount * Constants.SAMPLING_RATE;
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
    	reader.close();

    	// Finalize the output file and update toolkit state
        finalizePauseCSV();
        MCIAnalysis.pause_utilized = true;
        writer.close();
    }
}