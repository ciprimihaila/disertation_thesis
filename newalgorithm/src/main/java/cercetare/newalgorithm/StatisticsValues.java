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
public class StatisticsValues {

    private double mean;
    private double standardDev;

    public StatisticsValues(double mean, double standardDev) {
        this.mean = mean;
        this.standardDev = standardDev;
    }

    /**
     * @return the mean
     */
    public double getMean() {
        return mean;
    }

    /**
     * @return the standardDev
     */
    public double getStandardDev() {
        return standardDev;
    }

}
