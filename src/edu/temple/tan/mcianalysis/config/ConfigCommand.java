package edu.temple.tan.mcianalysis.config;

import java.util.ArrayList;
import java.util.List;

import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.Constants.CONFIG_FILE_COLUMN_ORDER;

/**
 * 
 * @author Sarah M. Lehman
 */
public class ConfigCommand {
	
	private String username;
	private String sourceFile;
	private String taskName;
	private int taskCompletionThreshold;
	private int accelProcess;
	private String calibStep;
	private List<AnalysisCommand> analysisOps;
	
	/*
	  ----------------------------------------------------------------
	 	EXPECTED FORMAT
	  ----------------------------------------------------------------
	    {
		Username = [single val or comma-delim list]
		File = [single file path or folder path]
		Task to Analyze = ["all", single val, or comma-delim list]
		Use Task Completion Threshold = ["none" / "false" or int val]
		Use Linear Processing = [boolean]
		Use Calibrated Step = ["none" / "false" or single val]
		Analysis = [OpName]:[first param]:[second param]
		}
	  ----------------------------------------------------------------
	 */
	
	/**
	 * 
	 * @param input
	 */
	public ConfigCommand(List<String> input) {
		this.analysisOps = new ArrayList<AnalysisCommand>();
		for (int i = 0; i < input.size(); i++) {
			// Remove the label and equals sign
			String rawInput = (input.get(i));
			String parsedInput = (rawInput.substring(rawInput.indexOf(Constants.DELIMITER_PARAMETER_ASSIGNMENT) + 1)).trim();

			// Can't use switch here because it requires a constant expression
			// ...Have to use stacked if's
			if (i == CONFIG_FILE_COLUMN_ORDER.USERNAME.ordinal()) {
				this.username = parsedInput;
			} else if (i == CONFIG_FILE_COLUMN_ORDER.INPUT_FILE.ordinal()) {
				this.sourceFile = parsedInput;
			} else if (i == CONFIG_FILE_COLUMN_ORDER.TASK.ordinal()) {
				this.taskName = parsedInput;
			} else if (i == CONFIG_FILE_COLUMN_ORDER.TASK_COMPLETION_THRESHOLD.ordinal()) {
				this.taskCompletionThreshold = 
						(parsedInput.equalsIgnoreCase("none") || parsedInput.equalsIgnoreCase("false")) 
							? 0 : Integer.parseInt(parsedInput);
			} else if (i == CONFIG_FILE_COLUMN_ORDER.ACCEL_PROCESSING.ordinal()) {
				this.accelProcess = 
						(parsedInput.equalsIgnoreCase("yes") || parsedInput.equalsIgnoreCase("true")) ? 1 : 0;
			} else if (i == CONFIG_FILE_COLUMN_ORDER.CALIBRATED_THRESHOLDS.ordinal()) {
				this.calibStep = 
						(parsedInput.equalsIgnoreCase("none") || parsedInput.equalsIgnoreCase("false")) 
							? "" : parsedInput.trim();
				//Logger.getLogger(ConfigCommand.class.getName()).log(Level.INFO, 
		        //		"New calibration step identified: " + calibStep, "");
			} else if (i >= CONFIG_FILE_COLUMN_ORDER.ANALYSES.ordinal()) {
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
	public int getTaskCompletionThreshold() {
		return this.taskCompletionThreshold;
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
	public String getCalibStep() {
		return this.calibStep;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<AnalysisCommand> getAnalysisOps() {
		return this.analysisOps;
	}
	
}