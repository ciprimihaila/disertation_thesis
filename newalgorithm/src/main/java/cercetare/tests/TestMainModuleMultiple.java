/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.tests;

import cercetare.dataparser.Constants;
import cercetare.dataparser.FileUtils;
import cercetare.newalgorithm.MainModuleI;
import cercetare.newalgorithm.MainModulePrepCategories;
import cercetare.newalgorithm.Preprocessing;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ciprian
 */
public class TestMainModuleMultiple {

    public static String collectionName = "test5h2";
    public static String app = "DDE5A5B2-E3F2-4725-94B9-0E16AA7FEC5D";
    public static int userMinEntries = 200;

    public static int historySize = 10;

    public static int navigationTypesNr = 4;
    public static int timeSlotNr = 10;

    public static void main(String[] args) {

        //result filename
        String fileName = Constants.RESULT_PATH + Constants.RESULT_FILE_NAME;

        List<String> users = Preprocessing.getUsersForApp(app, collectionName, userMinEntries);
        Collections.sort(users);

        int good = 0;
        int all = 0;
        for (String user : users) {

            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>Start for: ");

            FileUtils.writeToFile(fileName, ">>>>>>>>>>>>>>>>>>>>>>>Start for: ");
            FileUtils.writeToFile(fileName, "App: " + app);
            FileUtils.writeToFile(fileName, "User: " + user);
            System.out.println("App: " + app);
            System.out.println("User: " + user);

            Map<String, Integer> categories = Preprocessing.extractCategories(app, user, collectionName);

            List objs = Preprocessing.loadCategoriesForUserAndApp(app, user, collectionName);

            MainModuleI mainModule = new MainModulePrepCategories(objs, categories,
                    historySize, timeSlotNr, navigationTypesNr, fileName, true, true);

            double before = System.currentTimeMillis();
            double predRes = mainModule.sequencialPrediction();
            double after = System.currentTimeMillis();
            FileUtils.writeToFile(fileName, "Time (s): " + (after - before) / 1000);

            if (predRes > 0) {
                if (predRes >= 0.5) {
                    good++;
                }
                all++;
            }

            System.out.println(">>>>>>>>>>>>>>>>>>> End");
            FileUtils.writeToFile(fileName, ">>>>>>>>>>>>>>>>>>> End");
        }

        System.out.println("entries: " + all + " good: " + good);
        FileUtils.writeToFile(fileName, "entries: " + all + " good: " + good);
    }
}
