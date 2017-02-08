/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.temple.tan.mcianalysis;

import com.opencsv.CSVReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author philipcoulomb Initial Commit to setup the Github.
 */
public class MCIAnalysis {

    public static Set<String> requested_activities_set = new HashSet<>();
    public static String[][] configuration = new String[1000][1];
    public static String run_time;
    public static String acceleration_processing;
    public static boolean direction_utilized = false;
    public static boolean pause_utilized = false;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException {
        // TODO code application logic here
        String[][] commands;
        String target_file;
        String target_activity;
        String filePath = new File("").getAbsolutePath();
        String user_id;
        List<String> all_activities_csv_reader = new ArrayList<String>();

        CSVReader activity_csv_reader;

        commands = ScriptInterpreter.loadCommands();

        int i = 0;

        while (i < commands.length && commands[i][0] != null) {
            //------------------------------------------------------
            // The [i][0th] entry will always be the user id
            //------------------------------------------------------
            user_id = commands[i][0].trim();
            //------------------------------------------------------
            // The [i][1th] entry will always be the target files
            // relative file path
            //------------------------------------------------------
            target_file = filePath.concat(commands[i][1].toString().trim());

            //------------------------------------------------------
            // The [i][2]th entry will always be the targeted
            // activity
            //------------------------------------------------------
            target_activity = commands[i][2].toString().trim();

            //------------------------------------------------------------
            // CSVReader reader is one of two arguments to be passed to 
            // the analysis methods
            //------------------------------------------------------------
            CSVReader reader = new CSVReader(new FileReader(target_file), ',', '"', 0);
            
            int acceleration_process = Integer.valueOf(commands[i][3]);
            
            switch(acceleration_process)
            {
                case 0:
                    acceleration_processing = "Raw";
                    break;
                case 1:
                    acceleration_processing = "Linear";
                reader = AccelerationProcessing.convertToLinearAcceleration(reader, commands[i][1].toString().trim());
                break;
                default:
                    break;
            }
            if (!target_activity.equalsIgnoreCase("All")) {
                all_activities_csv_reader.add(ActivitySplit.generateActivitySpecificCSV(reader, user_id, target_activity));
            } else {
                all_activities_csv_reader = ActivitySplit.generateCSVForAllActivities(reader, user_id);
            }

            //----------------------------------------
            // k=3 because the 3rd element of the
            // multi dimensional array will contain
            // the first of the selected analysis.
            //----------------------------------------
            int k = 4;
            int length = commands[i].length;

            while (commands[i][k]!= null) {
                int j = 0;
                String analysis[];
                String param1 = null;
                String param2 = null;
                String class_name = new String();
                class_name = commands[i][k].trim();

                analysis = class_name.split(":");

                if (analysis.length > 2 || analysis.length == 2) {
                    param1 = analysis[1];
                }
                if (analysis.length > 3 || analysis.length == 3) {
                    param2 = analysis[2];
                }

                class_name = analysis[0];

                class_name = "edu.temple.tan.mcianalysis.".concat(class_name);
                Class analysis_class = Class.forName(class_name);
                Object class_object = (Object) analysis_class.newInstance();
                Method analysis_method = analysis_class.getMethod("begin_analysis", String.class, String.class, String.class, String.class);

                while (j < all_activities_csv_reader.size()) {
                    try{
                    analysis_method.invoke(class_object, all_activities_csv_reader.get(j), user_id, param1, param2);
                    }
                    catch(IllegalArgumentException e)
                    {
                        continue;
                    }
                    j++;
                }
                k++;
            }
            all_activities_csv_reader.clear();
            i++;
        }

       WriteConfigurationCSV.writeConfigurationSetupCSV();
       
       if(direction_utilized)
            DirectionAggregate.aggregateDirectionCSV();
       if(pause_utilized)
        PauseAggregate.aggregatePauseCSV();
    }

}
