package edu.temple.tan.mcianalysis.utils;

import java.util.Arrays;
import java.util.List;

public class Constants {
	
    public static final double SAMPLING_RATE = 34.4827586207;	// approx 29 times per second
    public static final String ANALYSIS_NAMESPACE = "edu.temple.tan.mcianalysis.analyses.";
    public static final String SIMPLE_TIME_FORMAT = "HH:mm:ss";
    
    public static final String CONFIG_FILE = "configuration.txt";
    public static final String ERROR_LOG = "Errors.txt";
    
    public static final String ANALYSIS_ACCELERATION = "Acceleration";
    public static final String ANALYSIS_DIRECTION = "Direction";
    public static final String ANALYSIS_PAUSE = "Pause";
    public static final String ANALYSIS_PAUSE_COUNT = "PauseCount";
    public static final String ANALYSIS_PAUSE_DURATION = "PauseDuration";
    public static final String ANALYSIS_STARTING_ACCELERATION = "StartingAcceleration";
    public static final String ANALYSIS_TASK_TIME = "TaskTime";

    public static final String FOLDER_NAME_FINAL = "/Final";
    public static final String FOLDER_NAME_INTERMEDIATE = "/Intermediate";
    public static final String FOLDER_NAME_LINEAR = "/Linear";
    public static final String FOLDER_NAME_ACCELERATION = ("/" + ANALYSIS_ACCELERATION);
    public static final String FOLDER_NAME_DIRECTION = ("/" + ANALYSIS_DIRECTION);
    public static final String FOLDER_NAME_PAUSE = ("/" + ANALYSIS_PAUSE);
    public static final String FOLDER_NAME_PAUSE_COUNT = ("/" + ANALYSIS_PAUSE_COUNT);
    public static final String FOLDER_NAME_PAUSE_DURATION = ("/" + ANALYSIS_PAUSE_DURATION);
    public static final String FOLDER_NAME_STARTING_ACCELERATION = ("/" + ANALYSIS_STARTING_ACCELERATION);
    public static final String FOLDER_NAME_TASK_TIME = ("/" + ANALYSIS_TASK_TIME);

    public static final String AGGREGATE_FILE_DIRECTION = "TaskDirections.csv";
    public static final String AGGREGATE_FILE_PAUSE_COUNT = "TaskPauseCount.csv";
    public static final String AGGREGATE_FILE_PAUSE_DURATION = "TaskPauseDuration.csv";
    public static final String AGGREGATE_FILE_TASK_TIME = "TaskTimes.csv";
    public static final String AGGREGATE_FILE_USERS = "UserSummary.csv";
    
    public static final List<String> AGGREGATE_FILES = Arrays.asList(
		AGGREGATE_FILE_DIRECTION, 
		AGGREGATE_FILE_PAUSE_COUNT, 
		AGGREGATE_FILE_PAUSE_DURATION, 
		AGGREGATE_FILE_TASK_TIME
	);
    
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
	
	public static enum PAUSE_AGGREGATE_COLUMN_ORDER {
		TASK,
		NUM_OF_PAUSES,
		TOTAL_TIME_PAUSED,
		AVG_PAUSE_DURATION
	}
	
	public static enum TASK_TIME_AGGREGATE_COLUMN_ORDER {
		TASK,
		TIME_IN_SEC
	}
	
	public static enum USER_AGGREGATE_COLUMN_ORDER {
		USER_NAME,
		TOTAL_TRIAL_TIME,
		TOTAL_PAUSE_COUNT,
		DISTINCT_PAUSE_COUNT,
		TOTAL_PAUSE_TIME
	}
	
	public static enum USER_AGGREGATE_SUBTASK_COLUMN_ORDER {
		COMPLETION_TIME,
		TOTAL_PAUSE_COUNT,
		DISTINCT_PAUSE_COUNT,
		TOTAL_PAUSE_TIME
	}
		
}