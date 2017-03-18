/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.tests;

import cercetare.newalgorithm.Preprocessing;
import cercetare.std.recomandation.StandardCFPrediction;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.bson.Document;

/**
 *
 * @author ciprian
 */
public class TestStandardRecomandationSingle {

    private static final String app = "DDE5A5B2-E3F2-4725-94B9-0E16AA7FEC5D";
    private static final String user = "6BD0D223-A39C-4524-940A-E2AD8A53AF33";//"0002F422-2102-46CE-8D24-5135293A8AF8";
    private static final String collection = "test5h2";
    private static final int historySize = 3;

    public static void main(String[] args) {
        List objs = Preprocessing.loadCategoriesForUserAndApp(app, user, collection);

        System.out.println("Debugging--------------");
        for (int i = 0; i < objs.size(); i++) {
            Document objDoc = ((Document) (objs.get(i)));
            System.out.println(objDoc.get("vertical").toString()
                    + " " + ((Date) objDoc.get("datetime")));
        }

        Map<String, Integer> categories = Preprocessing.extractCategories(app, user, collection);
        StandardCFPrediction cfPrediction = new StandardCFPrediction(categories, "");
        cfPrediction.createHistoryMatrix(objs, historySize);

    }
}
