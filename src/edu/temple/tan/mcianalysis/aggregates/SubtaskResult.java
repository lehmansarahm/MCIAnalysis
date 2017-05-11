package edu.temple.tan.mcianalysis.aggregates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SubtaskResult {
	
	private String subtaskName;
	private double completionTimeSec = 0.0;
	private int totalPauseCount = 0;
	private int distinctPauseCount = 0;
	private double totalPauseTime = 0.0;
	
	/**
	 * 
	 * @param subtaskName
	 */
	public SubtaskResult(String subtaskName) {
		this.subtaskName = subtaskName;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSubtaskName() {
		return subtaskName;
	}
	
	/**
	 * 
	 * @param completionTimeSec
	 */
	public void setCompletionTime(double completionTimeSec) {
		this.completionTimeSec = completionTimeSec;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getCompletionTime() {
		return completionTimeSec;
	}
	
	/**
	 * 
	 * @param newPauseCount
	 */
	public void addToTotalPauses(int newPauseCount) {
		totalPauseCount += newPauseCount;
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
	 * @param newPauseCount
	 */
	public void addToDistinctPauseCount(int newPauseCount) {
		distinctPauseCount += newPauseCount;
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
	 * @param newPauseTime
	 */
	public void addToTotalPauseTime(double newPauseTime) {
		totalPauseTime += newPauseTime;
	}

	/**
	 * 
	 * @return
	 */
	public double getTotalPauseTime() {
		return totalPauseTime;
	}

	// ------------------------------------------------------------------------------
	// ------------------------------------------------------------------------------
	
	private Map<String, List<String[]>> results = new HashMap<String, List<String[]>>();
	
	/**
	 * 
	 * @param resultHeader
	 * @param user
	 * @param resultValue
	 */
	public void addResult(String resultHeader, String user, String resultValue) {
		if (!results.containsKey(resultHeader)) results.put(resultHeader, new ArrayList<>());
		results.get(resultHeader).add(new String[] { user, resultValue });
	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String[] getResultValues() {
		List<String> resultValues = new ArrayList<>();
		resultValues.add(subtaskName);
		
	    Iterator<Entry<String, List<String[]>>> it = results.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        List<String[]> subtaskResults = (List<String[]>) pair.getValue();

	        for (String[] subtaskResult : subtaskResults) {
	        	resultValues.add(subtaskResult[1]);
	        }
	        it.remove();
	    }

	    return (String[]) resultValues.toArray(new String[resultValues.size()]);
	}

}