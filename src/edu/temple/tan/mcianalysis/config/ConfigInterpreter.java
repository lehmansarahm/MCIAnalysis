package edu.temple.tan.mcianalysis.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.MCIAnalysis;
import edu.temple.tan.mcianalysis.utils.Constants;

/**
 * Processes the contents of the configuration file 
 * 
 * @author Philip M. Coulomb
 * @author Sarah M. Lehman
 */
public class ConfigInterpreter {
    public static List<String> configuration = new ArrayList<String>();

	/**
	 * Reads in the configuration file, and loads the specific behaviors 
	 * the program has been asked to perform 
	 * 
	 * @return the parsed list of commands
	 */
    public static List<ConfigCommand> loadNewCommands() {
    	return loadNewCommands("/" + Constants.CONFIG_FILE);
    }
    
    /**
	 * Reads in the configuration file, and loads the specific behaviors 
	 * the program has been asked to perform 
     * 
     * @param fileName - the name of the configuration file to load
	 * @return the parsed list of commands
     */
    public static List<ConfigCommand> loadNewCommands(String fileName) {
    	List<ConfigCommand> commands = new ArrayList<ConfigCommand>();
    	
		try {
	        String fullFilePath = new File("").getAbsolutePath().concat(fileName);
			FileReader fileReader = new FileReader(fullFilePath);
	        BufferedReader bufferedReader = new BufferedReader(fileReader);
	        
	        // Read the contents of the file into our local store
	        String nextLine;
	        configuration.add("Configuration File Used:");
            while ((nextLine = bufferedReader.readLine()) != null) {
                configuration.add(nextLine);
            }
            bufferedReader.close();
            
            // now parse the local store for commands
            List<String> innerCommands = new ArrayList<String>();
            for (String config : configuration) {
            	String condensedConfig = config.replace(" ", "");
            	if (condensedConfig.equals("{")) {			// starting a new set of commands
            		innerCommands = new ArrayList<String>();
            	} else if (condensedConfig.equals("}")) {	// wrapping up a set of commands
            		commands.add(new ConfigCommand(innerCommands));
            	} else {
            		innerCommands.add(config); 				// adding a new inner command
            	}
            }
		} catch (IOException e) {
			// do something
		}
        
    	return commands;
    }

    /**
     * 
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void writeConfigSettingsToOutputFiles() throws FileNotFoundException, IOException {
        String absolute_path = new File("").getAbsolutePath();
        absolute_path = absolute_path.concat(Constants.FOLDER_NAME_FINAL);

        File directionFolder = new File(absolute_path);
        File[] listOfFiles = directionFolder.listFiles();

        //loop through the directories found within
        if (listOfFiles != null) {
	        for (int i = 0; i < listOfFiles.length; i++) {
	            if (listOfFiles[i].isDirectory()) {
	                File[] innerFiles = listOfFiles[i].listFiles();
	                //loop through the inner directory
	                for (int j = 0; j < innerFiles.length; j++) {
	                    if (innerFiles[j].isDirectory()) {
	                        File[] innerFiles2 = innerFiles[j].listFiles();
	                        //loop through the inner directory
	                        for (int k = 0; k < innerFiles2.length; k++) {
	                            if (innerFiles2[k].isDirectory()) {
	                                File[] innerMostFiles = innerFiles2[k].listFiles();
	                                for (int m = 0; m < innerMostFiles.length; m++) {
	                                    if (innerMostFiles[m].isDirectory()) {
	                                    	// do something
	                                    } else {
	                                        String inner_file_path = innerMostFiles[m].getAbsolutePath();
	                                        if (inner_file_path.contains(MCIAnalysis.run_time)) {
	                                            CSVWriter writer = new CSVWriter(new FileWriter(inner_file_path, true));
	                                            for (String config : configuration) {
	                                            	writer.writeNext(new String[] { config });
	                                            }
	                                            writer.close();
	                                        }
	                                    }
	                                }
	                            } else {
	                                String file_path = innerFiles2[k].getAbsolutePath();
	                                if (file_path.contains(MCIAnalysis.run_time)) {
	                                    CSVWriter writer = new CSVWriter(new FileWriter(file_path, true));
	                                    for (String config : configuration) {
	                                    	writer.writeNext(new String[] { config });
	                                    }
	                                    writer.close();
	                                }
	                            }
	                        }
	                    } else {
	                        String outer_file_path = innerFiles[j].getAbsolutePath();
	                        if (outer_file_path.contains(MCIAnalysis.run_time)) {
	                            CSVWriter writer = new CSVWriter(new FileWriter(outer_file_path, true));
	                            for (String config : configuration) {
	                            	writer.writeNext(new String[] { config });
	                            }
	                            writer.close();
	                        }
	                    }
	                }
	            }
	        }
        }
    }
}