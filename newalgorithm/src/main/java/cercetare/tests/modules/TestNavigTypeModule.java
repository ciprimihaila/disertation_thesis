/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.tests.modules;

import cercetare.newalgorithm.NavigTypeProbModule;
import cercetare.newalgorithm.Preprocessing;
import cercetare.newalgorithm.Tree;
import java.util.Map;

/**
 *
 * @author ciprian
 */
public class TestNavigTypeModule {

    public static void main(String[] args) {
        String app = "391E8F03-2C68-4A7C-90CD-20085C0DADAD";
        String user = "2768CF05-13F9-4343-9497-B74F36756AAB";
        String collection = "test2";
        int navTypes = 4;

        Map<String, Integer> categories = Preprocessing.extractCategories(app, user, collection);
        Iterable objs = Preprocessing.loadCategoriesForUserAndApp(app, user, collection);

        Tree tree = Preprocessing.buildHistoryTree(objs, categories);

        NavigTypeProbModule navSlotProbModule = new NavigTypeProbModule(tree, categories, navTypes);
        System.out.println("probability");

        double[] probabilities = navSlotProbModule.getNavigTypeProbabilities();

        for (int i = 0; i < probabilities.length; i++) {
            System.out.println(probabilities[i]);
        }
    }
}
