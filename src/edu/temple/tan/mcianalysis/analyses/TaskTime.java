/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.temple.tan.mcianalysis.analyses;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.MCIAnalysis;
import edu.temple.tan.mcianalysis.utils.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskTime implements Analysis {

	/**
	 * 
     * @param filePath
     * @param userID
     * @param param1
     * @param param2
	 */
    @Override
    public void beginAnalysis(String filePath, String userID, String param1, String param2) {
        try {
        	CSVReader reader = new CSVReader(new FileReader(filePath), ',', '"', 0);
            processTaskTime(reader, userID,filePath);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TaskTime.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TaskTime.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(TaskTime.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 
     * @param reader
     * @param userID
     * @param filePath
     * @throws IOException
     * @throws ParseException
     */
    private void processTaskTime(CSVReader reader, String userID, String filePath) throws IOException, ParseException {
        List<String[]> readerContents = reader.readAll();
        
        long durationInMS = 0;
        Date startTime = null, endTime = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.SIMPLE_TIME_FORMAT_LONG);
            startTime = dateFormat.parse(readerContents.get(1)[0]);
            endTime = dateFormat.parse(readerContents.get(readerContents.size() - 1)[0]);
        } catch (ParseException ex) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.SIMPLE_TIME_FORMAT_SHORT);
            startTime = dateFormat.parse(readerContents.get(1)[0]);
            endTime = dateFormat.parse(readerContents.get(readerContents.size() - 1)[0]);
        } finally {
            durationInMS = endTime.getTime() - startTime.getTime();
        }
        
        double taskTimeInSec = (((double)durationInMS) / 1000.0);
        double durationInSec = (((taskTimeInSec == Math.floor(taskTimeInSec)) && !Double.isInfinite(taskTimeInSec))) 
        		? ((Constants.SAMPLING_PERIOD * (readerContents.size() - 1)) / 1000.0) : taskTimeInSec;

        updateTaskTime(filePath, durationInSec, userID);
    }

    /**
     * 
     * @param filePath
     * @param taskDurInSec
     * @param userID
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void updateTaskTime(String filePath, double taskDurInSec, String userID) 
    							throws FileNotFoundException, IOException {
        String csvFilePath = initialFileSetup(filePath, userID);
        List<String[]> fileContents = new ArrayList<String[]>();
        
        double totalSec = 0, avgSec = 0;
        double totalSqFromMean = 0, stdDev = 0;
        
        try {
        	CSVReader reader = new CSVReader(new FileReader(csvFilePath), ',', '"', 0);
        	fileContents = reader.readAll();
            reader.close();

            fileContents.remove(fileContents.size() - 1);
            fileContents.add(fileContents.size() - 1, 
            		new String[] { userID, String.valueOf(taskDurInSec) });
            
            for (int i = 1; i < fileContents.size(); i++) {
                totalSec += Double.parseDouble(fileContents.get(i)[1]);
                totalSqFromMean += 
                		(Math.pow((Double.parseDouble(fileContents.get(i)[1]) - avgSec), 2));
            }
            
            int recordCount = (fileContents.size() - 1);
            avgSec = totalSec / (double)(recordCount - 1);
            stdDev = Math.sqrt(totalSqFromMean / (double)(recordCount - 2));
        } catch (FileNotFoundException ex) {
        	fileContents.add(new String[] { "User ID", "Task Time" });
            fileContents.add(new String[] { userID, String.valueOf(taskDurInSec) });
            totalSec = taskDurInSec;
            avgSec = taskDurInSec;
        }

        String[] totalLine = new String[6];
        totalLine[0] = "Total Task Time:";
        totalLine[2] = "Average Task Time:";
        totalLine[4] = "Task Time Sample Standard Deviation:";
        totalLine[1] = String.valueOf(totalSec);
        totalLine[3] = String.valueOf(avgSec);
        totalLine[5] = String.valueOf(stdDev);
        fileContents.add(totalLine);

        CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath));
        writer.writeAll(fileContents);
        writer.close();
        
		/*new_line = new String[2];
		total_line = new String[6];
		
		new_line[0] = "User ID:";
		new_line[1] = "Task Time (Seconds):";
		writer.writeNext(new_line);
		
		new_line[0] = userID;
		new_line[1] = String.valueOf(taskDurInSec);
		writer.writeNext(new_line);
		
		total_line[0] = "Total Task Time:";
		total_line[1] = String.valueOf(taskDurInSec);
		total_line[2] = "Average Task Time:";
		total_line[3] = String.valueOf(taskDurInSec);
		total_line[4] = "Task Time Sample Standard Deviation:";
		total_line[5] = "0";
		
		writer.writeNext(total_line);
		    
		writer.close();*/
        
        MCIAnalysis.taskTimeUtilized = true;
    }

    /**
     * The initialFileSetup method is responsible for ensuring the proper
     * directories are setup and that the CSVWriter has a valid and clear filename
     * to write to.
     * 
     * @param filePath
     * @param userID
     * @return
     */
    private String initialFileSetup(String filePath, String userID) {
        String[] pathComponents = filePath.split("/");
        String targetFileName = pathComponents[pathComponents.length - 1];
        
        String absolutePath = new File("").getAbsolutePath();
        absolutePath = absolutePath.concat(Constants.FOLDER_NAME_FINAL);
        new File(absolutePath).mkdirs();
        
        absolutePath = absolutePath.concat(Constants.FOLDER_NAME_TASK_TIME);
        new File(absolutePath).mkdirs();
        
        absolutePath = absolutePath.concat("/" + userID);
        new File(absolutePath).mkdirs();
        
        absolutePath = absolutePath.concat("/");
        absolutePath = absolutePath.concat(targetFileName);
        return absolutePath;
    }

}