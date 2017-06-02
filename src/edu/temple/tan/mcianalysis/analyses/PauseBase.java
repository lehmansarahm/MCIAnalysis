package edu.temple.tan.mcianalysis.analyses;

import java.io.IOException;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.Constants.INPUT_FILE_COLUMN_ORDER;
import edu.temple.tan.mcianalysis.utils.Constants.PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER;
import edu.temple.tan.mcianalysis.utils.Constants.PAUSE_OUTPUT_FILE_TOTALS_COLUMN_ORDER;
import edu.temple.tan.mcianalysis.utils.ToolkitUtils;

/**
 * Base class for shared functionality for common pause analysis operations
 * 
 * @author Sarah M. Lehman
 */
public class PauseBase {
	
	protected CSVReader reader;
	protected CSVWriter writer;
	
	protected double pauseThreshold;
	protected int pauseWindow;
	protected int totalPauseCount;
	protected double totalPauseDuration;
	
	/**
	 * 
	 * @param pauseThresholdIn
	 * @param pauseWindowIn
	 * @return
	 */
	protected boolean validateParameters(String pauseThresholdIn, String pauseWindowIn) {
    	if (pauseThresholdIn == null) {
    		System.out.println("No minimum magnitude provided for Pause analysis.  " +
    				"Operation could not be performed.");
    		return false;
    	} else if (pauseWindowIn == null) {
    		System.out.println("No pause window value provided for Pause analysis.  " +
    				"Operation could not be performed.");
    		return false;
    	}
    	return true;
	}
	
	/**
	 * 
	 * @param nextLine
	 * @return
	 */
	protected boolean isHeaderLine(String nextLine[]) {
		String time =  nextLine[INPUT_FILE_COLUMN_ORDER.TIME.ordinal()];
		return (time.equals(Constants.DATA_COLUMN_TIME));
	}
	
	/**
	 * 
	 * @param nextLine
	 * @return
	 */
	protected static double calculateMagnitude(String nextLine[]) {
		String accelX = nextLine[INPUT_FILE_COLUMN_ORDER.ACCEL_X.ordinal()];
		String accelY = nextLine[INPUT_FILE_COLUMN_ORDER.ACCEL_Y.ordinal()];
		String accelZ = nextLine[INPUT_FILE_COLUMN_ORDER.ACCEL_Z.ordinal()];
		return ToolkitUtils.calculateMagnitude(Double.parseDouble(accelX), 
				Double.parseDouble(accelY), Double.parseDouble(accelZ));
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
     */
	protected void addToPauseCSV(String startTime, String startLineNum,  String endTime, 
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
    protected static String[] generateNextLine(String startTime, String startLineNum,  String endTime, String endLineNum, 
	  String duration) {
        String nextLine[] = new String[PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.values().length];
        nextLine[PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.START_TIME.ordinal()] = startTime;
        nextLine[PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.START_NUM.ordinal()] = startLineNum;
        nextLine[PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.END_TIME.ordinal()] = endTime;
        nextLine[PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.END_NUM.ordinal()] = endLineNum;
        nextLine[PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.DURATION.ordinal()] = duration;
        return nextLine;
    }
    
    /**
     * Generates the closing content of the pause output file based on the provided parameter values
     * 
     * @throws IOException
     */
    protected void finalizePauseCSV() throws IOException {
    	double averagePauseDuration = (totalPauseCount != 0) ? (totalPauseDuration / totalPauseCount) : 0;

        String totalLine[] = new String[PAUSE_OUTPUT_FILE_TOTALS_COLUMN_ORDER.values().length];
        totalLine[PAUSE_OUTPUT_FILE_TOTALS_COLUMN_ORDER.PAUSE_NUM_LABEL.ordinal()] = "Number of Pauses:";
        totalLine[PAUSE_OUTPUT_FILE_TOTALS_COLUMN_ORDER.PAUSE_NUM_VALUE.ordinal()] = String.valueOf(totalPauseCount);
        totalLine[PAUSE_OUTPUT_FILE_TOTALS_COLUMN_ORDER.TOTAL_PAUSE_TIME_LABEL.ordinal()] = "Total Time Spent Paused:";
        totalLine[PAUSE_OUTPUT_FILE_TOTALS_COLUMN_ORDER.TOTAL_PAUSE_TIME_VALUE.ordinal()] = String.valueOf(totalPauseDuration);
        totalLine[PAUSE_OUTPUT_FILE_TOTALS_COLUMN_ORDER.AVG_PAUSE_TIME_LABEL.ordinal()] = "Average Pause Duration:";
        totalLine[PAUSE_OUTPUT_FILE_TOTALS_COLUMN_ORDER.AVG_PAUSE_TIME_VALUE.ordinal()] = String.valueOf(averagePauseDuration);

        writer.writeNext(totalLine);
    }
    
}