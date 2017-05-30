package edu.temple.tan.mcianalysis.aggregates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.temple.tan.mcianalysis.MCIAnalysis;
import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.Constants.USER_AGGREGATE_COLUMN_ORDER;

public class UserSummary {

	private String userName;
	private double totalTrialTimeSec = 0.0;
	
	private int totalPauseCount = 0;
	private int distinctPauseCount = 0;
	private double totalPauseTimeSec = 0.0;
	
	private int totalDirectionChanges = 0;
	
	private static final boolean INCLUDE_PAUSE_DATA = MCIAnalysis.pauseUtilized;
	private static final boolean INCLUDE_SUDDEN_MOTION_DATA = MCIAnalysis.directionUtilized;
	private Map<String, SubtaskResult> subtasks = new HashMap<String, SubtaskResult>();
	
	/**
	 * 
	 * @param userName
	 */
	public UserSummary(String userName) {
		this.userName = userName;
	}
	
	/**
	 * 
	 */
	public String toString() {
		String out = "\n...User Name: " + userName;
		out += "\n...Total Trial Time in Sec: " + totalTrialTimeSec;
		
		out += "\n...Total Pause Count: " + totalPauseCount;
		out += "\n...Distinct Pause Count: " + distinctPauseCount;
		out += "\n...Total Pause Time in Sec: " + totalPauseTimeSec;
		
		out += "\n...Total Number of Sudden Direction Changes: " + totalDirectionChanges;
		
		out += "\n\nSUBTASKS\n----------------------------------------";
	    Iterator<Entry<String, SubtaskResult>> it = subtasks.entrySet().iterator();
	    while (it.hasNext()) {
	    	out += it.next().getValue() + "\n";
	    }
		
	    return out;
	}
	
	/**
	 * 
	 * @return
	 */
	public LinkedList<String> getFirstHeaderLine() {
		LinkedList<String> firstHeaderLine = new LinkedList<String>();
		for (int i = 0; i < Constants.USER_AGGREGATE_COLUMN_ORDER.values().length; i++) {
			firstHeaderLine.add("");
		}
		return firstHeaderLine;
	}
	
	/**
	 * 
	 * @return
	 */
	public LinkedList<String> getSecondHeaderLine() {
		LinkedList<String> secondHeaderLine = new LinkedList<String>();
		secondHeaderLine.add("User Name");
		secondHeaderLine.add("Total Trial Time (s)");

		secondHeaderLine.add("Total Pause Count");
		secondHeaderLine.add("Distinct Pause Count");
		secondHeaderLine.add("Total Time Paused (s)");
		secondHeaderLine.add("Total Direction Change Count");

		return secondHeaderLine;
	}
	
	/**
	 * 
	 * @param analysis
	 * @param summaryLine
	 */
	public void addAggregateSummary(String analysis, String[] summaryLine) {
		String[] subtaskComponents = summaryLine[0].split(Constants.DELIMITER_TIMESTAMP);
		String taskName = subtaskComponents[subtaskComponents.length - 1];
		addSubtask(taskName);
		
        /*Logger.getLogger(UserSummary.class.getName()).log(Level.INFO, 
        		"Adding summary line for analysis: " + analysis 
        		+ ", and subtask: " + taskName + ", and user: " + userName, "");*/
		
		SubtaskResult str = subtasks.get(taskName);
		switch (analysis) {
		case Constants.ANALYSIS_PAUSE_COUNT:
			int subtaskPauseCount = 
				Integer.parseInt(summaryLine[Constants.PAUSE_AGGREGATE_COLUMN_ORDER.NUM_OF_PAUSES.ordinal()]);
			str.addToTotalPauses(subtaskPauseCount);
			totalPauseCount += subtaskPauseCount;
			break;
		case Constants.ANALYSIS_PAUSE_DURATION:
			int subtaskIndivPauseCount = 
				Integer.parseInt(summaryLine[Constants.PAUSE_AGGREGATE_COLUMN_ORDER.NUM_OF_PAUSES.ordinal()]);
			str.addToDistinctPauseCount(subtaskIndivPauseCount);
			distinctPauseCount += subtaskIndivPauseCount;
			
			double subtaskTotalPauseTimeMS = 
				Double.parseDouble(summaryLine[Constants.PAUSE_AGGREGATE_COLUMN_ORDER.TOTAL_TIME_PAUSED.ordinal()]);
			str.addToTotalPauseTime(subtaskTotalPauseTimeMS);
			totalPauseTimeSec += (subtaskTotalPauseTimeMS / 1000.0d);
			break;
		case Constants.ANALYSIS_DIRECTION:
			int directionChanges = 
				Integer.parseInt(summaryLine[Constants.SUDDEN_MOVEMENT_AGGREGATE_COLUMN_ORDER.NUMBER_OF_DIRECTION_CHANGES.ordinal()]);
			str.addToTotalDirectionChanges(directionChanges);
			totalDirectionChanges += directionChanges;
			
			double axis1AvgChange = 
				Double.parseDouble(summaryLine[Constants.SUDDEN_MOVEMENT_AGGREGATE_COLUMN_ORDER.AXIS_1_AVERAGE_ACCEL_CHANGE.ordinal()]);
			str.setAxis1AvgChange(axis1AvgChange);
			double axis2AvgChange = 
				Double.parseDouble(summaryLine[Constants.SUDDEN_MOVEMENT_AGGREGATE_COLUMN_ORDER.AXIS_2_AVERAGE_ACCEL_CHANGE.ordinal()]);
			str.setAxis2AvgChange(axis2AvgChange);
			break;
		case Constants.ANALYSIS_TASK_TIME:
			double subtaskTimeSec = 
				Double.parseDouble(summaryLine[Constants.TASK_TIME_AGGREGATE_COLUMN_ORDER.TIME_IN_SEC.ordinal()]);
			str.setCompletionTime(subtaskTimeSec);
			totalTrialTimeSec += subtaskTimeSec;
			
	        /*Logger.getLogger(UserSummary.class.getName()).log(Level.INFO, 
	    		"Saving task completion time: " + subtaskTimeSec
    				+ "\n...for analysis: " + analysis 
	    			+ "\n...and subtask: " + taskName 
	    			+ "\n...and user: " + userName + "\n", "");*/
			break;
		default:
			// do nothing
			break;
		}
		
		subtasks.put(taskName, str);
	}

