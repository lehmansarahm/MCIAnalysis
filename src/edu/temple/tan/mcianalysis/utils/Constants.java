package edu.temple.tan.mcianalysis.utils;

import java.util.Arrays;
import java.util.List;

public class Constants {
	
    public static final double SAMPLING_PERIOD = 34.4827586207;	// milliseconds ... approx 29 times per second
    public static final String ANALYSIS_NAMESPACE = "edu.temple.tan.mcianalysis.analyses.";
    public static final String SIMPLE_TIME_FORMAT_LONG = "HH:mm:ss.SSS";
    public static final String SIMPLE_TIME_FORMAT_SHORT = "HH:mm:ss";
    public static final String SIMPLE_DATE_TIME_FORMAT = "yyyyMMdd-HHmm";

    public static final String DELIMITER_FILENAME = "_";
    public static final String DELIMITER_FILEPATH = "/";
    public static final String DELIMITER_PARAMETER = ",";
    public static final String DELIMITER_PARAMETER_ASSIGNMENT = "=";
    public static final String DELIMITER_SPACE = "-";
    public static final String DELIMITER_TIMESTAMP = ":";
    public static final String DELIMITER_ACTIVITY_NOT_FOUND = "-1";
    
    public static final String CONFIG_FILE = "configuration.txt";
    public static final String ERROR_LOG = "Errors.txt";
    
    public static final String FLAG_START = "start";
    public static final String FLAG_QUIT = "quit";

    public static final double CALIBRATION_PAUSE_THRESHOLD_PERCENTAGE = 0.5d;
    public static final double CALIBRATION_SUDDEN_MOTION_THRESHOLD_PERCENTAGE = 0.9d;

    public static final String DATA_COLUMN_TIME = "Time";
    public static final String DATA_COLUMN_RECORD_NO = "Record No";
    public static final String DATA_COLUMN_AZIMUTH = "Azimuth";
    public static final String DATA_COLUMN_PITCH = "Pitch";
    public static final String DATA_COLUMN_ROLL = "Roll";
    public static final String DATA_COLUMN_ACCEL_X = "Accel-X";
    public static final String DATA_COLUMN_ACCEL_Y = "Accel-Y";
    public static final String DATA_COLUMN_ACCEL_Z = "Accel-Z";
    public static final String DATA_COLUMN_ACCEL_MAG = "Accel-Mag";
    public static final String DATA_COLUMN_SPEED = "Speed";
    public static final String DATA_COLUMN_SPEED_EMA = "Speed-EMA";
    public static final String DATA_COLUMN_SPEED_EMA_SCALED = "Speed-EMA-Scaled";
    public static final String DATA_COLUMN_START_END = "Start/Quit";
    public static final String DATA_COLUMN_ACTIVITY = "Activity";
    
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
    
    public static enum INTERM_FILE_COLUMN_ORDER { 
    	TIME, 
    	RECORD_NUM, 
    	AZIMUTH, 
    	PITCH, 
    	ROLL, 
    	ACCEL_X, 
    	ACCEL_Y, 
    	ACCEL_Z, 
    	ACCEL_MAG,
    	SPEED,
    	START_END, 
    	ACTIVITY 
	};
    
    public static enum PREPROC_FILE_COLUMN_ORDER { 
    	TIME, 
    	RECORD_NUM, 
    	AZIMUTH, 
    	PITCH, 
    	ROLL, 
    	ACCEL_X, 
    	ACCEL_Y, 
    	ACCEL_Z, 
    	ACCEL_MAG,
    	SPEED,
    	SPEED_EMA,
    	SPEED_EMA_SCALED,
    	START_END, 
    	ACTIVITY 
	};
    
    public static final String ANALYSIS_ACCELERATION = "Acceleration";
    public static final String ANALYSIS_DIRECTION = "Direction";
    public static final String ANALYSIS_PAUSE = "Pause";
    public static final String ANALYSIS_PAUSE_COUNT = "PauseCount";
    public static final String ANALYSIS_PAUSE_DURATION = "PauseDuration";
    public static final String ANALYSIS_STARTING_ACCELERATION = "StartingAcceleration";
    public static final String ANALYSIS_TASK_TIME = "TaskTime";

    public static final String FOLDER_NAME_FINAL = "/Final";
    public static final String FOLDER_NAME_INTERM_ACT_SPLIT = "/Phase2a-Intermediate-ActivitySplit";
    public static final String FOLDER_NAME_INTERM_CALIBRATIONS = "/Phase2b-Intermediate-Calibration";
    public static final String FOLDER_NAME_PREPROCESSING_LINEAR = "/Phase1a-Preprocessing-Linear";
    public static final String FOLDER_NAME_PREPROCESSING_LPFILTER = "/Phase1b-Preprocessing-LowPassFilter";
    public static final String FOLDER_NAME_PREPROCESSING_NORM = "/Phase1c-Preprocessing-Normalization";
    public static final String FOLDER_NAME_ACCELERATION = ("/" + ANALYSIS_ACCELERATION);
    public static final String FOLDER_NAME_DIRECTION = ("/" + ANALYSIS_DIRECTION);
    public static final String FOLDER_NAME_PAUSE = ("/" + ANALYSIS_PAUSE);
    public static final String FOLDER_NAME_PAUSE_COUNT = ("/" + ANALYSIS_PAUSE_COUNT);
    public static final String FOLDER_NAME_PAUSE_DURATION = ("/" + ANALYSIS_PAUSE_DURATION);
    public static final String FOLDER_NAME_STARTING_ACCELERATION = ("/" + ANALYSIS_STARTING_ACCELERATION);
    public static final String FOLDER_NAME_TASK_TIME = ("/" + ANALYSIS_TASK_TIME);

