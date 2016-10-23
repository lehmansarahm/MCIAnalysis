/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcianaylsis;
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
 * @author philipcoulomb
 * Initial Commit to setup the Github.
 */
public class MCIAnaylsis {

    public static Set<String> requested_activities_set = new HashSet<>();
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
        
        while(i < commands.length && commands[i].length != 100)
        {
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
            CSVReader reader = new CSVReader(new FileReader(target_file), ',' , '"' , 0);
            
            if(!target_activity.equalsIgnoreCase("All"))
            {
                all_activities_csv_reader.add(ActivitySplit.generateActivitySpecificCSV(reader, user_id,target_activity));
            }
            else
            {
                all_activities_csv_reader = ActivitySplit.generateCSVForAllActivities(reader,user_id);
            }
            
            //----------------------------------------
            // k=3 because the 3rd element of the
            // multi dimensional array will contain
            // the first of the selected analysis.
            //----------------------------------------
            int k = 3;
            int length = commands[i].length;
            
            while(k<length)
            {
                int j = 0;
                String class_name = new String();
                class_name = commands[i][k].toLowerCase().trim();
                class_name = class_name.substring(0, 1).toUpperCase().concat(class_name.substring(1));
                class_name = "mcianaylsis.".concat(class_name);
                Class analysis_class = Class.forName(class_name);
                Object class_object = (Object) analysis_class.newInstance();
                Method analysis_method = analysis_class.getMethod("begin_analysis", String.class);
                
                while(j<all_activities_csv_reader.size())
                {
                    analysis_method.invoke(class_object, all_activities_csv_reader.get(j));
                    j++;
                }
            k++;
            }
            
            i++;
        }
    }
    
}
