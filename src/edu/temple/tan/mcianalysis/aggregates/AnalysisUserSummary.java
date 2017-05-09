package edu.temple.tan.mcianalysis.aggregates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnalysisUserSummary {

	private String analysisName;
	private Map<String, SubtaskResult> subtasks = new HashMap<String, SubtaskResult>();
	
	/**
	 * 
	 * @param analysisName
	 */
	public AnalysisUserSummary(String analysisName) {
		this.analysisName = analysisName;
	}
	
	/**
	 * 
	 * @param user
	 * @param headers
	 * @param summaryLine
	 */
	public void addUserSummary(String user, String[] headers, String[] summaryLine) {
		if (headers.length == summaryLine.length) {
			String[] subtaskComponents = summaryLine[0].split(":");
			String subtask = subtaskComponents[subtaskComponents.length - 1];
			for (int i = 1; i < summaryLine.length; i++) {
				String header = headers[i];
				String summary = summaryLine[i];
				
				if (!subtasks.containsKey(subtask)) subtasks.put(subtask, new SubtaskResult(subtask));
				subtasks.get(subtask).addResult(header, user, summary);
			}
		} else Logger.getLogger(AnalysisUserSummary.class.getName()).log(Level.SEVERE, null, "Bad summary input");
	}
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<String[]> toOutputArray() {
		List<String[]> output = new ArrayList<>();
		
		output.add(new String[] {"New Analysis Summary:", analysisName});
		boolean headersPrinted = false;
		
	    Iterator<Entry<String, SubtaskResult>> it = subtasks.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        SubtaskResult subtaskResult = (SubtaskResult) pair.getValue();
	        
	        if (!headersPrinted) {
	        	output.add(subtaskResult.getResultHeaders());
	        	headersPrinted = !headersPrinted;
	        }
	        
	        String[] resultValues = subtaskResult.getResultValues();
	        if (!resultValues[0].isEmpty()) output.add(resultValues);
	        it.remove();
	    }

		// spacing
		output.add(new String[2]);
		output.add(new String[2]);
		
		return output;
	}
	
	/**
	 * 
	 */
	class SubtaskResult {
		
		private String subtaskName;
		private Map<String, List<String[]>> results = new HashMap<String, List<String[]>>();
		
		/**
		 * 
		 * @param subtaskName
		 */
		public SubtaskResult(String subtaskName) {
			this.subtaskName = subtaskName;
		}
		
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
		public String[] getResultHeaders() {
			List<String> resultHeaders = new ArrayList<>();
			resultHeaders.add("Subtask:");
			
		    Iterator<Entry<String, List<String[]>>> it = results.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        String metric = (String) pair.getKey();
		        List<String[]> subtaskResults = (List<String[]>) pair.getValue();
		        
		        for (String[] subtaskResult : subtaskResults) {
		        	resultHeaders.add(metric + " (" + subtaskResult[0] + ")");
		        }
		        it.remove();
		    }
		    
		    return (String[]) resultHeaders.toArray(new String[resultHeaders.size()]);
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
}