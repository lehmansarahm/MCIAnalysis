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
	private int accelProcess;
	private int calibThresholds;
	private List<AnalysisCommand> analysisOps;
	
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
			} else if (i == CONFIG_FILE_COLUMN_ORDER.ACCEL_PROCESSING.ordinal()) {
				this.accelProcess = 
						(parsedInput.equalsIgnoreCase("yes") || parsedInput.equalsIgnoreCase("true")) ? 1 : 0;
			} else if (i == CONFIG_FILE_COLUMN_ORDER.CALIBRATED_THRESHOLDS.ordinal()) {
				this.calibThresholds = 
						(parsedInput.equalsIgnoreCase("yes") || parsedInput.equalsIgnoreCase("true")) ? 1 : 0;
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
	public int getAccelProcess() {
		return this.accelProcess;
	}
	
	/**
	 * 
	 * @return
	 */
	public int useCalibThresholds() {
		return this.calibThresholds;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<AnalysisCommand> getAnalysisOps() {
		return this.analysisOps;
	}
	
}