	/**
	 * 
	 * @param subtask
	 */
	public void addSubtask(String subtask) {
		if (!subtasks.containsKey(subtask)) {
			subtasks.put(subtask, new SubtaskResult(subtask));
		}
	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<LinkedList<String>> toOutputList() {
		LinkedList<String> headersLine1 = getFirstHeaderLine();
		LinkedList<String> headersLine2 = getSecondHeaderLine();
		LinkedList<String> subtaskOutput = new LinkedList();
		
	    Iterator<Entry<String, SubtaskResult>> it = subtasks.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        SubtaskResult str = (SubtaskResult) pair.getValue();
	        String subtaskName = str.getSubtaskName();

	        // subtask column 1
	        headersLine1.add(subtaskName);
	        headersLine2.add("Completion Time (s)");
	        subtaskOutput.add(Double.toString(str.getCompletionTime()));

	        // subtask column 2
	        headersLine1.add(subtaskName);
	        headersLine2.add("Total Pause Count");
	        subtaskOutput.add(INCLUDE_PAUSE_DATA ? Integer.toString(str.getTotalPauseCount()) 
	        		: Constants.DELIMITER_ACTIVITY_NOT_FOUND);

	        // subtask column 3
	        headersLine1.add(subtaskName);
	        headersLine2.add("Distinct Pause Count");
	        subtaskOutput.add(INCLUDE_PAUSE_DATA ? Integer.toString(str.getDistinctPauseCount()) 
	        		: Constants.DELIMITER_ACTIVITY_NOT_FOUND);

	        // subtask column 4
	        headersLine1.add(subtaskName);
	        headersLine2.add("Total Time Paused (s)");
	        subtaskOutput.add(INCLUDE_PAUSE_DATA ? Double.toString((str.getTotalPauseTime() / 1000.0)) 
	        		: Constants.DELIMITER_ACTIVITY_NOT_FOUND);

	        // subtask column 5
	        headersLine1.add(subtaskName);
	        headersLine2.add("Total Direction Changes");
	        subtaskOutput.add(INCLUDE_SUDDEN_MOTION_DATA ? Integer.toString(str.getTotalDirectionChanges()) 
	        		: Constants.DELIMITER_ACTIVITY_NOT_FOUND);

	        // subtask column 6
	        headersLine1.add(subtaskName);
	        headersLine2.add("Axis 1 Avg Change");
	        subtaskOutput.add(INCLUDE_SUDDEN_MOTION_DATA ? Double.toString(str.getAxis1AvgChange()) 
	        		: Constants.DELIMITER_ACTIVITY_NOT_FOUND);

	        // subtask column 7
	        headersLine1.add(subtaskName);
	        headersLine2.add("Axis 2 Avg Change");
	        subtaskOutput.add(INCLUDE_SUDDEN_MOTION_DATA ? Double.toString(str.getAxis2AvgChange()) 
	        		: Constants.DELIMITER_ACTIVITY_NOT_FOUND);
	        
	        // avoid concurrent modification
	        it.remove();
	    }
	    
	    // write the totals
	    subtaskOutput.add(USER_AGGREGATE_COLUMN_ORDER.USER_NAME.ordinal(), userName);
	    subtaskOutput.add(USER_AGGREGATE_COLUMN_ORDER.TOTAL_TRIAL_TIME.ordinal(), Double.toString(totalTrialTimeSec));

	    subtaskOutput.add(USER_AGGREGATE_COLUMN_ORDER.TOTAL_PAUSE_COUNT.ordinal(), 
    		INCLUDE_PAUSE_DATA ? Integer.toString(totalPauseCount) : Constants.DELIMITER_ACTIVITY_NOT_FOUND);
	    subtaskOutput.add(USER_AGGREGATE_COLUMN_ORDER.DISTINCT_PAUSE_COUNT.ordinal(), 
    		INCLUDE_PAUSE_DATA ? Integer.toString(distinctPauseCount) : Constants.DELIMITER_ACTIVITY_NOT_FOUND);
	    subtaskOutput.add(USER_AGGREGATE_COLUMN_ORDER.TOTAL_PAUSE_TIME.ordinal(), 
    		INCLUDE_PAUSE_DATA ? Double.toString(totalPauseTimeSec) : Constants.DELIMITER_ACTIVITY_NOT_FOUND);

	    subtaskOutput.add(USER_AGGREGATE_COLUMN_ORDER.TOTAL_DIRECTION_CHANGES.ordinal(), 
    		INCLUDE_SUDDEN_MOTION_DATA ? Integer.toString(totalDirectionChanges) : Constants.DELIMITER_ACTIVITY_NOT_FOUND);
	    
	    // stitch it all together
	    List<LinkedList<String>> finalOutput = new ArrayList<>();
	    finalOutput.add(headersLine1);
	    finalOutput.add(headersLine2);
	    finalOutput.add(subtaskOutput);
		return finalOutput;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getTotalTrialTime() {
		return totalTrialTimeSec;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getTotalPauseCount() {
		return totalPauseCount;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getDistinctPauseCount() {
		return distinctPauseCount;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getTotalPauseTime() {
		return totalPauseTimeSec;
	}
	
}