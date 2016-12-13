/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mcianalysis;

import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author philipcoulomb
 */
public class WriteConfigurationCSV {

    public static void writeConfigurationSetupCSV() throws FileNotFoundException, IOException {
        String absolute_path = new File("").getAbsolutePath();
        absolute_path = absolute_path.concat("/Final");

        File directionFolder = new File(absolute_path);

        File[] listOfFiles = directionFolder.listFiles();

        //loop through the directories found within
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                File[] innerFiles = listOfFiles[i].listFiles();

                //loop through the inner directory
                for (int j = 0; j < innerFiles.length; j++) {

                    if (innerFiles[j].isDirectory()) {
                        File[] innerFiles2 = innerFiles[j].listFiles();

                        //loop through the inner directory
                        for (int k = 0; k < innerFiles2.length; k++) {

                            if (innerFiles2[k].isDirectory()) {
                                File[] innerMostFiles = innerFiles2[k].listFiles();

                                for (int m = 0; m < innerMostFiles.length; m++) {
                                    if (innerMostFiles[m].isDirectory()) {

                                    } else {
                                        String inner_file_path = innerMostFiles[m].getAbsolutePath();

                                        if (inner_file_path.contains(MCIAnaylsis.run_time)) {
                                            CSVWriter writer = new CSVWriter(new FileWriter(inner_file_path, true));

                                            int l = 0;

                                            while (MCIAnaylsis.configuration[l][0] != null) {
                                                writer.writeNext(MCIAnaylsis.configuration[l]);
                                                l++;
                                            }
                                            writer.close();
                                        }
                                    }
                                }

                            } else {
                                String file_path = innerFiles2[k].getAbsolutePath();
                                if (file_path.contains(MCIAnaylsis.run_time)) {
                                    CSVWriter writer = new CSVWriter(new FileWriter(file_path, true));

                                    int l = 0;

                                    while (MCIAnaylsis.configuration[l][0] != null) {
                                        writer.writeNext(MCIAnaylsis.configuration[l]);
                                        l++;
                                    }
                                    writer.close();
                                }
                            }
                        }
                    } else {
                        String outer_file_path = innerFiles[j].getAbsolutePath();
                        if (outer_file_path.contains(MCIAnaylsis.run_time)) {
                            CSVWriter writer = new CSVWriter(new FileWriter(outer_file_path, true));

                            int l = 0;

                            while (MCIAnaylsis.configuration[l][0] != null) {
                                writer.writeNext(MCIAnaylsis.configuration[l]);
                                l++;
                            }
                            writer.close();
                        }
                    }

                }
            }
        }
    }
}
