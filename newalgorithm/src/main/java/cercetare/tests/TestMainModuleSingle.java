/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.tests;

import cercetare.newalgorithm.MainModuleI;
import cercetare.newalgorithm.MainModulePrepCategories;
import cercetare.newalgorithm.Preprocessing;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.bson.Document;

/**
 *
 * @author ciprian
 */
public class TestMainModuleSingle {

    private static final String APP = "DDE5A5B2-E3F2-4725-94B9-0E16AA7FEC5D";
    private static final String USER = "6BD0D223-A39C-4524-940A-E2AD8A53AF33";//0002F422-2102-46CE-8D24-5135293A8AF8";//"6BD0D223-A39C-4524-940A-E2AD8A53AF33";
    private static final String COLLECTION = "test5h2";
    private static final int HISTORY_SIZE = 3;

    private static final int navigationTypesNr = 4;
    private static final int timeSlotNr = 10;

    public static void main(String[] args) {

        Map<String, Integer> distinctCategories = Preprocessing.extractCategories(APP, USER, COLLECTION);

        List data = Preprocessing.loadCategoriesForUserAndApp(APP, USER, COLLECTION);

        System.out.println("Debugging--------------");
        for (int i = 0; i < data.size(); i++) {
            Document objDoc = ((Document) (data.get(i)));
            System.out.println(objDoc.get("vertical").toString()
                    + " " + ((Date) objDoc.get("datetime")));
        }

        MainModuleI mainModule = new MainModulePrepCategories(data, distinctCategories,
                HISTORY_SIZE, timeSlotNr, navigationTypesNr, "", true, true);
        mainModule.sequencialPrediction();

    }
}
