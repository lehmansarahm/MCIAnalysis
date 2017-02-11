package edu.temple.tan.mcianalysis.config;

/**
 * 
 * @author Sarah M. Lehman
 */
public class AnalysisCommand {
	private String operationName;
	private String param1;
	private String param2;
	
	/**
	 * 
	 * @param input
	 */
	public AnalysisCommand(String input) {
		String[] inputs = input.split(":");
		this.operationName = inputs[0];
		this.param1 = (inputs.length >= 2) ? inputs[1] : null;
		this.param2 = (inputs.length >= 3) ? inputs[2] : null;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getOperationName() {
		return this.operationName;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getParam1() {
		return this.param1;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getParam2() {
		return this.param2;
	}
}