    // -------------------------------------------------------------------
    // -------------------------------------------------------------------
    //						AGGREGATE FILES
    // -------------------------------------------------------------------
    // -------------------------------------------------------------------

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

    // -------------------------------------------------------------------
    // -------------------------------------------------------------------
    //						CALIBRATION FILES
    // -------------------------------------------------------------------
    // -------------------------------------------------------------------
    
    public static enum CALIB_FILE_COLUMN_ORDER {
    	USERNAME,
    	AVERAGE_ACCELERATION,
    	AVERAGE_SPEED,
    	PAUSE_THRESHOLD,
    	SUDDEN_MOTION_THRESHOLD
    }

    // -------------------------------------------------------------------
    // -------------------------------------------------------------------
    //						CONFIG FILES
    // -------------------------------------------------------------------
    // -------------------------------------------------------------------
    
    public static enum CONFIG_FILE_COLUMN_ORDER {
    	USERNAME,
    	INPUT_FILE,
    	TASK,
    	ACCEL_PROCESSING,
    	CALIBRATED_THRESHOLDS,
    	ANALYSES
    };
    
    public static enum CONFIG_FILE_ANALYSIS_ORDER {
    	OPERATION_NAME,
    	PARAM1,
    	PARAM2
    }

    // -------------------------------------------------------------------
    // -------------------------------------------------------------------
    //						PAUSE ANALYSIS
    // -------------------------------------------------------------------
    // -------------------------------------------------------------------
    
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

    // -------------------------------------------------------------------
    // -------------------------------------------------------------------
    //						SUDDEN MOVEMENT ANALYSIS
    // -------------------------------------------------------------------
    // -------------------------------------------------------------------
    public static enum SUDDEN_MOVEMENT_OUTPUT_FILE_CONTENT_COLUMN_ORDER { 
		DIRECTION_CHANGE_TIME, 
		AXIS_1_STARTING_ACCEL, 
		AXIS_1_NEXT_ACCEL, 
		AXIS_1_ACCEL_DELTA, 
		AXIS_2_STARTING_ACCEL, 
		AXIS_2_NEXT_ACCEL, 
		AXIS_2_ACCEL_DELTA
	};
	
	public static enum SUDDEN_MOVEMENT_OUTPUT_FILE_TOTALS_COLUMN_ORDER { 
		NUMBER_OF_DIRECTION_CHANGES_LABEL,
		NUMBER_OF_DIRECTION_CHANGES_VALUE,
		AXIS_1_AVERAGE_ACCEL_CHANGE_LABEL,
		AXIS_1_AVERAGE_ACCEL_CHANGE_VALUE,
		AXIS_2_AVERAGE_ACCEL_CHANGE_LABEL,
		AXIS_2_AVERAGE_ACCEL_CHANGE_VALUE
	};
	
	public static enum SUDDEN_MOVEMENT_AGGREGATE_COLUMN_ORDER {
		TASK,
		NUMBER_OF_DIRECTION_CHANGES,
		AXIS_1_AVERAGE_ACCEL_CHANGE,
		AXIS_2_AVERAGE_ACCEL_CHANGE
	}

    // -------------------------------------------------------------------
    // -------------------------------------------------------------------
    //						TASK TIME ANALYSIS
    // -------------------------------------------------------------------
    // -------------------------------------------------------------------
	
	public static enum TASK_TIME_AGGREGATE_COLUMN_ORDER {
		TASK,
		TIME_IN_SEC
	}

    // -------------------------------------------------------------------
    // -------------------------------------------------------------------
    //						USER COMPARISON
    // -------------------------------------------------------------------
    // -------------------------------------------------------------------
	
	public static enum USER_AGGREGATE_COLUMN_ORDER {
		USER_NAME,
		TOTAL_TRIAL_TIME,
		TOTAL_PAUSE_COUNT,
		DISTINCT_PAUSE_COUNT,
		TOTAL_PAUSE_TIME,
		TOTAL_DIRECTION_CHANGES
	}
	
	public static enum USER_AGGREGATE_SUBTASK_COLUMN_ORDER {
		COMPLETION_TIME,
		TOTAL_PAUSE_COUNT,
		DISTINCT_PAUSE_COUNT,
		TOTAL_PAUSE_TIME,
		TOTAL_DIRECTION_CHANGES,
		AVG_ACCEL_CHANGE_AXIS_1,
		AVG_ACCEL_CHANGE_AXIS_2
	}
		
}