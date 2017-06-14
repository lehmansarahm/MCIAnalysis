package edu.temple.tan.mcianalysis.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LogManager {

	private static List<String> errorMessages = new ArrayList<>();
	private static List<String> infoMessages = new ArrayList<>();
	private static List<String> userMessages = new ArrayList<>();
	
	/**
	 * 
	 * @param message
	 */
	public static void error(String message) { errorMessages.add(message + "\n"); }
	
	/**
	 * 
	 * @param source
	 * @param message
	 */
	public static void error(Class<?> source, String message) { 
		errorMessages.add(source.getName() + "\n" + message + "\n"); 
	}
	
	/**
	 * 
	 * @param source
	 * @param ex
	 */
	public static void error(Class<?> source, Exception ex) { 
		errorMessages.add(source.getName() + "\n" + ex.getMessage() + "\n" + ex.getStackTrace()); 
	}
	
	/**
	 * 
	 * @param message
	 */
	public static void info(String message) { infoMessages.add(message + "\n"); }
	
	/**
	 * 
	 * @param source
	 * @param message
	 */
	public static void info(Class<?> source, String message) { 
		infoMessages.add(source.getName() + "\n" + message + "\n"); 
	}
	
	/**
	 * 
	 * @param message
	 */
	public static void user(String message) { userMessages.add(message + "\n"); }
	
	/**
	 * 
	 * @param source
	 * @param message
	 */
	public static void user(Class<?> source, String message) { 
		userMessages.add(source.getName() + "\n" + message + "\n"); 
	}
	
	/**
	 * 
	 */
	public static void writeAll() {
		writeLog(Constants.LOG_FILE_ERRORS, errorMessages);
		writeLog(Constants.LOG_FILE_INFO, infoMessages);
		writeLog(Constants.LOG_FILE_USER_RESULTS, userMessages);
	}
	
	/**
	 * 
	 * @param logFileName
	 * @param logFileContents
	 */
	private static void writeLog(String logFileName, List<String> logFileContents) {
        String logFilePath = new File("").getAbsolutePath().concat(Constants.FOLDER_NAME_DEBUG + "/" + logFileName);
        try {
            FileWriter fileWriter = new FileWriter(logFilePath); 
            for (String summary: logFileContents) { fileWriter.write(summary); }
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}