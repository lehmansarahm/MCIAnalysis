package edu.temple.tan.mcianalysis.preprocessing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.ToolkitUtils;
import edu.temple.tan.mcianalysis.utils.Constants.PREPROC_FILE_COLUMN_ORDER;

public class NormalizationProcessing {
	
	private static int RAW_SPEED_EMA_INDEX = PREPROC_FILE_COLUMN_ORDER.SPEED_EMA.ordinal();
	private static int RAW_SPEED_SCALED_INDEX = PREPROC_FILE_COLUMN_ORDER.SPEED_EMA_SCALED.ordinal();
	
	/**
	 * 
	 * @param inputReader
	 * @param inputFileName
	 * @return
	 * @throws IOException
	 */
    public static String normalize(CSVReader inputReader, String inputFileName) throws IOException {
        String filterOutputDir = (new File("").getAbsolutePath()).concat(Constants.FOLDER_NAME_PREPROCESSING_NORM);
        new File(filterOutputDir).mkdirs();
        
        String fileNameComponents[] = inputFileName.split("/");
        inputFileName = fileNameComponents[fileNameComponents.length - 1];
        String newFilePath = filterOutputDir.concat("/" + inputFileName);

        CSVWriter writer = new CSVWriter(new FileWriter(newFilePath));
        writer.writeNext(ToolkitUtils.getPreprocHeaderLine());
        writer.flush();
        
        double[] minMax = new double[] { 0.0d, 0.0d };
        List<String[]> minMaxLines = inputReader.readAll();
        if (minMaxLines.size() > 0) {
        	// copy the reader contents so we can go through them a second time
        	List<String[]> contentLines = new ArrayList<String[]>(minMaxLines);
        	
        	// iterate through reader contents first time to find min / max speed
	        for (String[] nextLine : minMaxLines) {
	            if (!ToolkitUtils.isHeaderLine(nextLine)) {
		            if (nextLine.length > RAW_SPEED_EMA_INDEX) {
		            	double speed = Double.parseDouble(nextLine[RAW_SPEED_EMA_INDEX]);
		            	if (speed < minMax[0]) minMax[0] = speed;
		            	else if (speed > minMax[1]) minMax[1] = speed;
		            }
		            if (nextLine.length > (PREPROC_FILE_COLUMN_ORDER.START_END.ordinal()) && 
		            		nextLine[PREPROC_FILE_COLUMN_ORDER.START_END.ordinal()].equalsIgnoreCase("quit")) 
		            	break;
	            }
	        }
	        
	        // iterate through a second time to compute normalized values
	        for (String[] nextLine : contentLines) {
	            if (!ToolkitUtils.isHeaderLine(nextLine)) {
		            if (nextLine.length > RAW_SPEED_EMA_INDEX)
		            	writeNormalization(writer, nextLine, minMax[0], minMax[1]);
		            if (nextLine.length > (PREPROC_FILE_COLUMN_ORDER.START_END.ordinal()) && 
		            		nextLine[PREPROC_FILE_COLUMN_ORDER.START_END.ordinal()].equalsIgnoreCase("quit")) 
		            	break;
	            }
	        }
        } else {
			Logger.getLogger(NormalizationProcessing.class.getName()).log(Level.INFO, 
	        		"No reader lines found in input file: " + inputFileName, "");
        }
        
        writer.close();
        return newFilePath;
    }
    
    /**
     * 
     * @param writer
     * @param nextLine
     */
    private static void writeNormalization(CSVWriter writer, String[] nextLine, double min, double max) {
    	double rawSpeed = Double.parseDouble(nextLine[RAW_SPEED_EMA_INDEX]);
    	double normalizedSpeed = (rawSpeed - min) / (max - min);
    	nextLine[RAW_SPEED_SCALED_INDEX] = Double.toString(normalizedSpeed);
    	writer.writeNext(nextLine);
    }

}