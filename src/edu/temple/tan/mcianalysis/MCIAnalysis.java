package edu.temple.tan.mcianalysis;

import com.opencsv.CSVReader;

import edu.temple.tan.mcianalysis.aggregates.DirectionAggregate;
import edu.temple.tan.mcianalysis.aggregates.PauseAggregate;
import edu.temple.tan.mcianalysis.analyses.Analysis;
import edu.temple.tan.mcianalysis.utils.AccelerationProcessing;
import edu.temple.tan.mcianalysis.utils.ActivitySplit;
import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.ScriptInterpreter;
import edu.temple.tan.mcianalysis.utils.WriteConfigurationCSV;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;

/**
 * Primary executable class within the MCI Analysis toolkit.  Searches file 
 * system for applicable configuration files, parses them, and executes the 
 * desired analysis operations with the appropriate input data and parameters.
 * 
 * @author Philip M. Coulomb
 * @author Sarah M. Lehman
 */
public class MCIAnalysis {

    public static Set<String> requested_activities_set = new HashSet<>();
    public static String[][] configuration = new String[1000][1];
    public static String run_time;
    public static String acceleration_processing;
    public static boolean direction_utilized = false;
    public static boolean pause_utilized = false;
    
    /**
     * Primary operation method
     * 
     * @param args the command line arguments, none currently accepted
     */
    @SuppressWarnings({ "resource", "unchecked" })
	public static void main(String[] args) throws FileNotFoundException, IOException, 
    	ClassNotFoundException, NoSuchMethodException, IllegalAccessException, 
    	IllegalArgumentException, InvocationTargetException, InstantiationException {
    	
    	// before anything else, clear out artifacts from last execution
    	removeOldArtifacts();
    	
        String[][] commands = ScriptInterpreter.loadCommands();
        String filePath = new File("").getAbsolutePath();
        List<String> csvActivityList = new ArrayList<String>();

        int i = 0;
        while (i < commands.length && 
		  commands[i][Constants.CONFIG_FILE_COLUMN_ORDER.USERNAME.ordinal()] != null) {
        	// Retrieve the initial command details (username, input file, 
        	// target activity, accel. proc. mode)
        	String userID = 
        			commands[i][Constants.CONFIG_FILE_COLUMN_ORDER.USERNAME.ordinal()].trim();
            String rawFilename = 
            		commands[i][Constants.CONFIG_FILE_COLUMN_ORDER.INPUT_FILE.ordinal()].toString().trim();
            String targetFile = filePath.concat(rawFilename);
            
            String targetActivity = 
            		commands[i][Constants.CONFIG_FILE_COLUMN_ORDER.TASK.ordinal()].toString().trim();
            int acceleration_process = Integer.valueOf(
            		commands[i][Constants.CONFIG_FILE_COLUMN_ORDER.ACCEL_PROCESSING.ordinal()]);

            // CSVReader reader is one of two arguments to be passed to the
            // analysis methods, to be populated based on accel. processing selection
            CSVReader reader = new CSVReader(new FileReader(targetFile), ',', '"', 0);
            switch (acceleration_process) {
                case 0:
                    acceleration_processing = "Raw";
                    break;
                case 1:
                    acceleration_processing = "Linear";
	                reader = AccelerationProcessing.convertToLinearAcceleration(reader, rawFilename);
	                break;
                default:
                    break;
            }
            
            // Split out activity list based on user selection
            if (!targetActivity.equalsIgnoreCase("All")) {
                csvActivityList.add(ActivitySplit.generateActivitySpecificCSV(reader, userID, targetActivity));
            } else {
                csvActivityList = ActivitySplit.generateCSVForAllActivities(reader, userID);
            }

            // Iterate through analysis operations provided in config file
            int k = Constants.CONFIG_FILE_COLUMN_ORDER.ANALYSES.ordinal();
            while (commands[i][k]!= null) {
                String className = commands[i][k].trim();
                String analysis[] = className.split(":");
                String param1 = null, param2 = null;

                // Retrieve first and second parameters, if available
                if (analysis.length >= 2) {
                    param1 = analysis[Constants.CONFIG_FILE_ANALYSIS_ORDER.PARAM1.ordinal()];
                }
                if (analysis.length >= 3) {
                    param2 = analysis[Constants.CONFIG_FILE_ANALYSIS_ORDER.PARAM2.ordinal()];
                }

                // Invoke analysis class by reflection
                className = Constants.ANALYSIS_NAMESPACE.concat(
                		analysis[Constants.CONFIG_FILE_ANALYSIS_ORDER.OPERATION_NAME.ordinal()]);
                Class<Analysis> analysisClass = (Class<Analysis>) Class.forName(className);
                Object classObject = (Object) analysisClass.newInstance();
                Method analysisMethod = analysisClass.getMethod("beginAnalysis", 
                		String.class, String.class, String.class, String.class);

                // Perform analysis for all activities indicated by config file
                int j = 0;
                while (j < csvActivityList.size()) {
                    try{
                    	analysisMethod.invoke(classObject, csvActivityList.get(j), userID, param1, param2);
                    } catch(IllegalArgumentException e) {
                        continue;
                    }
                    j++;
                }
                
                // Analysis operation complete for all activities.  Progress to next.
                k++;	
            }
            
            // Clear out the activity list for this command set, and progress to the next
            csvActivityList.clear();
            i++;
        }

        // Write out final results
		WriteConfigurationCSV.writeConfigurationSetupCSV();
		if (direction_utilized) DirectionAggregate.aggregateDirectionCSV();
		if (pause_utilized) PauseAggregate.aggregatePauseCSV();
	}
    
    /**
     * Support method to clean up any remaining artifacts from previous executions 
     * before proceeding with the current execution
     */
    private static void removeOldArtifacts() {
        String absolute_path = new File("").getAbsolutePath();
        
        File folder = new File(absolute_path.concat("/Final"));
        try {
			FileUtils.deleteDirectory(folder);
		} catch (IOException e) {
			// do nothing ... can't delete it if it wasn't there
		}
        
        folder = new File(absolute_path.concat("/Intermediate"));
        try {
			FileUtils.deleteDirectory(folder);
		} catch (IOException e) {
			// do nothing ... can't delete it if it wasn't there
		}
        
        folder = new File(absolute_path.concat("/Linear"));
        try {
			FileUtils.deleteDirectory(folder);
		} catch (IOException e) {
			// do nothing ... can't delete it if it wasn't there
		}
    }
}