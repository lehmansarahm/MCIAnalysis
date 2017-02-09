package edu.temple.tan.mcianalysis.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import edu.temple.tan.mcianalysis.MCIAnalysis;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author philipcoulomb
 */
public class ScriptInterpreter {

//-----------------------------------------------------------------
// The loadCommands method will read in the configuration file
// and load in the specific behavior the program has been 
// asked to perform
//-----------------------------------------------------------------
    public static String[][] loadCommands() {
        String[][] commands = new String[30][100];
        String fileName = "/configuration.txt";
        char[] tempCommand = new char[1000];
        String[] commandComponents = new String[30];
        String[] innerCommands = new String[3];
        String line;

        String filePath = new File("").getAbsolutePath();
        String fullFilePath = filePath.concat(fileName);
        Scanner s = null;
        int commandCount = 0;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader
                    = new FileReader(fullFilePath);

                        // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader
                    = new BufferedReader(fileReader);
            
            

            char[] fullFileContents = new char[1024];
            int i = 1;
            int r;
            
            MCIAnalysis.configuration[0][0] = "Configuration File Used:";
            while ((line = bufferedReader.readLine()) != null) {
    // keep appending last line read to buffer
                MCIAnalysis.configuration[i][0] = line;
                i++;
            }
            bufferedReader.close();
            
            fileReader
                    = new FileReader(fullFilePath);
            bufferedReader = new BufferedReader(fileReader);
            i = 0;
            
            

            
            //--------------------------------------------------------------
            // The configuration script will be comma separated values so 
            // this will build the command list
            //--------------------------------------------------------------
            while ((r = bufferedReader.read()) != -1) {
                fullFileContents[i] = (char) r;
                i++;
                //commands[commandCount] = commands[commandCount][0].split(",");
                //commandCount++;
            }
            fullFileContents[i] = '\0';

            i = 0;
            int j = 0;
            boolean firstBraceRead = false;
            int k = 0;

           
            while (fullFileContents[i] != '\0') {
                if (!firstBraceRead) {
                    firstBraceRead = true;
                }
                if (fullFileContents[i] == '{' && firstBraceRead) {
                    firstBraceRead = true;
                } else if (fullFileContents[i] == '}') {
                    commands[k][0] = String.valueOf(tempCommand);
                    Arrays.fill(tempCommand, '\0');
                    k++;
                    j = 0;
                } else {
                    if(fullFileContents[i] != '\r')
                    {
                    tempCommand[j] = fullFileContents[i];
                    }
                    else
                    {
                        tempCommand[j] = '\n';
                    }
                    j++;
                }
                i++;
            }

            i = 0;
            k = 4;
            while (commands[i][0] != null) {
                commandComponents = commands[i][0].split("\n");

                while (j < commandComponents.length) {
                    if (!(commandComponents[j].equalsIgnoreCase(""))) {
                        innerCommands = commandComponents[j].split("=");

                        if (innerCommands.length == 2) {
                            innerCommands[0] = innerCommands[0].trim();
                            innerCommands[1] = innerCommands[1].trim();

                            if (innerCommands[0].equalsIgnoreCase("Username")) {
                                commands[i][0] = innerCommands[1];
                            } else if (innerCommands[0].equalsIgnoreCase("File")) {
                                commands[i][1] = innerCommands[1];
                            } else if (innerCommands[0].equalsIgnoreCase("Task")) {
                                commands[i][2] = innerCommands[1];
                            } else if (innerCommands[0].equalsIgnoreCase("Acceleration Process")) {
                                commands[i][3] = innerCommands[1];
                            } else if (innerCommands[0].equalsIgnoreCase("Analysis")) {
                                commands[i][k] = innerCommands[1];
                                k++;
                            }
                        }
                    }
                    j++;
                }
                i++;
                j=0;
                k=4;
            }
            // Always close files.
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '"
                    + fullFilePath + "'");
        } catch (IOException ex) {
            System.out.println(
                    "Error reading file '"
                    + fullFilePath + "'");
            // Or we could just do this: 
            // ex.printStackTrace();
        }

        return commands;
    }

}
