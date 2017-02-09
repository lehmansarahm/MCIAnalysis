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
public class PauseCount implements Analysis {
	
	private static final String OUTPUT_DIR_NAME = "Pause";
	
	private static CSVWriter writer;
	private static int totalPauseCount;
	private static double totalPauseDuration;

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
    	if (pauseThresholdIn == null) {
    		System.out.println("No minimum magnitude provided for Pause analysis.  " +
    				"Operation could not be performed.");
    		return;
    	} else if (pauseWindowIn == null) {
    		System.out.println("No pause window value provided for Pause analysis.  " +
    				"Operation could not be performed.");
    		return;
    	}
    	
    	// Alright, assuming we have valid parameters, let's continue ... 
    	double pauseThreshold = Double.parseDouble(pauseThresholdIn);
    	int pauseWindow = Integer.parseInt(pauseWindowIn);
    	String startTime = "", startNo = "", endTime, endNo, nextLine[];
    	boolean currentlyPaused = false;
        double currentDuration;
    	int windowCount = 0;
    	
    	// Iterate through reader contents ...
    	CSVReader reader = new CSVReader(new FileReader(filePath), ',', '"', 0);
    	reader.readNext();
    	while ((nextLine = reader.readNext()) != null) {
    		double accelX = Double.parseDouble(nextLine[Constants.INPUT_FILE_COLUMN_ORDER.ACCEL_X.ordinal()]);
    		double accelY = Double.parseDouble(nextLine[Constants.INPUT_FILE_COLUMN_ORDER.ACCEL_Y.ordinal()]);
    		double accelZ = Double.parseDouble(nextLine[Constants.INPUT_FILE_COLUMN_ORDER.ACCEL_Z.ordinal()]);
    		
    		double currentMagnitude = ToolkitUtils.calculateMagnitude(accelX, accelY, accelZ);
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
    	reader.close();

    	// Finalize the output file and update toolkit state
        finalizePauseCSV();
        MCIAnalysis.pause_utilized = true;
        writer.close();
    }

    /**
     * Called once an identified pause has ended.  Each line of the CSV will feature 
     * the time and corresponding row number of the pause's starting and ending scans,
     * as well as the duration of the pause from start to end.  The final line of the 
     * CSV will feature totals such as number of pauses, total time spent paused, and 
     * the average duration of pauses.
     * 
     * @param startTime
     * @param startLineNum
     * @param endTime
     * @param endLineNum
     * @param duration
     * 
     * @throws IOException
     * @throws NumberFormatException
     */
    private void addToPauseCSV(String startTime, String startLineNum, String endTime, 
	  String endLineNum, double duration) {
    	// check to see if we need a header row
		if (totalPauseCount == 0) {
            writer.writeNext(generateNextLine("Pause Start Time", "Pause Start Line", 
            		"Pause End Time", "Pause End Line", "Pause Duration"));
		}
		
		// write the next content line
        writer.writeNext(generateNextLine(startTime, startLineNum, endTime, 
    		endLineNum, String.valueOf(duration)));
    }
    
    /**
     * Generates a content line for the output file based on the provided parameter values
     * 
     * @param startTime
     * @param startLineNum
     * @param endTime
     * @param endLineNum
     * @param duration
     * 
     * @return
     */
    private String[] generateNextLine(String startTime, String startLineNum, String endTime, String endLineNum, String duration) {
        String nextLine[] = new String[Constants.PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.values().length];
        nextLine[Constants.PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.START_TIME.ordinal()] = startTime.toString();
        nextLine[Constants.PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.START_NUM.ordinal()] = startLineNum;
        nextLine[Constants.PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.END_TIME.ordinal()] = endTime.toString();
        nextLine[Constants.PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.END_NUM.ordinal()] = endLineNum;
        nextLine[Constants.PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.DURATION.ordinal()] = duration;
        return nextLine;
    }
    
    /**
     * Generates the closing content of the pause output file based on the provided parameter values
     * 
     * @param filePath
     * @param pauseCount
     * @param totalPauseDuration
     * 
     * @throws IOException
     */
    private void finalizePauseCSV() throws IOException {
    	double averagePauseDuration = (totalPauseCount != 0) ? (totalPauseDuration / totalPauseCount) : 0;

        String totalLine[] = new String[Constants.PAUSE_OUTPUT_FILE_TOTALS_COLUMN_ORDER.values().length];
        totalLine[0] = "Number of Pauses:";
        totalLine[1] = String.valueOf(totalPauseCount);
        totalLine[2] = "Total Time Spent Paused:";
        totalLine[3] = String.valueOf(totalPauseDuration);
        totalLine[4] = "Average Pause Duration:";
        totalLine[5] = String.valueOf(averagePauseDuration);

        writer.writeNext(totalLine);
    }
}