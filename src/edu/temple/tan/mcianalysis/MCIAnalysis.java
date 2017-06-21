package edu.temple.tan.mcianalysis;

import com.opencsv.CSVReader;

import edu.temple.tan.mcianalysis.aggregates.DirectionAggregate;
import edu.temple.tan.mcianalysis.aggregates.PauseAggregate;
import edu.temple.tan.mcianalysis.aggregates.TimeAggregate;
import edu.temple.tan.mcianalysis.aggregates.UserAggregate;
import edu.temple.tan.mcianalysis.analyses.Analysis;
import edu.temple.tan.mcianalysis.config.AnalysisCommand;
import edu.temple.tan.mcianalysis.config.ConfigCommand;
import edu.temple.tan.mcianalysis.config.ConfigInterpreter;
import edu.temple.tan.mcianalysis.intermediates.ActivityFilter;
import edu.temple.tan.mcianalysis.intermediates.ActivitySplit;
import edu.temple.tan.mcianalysis.intermediates.CalibrationProcessing;
import edu.temple.tan.mcianalysis.preprocessing.AccelerationProcessing;
import edu.temple.tan.mcianalysis.preprocessing.EMAProcessing;
import edu.temple.tan.mcianalysis.preprocessing.NormalizationProcessing;
import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.LogManager;
import edu.temple.tan.mcianalysis.utils.ToolkitUtils;

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

/**
 * Primary executable class within the MCI Analysis toolkit.  Searches file 
 * system for applicable configuration files, parses them, and executes the 
 * desired analysis operations with the appropriate input data and parameters.
 * 
 * @author Philip M. Coulomb
 * @author Sarah M. Lehman
 */
public class MCIAnalysis {

    public static Set<String> requestedActivitySet = new HashSet<>();
    public static String runTime;
    public static String accelerationProcessing;
    
    public static boolean calibThresholdsUtilized = false;
    public static boolean directionUtilized = false;
    public static boolean pauseUtilized = false;
    public static boolean taskTimeUtilized = false;
    
