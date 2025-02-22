/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.temple.tan.mcianalysis.analyses;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import edu.temple.tan.mcianalysis.MCIAnalysis;
import edu.temple.tan.mcianalysis.utils.LogManager;
import edu.temple.tan.mcianalysis.utils.ToolkitUtils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        } catch (IOException | ParseException ex) {
        	LogManager.error(TaskTime.class, ex);
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
        Date startTime = null, endTime = null;
        
        if (readerContents.size() > 1) {
        	startTime = ToolkitUtils.getDateTime(readerContents.get(1)[0]);
        	endTime = ToolkitUtils.getDateTime(readerContents.get(readerContents.size() - 1)[0]);
        }

        if (endTime != null && startTime != null) {
            double durationInSec = ToolkitUtils.getSecondsBetweenDates(startTime, endTime, (readerContents.size() - 1));
            updateTaskTime(filePath, durationInSec, userID);
        }
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
        String csvFilePath = ToolkitUtils.initializeAnalysisOutputDirs(filePath, userID, "TaskTime");
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
        
        MCIAnalysis.taskTimeUtilized = true;
    }

}