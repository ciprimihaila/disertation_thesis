/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.tests.modules;

import cercetare.newalgorithm.ContinueProbModule;
import cercetare.newalgorithm.NavigTypeProbModule;
import cercetare.newalgorithm.Preprocessing;
import cercetare.newalgorithm.ResultModule;
import cercetare.newalgorithm.TimeSlotProbModule;
import cercetare.newalgorithm.Tree;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ciprian
 */
public class TestResultModule {

    public static String app = "DDE5A5B2-E3F2-4725-94B9-0E16AA7FEC5D";//"391E8F03-2C68-4A7C-90CD-20085C0DADAD";//"DDE5A5B2-E3F2-4725-94B9-0E16AA7FEC5D";
    public static String user = "42877B41-B0CB-4E4A-828D-E400A5B74015";//"2768CF05-13F9-4343-9497-B74F36756AAB";//"4B6C53D6-7921-403D-88D4-A3D910D7B98F";//
    public static int slotNr = 30;
    public static int navigationTypeNr = 4;

    public static void main(String[] args) {

        Map<String, Integer> categories = Preprocessing.extractCategories(app, user, "test2");

        List objs = Preprocessing.loadCategoriesForUserAndApp(app, user, "test2");

        Collection historyData = objs.subList(0, objs.size() / 2);

        Tree tree = Preprocessing.buildHistoryTree(historyData, categories);

        ContinueProbModule continueProbModule = new ContinueProbModule(categories);
        TimeSlotProbModule timeSlotProbModule = new TimeSlotProbModule(categories, slotNr, tree);
        NavigTypeProbModule navigTypeProbModule = new NavigTypeProbModule(tree, categories, navigationTypeNr);

        double[] continueProbabilities = continueProbModule.getCategoriesProbabilities(tree.getCurrentSequence(), tree.getRoot());
        double[] timeSlotProbabilities = timeSlotProbModule.getCategoriesProbabilities(null);
        double[] navigationTypesProb = navigTypeProbModule.getNavigTypeProbabilities();

        int categoryIndex = ResultModule.getMaxProbability(
                continueProbabilities, timeSlotProbabilities, navigationTypesProb);

        System.out.println(">>>>>>>>>>>>>>>>>>>>");
        System.out.println("Next Category: " + categories.keySet().toArray()[categoryIndex]);

    }

}