    /**
     * Primary operation method
     * 
     * @param args the command line arguments, none currently accepted
     */
    @SuppressWarnings({ "unchecked" })
	public static void main(String[] args) throws FileNotFoundException, IOException, 
    	ClassNotFoundException, NoSuchMethodException, IllegalAccessException, 
    	IllegalArgumentException, InvocationTargetException, InstantiationException {
    	
    	// before anything else, clear out artifacts from last execution
    	ToolkitUtils.removeOldArtifacts();
    	ToolkitUtils.initializeAppDirs();
    	
        List<ConfigCommand> commands = ConfigInterpreter.loadNewCommands();
        String filePath = new File("").getAbsolutePath();

        for (ConfigCommand command : commands) {
        	String[] userIDList = command.getUsername().split(Constants.DELIMITER_PARAMETER);
            String[] rawFilenameList = command.getSourceFile().split(Constants.DELIMITER_PARAMETER);

            // ----------------------------------------------------------------------------------------
            // ----------------------------------------------------------------------------------------
            //				INITIAL PROCESSING - RETRIEVE LIST OF RAW INPUT FILES
            // ----------------------------------------------------------------------------------------
            // ----------------------------------------------------------------------------------------
            
            // parse out the input files
            if (rawFilenameList.length == 1) {
            	List<String> inputFiles = new ArrayList<>();
            	String inputFolderPath = rawFilenameList[0];
            	
            	File inputFolder = new File("." + inputFolderPath);
            	if (inputFolder.exists() && inputFolder.isDirectory()) {
	            	for (File inputFile : inputFolder.listFiles()) {
	            		if (inputFile.getName().endsWith(".csv")) {
	            			String newInputFilePath = inputFolderPath + "/" + inputFile.getName();
	            			inputFiles.add(newInputFilePath);
	            		}
	            	}
            	}
            	
            	if (userIDList.length == inputFiles.size()) {
                	rawFilenameList = new String[userIDList.length];
                	for (int i = 0; i < userIDList.length; i++) {
                		String userID = userIDList[i];
                		for (int j = 0; j < inputFiles.size(); j++) {
                			String inputFilePath = inputFiles.get(j);
                			if (inputFilePath.toLowerCase().contains(userID.toLowerCase())) {
                				rawFilenameList[i] = inputFilePath;
                			}
                		}
                	}
            	}
            }
            
            // only continue with the analysis if the number of users 
            // provided matches the number of data files
            if (userIDList.length == rawFilenameList.length) {
            	String rawTargetActivity = command.getTaskName();
                accelerationProcessing = (command.getAccelProcess() == 1) ? "Linear" : "Raw";
                
                String calibrationStep = command.getCalibStep();
                calibThresholdsUtilized = (!calibrationStep.equals(""));
                
                List<String> csvActivityList = new ArrayList<>();
                for (int i = 0; i < userIDList.length; i++) {
                	// Retrieve the initial command details (username, input file, 
                	// target activity, accel. proc. mode)
            		String userID = userIDList[i];
            		String rawFilename = getFilename(userID, rawFilenameList);
                    String targetFilePath = filePath.concat(rawFilename);

                    // ----------------------------------------------------------------------------
                    // ----------------------------------------------------------------------------
                    //		PREPROCESSING, PART ONE - LINEAR CONVERSION
                    // ----------------------------------------------------------------------------
                    // ----------------------------------------------------------------------------

                	// if using accel processing, convert all input files to linear
                	if (accelerationProcessing.equals("Linear")) {
                        // CSVReader reader is one of two arguments to be passed to the
                        // analysis methods, to be populated based on accel. processing selection
	                    CSVReader reader = new CSVReader(new FileReader(targetFilePath), ',', '"', 0);
		                targetFilePath = AccelerationProcessing.convertToLinearAcceleration(reader, rawFilename);
                	}

                    // ----------------------------------------------------------------------------
                    // ----------------------------------------------------------------------------
                    //		PREPROCESSING, PART TWO - LOW PASS FILTER
                    // ----------------------------------------------------------------------------
                    // ----------------------------------------------------------------------------

                    CSVReader emaReader = new CSVReader(new FileReader(targetFilePath), ',', '"', 0);
                    String emaFilePath = EMAProcessing.convertToMovingAverage(emaReader, rawFilename);
                    emaReader.close();

                    // ----------------------------------------------------------------------------
                    // ----------------------------------------------------------------------------
                    //		PREPROCESSING, PART THREE - NORMALIZATION
                    // ----------------------------------------------------------------------------
                    // ----------------------------------------------------------------------------

                    CSVReader normReader = new CSVReader(new FileReader(emaFilePath), ',', '"', 0);
                    NormalizationProcessing.normalize(normReader, rawFilename);
                    normReader.close();

                    // ----------------------------------------------------------------------------
                    // ----------------------------------------------------------------------------
                    //		INTERMEDIATE PROCESSING, PART ONE - ACTIVITY SPLIT
                    // ----------------------------------------------------------------------------
                    // ----------------------------------------------------------------------------

                    // Split out activity list based on user selection
                    List<String> userCSVActivityList = new ArrayList<>();
                    if (!rawTargetActivity.equalsIgnoreCase("All")) {
                        String[] targetActivities = rawTargetActivity.split(Constants.DELIMITER_PARAMETER);
                        for (String targetActivity : targetActivities) {
                        	targetActivity = targetActivity.trim();
                            CSVReader reader = new CSVReader(new FileReader(targetFilePath), ',', '"', 0);
                            String intermFilePath = ActivitySplit.generateActivitySpecificCSV(reader, userID, targetActivity);
                            if (intermFilePath != null) userCSVActivityList.add(intermFilePath);
                        	reader.close();
                        }
                    } else {
                        CSVReader reader = new CSVReader(new FileReader(targetFilePath), ',', '"', 0);
                        userCSVActivityList = ActivitySplit.generateCSVForAllActivities(reader, userID);
                        reader.close();
                    }

                    // ----------------------------------------------------------------------------
                    // ----------------------------------------------------------------------------
                    //		INTERMEDIATE PROCESSING, PART THREE - THRESHOLD CALIBRATION
                    //							(out of order)
                    // ----------------------------------------------------------------------------
                    // ----------------------------------------------------------------------------
                    
                    // If necessary, calibrate the pause / sudden movement thresholds
                    if (calibThresholdsUtilized) {
                    	CalibrationProcessing.calibrateThresholdsForUser(userID, userCSVActivityList, calibrationStep);
                    }
                    
                    // Now that user-specific processing is over, add the user activities to the main list
                    csvActivityList.addAll(userCSVActivityList);
                }

                // --------------------------------------------------------------------------------
                // --------------------------------------------------------------------------------
                //	INTERMEDIATE PROCESSING, PART TWO - FILTER ACTIVITY BY COMPLETION PERCENTAGE
                //								(out of order)
                // --------------------------------------------------------------------------------
                // --------------------------------------------------------------------------------

                int taskCompletionThreshold = command.getTaskCompletionThreshold();
                csvActivityList = ActivityFilter.filterByTaskCompletion(csvActivityList, taskCompletionThreshold, userIDList.length);
                
                // for every activity file in the final list, run the desired analysis ops
            	for (String targetFilePath : csvActivityList) {
            		String filename = ToolkitUtils.getFileNameFromAbsolutePath(targetFilePath);
            		String userID = ToolkitUtils.getUsernameFromFileName(filename);

                    // ----------------------------------------------------------------------------
                    // ----------------------------------------------------------------------------
                    //					FINAL PROCESSING - ANALYIS OPERATIONS
                    // ----------------------------------------------------------------------------
                    // ----------------------------------------------------------------------------
            		
                    // Iterate through analysis operations provided in config file
                    List<AnalysisCommand> analysisOps = command.getAnalysisOps();
                    for (AnalysisCommand analysisOp : analysisOps) {
                        // Invoke analysis class by reflection
                        String className = Constants.ANALYSIS_NAMESPACE.concat(analysisOp.getOperationName());
                        Class<Analysis> analysisClass = (Class<Analysis>) Class.forName(className);
                        Object classObject = (Object) analysisClass.newInstance();
                        Method analysisMethod = analysisClass.getMethod("beginAnalysis", 
                        		String.class, String.class, String.class, String.class);

                        // Perform analysis against target file as indicated by config file
                        try {
                        	analysisMethod.invoke(classObject, targetFilePath, userID, 
                    			analysisOp.getParam1(), analysisOp.getParam2());
                        } catch(IllegalArgumentException e) {
                            continue;
                        }
                    }
                    
                    LogManager.info(MCIAnalysis.class, "Finished processing input file: " 
                    		+ targetFilePath + " for user: " + userID + "\n");
            	}
            	
                // Clear out the activity list for this command set, and progress to the next
                csvActivityList.clear();
            }
        }

        // ----------------------------------------------------------------------------------------
        // ----------------------------------------------------------------------------------------
        //					POST PROCESSING - DATA AGGREGATION AND LOG FILE DUMP
        // ----------------------------------------------------------------------------------------
        // ----------------------------------------------------------------------------------------

        // Write out final results
		ConfigInterpreter.writeConfigSettingsToOutputFiles();
		if (directionUtilized) DirectionAggregate.aggregateDirectionCSV();
		if (pauseUtilized) PauseAggregate.aggregatePauseCSV();
		if (taskTimeUtilized) TimeAggregate.aggregateTimeCSV();
		if (taskTimeUtilized && (pauseUtilized || directionUtilized)) UserAggregate.aggregateUserResultsCSV();
        LogManager.writeAll();
	}
    
    /**
     * 
     * @param userID
     * @param rawFilenameList
     * @return
     */
    private static String getFilename(String userID, String[] rawFilenameList) {
    	String filename = "";
    	for (int i = 0; i < rawFilenameList.length; i++) {
    		String rawFilename = rawFilenameList[i].toLowerCase();
    		if (rawFilename.contains(userID.toLowerCase())) {
    			filename = rawFilenameList[i];
    			break;
    		}
    	}
    	return filename;
    }
}