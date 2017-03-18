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
import java.util.List;
import java.util.Map;

/**
 * navigation types module
 *
 * @author ciprian
 */
public class NavigTypeProbModule {

    private final Tree historyTree;
    private final Map<String, Integer> categories;
    private final int navigationTypes;

    public NavigTypeProbModule(Tree historyTree, Map<String, Integer> categories, int navigationTypes) {
        this.historyTree = historyTree;
        this.categories = categories;
        this.navigationTypes = navigationTypes;
    }

    private int[][] createRithmNavMatrix(double mediumThreshold, double devThreshold) {

        int windowSize = historyTree.getCurrentSequence().size();
        int[][] resultM = new int[categories.size()][navigationTypes];

        List<String> categoriesDiffs = historyTree.getCategories();
        List<Integer> deltaTimes = historyTree.getTimeDifferences();

        int i = 1;
        long sum = deltaTimes.get(0);
        while (i < deltaTimes.size()) {

            sum += deltaTimes.get(i);
            double mean = (double) sum / (i + 1);
            int startIndex = ((i - windowSize) < 0) ? 0 : i - windowSize;
            double deviation = Utils.computeStandardDeviation(
                    deltaTimes.subList(startIndex, i), mean);

            String category = categoriesDiffs.get(i);

            if (mean >= mediumThreshold) {
                if (deviation > devThreshold) {
                    resultM[categories.get(category)][0]++;
                } else {
                    resultM[categories.get(category)][1]++;
                }
            } else if (mean < mediumThreshold) {
                if (deviation > devThreshold) {
                    resultM[categories.get(category)][2]++;
                } else {
                    resultM[categories.get(category)][3]++;
                }
            }

            if (i >= windowSize) {
                sum -= deltaTimes.get(i - windowSize);
            }

            i++;
        }

        return resultM;
    }

    public double computeCategoryRithmProbability(int[][] matrix, int categId, int ritmId) {
        int sum = 0;
        for (int j = 0; j < matrix[categId].length; j++) {
            sum += matrix[categId][j];
        }
        return (double) matrix[categId][ritmId] / sum;
    }

    public static double[] computeCategoriesRithmProbabilities(int[][] matrix, int ritmId) {
        double[] result = new double[matrix.length];
        int colSum = 0;
        for (int j = 0; j < matrix.length; j++) {
            colSum += matrix[j][ritmId];
        }
        for (int i = 0; i < matrix.length; i++) {
            if (colSum != 0) {
                result[i] = (double) matrix[i][ritmId] / colSum;
            } else {
                result[i] = 0;
            }
        }
        return result;
    }

    public double computeRithmProbability(int[][] matrix, int ritmId) {
        int matrixSum = 0;
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                matrixSum += matrix[i][j];
            }
        }

        int rithmSum = 0;
        for (int i = 0; i < matrix.length; i++) {
            rithmSum += matrix[i][ritmId];
        }
        return (double) rithmSum / matrixSum;
    }

    @Deprecated
    public static int getRithmCurrentSequence(Tree tree,
            double mediumThreshold, double devThreshold) {
        int sum = 0;
        List<Node> currentSequence = tree.getCurrentSequence();
        List<Integer> devItems = new ArrayList<>();
        LocalTime prevTime = LocalDateTime.ofInstant(currentSequence.get(0).getDates().get(0).toInstant(),
                ZoneId.systemDefault()).toLocalTime();
        for (int i = 1; i < currentSequence.size(); i++) {
            LocalTime currentTime = LocalDateTime.ofInstant(currentSequence.get(i).getDates().get(0).toInstant(),
                    ZoneId.systemDefault()).toLocalTime();
            int diff = currentTime.minusSeconds(prevTime.toSecondOfDay()).toSecondOfDay();
            devItems.add(diff);
            sum += diff;
        }

        double meanC = (double) sum / currentSequence.size();
        double devC = Utils.computeStandardDeviation(devItems, meanC);
        if (meanC > mediumThreshold) {
            if (devC > devThreshold) {
                return 0;
            } else {
                return 1;
            }
        } else if (meanC < mediumThreshold) {
            if (devC > devThreshold) {
                return 2;
            } else {
                return 3;
            }
        }

        return -1;

    }

    public static int getRithmCurrentSequence(StatisticsValues statisticsValues,
            double currentMean, double currentDevStd) {

        double mediumThreshold = statisticsValues.getMean();
        double devStdThreshold = statisticsValues.getStandardDev();

        if (currentMean > mediumThreshold) {
            if (currentDevStd > devStdThreshold) {
                return 0;
            } else {
                return 1;
            }
        } else if (currentMean < mediumThreshold) {
            if (currentDevStd > devStdThreshold) {
                return 2;
            } else {
                return 3;
            }
        }

        return -1;
    }

    public double[] getNavigTypeProbabilities() {

        double stdDeviation = historyTree.getStatisticValues().getStandardDev();
        double mean = historyTree.getStatisticValues().getMean();
        int[][] rithmMatrix = createRithmNavMatrix(mean, stdDeviation);

        int currentRithm = NavigTypeProbModule.getRithmCurrentSequence(historyTree, mean, stdDeviation);

        double[] rithmProb = NavigTypeProbModule.computeCategoriesRithmProbabilities(
                rithmMatrix, currentRithm);
        return rithmProb;
    }

    private int getMostUsedRithm(int navigationTypes, int[][] matrix) {
        int maxRithm = 0;
        int maxSum = 0;
        for (int j = 0; j < navigationTypes; j++) {
            int currentSum = 0;
            for (int i = 0; i < matrix.length; i++) {
                currentSum += matrix[i][j];
            }
            if (currentSum > maxSum) {
                maxSum = currentSum;
                maxRithm = j;
            }
        }
        return maxRithm;
    }

    public double[] getNavigTypeProbabilities2(StatisticsValues statisticsVals, int currentRithm) {

        double stdDeviation = statisticsVals.getStandardDev();
        double mean = statisticsVals.getMean();
        int[][] rithmMatrix = createRithmNavMatrix(mean, stdDeviation);

        double[] rithmProb = computeCategoriesRithmProbabilities(rithmMatrix, currentRithm);

        if (Utils.testNanOrZeroArray(rithmProb)) {
            rithmProb = computeCategoriesRithmProbabilities(rithmMatrix,
                    getMostUsedRithm(navigationTypes, rithmMatrix));
        }

        return rithmProb;
    }

}
