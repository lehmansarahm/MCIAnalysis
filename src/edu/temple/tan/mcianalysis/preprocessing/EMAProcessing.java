package edu.temple.tan.mcianalysis.preprocessing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.ToolkitUtils;
import edu.temple.tan.mcianalysis.utils.Constants.INPUT_FILE_COLUMN_ORDER;
import edu.temple.tan.mcianalysis.utils.Constants.INTERM_FILE_COLUMN_ORDER;
import edu.temple.tan.mcianalysis.utils.Constants.PREPROC_FILE_COLUMN_ORDER;

public class EMAProcessing {
	
	private static final double EMA_ALPHA = 0.5d;
	private static final int EMA_SAMPLE_SIZE = 20;

    /**
     * 
     * @param inputReader
     * @param inputFileName
     * @return
     * @throws IOException
     */
    public static String convertToMovingAverage(CSVReader inputReader, String inputFileName) throws IOException {
        String filterOutputDir = (new File("").getAbsolutePath()).concat(Constants.FOLDER_NAME_PREPROCESSING_LPFILTER);
        new File(filterOutputDir).mkdirs();
        
        String fileNameComponents[] = inputFileName.split("/");
        inputFileName = fileNameComponents[fileNameComponents.length - 1];
        String newFilePath = filterOutputDir.concat("/" + inputFileName);

        CSVWriter writer = new CSVWriter(new FileWriter(newFilePath));
        writer.writeNext(ToolkitUtils.getPreprocHeaderLine());
        writer.flush();
        
        ExponentialMovingAverage ema = new ExponentialMovingAverage(EMA_ALPHA, EMA_SAMPLE_SIZE);
        List<String[]> readerLines = inputReader.readAll();
        if (readerLines.size() > 0) {
	        for (String[] nextLine : readerLines) {
	            if (!ToolkitUtils.isHeaderLine(nextLine)) {
		            if (nextLine.length > Constants.INPUT_FILE_COLUMN_ORDER.ACCEL_Z.ordinal())
		            	writeMovingAverage(writer, ema, nextLine);
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
     * @param ema
     * @param nextLine
     * @throws IOException
     */
    private static void writeMovingAverage(CSVWriter writer, ExponentialMovingAverage ema, String[] nextLine) throws IOException {
    	if (ToolkitUtils.isNumeric(nextLine[PREPROC_FILE_COLUMN_ORDER.SPEED.ordinal()].trim())) {
            String[] writeLine = new String[PREPROC_FILE_COLUMN_ORDER.values().length];
            
        	writeLine[PREPROC_FILE_COLUMN_ORDER.TIME.ordinal()] = 
        			nextLine[INTERM_FILE_COLUMN_ORDER.TIME.ordinal()];
        	writeLine[PREPROC_FILE_COLUMN_ORDER.RECORD_NUM.ordinal()] = 
        			nextLine[INTERM_FILE_COLUMN_ORDER.RECORD_NUM.ordinal()];
        	writeLine[PREPROC_FILE_COLUMN_ORDER.AZIMUTH.ordinal()] = 
        			nextLine[INTERM_FILE_COLUMN_ORDER.AZIMUTH.ordinal()];
        	writeLine[PREPROC_FILE_COLUMN_ORDER.PITCH.ordinal()] = 
        			nextLine[INTERM_FILE_COLUMN_ORDER.PITCH.ordinal()];
        	writeLine[PREPROC_FILE_COLUMN_ORDER.ROLL.ordinal()] = 
        			nextLine[INTERM_FILE_COLUMN_ORDER.ROLL.ordinal()];

        	writeLine[PREPROC_FILE_COLUMN_ORDER.ACCEL_X.ordinal()] = 
        			nextLine[INTERM_FILE_COLUMN_ORDER.ACCEL_X.ordinal()];
        	writeLine[PREPROC_FILE_COLUMN_ORDER.ACCEL_Y.ordinal()] = 
        			nextLine[INTERM_FILE_COLUMN_ORDER.ACCEL_Y.ordinal()];
        	writeLine[PREPROC_FILE_COLUMN_ORDER.ACCEL_Z.ordinal()] = 
        			nextLine[INTERM_FILE_COLUMN_ORDER.ACCEL_Z.ordinal()];
        	writeLine[PREPROC_FILE_COLUMN_ORDER.ACCEL_MAG.ordinal()] = 
        			nextLine[INTERM_FILE_COLUMN_ORDER.ACCEL_MAG.ordinal()];
        	
        	String rawSpeed = nextLine[INTERM_FILE_COLUMN_ORDER.SPEED.ordinal()];
        	writeLine[PREPROC_FILE_COLUMN_ORDER.SPEED.ordinal()] = rawSpeed;

        	double speed = Double.parseDouble(rawSpeed);
        	writeLine[PREPROC_FILE_COLUMN_ORDER.SPEED_EMA.ordinal()] = 
        			Double.toString(ema.getAverage(speed));
        	
        	writeLine[PREPROC_FILE_COLUMN_ORDER.START_END.ordinal()] = 
        			nextLine[INTERM_FILE_COLUMN_ORDER.START_END.ordinal()];
        	writeLine[PREPROC_FILE_COLUMN_ORDER.ACTIVITY.ordinal()] = 
        			nextLine[INTERM_FILE_COLUMN_ORDER.ACTIVITY.ordinal()];
        	
	        writer.writeNext(writeLine);
	        writer.flush();
    	} else {
			Logger.getLogger(AccelerationProcessing.class.getName()).log(Level.INFO, 
	        		"Cannot parse speed data at time: " 
        				+ nextLine[PREPROC_FILE_COLUMN_ORDER.TIME.ordinal()], "");
        }
    }
    
}