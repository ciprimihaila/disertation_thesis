/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.dataparser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ciprian
 */
public class FileUtils {

    /**
     * read file and return lines
     *
     * @param fileName
     * @return
     */
    public static List<String> readFile(String fileName) {
        List<String> list = new ArrayList<>();
        BufferedReader br = null;
        try {

            String sCurrentLine;

            br = new BufferedReader(new FileReader(fileName));

            while ((sCurrentLine = br.readLine()) != null) {
                list.add(sCurrentLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return list;
    }

    public static void writeToFile(String fileName, List<String> lines) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(fileName));
            for (int i = 0; i < lines.size(); i++) {
                bw.write(lines.get(i));
                bw.newLine();
            }

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    public static void writeToFile(String fileName, String line) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(fileName, true));
            bw.write(line);
            bw.newLine();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

}
