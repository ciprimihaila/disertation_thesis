/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.newalgorithm;

import cercetare.dataparser.FileUtils;
import static cercetare.newalgorithm.Preprocessing.cycleDetectionTree;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bson.Document;

/**
 *
 * @author ciprian - c1 c2 c3 c4
 */
public class MainModulePrepCategories implements MainModuleI {

    private static Tree tree;

    private static int matchNr;
    private static int testNr;

    private static int currentId;

    private static List<Integer> delta;
    private static List<String> categs;
    private static List<Node> currentSequence;
    private static long sum;

    private static Node prevNode;

    private static double[] continueProbabilities;
    private static double[] timeSlotProbabilities;
    private static double[] navigationTypesProb;

    private final String fileName;

    private static ContinueProbModule continueProbModule;
    private static TimeSlotProbModule timeSlotProbModule;
    private static NavigTypeProbModule navigTypeProbModule;

    private final Iterable data;
    private final Map<String, Integer> categories;
    private final int skipNr;

    /**
     *
     * @param data
     * @param categories
     * @param skipNr - history size
     * @param timeSlotNr
     * @param navigationTypeNr
     * @param resultFileName
     * @param useTimeModule
     * @param useNavigModule
     */
    public MainModulePrepCategories(Iterable data, Map<String, Integer> categories, int skipNr,
            int timeSlotNr, int navigationTypeNr, String resultFileName,
            boolean useTimeModule, boolean useNavigModule) {
        this.data = data;
        this.categories = categories;
        this.skipNr = skipNr;
        this.fileName = resultFileName;

        init();

        // init modules
        continueProbModule = new ContinueProbModule(categories);

        if (useTimeModule) {
            timeSlotProbModule = new TimeSlotProbModule(categories, timeSlotNr, tree);
        }
        if (useNavigModule) {
            navigTypeProbModule = new NavigTypeProbModule(tree, categories, navigationTypeNr);
        }
    }

    private static void init() {
        matchNr = 0;
        testNr = 0;
        tree = new Tree();
        currentId = 1;
        prevNode = null;

        clearSequenceInfo();
    }

    private static void clearSequenceInfo() {
        delta = new ArrayList<>();
        categs = new ArrayList<>();
        currentSequence = new ArrayList<>();
        sum = 0;
        prevNode = null;
    }

    /**
     *
     * @return
     */
    @Override
    public double sequencialPrediction() {

        Iterator dataIterator = data.iterator();
        while (dataIterator.hasNext()) {

            Document object = (Document) dataIterator.next();
            String category = object.get("vertical").toString();
            Date date = ((Date) object.get("datetime"));
            LocalTime currentTime = LocalDateTime.ofInstant(date.toInstant(),
                    ZoneId.systemDefault()).toLocalTime();

            Node currentNode = new Node(category, date, new ArrayList<>());

            if (prevNode != null && !prevNode.getCategory().equals(category)) {

                currentId++;
                if (cycleDetectionTree(currentSequence, category)) {

                    currentSequence.add(prevNode);
                    System.out.print("New sequence: ");
                    for (Node s : currentSequence) {
                        System.out.print(s.getCategory() + " -> ");
                    }
                    System.out.println(" ");

                    if (currentSequence.size() > 0) {
                        tree.addSequence(currentSequence, false);
                        tree.addDelta(delta);
                        tree.addToSum(sum);
                        tree.addDiffs(categs, delta);
                        clearSequenceInfo();
                    }

                }

                if (prevNode != null && prevNode.getDates().get(0) != null) {
                    LocalTime prevTime = LocalDateTime.ofInstant(prevNode.getDates().
                            get(0).toInstant(),
                            ZoneId.systemDefault()).toLocalTime();

                    int diff = currentTime.minusSeconds(prevTime.toSecondOfDay()).toSecondOfDay();
                    categs.add(category);
                    delta.add(diff);
                    sum += diff;

                }

                if (prevNode != null) {
                    currentSequence.add(prevNode);
                }

                if (currentSequence.size() >= 1 && currentId > skipNr) { // perform prediction

                    tree.addSequence(currentSequence, true);

                    continueProbabilities = continueProbModule.getCategoriesProbabilities(currentSequence, tree.getRoot());
                    timeSlotProbabilities = new double[categories.size()];
                    if (timeSlotProbModule != null) {
                        timeSlotProbabilities = timeSlotProbModule.getCategoriesProbabilities(prevNode);
                    }

                    navigationTypesProb = new double[categories.size()];

                    StatisticsValues treeStatiscValues = tree.computeStatisticsValues();

                    double mean = (double) sum / delta.size();
                    double devs = Utils.computeStandardDeviation(delta, mean);

                    int currentRythm = NavigTypeProbModule.getRithmCurrentSequence(
                            treeStatiscValues, mean, devs);

                    if (currentRythm >= 0 && navigTypeProbModule != null) {
                        navigationTypesProb = navigTypeProbModule.getNavigTypeProbabilities2(
                                treeStatiscValues, currentRythm);
                    }

                    int categoryIndex = ResultModule.getMaxSumProbability(
                            continueProbabilities, timeSlotProbabilities, navigationTypesProb);

                    if (categoryIndex >= 0) {
                        System.out.println(">>>>>>>>>>>>>>>>>>>>");
                        System.out.println("Expected Category: " + category);
                        System.out.println("Next Category: " + categories.keySet().toArray()[categoryIndex]);

                        if (category.equals(categories.keySet().toArray()[categoryIndex])) {
                            matchNr++;
                        }
                    } else {
                        System.out.println("Expected Category " + category);
                        System.out.println("History size too short. No prediction");
                    }
                    testNr++;
                } else if ((currentSequence.size() == 0) && currentId > skipNr) {
                    continueProbabilities = continueProbModule.getCategoriesProbabilities(currentSequence, tree.getRoot());
                    int categoryIndex = ResultModule.getMaxProbabilityFromArray(continueProbabilities);
                    if (categoryIndex >= 0) {
                        System.out.println(">>>>>>>>>>>>>>>>>>>>");
                        System.out.println("Expected Category: " + category);
                        System.out.println("Next Category: " + categories.keySet().toArray()[categoryIndex]);

                        if (category.equals(categories.keySet().toArray()[categoryIndex])) {
                            matchNr++;
                        }
                    } else {
                        System.out.println("Expected Category " + category);
                        System.out.println("History size too short. No prediction");
                    }
                    testNr++;
                }

            }

            prevNode = currentNode;
        }

        System.out.println("Match Number: " + matchNr);
        System.out.println("Test Number: " + testNr);
        System.out.println("Accuracy: " + (double) (matchNr * 100) / testNr);

        if (!fileName.isEmpty()) {
            FileUtils.writeToFile(fileName, "Categories: " + categories.size());
            FileUtils.writeToFile(fileName, "Match Number: " + matchNr);
            FileUtils.writeToFile(fileName, "Test Number: " + testNr);
            FileUtils.writeToFile(fileName, "Accuracy: " + (double) (matchNr * 100) / testNr);

        }

        if (testNr > 0) {
            return (double) matchNr / testNr;
        } else {
            return -1;
        }
    }

}
