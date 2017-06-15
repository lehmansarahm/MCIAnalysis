package edu.temple.tan.mcianalysis.intermediates;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.LogManager;
import edu.temple.tan.mcianalysis.utils.ToolkitUtils;

public class ActivityFilter {
	
	private static Map<String, Integer> activityCounts = new HashMap<>();

	/**
	 * 
	 * @param activityFilePaths
	 * @param taskCompletionThreshold
	 * @param userCount
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> filterByTaskCompletion(List<String> activityFilePaths, int taskCompletionThreshold, int userCount) {
        String absolutePath = new File("").getAbsolutePath();
        absolutePath = absolutePath.concat(Constants.FOLDER_NAME_INTERM_ACT_FILTER);
        new File(absolutePath).mkdirs();
        
        // iterate through once to count occurrences of each activity
        for (String activityFilePath : activityFilePaths) {
        	String filename = ToolkitUtils.getFileNameFromAbsolutePath(activityFilePath);
        	String activity = ToolkitUtils.getActivityNameFromFileName(filename);
        	addCount(activity);
        }
        
        // now iterate through the map to filter out activities with insufficient populations
	    Iterator<Entry<String, Integer>> it = activityCounts.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        int activityCount = (int)pair.getValue();	        
	    	double taskCompletion = ((double)activityCount / (double)userCount) * 100;
	    	if (taskCompletion < taskCompletionThreshold) { it.remove(); }
	    }
	    
	    // now iterate through a final time, and copy any files matching the remaining 
	    // entries in the map to the new directory
	    List<String> newActivityFilePaths = new ArrayList<>();
        for (String activityFilePath : activityFilePaths) {
        	String filename = ToolkitUtils.getFileNameFromAbsolutePath(activityFilePath);
        	String activity = ToolkitUtils.getActivityNameFromFileName(filename);
        	if (activityCounts.containsKey(activity)) {
        		try {
            		File oldActivityFile = new File(activityFilePath);
            		String newActivityFilePath = absolutePath.concat("/" + filename);
            		File newActivityFile = new File(newActivityFilePath);
            		newActivityFile.createNewFile();
            		
					Files.copy(oldActivityFile.toPath(), newActivityFile.toPath(), 
							StandardCopyOption.REPLACE_EXISTING);
					newActivityFilePaths.add(newActivityFile.getAbsolutePath());
				} catch (IOException ex) {
					LogManager.error(ActivityFilter.class, ex);
				}
        	}
        }
        
		return newActivityFilePaths;
	}
	
	/**
	 * 
	 * @param activity
	 */
	private static void addCount(String activity) {
		if (!activityCounts.containsKey(activity)) activityCounts.put(activity, 0);
		int newCount = activityCounts.get(activity) + 1;
		activityCounts.put(activity, newCount);
	}
	
}