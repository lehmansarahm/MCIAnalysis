package edu.temple.tan.mcianalysis.config;

import java.util.ArrayList;
import java.util.List;

import edu.temple.tan.mcianalysis.utils.Constants;

/**
 * 
 * @author Sarah M. Lehman
 */
public class ConfigCommand {
	private String username;
	private String sourceFile;
	private String taskName;
	private int accelProcess;
	private List<AnalysisCommand> analysisOps;
	
	/**
	 * 
	 * @param input
	 */
	public ConfigCommand(List<String> input) {
		this.analysisOps = new ArrayList<AnalysisCommand>();
		for (int i = 0; i < input.size(); i++) {
			// Remove the label and equals sign
			String parsedInput = (input.get(i).substring(input.get(i).indexOf("=") + 1)).trim();
			
			// Can't use switch here because it requires a constant expression
			// Have to use stacked if's
			if (i == Constants.CONFIG_FILE_COLUMN_ORDER.USERNAME.ordinal()) {
				this.username = parsedInput;
			} else if (i == Constants.CONFIG_FILE_COLUMN_ORDER.INPUT_FILE.ordinal()) {
				this.sourceFile = parsedInput;
			} else if (i == Constants.CONFIG_FILE_COLUMN_ORDER.TASK.ordinal()) {
				this.taskName = parsedInput;
			} else if (i == Constants.CONFIG_FILE_COLUMN_ORDER.ACCEL_PROCESSING.ordinal()) {
				this.accelProcess = Integer.parseInt(parsedInput);
			} else if (i >= Constants.CONFIG_FILE_COLUMN_ORDER.ANALYSES.ordinal()) {
				this.analysisOps.add(new AnalysisCommand(parsedInput));
			}
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSourceFile() {
		return this.sourceFile;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTaskName() {
		return this.taskName;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getAccelProcess() {
		return this.accelProcess;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<AnalysisCommand> getAnalysisOps() {
		return this.analysisOps;
	}
}