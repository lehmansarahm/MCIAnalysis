package edu.temple.tan.mcianalysis.aggregates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.temple.tan.mcianalysis.utils.Constants;

public class UserSummary {

	private String userName;
	private double totalTrialTimeSec = 0.0;
	private int totalPauseCount = 0;
	private int distinctPauseCount = 0;
	private double totalPauseTimeSec = 0.0;
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
	 * @param analysis
	 * @param summaryLine
	 */
	public void addAggregateSummary(String analysis, String[] summaryLine) {
		String[] subtaskComponents = summaryLine[0].split(":");
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
			break;
		case Constants.ANALYSIS_PAUSE_DURATION:
			int subtaskIndivPauseCount = 
				Integer.parseInt(summaryLine[Constants.PAUSE_AGGREGATE_COLUMN_ORDER.NUM_OF_PAUSES.ordinal()]);
			str.addToDistinctPauseCount(subtaskIndivPauseCount);
			double subtaskTotalPauseTime = 
				Double.parseDouble(summaryLine[Constants.PAUSE_AGGREGATE_COLUMN_ORDER.TOTAL_TIME_PAUSED.ordinal()]);
			str.addToTotalPauseTime(subtaskTotalPauseTime);
			break;
		case Constants.ANALYSIS_TASK_TIME:
			double subtaskTimeSec = 
				Double.parseDouble(summaryLine[Constants.TASK_TIME_AGGREGATE_COLUMN_ORDER.TIME_IN_SEC.ordinal()]);
			str.setCompletionTime(subtaskTimeSec);
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
	public List<String[]> toOutputArray() {
    	List<String> headersLine1 = new LinkedList(Arrays.asList("", "", "", "", ""));
    	List<String> headersLine2 = new LinkedList(Arrays.asList("User Name:", "Total Trial Time (s)", 
    			"Total Pause Count", "Distinct Pause Count", "Total Time Paused (s)"));
		List<String> subtaskOutput = new LinkedList(Arrays.asList(userName)); // , "", "", "", ""));
		
	    Iterator<Entry<String, SubtaskResult>> it = subtasks.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        SubtaskResult str = (SubtaskResult) pair.getValue();
	        String subtaskName = str.getSubtaskName();

	        // subtask column 1
	        headersLine1.add(subtaskName);
	        headersLine2.add("Completion Time (s)");
	        subtaskOutput.add(Double.toString(str.getCompletionTime()));
	        totalTrialTimeSec += str.getCompletionTime();

	        // subtask column 2
	        headersLine1.add(subtaskName);
	        headersLine2.add("Total Pause Count");
	        subtaskOutput.add(Integer.toString(str.getTotalPauseCount()));
	        totalPauseCount += str.getTotalPauseCount();

	        // subtask column 3
	        headersLine1.add(subtaskName);
	        headersLine2.add("Distinct Pause Count");
	        subtaskOutput.add(Integer.toString(str.getDistinctPauseCount()));
	        distinctPauseCount += str.getDistinctPauseCount();

	        // subtask column 4
	        headersLine1.add(subtaskName);
	        headersLine2.add("Total Time Paused (s)");
	        double pauseTimeSec = (str.getTotalPauseTime() / 1000.0);
	        subtaskOutput.add(Double.toString(pauseTimeSec));
	        totalPauseTimeSec += pauseTimeSec;
	        
	        // avoid concurrent modification
	        it.remove();
	    }

	    // write the totals
	    subtaskOutput.add(1, Double.toString(totalTrialTimeSec));
	    subtaskOutput.add(2, Integer.toString(totalPauseCount));
	    subtaskOutput.add(3, Integer.toString(distinctPauseCount));
	    subtaskOutput.add(4, Double.toString(totalPauseTimeSec));
	    
	    // stitch it all together
	    List<String[]> finalOutput = new ArrayList<>();
	    finalOutput.add((String[]) headersLine1.toArray(new String[headersLine1.size()]));
	    finalOutput.add((String[]) headersLine2.toArray(new String[headersLine2.size()]));
	    finalOutput.add((String[]) subtaskOutput.toArray(new String[subtaskOutput.size()]));
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