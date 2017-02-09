package edu.temple.tan.mcianalysis.utils;

public class Constants {
	
    public static final double SAMPLING_RATE = 34.4827586207;	// approx 29 times per second
    public static final String ANALYSIS_NAMESPACE = "edu.temple.tan.mcianalysis.analyses.";
    
    public static final String FINAL_OUTPUT_FOLDER_NAME = "/Final";
    public static final String INTERMEDIATE_FOLDER_NAME = "/Intermediate";
    
    public static enum CONFIG_FILE_COLUMN_ORDER {
    	USERNAME,
    	INPUT_FILE,
    	TASK,
    	ACCEL_PROCESSING,
    	ANALYSES
    };
    
    public static enum CONFIG_FILE_ANALYSIS_ORDER {
    	OPERATION_NAME,
    	PARAM1,
    	PARAM2
    }
    
    public static enum INPUT_FILE_COLUMN_ORDER { 
    	TIME, 
    	RECORD_NUM, 
    	AZIMUTH, 
    	PITCH, 
    	ROLL, 
    	ACCEL_X, 
    	ACCEL_Y, 
    	ACCEL_Z, 
    	START_END, 
    	ACTIVITY 
	};
	
	public static enum PAUSE_OUTPUT_FILE_CONTENT_COLUMN_ORDER { 
		START_TIME, 
		START_NUM, 
		END_TIME, 
		END_NUM, 
		DURATION
	};
	
	public static enum PAUSE_OUTPUT_FILE_TOTALS_COLUMN_ORDER { 
		PAUSE_NUM_LABEL, 
		PAUSE_NUM_VALUE, 
		TOTAL_PAUSE_TIME_LABEL, 
		TOTAL_PAUSE_TIME_VALUE, 
		AVG_PAUSE_TIME_LABEL, 
		AVG_PAUSE_TIME_VALUE 
	};
		
}