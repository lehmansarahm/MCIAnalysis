/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.temple.tan.mcianalysis.preprocessing;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.Constants.INPUT_FILE_COLUMN_ORDER;
import edu.temple.tan.mcianalysis.utils.Constants.INTERM_FILE_COLUMN_ORDER;
import edu.temple.tan.mcianalysis.utils.ToolkitUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccelerationProcessing {

    private static float[] GRAVITY = new float[] { 0.0F, 0.0F, 0.0F};
    private static final int ACCEL_X = 0;
    private static final int ACCEL_Y = 1;
    private static final int ACCEL_Z = 2;

    private static final int RAW_LINE_NO_INDEX = INPUT_FILE_COLUMN_ORDER.RECORD_NUM.ordinal();
    private static final int RAW_ACCEL_X_INDEX = INPUT_FILE_COLUMN_ORDER.ACCEL_X.ordinal();
    private static final int RAW_ACCEL_Y_INDEX = INPUT_FILE_COLUMN_ORDER.ACCEL_Y.ordinal();
    private static final int RAW_ACCEL_Z_INDEX = INPUT_FILE_COLUMN_ORDER.ACCEL_Z.ordinal();
    private static final int RAW_ACTIVITY_INDEX = INPUT_FILE_COLUMN_ORDER.ACTIVITY.ordinal();
    
    private static final boolean USE_NOISE_FILTERING = false;

    /**
     * 
     * @param inputReader
     * @param inputFileName
     * @return
     * @throws IOException
     */
    public static String convertToLinearAcceleration(CSVReader inputReader, String inputFileName) throws IOException {
        String linearOutputDir = (new File("").getAbsolutePath()).concat(Constants.FOLDER_NAME_PREPROCESSING_LINEAR);
        new File(linearOutputDir).mkdirs();
        
        String fileNameComponents[] = inputFileName.split("/");
        inputFileName = fileNameComponents[fileNameComponents.length - 1];
        String newFilePath = linearOutputDir.concat("/" + inputFileName);

        CSVWriter writer = new CSVWriter(new FileWriter(newFilePath));
        writer.writeNext(ToolkitUtils.getIntermHeaderLine());
        writer.flush();

        List<String[]> readerLines = inputReader.readAll();
        if (readerLines.size() > 0) {
	        for (String[] nextLine : readerLines) {
	            if (!ToolkitUtils.isHeaderLine(nextLine)) {
		            if (nextLine.length > RAW_ACCEL_Z_INDEX)
		            	writeLinearAcceleration(writer, nextLine);
		            if (nextLine.length > (INPUT_FILE_COLUMN_ORDER.START_END.ordinal()) && 
		            		nextLine[INPUT_FILE_COLUMN_ORDER.START_END.ordinal()].equalsIgnoreCase("quit")) 
		            	break;
	            }
	        }
        } else {
			Logger.getLogger(AccelerationProcessing.class.getName()).log(Level.INFO, 
	        		"No reader lines found in input file: " + inputFileName, "");
        }
        
        writer.close();
        return newFilePath;
    }

    /**
     * 
     * @param writer
     * @param nextLine
     * @throws IOException
     */
    private static void writeLinearAcceleration(CSVWriter writer, String[] nextLine) throws IOException {
    	String rawXAccel = nextLine[RAW_ACCEL_X_INDEX].trim();
    	String rawYAccel = nextLine[RAW_ACCEL_Y_INDEX].trim();
    	String rawZAccel = nextLine[RAW_ACCEL_Z_INDEX].trim();
    	
    	if (ToolkitUtils.isNumeric(rawXAccel, rawYAccel, rawZAccel)) {
            final float alpha = 0.8f;
            final float noise = 0.1f;
            
            float xAccel = Float.valueOf(rawXAccel);
            float yAccel = Float.valueOf(rawYAccel);
            float zAccel = Float.valueOf(rawZAccel);

            GRAVITY[ACCEL_X] = alpha * GRAVITY[ACCEL_X] + (1 - alpha) * xAccel;
            GRAVITY[ACCEL_Y] = alpha * GRAVITY[ACCEL_Y] + (1 - alpha) * yAccel;
            GRAVITY[ACCEL_Z] = alpha * GRAVITY[ACCEL_Z] + (1 - alpha) * zAccel;
            
            float noGravX = (xAccel - GRAVITY[ACCEL_X]);
            float noNoiseX = (Math.abs(noGravX) < noise) ? 0.0f : noGravX;
	        nextLine[RAW_ACCEL_X_INDEX] = USE_NOISE_FILTERING ? String.valueOf(noNoiseX) : String.valueOf(noGravX);
            
            float noGravY = (yAccel - GRAVITY[ACCEL_Y]);
            float noNoiseY = (Math.abs(noGravY) < noise) ? 0.0f : noGravY;
	        nextLine[RAW_ACCEL_Y_INDEX] = USE_NOISE_FILTERING ? String.valueOf(noNoiseY) : String.valueOf(noGravY);
            
            float noGravZ = (zAccel - GRAVITY[ACCEL_Z]);
            float noNoiseZ = (Math.abs(noGravZ) < noise) ? 0.0f : noGravZ;
	        nextLine[RAW_ACCEL_Z_INDEX] = USE_NOISE_FILTERING ? String.valueOf(noNoiseZ) : String.valueOf(noGravZ);
	        
	        if (nextLine.length >= (RAW_ACTIVITY_INDEX + 1)) {
		        String[] activityNameComponents = nextLine[RAW_ACTIVITY_INDEX].split(Constants.DELIMITER_TIMESTAMP);
		        String activityName = activityNameComponents[activityNameComponents.length - 1];
		        nextLine[RAW_ACTIVITY_INDEX] = activityName.replaceAll("[^\\dA-Za-z ]", "");
	        }
	
	        writer.writeNext(getWriteLine(nextLine));
	        writer.flush();
    	} else {
			Logger.getLogger(AccelerationProcessing.class.getName()).log(Level.INFO, 
	        		"Cannot parse acceleration data at line number: " + nextLine[RAW_LINE_NO_INDEX], "");
        }
    }

    /**
     * 
     * @param nextLine
     * @return
     */
    private static String[] getWriteLine(String[] nextLine) {
        String[] writeLine = new String[INTERM_FILE_COLUMN_ORDER.values().length];
        
        String rawCurrentTime = nextLine[INPUT_FILE_COLUMN_ORDER.TIME.ordinal()];
    	writeLine[INTERM_FILE_COLUMN_ORDER.TIME.ordinal()] = rawCurrentTime;
    	
    	writeLine[INTERM_FILE_COLUMN_ORDER.RECORD_NUM.ordinal()] = 
    			nextLine[INPUT_FILE_COLUMN_ORDER.RECORD_NUM.ordinal()];
    	writeLine[INTERM_FILE_COLUMN_ORDER.AZIMUTH.ordinal()] = 
    			nextLine[INPUT_FILE_COLUMN_ORDER.AZIMUTH.ordinal()];
    	writeLine[INTERM_FILE_COLUMN_ORDER.PITCH.ordinal()] = 
    			nextLine[INPUT_FILE_COLUMN_ORDER.PITCH.ordinal()];
    	writeLine[INTERM_FILE_COLUMN_ORDER.ROLL.ordinal()] = 
    			nextLine[INPUT_FILE_COLUMN_ORDER.ROLL.ordinal()];
    	
    	String rawAccelX = nextLine[INPUT_FILE_COLUMN_ORDER.ACCEL_X.ordinal()];
    	writeLine[INTERM_FILE_COLUMN_ORDER.ACCEL_X.ordinal()] = rawAccelX;

    	String rawAccelY = nextLine[INPUT_FILE_COLUMN_ORDER.ACCEL_Y.ordinal()];
    	writeLine[INTERM_FILE_COLUMN_ORDER.ACCEL_Y.ordinal()] = rawAccelY;

    	String rawAccelZ = nextLine[INPUT_FILE_COLUMN_ORDER.ACCEL_Z.ordinal()];
    	writeLine[INTERM_FILE_COLUMN_ORDER.ACCEL_Z.ordinal()] = rawAccelZ;

    	double accelMag = ToolkitUtils.calculateMagnitude(Double.parseDouble(rawAccelX), 
    			Double.parseDouble(rawAccelY), Double.parseDouble(rawAccelZ));
    	writeLine[INTERM_FILE_COLUMN_ORDER.ACCEL_MAG.ordinal()] = Double.toString(accelMag);
    	
    	double instantaneousSpeed = accelMag * (Constants.SAMPLING_PERIOD / 1000.0d);
    	writeLine[INTERM_FILE_COLUMN_ORDER.SPEED.ordinal()] = Double.toString(instantaneousSpeed);
    	
    	writeLine[INTERM_FILE_COLUMN_ORDER.START_END.ordinal()] = 
    			nextLine[INPUT_FILE_COLUMN_ORDER.START_END.ordinal()];
    	writeLine[INTERM_FILE_COLUMN_ORDER.ACTIVITY.ordinal()] = 
    			nextLine[INPUT_FILE_COLUMN_ORDER.ACTIVITY.ordinal()];
    	
    	return writeLine;
    }

}