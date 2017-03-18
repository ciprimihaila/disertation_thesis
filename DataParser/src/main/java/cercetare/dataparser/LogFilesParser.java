/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.dataparser;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 * parser log files and insert in database
 *
 * @author ciprian
 */
public class LogFilesParser {

    private static final String PATH = "/home/ciprian/Desktop/new_data_amazon/";
    private static final String COLLECTION_NAME = "test5h2";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final File folder = new File(PATH);
        Map<String, Integer> categories = new HashMap<>();

        if (folder.listFiles().length == 0) {
            System.out.println("No log files");
        }

        for (File subfolder : folder.listFiles()) {
            for (File file : subfolder.listFiles()) {
                List<String> lines = FileUtils.readFile(PATH + "/" + subfolder.getName() + "/" + file.getName());
                for (String line : lines) {
                    try {
                        String[] parts = line.split("\t");
                        Map<String, Object> rowContent = new HashMap();
                        rowContent.put(Constants.DATETIME_KEY, formatter.parse(parts[0]));
                        JSONObject json = new JSONObject(parts[1]);
                        String vertical = json.getString(Constants.CATEGORY_KEY);

                        if (!vertical.equals("nodata")) {
                            String appKey = json.getString("appKey");
                            String appInstanceUid = json.getString("appInstanceUid");
                            rowContent.put(Constants.APP_KEY, appKey);
                            rowContent.put(Constants.USER_KEY, appInstanceUid);
                            Integer index = categories.get(vertical);
                            if (index == null) {
                                index = categories.size();
                                categories.put(vertical, index);
                            }
                            rowContent.put(Constants.CATEGORY_KEY, vertical);
                            DbUtils.insert(Constants.DB_NAME, COLLECTION_NAME, rowContent);
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(LogFilesParser.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Exception e) {
                        if (!e.getMessage().equals("JSONObject[\"vertical\"] not found.")) {
                            System.out.println("file: " + file.getName());
                            System.out.println("line: " + line);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        DbUtils.closeClient();
    }
}
