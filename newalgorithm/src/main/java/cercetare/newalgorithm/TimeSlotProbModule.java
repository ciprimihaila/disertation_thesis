/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.newalgorithm;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javafx.util.Pair;

/**
 * Time slots probabilities module
 *
 * @author ciprian
 */
public class TimeSlotProbModule {

    private final Map<String, Integer> categories;
    private final int slotNr;
    private final Tree historyTree;

    public TimeSlotProbModule(Map<String, Integer> categories, int slotNr, Tree historyTree) {
        this.categories = categories;
        this.slotNr = slotNr;
        this.historyTree = historyTree;
    }

    private List<Pair<LocalTime, LocalTime>> computeSlotsTime(int nr) {
        List<Pair<LocalTime, LocalTime>> result = new ArrayList<>();
        int intervalLengthSeconds = (int) 24 * 3600 / nr;

        LocalTime lt = LocalTime.of(0, 0);
        for (int i = 0; i < nr; i++) {
            LocalTime lt1 = lt.withSecond(1);
            LocalTime lt2 = lt1.plusSeconds(intervalLengthSeconds);
            result.add(new Pair<>(lt1, lt2));
            lt = lt2;
        }
        return result;
    }

    private static int findSlotId(List<Pair<LocalTime, LocalTime>> timeSlots, LocalTime time) {
        for (int i = 0; i < timeSlots.size(); i++) {
            Pair<LocalTime, LocalTime> pair = timeSlots.get(i);
            if ((pair.getKey().isBefore(time)
                    && pair.getValue().isAfter(time)) || time.equals(pair.getValue())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * build the time - categories matrix using tree
     *
     * @param categories
     * @param slots
     * @param tree
     * @return
     */
    private int[][] computeCategoriesSlotTimesMatrix(List<Pair<LocalTime, LocalTime>> timeSlots) {
        int[][] result = new int[categories.size()][timeSlots.size()];
        for (Node node : historyTree.getNodes()) {
            for (Date date : node.getDates()) {
                LocalDateTime datetime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
                LocalTime time = datetime.toLocalTime();
                int slotId = findSlotId(timeSlots, time);
                result[categories.get(node.getCategory())][slotId]++;
            }
        }
        return result;
    }

    public int getTimeSlotForNode(Node node, List<Pair<LocalTime, LocalTime>> timeSlots) {
        LocalDateTime datetime = LocalDateTime.ofInstant(node.getDates().get(0).toInstant(),
                ZoneId.systemDefault());
        LocalTime currentTime = datetime.toLocalTime();

        for (int i = 0; i < timeSlots.size(); i++) {
            if ((timeSlots.get(i).getKey().isBefore(currentTime)
                    || timeSlots.get(i).getKey().equals(currentTime))
                    && timeSlots.get(i).getValue().isAfter(currentTime)) {
                return i;
            }
        }
        return -1;
    }

    public static double[] computeCategorySlotProb(int[][] matrix, int idSlot) {
        double[] result = new double[matrix.length];
        int colSum = 0;
        for (int j = 0; j < matrix.length; j++) {
            colSum += matrix[j][idSlot];
        }
        for (int i = 0; i < matrix.length; i++) {
            if (colSum != 0) {
                result[i] = (double) matrix[i][idSlot] / colSum;
            }
        }
        return result;
    }

    /**
     * probabilitatea accesării fiecărei categorii în parte în fiecare slot de
     * timp ca fiind numărul de accesări ale fiecărei categorii în fiecare slot
     * de timp împărțit la numărul total de accesări ale categoriei respective
     *
     * @return
     */
    public static double[] computeProbability1(int[][] matrix, int idCateg) {
        double[] result = new double[matrix[0].length];
        int[] row = matrix[idCateg];
        int rowSum = 0;
        for (int j = 0; j < row.length; j++) {
            rowSum += row[j];
        }
        for (int j = 0; j < row.length; j++) {
            result[j] = (double) row[j] / rowSum;
        }
        return result;
    }

    /**
     * probabilitatea de a accesa pagini web într-un anumit slot de timp ca
     * fiind numărul de accesări al oricărei categorii în fiecare slot de timp
     * împărțit la numărul total de accesări
     *
     * @param matrix
     * @param idCateg
     * @return
     */
    public static double[] computeProbability2(int[][] matrix) {
        double[] result = new double[matrix[0].length];
        int matrixSum = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrixSum += matrix[i][j];
            }
        }
        for (int j = 0; j < matrix[0].length; j++) {
            int sumCol = 0;
            for (int i = 0; i < matrix.length; i++) {
                sumCol += matrix[i][j];
            }
            result[j] = (double) sumCol / matrixSum;
        }
        return result;
    }

    /**
     * module output array of probabilities for each category
     *
     * @param categories
     * @param slotNr
     * @param historyTree
     * @param last
     * @return
     */
    public double[] getCategoriesProbabilities(Node last) {

        List<Pair<LocalTime, LocalTime>> timeSlots = computeSlotsTime(slotNr);

        int[][] slotTimeMatrix = computeCategoriesSlotTimesMatrix(timeSlots);
        if (last == null) {
            last = historyTree.getCurrentSequence().get(historyTree.getCurrentSequence().size() - 1);
        }
        int currentTimeSlot = getTimeSlotForNode(last, timeSlots);
        double[] categoryProb = computeCategorySlotProb(slotTimeMatrix, currentTimeSlot);

        return categoryProb;
    }

}
