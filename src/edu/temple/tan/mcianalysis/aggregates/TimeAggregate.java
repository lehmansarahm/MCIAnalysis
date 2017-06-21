package edu.temple.tan.mcianalysis.aggregates;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.utils.Constants;
import edu.temple.tan.mcianalysis.utils.ToolkitUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class TimeAggregate {

	/**
	 * 
	 * @throws IOException
	 */
    public static void aggregateTimeCSV() throws IOException {
        String absolute_path = new File("").getAbsolutePath();
        File timeFolder = new File(absolute_path.concat(Constants.FOLDER_NAME_FINAL + Constants.FOLDER_NAME_TASK_TIME));
        if (timeFolder.exists()) aggregateTimeDirectory(timeFolder);
    }
    
    /**
     * 
     * @param folder
     * @throws IOException
     */
    private static void aggregateTimeDirectory(File folder) throws IOException {
        File[] listOfFiles = folder.listFiles();

        //loop through the directories found within
        for (int i = 0; i < listOfFiles.length; i++) {
            //if the file is a directory we want to go into it and aggregate the direction changes
            if (listOfFiles[i].isDirectory()) {
                File[] innerFiles = listOfFiles[i].listFiles();
                String total_write_line[] = new String[2];
                String writer_path = listOfFiles[i].getAbsolutePath();
                writer_path = writer_path.concat(Constants.DELIMITER_FILEPATH + Constants.AGGREGATE_FILE_TASK_TIME);
                CSVWriter writer = new CSVWriter(new FileWriter(writer_path));

                total_write_line[0] = "Task:";
                total_write_line[1] = "Time (Sec):";

                writer.writeNext(total_write_line);

                //loop through the inner directory
                for (int j = 0; j < innerFiles.length; j++) {
                	String fileName = innerFiles[j].getName();
                    if (!fileName.equals(Constants.AGGREGATE_FILE_TASK_TIME)) {
                        String task_name = ToolkitUtils.getActivityNameFromFileName(fileName);
                        
                        List<String[]> read_all = new ArrayList<String[]>();
                        CSVReader reader = new CSVReader(new FileReader(innerFiles[j].getAbsolutePath()), ',', '"', 0);
                        read_all = reader.readAll();
                        
                        int m = 0;
                        while(m < read_all.size() && !read_all.get(m)[0].equalsIgnoreCase("Configuration File Used:")) {
                            m++;
                        }
                        
                        total_write_line[0] = task_name;
                        total_write_line[1] = read_all.get(m-1)[1];
                        writer.writeNext(total_write_line);
                        reader.close();
                    }
                }
                writer.flush();
                writer.close();
            }
        }
    }
}