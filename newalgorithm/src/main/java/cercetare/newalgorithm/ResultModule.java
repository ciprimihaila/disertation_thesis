/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.newalgorithm;

/**
 *
 * @author ciprian
 */
public class ResultModule {

    private static boolean testNanArray(double[] array) {
        for (int i = 0; i < array.length; i++) {
            if (Double.isNaN(array[i])) {
                return true;
            }
        }
        return false;
    }

    public static int getMaxProbability(double[] continueProb,
            double[] timeProb, double[] navigProb) {
        double max = 0;
        int index = -1;
        if (testNanArray(navigProb) || testNanArray(continueProb) || testNanArray(timeProb)) {
            return index;
        }
        for (int i = 0; i < continueProb.length; i++) {
            double prob = continueProb[i] * timeProb[i] * navigProb[i];
            if (prob > max) {
                max = prob;
                index = i;
            }
        }
        return index;
    }

    public static int getMaxSumProbability(double[] continueProb,
            double[] timeProb, double[] navigProb) {
        double max = 0;
        int index = -1;
        if (testNanArray(navigProb) || testNanArray(continueProb) || testNanArray(timeProb)) {
            return index;
        }
        for (int i = 0; i < timeProb.length; i++) {
            try {
                double prob = continueProb[i] + timeProb[i] + navigProb[i];
                if (prob > max) {
                    max = prob;
                    index = i;
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        return index;
    }

    /**
     * logarithmic max
     *
     * @param continueProb
     * @param timeProb
     * @param navigProb
     * @return
     */
    public static int getMaxLogProbability(double[] continueProb,
            double[] timeProb, double[] navigProb) {
        double max = 0;
        int index = -1;
        if (testNanArray(navigProb) || testNanArray(continueProb) || testNanArray(timeProb)) {
            return index;
        }
        for (int i = 0; i < continueProb.length; i++) {
            double prob = Math.log(continueProb[i]) + Math.log(timeProb[i])
                    + Math.log(navigProb[i]);
            if (prob > max) {
                max = prob;
                index = i;
            }
        }
        return index;
    }

    public static int getMaxProbabilityFromArray(double[] array) {
        int maxI = 0;
        double maxV = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] > maxV) {
                maxV = array[i];
                maxI = i;
            }
        }
        return maxI;
    }

}
