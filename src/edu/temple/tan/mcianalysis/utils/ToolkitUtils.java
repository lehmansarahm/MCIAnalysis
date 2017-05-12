package edu.temple.tan.mcianalysis.utils;

import java.io.File;

/**
 * General purpose utility methods useful to whole toolkit
 *
 * @author Philip M. Coulomb
 * @author Sarah M. Lehman
 */
public class ToolkitUtils {
    /**
     * Calculates the magnitude of a vector according to formula:
     * mag=sqrt(x^2+y^2+z^2) 
     * 
     * @param accelX - the X axis component of the acceleration vector
     * @param accelY - the Y axis component of the acceleration vector
     * @param accelZ - the Z axis component of the acceleration vector
     * @return the magnitude of the provided vector
     */
    public static double calculateMagnitude(double accelX, double accelY, double accelZ) {
        double magX = Math.pow(accelX, 2);
        double magY = Math.pow(accelY, 2);
        double magZ = Math.pow(accelZ, 2);
        double magnitude = Math.sqrt(magX + magY + magZ);
        return magnitude;
    }

    /**
     * Ensures proper output directories are set up, and that the CSV Writer has 
     * a valid and clear filename to write to.
     * 
     * @param localFilePath - local file path for which to create the output directories
     * @param userID - the user ID by which to organize the output materials
     * @param analysisName - the name of the analysis operation to initialize
     * 
     * @return the absolute file path of the output directory
     */
    public static String initializeAnalysisOutputDirs(String localFilePath, String userID, 
	  String analysisName) {
        String[] pathComponents = localFilePath.split("/");
        String finalFilename = pathComponents[pathComponents.length - 1];
        String absolutePath = new File("").getAbsolutePath();
        
        absolutePath = absolutePath.concat(Constants.FOLDER_NAME_FINAL);
        new File(absolutePath).mkdirs();
        
        absolutePath = absolutePath.concat("/" + analysisName);
        new File(absolutePath).mkdirs();

        absolutePath = absolutePath.concat("/".concat(userID));
        new File(absolutePath).mkdirs();

        absolutePath = absolutePath.concat(("/" + analysisName + "_").concat(finalFilename));
        return absolutePath;
    }

    /**
     * 
     * @param s
     * @return
     */
    public static boolean isNumeric(String s) {  
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");  
    }
    
}