/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.std.recomandation;

import cercetare.dataparser.FileUtils;
import cercetare.newalgorithm.Preprocessing;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bson.Document;

/**
 *
 * @author ciprian
 */
public class StandardCFPrediction {

    private final Map<String, Integer> categories;
    private final String fileName;

    public StandardCFPrediction(Map<String, Integer> categories, String logFileName) {
        this.categories = categories;
        this.fileName = logFileName;
    }

    private static boolean pageVisited(List<Page> seq, String category) {
        for (Page p : seq) {
            if (p.getCategory().equals(category)) {
                return true;
            }
        }
        return false;
    }

    private String standardPrediction(Matrix result, String category) {

        CollaborativeFiltering stdrec = new CollaborativeFiltering(result.convertToArrayMatrix(categories.size() + 3));
        int[][] pred = stdrec.collaborativeFilteringUsers();
        int length = result.getCurrentSequence().size();

        double maxp = 0;
        String predCateg = "";
        int[] rez = pred[pred.length - 1];
        for (Map.Entry<String, Integer> entry : categories.entrySet()) {
            if (!pageVisited(result.getCurrentSequence(), entry.getKey())) {
                double prob = 1 - (double) (length - rez[entry.getValue()]) / (categories.size() + 1);
                if (prob > maxp) {
                    maxp = prob;
                    predCateg = entry.getKey();
                }
            }
        }

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("Expected categ: " + category);
        System.out.println("Predicted categ: " + predCateg);

        return predCateg;
    }

    private static boolean cycleDetection(List<Page> sequence, String category) {
        for (Page page : sequence) {
            if (page.getCategory().equals(category)) {
                return true;
            }
        }
        return false;
    }

    public double createHistoryMatrix(Iterable data, int trainSize) {

        Matrix result = new Matrix();
        Iterator dataIterator = data.iterator();
        List<Page> sequence = new ArrayList<>();

        Page startPage = new Page(Preprocessing.getIdForDelimiter(categories.size(), Preprocessing.SEQDELIM.START),
                "START", null);
        Page continuePage = new Page(Preprocessing.getIdForDelimiter(categories.size(), Preprocessing.SEQDELIM.CONTINUE),
                "CONTINUE", null);

        sequence.add(startPage);

        int nrGoldPred = 0;
        int nrAllPred = 0;

        Map<String, Integer> cycleGeneration = new HashMap<>();

        int i = 1;
        String prevCategory = "";

        while (dataIterator.hasNext()) {

            Document object = (Document) dataIterator.next();

            String category = object.get("vertical").toString();

            if (!"".equals(prevCategory) && !prevCategory.equals(category)) {
                i++;
                Date date = ((Date) object.get("datetime"));

                if (cycleDetection(sequence, category)) {

                    sequence.add(continuePage);

                    for (Page p : sequence) {
                        System.out.print(p.getCategory() + " -> ");
                    }
                    System.out.println("");

                    result.addRow(sequence);
                    sequence = new ArrayList<>();
                    sequence.add(startPage);

                    if (i > trainSize) {
                        nrAllPred++;
                        result.setCurrentSequence(sequence);
                        if (category.equals(standardPrediction(result, category))) {
                            nrGoldPred++;
                        }

                    }

                    sequence.add(new Page(categories.get(category), category, date));

                    Integer count = cycleGeneration.get(category);
                    if (count == null) {
                        cycleGeneration.put(category, 1);
                    } else {
                        cycleGeneration.put(category, count + 1);
                    }

                } else {
                    if (i > trainSize) {

                        nrAllPred++;
                        result.addRow(sequence);
                        result.setCurrentSequence(sequence);

                        if (category.equals(standardPrediction(result, category))) {
                            nrGoldPred++;
                        }

                        result.removeLastRow();

                    }
                    sequence.add(new Page(categories.get(category), category, date));
                }

            }
            prevCategory = category;
        }

        sequence.add(new Page(Preprocessing.getIdForDelimiter(categories.size(), Preprocessing.SEQDELIM.STOP),
                "STOP", null));

        result.addRow(sequence);
        result.setCurrentSequence(sequence);
        result.setCycleGeneration(cycleGeneration);

        System.out.println("Total teste: " + nrAllPred + " Good: " + nrGoldPred);
        System.out.println(((double) nrGoldPred / nrAllPred) * 100);

        if (!fileName.equals("")) {
            FileUtils.writeToFile(fileName, "Categories: " + categories.size());
            FileUtils.writeToFile(fileName, "Match Number: " + nrGoldPred);
            FileUtils.writeToFile(fileName, "Test Number: " + nrAllPred);
            FileUtils.writeToFile(fileName, "Accuracy: " + (double) (nrGoldPred * 100) / nrAllPred);
        }

        if (nrAllPred > 0) {
            return (double) nrGoldPred / nrAllPred;
        } else {
            return -1;
        }

    }
}
