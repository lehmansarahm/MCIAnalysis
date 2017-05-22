package edu.temple.tan.mcianalysis.analyses;

import java.io.IOException;
import com.opencsv.CSVWriter;
import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.ToolkitUtils;

/**
 * Base class for shared functionality for common pause analysis operations
 * 
 * @author Sarah M. Lehman
 */
public class PauseBase {
	protected static CSVWriter writer;
	protected static int totalPauseCount;
	protected static double totalPauseDuration;
	
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
		String time =  nextLine[Constants.INPUT_FILE_COLUMN_ORDER.TIME.ordinal()];
		return (time.equals(Constants.DATA_COLUMN_TIME));
	}
	
	/**
	 * 
	 * @param nextLine
	 * @return
	 */
	protected double calculateMagnitude(String nextLine[]) {
		String accelX = nextLine[Constants.INPUT_FILE_COLUMN_ORDER.ACCEL_X.ordinal()];
		String accelY = nextLine[Constants.INPUT_FILE_COLUMN_ORDER.ACCEL_Y.ordinal()];
		String accelZ = nextLine[Constants.INPUT_FILE_COLUMN_ORDER.ACCEL_Z.ordinal()];
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
     * 
     * @throws IOException
     * @throws NumberFormatException
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
    protected String[] generateNextLine(String startTime, String startLineNum,  String endTime, String endLineNum, 
	  String duration) {
        String nextLine[] = new String[Constants.PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.values().length];
        nextLine[Constants.PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.START_TIME.ordinal()] = startTime;
        nextLine[Constants.PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.START_NUM.ordinal()] = startLineNum;
        nextLine[Constants.PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER.END_TIME.ordinal()] = endTime;
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
    protected void finalizePauseCSV() throws IOException {
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