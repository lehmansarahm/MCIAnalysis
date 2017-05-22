/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.temple.tan.mcianalysis.utils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.utils.Constants.INPUT_FILE_COLUMN_ORDER;

import java.io.File;
import java.io.FileReader;
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

    private static final int RAW_TIME_INDEX = INPUT_FILE_COLUMN_ORDER.TIME.ordinal();
    private static final int RAW_ACCEL_X_INDEX = INPUT_FILE_COLUMN_ORDER.ACCEL_X.ordinal();
    private static final int RAW_ACCEL_Y_INDEX = INPUT_FILE_COLUMN_ORDER.ACCEL_Y.ordinal();
    private static final int RAW_ACCEL_Z_INDEX = INPUT_FILE_COLUMN_ORDER.ACCEL_Z.ordinal();
    private static final int RAW_ACTIVITY_INDEX = INPUT_FILE_COLUMN_ORDER.ACTIVITY.ordinal();

    /**
     * 
     * @param inputReader
     * @param inputFileName
     * @return
     * @throws IOException
     */
    public static CSVReader convertToLinearAcceleration(CSVReader inputReader, String inputFileName) throws IOException {
        String linearOutputDir = (new File("").getAbsolutePath()).concat(Constants.FOLDER_NAME_LINEAR);
        new File(linearOutputDir).mkdirs();
        
        String fileNameComponents[] = inputFileName.split("/");
        inputFileName = fileNameComponents[fileNameComponents.length - 1];
        String newFilePath = linearOutputDir.concat("/" + inputFileName);

        String[] nextWriteLine = new String[INPUT_FILE_COLUMN_ORDER.values().length];
        nextWriteLine[RAW_TIME_INDEX] = Constants.DATA_COLUMN_TIME;
        nextWriteLine[INPUT_FILE_COLUMN_ORDER.RECORD_NUM.ordinal()] = Constants.DATA_COLUMN_RECORD_NO;
        nextWriteLine[INPUT_FILE_COLUMN_ORDER.AZIMUTH.ordinal()] = Constants.DATA_COLUMN_AZIMUTH;
        nextWriteLine[INPUT_FILE_COLUMN_ORDER.PITCH.ordinal()] = Constants.DATA_COLUMN_PITCH;
        nextWriteLine[INPUT_FILE_COLUMN_ORDER.ROLL.ordinal()] = Constants.DATA_COLUMN_ROLL;
        nextWriteLine[RAW_ACCEL_X_INDEX] = Constants.DATA_COLUMN_ACCEL_X;
        nextWriteLine[RAW_ACCEL_Y_INDEX] = Constants.DATA_COLUMN_ACCEL_Y;
        nextWriteLine[RAW_ACCEL_Z_INDEX] = Constants.DATA_COLUMN_ACCEL_Z;
        nextWriteLine[INPUT_FILE_COLUMN_ORDER.START_END.ordinal()] = Constants.DATA_COLUMN_START_END;
        nextWriteLine[RAW_ACTIVITY_INDEX] = Constants.DATA_COLUMN_ACTIVITY;

        CSVWriter writer = new CSVWriter(new FileWriter(newFilePath));
        writer.writeNext(nextWriteLine);
        writer.flush();

        List<String[]> readerLines = inputReader.readAll();
        if (readerLines.size() > 0) {
	        for (String[] nextLine : readerLines) {
	            if (!nextLine[RAW_TIME_INDEX].equals(Constants.DATA_COLUMN_TIME)) {
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
        CSVReader linearReader = new CSVReader(new FileReader(newFilePath), ',', '"', 0);
        return linearReader;
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
	        nextLine[RAW_ACCEL_X_INDEX] = String.valueOf(noNoiseX);
            
            float noGravY = (yAccel - GRAVITY[ACCEL_Y]);
            float noNoiseY = (Math.abs(noGravY) < noise) ? 0.0f : noGravY;
	        nextLine[RAW_ACCEL_Y_INDEX] = String.valueOf(noNoiseY);
            
            float noGravZ = (zAccel - GRAVITY[ACCEL_Z]);
            float noNoiseZ = (Math.abs(noGravZ) < noise) ? 0.0f : noGravZ;
	        nextLine[RAW_ACCEL_Z_INDEX] = String.valueOf(noNoiseZ);
	        
	        if (nextLine.length > (RAW_ACTIVITY_INDEX + 1)) {
		        String[] activityNameComponents = nextLine[RAW_ACTIVITY_INDEX].split(":");
		        String activityName = activityNameComponents[activityNameComponents.length - 1];
		        nextLine[RAW_ACTIVITY_INDEX] = activityName.replaceAll("[^\\dA-Za-z ]", "");
	        }
	
	        writer.writeNext(nextLine);
	        writer.flush();
    	} else {
			Logger.getLogger(AccelerationProcessing.class.getName()).log(Level.INFO, 
	        		"Cannot parse accel data at time: " + nextLine[RAW_TIME_INDEX], "");
        }
    }

}
