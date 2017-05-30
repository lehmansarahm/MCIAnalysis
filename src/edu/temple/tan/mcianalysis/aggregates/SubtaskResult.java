package edu.temple.tan.mcianalysis.aggregates;

public class SubtaskResult {
	
	private String subtaskName;
	private double completionTimeSec = 0.0;
	
	// pause data
	private int totalPauseCount = 0;
	private int distinctPauseCount = 0;
	private double totalPauseTime = 0.0d;
	
	// sudden motion data
	private int totalDirectionChanges = 0;
	private double axis1AvgChange = 0.0d;
	private double axis2AvgChange = 0.0d;
	
	/**
	 * 
	 * @param subtaskName
	 */
	public SubtaskResult(String subtaskName) {
		this.subtaskName = (subtaskName.equals("") ? "[blank]" : subtaskName);
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

	// -------------------------------------------------------------------
	// -------------------------------------------------------------------
	
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
	
	// -------------------------------------------------------------------
	// -------------------------------------------------------------------
	
	/**
	 * 
	 * @param newDirectionChangeCount
	 */
	public void addToTotalDirectionChanges(int newDirectionChangeCount) {
		totalDirectionChanges += newDirectionChangeCount;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getTotalDirectionChanges() {
		return totalDirectionChanges;
	}
	
	/**
	 * 
	 * @param avgChange
	 */
	public void setAxis1AvgChange(double avgChange) {
		axis1AvgChange = avgChange;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getAxis1AvgChange() {
		return axis1AvgChange;
	}
	
	/**
	 * 
	 * @param avgChange
	 */
	public void setAxis2AvgChange(double avgChange) {
		axis2AvgChange = avgChange;
	}
	
	/**
	 * 
	 * @return
	 */
	public double getAxis2AvgChange() {
		return axis2AvgChange;
	}

}