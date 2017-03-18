/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.newalgorithm;

import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/**
 *
 * @author ciprian
 */
public class Utils {

    public final static Logger LOGGER = Logger.getLogger("GLOBAL");

    public static Handler logHandler = new StreamHandler(System.out, new Formatter() {
        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();
            sb.append(record.getMessage());
            return sb.toString();
        }
    });

    public static double computeMean(List<Integer> items) {
        if (items.size() == 0) {
            return 0;
        }
        int s = 0;
        for (int i = 0; i < items.size(); i++) {
            s += items.get(i);
        }
        return (double) s / items.size();
    }

    public static double computeStandardDeviation(List<Integer> items, double mean) {
        if (items.size() == 0) {
            return 0;
        }
        int s = 0;
        for (int i = 0; i < items.size(); i++) {
            s += Math.pow((items.get(i) - mean), 2);
        }
        return Math.sqrt((double) s / (items.size())); // - 1 ????????
    }

    public static boolean testNanOrZeroArray(double[] array) {
        for (int i = 0; i < array.length; i++) {
            if ((Double.isNaN(array[i]) == false)) {
                if (array[i] != 0) {
                    return false;
                }
            }

        }
        return true;
    }

}
