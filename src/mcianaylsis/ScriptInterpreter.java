package mcianaylsis;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

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
public static String[][] loadCommands()
{
    String [][] commands = new String[30][100];
    String fileName = "/configuration.txt";
    String filePath = new File("").getAbsolutePath();
    String fullFilePath = filePath.concat(fileName);
    int commandCount = 0;
    
    
    try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = 
                new FileReader(fullFilePath);
            
            // Always wrap FileReader in BufferedReader.
            BufferedReader bufferedReader = 
                new BufferedReader(fileReader);

            while((commands[commandCount][0] = bufferedReader.readLine()) != null) {
                commands[commandCount] = commands[commandCount][0].split(",");
                commandCount++;
            }   

            // Always close files.
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" + 
                fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println(
                "Error reading file '" 
                + fileName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
    
    return commands;
}
    
}
