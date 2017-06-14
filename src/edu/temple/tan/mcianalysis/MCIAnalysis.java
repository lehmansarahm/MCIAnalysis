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
import edu.temple.tan.mcianalysis.intermediates.ActivitySplit;
import edu.temple.tan.mcianalysis.intermediates.CalibrationProcessing;
import edu.temple.tan.mcianalysis.preprocessing.AccelerationProcessing;
import edu.temple.tan.mcianalysis.preprocessing.EMAProcessing;
import edu.temple.tan.mcianalysis.preprocessing.NormalizationProcessing;
import edu.temple.tan.mcianalysis.utils.Constants;

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
import java.util.logging.Level;
import java.util.logging.Logger;

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
    	removeOldArtifacts();
    	
        List<ConfigCommand> commands = ConfigInterpreter.loadNewCommands();
        String filePath = new File("").getAbsolutePath();

        for (ConfigCommand command : commands) {
        	String[] userIDList = command.getUsername().split(Constants.DELIMITER_PARAMETER);
            String[] rawFilenameList = command.getSourceFile().split(Constants.DELIMITER_PARAMETER);
            
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
                    			//Logger.getLogger(MCIAnalysis.class.getName()).log(Level.INFO, 
                    	        //		"New input file path: " + inputFilePath + " for user: " + userID, "");
                				rawFilenameList[i] = inputFilePath;
                			}
                		}
                	}
            	}
            }
            
            // only continue with the analysis if the number of users 
            // provided matches the number of data files
            if (userIDList.length == rawFilenameList.length) {
            	for (int i = 0; i < userIDList.length; i++) {
                	// Retrieve the initial command details (username, input file, 
                	// target activity, accel. proc. mode)
            		String userID = userIDList[i];
                	//System.out.println("Parsed userID: " + userID);

            		String rawFilename = getFilename(userID, rawFilenameList);
                    String targetFilePath = filePath.concat(rawFilename);
                	//System.out.println("Parsed targetFile: " + targetFile);
                	
                    int accelerationProcess = command.getAccelProcess();
                	//System.out.println("Parsed acceleration_process: " + 
                	//		((acceleration_process == 0) ? "raw" : "linear"));

                    // CSVReader reader is one of two arguments to be passed to the
                    // analysis methods, to be populated based on accel. processing selection
                    switch (accelerationProcess) {
                        case 0:
                            accelerationProcessing = "Raw";
                            break;
                        case 1:
                            accelerationProcessing = "Linear";
                            CSVReader reader = new CSVReader(new FileReader(targetFilePath), ',', '"', 0);
        	                targetFilePath = AccelerationProcessing.convertToLinearAcceleration(reader, rawFilename);
        	                break;
                        default:
                            break;
                    }

                    CSVReader emaReader = new CSVReader(new FileReader(targetFilePath), ',', '"', 0);
                    String emaFilePath = EMAProcessing.convertToMovingAverage(emaReader, rawFilename);
                    emaReader.close();

                    CSVReader normReader = new CSVReader(new FileReader(emaFilePath), ',', '"', 0);
                    NormalizationProcessing.normalize(normReader, rawFilename);
                    normReader.close();

                	String rawTargetActivity = command.getTaskName();
                	//System.out.println("Parsed raw targetActivity: " + rawTargetActivity);
                    
                    // Split out activity list based on user selection
                    List<String> csvActivityList = new ArrayList<String>();
                    if (!rawTargetActivity.equalsIgnoreCase("All")) {
                        String[] targetActivities = rawTargetActivity.split(Constants.DELIMITER_PARAMETER);
                        for (String targetActivity : targetActivities) {
                            /*Logger.getLogger(MCIAnalysis.class.getName()).log(Level.INFO, 
                            		"Splitting activity sheet for target: \'" + targetActivity 
                            		+ "\' using source file: " + targetFilePath + " for user: " 
                    				+ userID + "\n\n", "");*/
                            CSVReader reader = new CSVReader(new FileReader(targetFilePath), ',', '"', 0);
                            String intermFilePath = ActivitySplit.generateActivitySpecificCSV(reader, userID, targetActivity.trim());
                            if (intermFilePath != null) csvActivityList.add(intermFilePath);
                        	reader.close();
                        }
                    } else {
                        CSVReader reader = new CSVReader(new FileReader(targetFilePath), ',', '"', 0);
                        csvActivityList = ActivitySplit.generateCSVForAllActivities(reader, userID);
                        reader.close();
                    }
                    
                    // If necessary, calibrate the pause / sudden movement thresholds
                    String calibrationStep = command.getCalibStep();
                    calibThresholdsUtilized = (!calibrationStep.equals(""));
                    if (calibThresholdsUtilized) {
                    	CalibrationProcessing.calibrateThresholdsForUser(userID, csvActivityList, calibrationStep);
                    	//System.out.println("Parsed calibration step: " + calibrationStep);
                    }

                    // Iterate through analysis operations provided in config file
                    List<AnalysisCommand> analysisOps = command.getAnalysisOps();
                    for (AnalysisCommand analysisOp : analysisOps) {
                        // Invoke analysis class by reflection
                        String className = Constants.ANALYSIS_NAMESPACE.concat(analysisOp.getOperationName());
                        Class<Analysis> analysisClass = (Class<Analysis>) Class.forName(className);
                        Object classObject = (Object) analysisClass.newInstance();
                        Method analysisMethod = analysisClass.getMethod("beginAnalysis", 
                        		String.class, String.class, String.class, String.class);

                        // Perform analysis for all activities indicated by config file
                        for (String activity : csvActivityList) {
                            try {
                            	analysisMethod.invoke(classObject, activity, userID, 
                        			analysisOp.getParam1(), analysisOp.getParam2());
                            } catch(IllegalArgumentException e) {
                                continue;
                            }
                        }
                    }
                    
                    // Clear out the activity list for this command set, and progress to the next
                    Logger.getLogger(MCIAnalysis.class.getName()).log(Level.INFO, 
                    		"Finished processing input file: " + targetFilePath + " for user: " 
            				+ userID + "\n", "");
                    csvActivityList.clear();
            	}
            }
        }

        // Write out final results
		ConfigInterpreter.writeConfigSettingsToOutputFiles();
		if (directionUtilized) DirectionAggregate.aggregateDirectionCSV();
		if (pauseUtilized) PauseAggregate.aggregatePauseCSV();
		if (taskTimeUtilized) TimeAggregate.aggregateTimeCSV();
		if (taskTimeUtilized && (pauseUtilized || directionUtilized)) UserAggregate.aggregateUserResultsCSV();
	}
    
    /**
     * 
     * @param userID
     * @param rawFilenameList
     * @return
     */
    private static String getFilename(String userID, String[] rawFilenameList) {
    	String filename = "";
        //Logger.getLogger(MCIAnalysis.class.getName()).log(Level.INFO, 
        //		"Getting input file for user: " + userID, "");
    	for (int i = 0; i < rawFilenameList.length; i++) {
    		String rawFilename = rawFilenameList[i].toLowerCase();
            //Logger.getLogger(MCIAnalysis.class.getName()).log(Level.INFO, 
            //		"Comparing file name: " + rawFilename, "");
    		if (rawFilename.contains(userID.toLowerCase())) {
    			filename = rawFilenameList[i];
    			break;
    		}
    	}
    	return filename;
    }
    
    /**
     * Support method to clean up any remaining artifacts from previous executions 
     * before proceeding with the current execution
     */
    private static void removeOldArtifacts() {
        String absolute_path = new File("").getAbsolutePath();
        
        File folder = new File(absolute_path.concat(Constants.FOLDER_NAME_FINAL));
        try {
			FileUtils.deleteDirectory(folder);
		} catch (IOException e) {
			// do nothing ... can't delete it if it wasn't there
		}
        
        folder = new File(absolute_path.concat(Constants.FOLDER_NAME_INTERM_ACT_SPLIT));
        try {
			FileUtils.deleteDirectory(folder);
		} catch (IOException e) {
			// do nothing ... can't delete it if it wasn't there
		}
        
        folder = new File(absolute_path.concat(Constants.FOLDER_NAME_PREPROCESSING_LINEAR));
        try {
			FileUtils.deleteDirectory(folder);
		} catch (IOException e) {
			// do nothing ... can't delete it if it wasn't there
		}
    }
}