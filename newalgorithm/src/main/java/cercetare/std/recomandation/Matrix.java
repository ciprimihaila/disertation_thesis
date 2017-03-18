/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.std.recomandation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ciprian
 */
public class Matrix {

    private final List<List<Page>> matrix;
    private Map<String, Integer> cycleGeneration;
    private List<Page> currentSequence;

    public Matrix() {
        matrix = new ArrayList<>();
    }

    public void addRow(List<Page> row) {
        matrix.add(row);
    }

    public void removeLastRow() {
        matrix.remove(matrix.size() - 1);
    }

    public void setCurrentSequence(List<Page> seq) {
        this.currentSequence = seq;
    }

    public int[][] convertToArrayMatrix(int colSize) {
        int[][] result = new int[matrix.size()][colSize + 3];
        int rating;
        for (int i = 0; i < matrix.size(); i++) {
            rating = 1;
            for (int j = 0; j < matrix.get(i).size(); j++) {
                result[i][matrix.get(i).get(j).getCategoryId()] = rating;
                rating++;
            }
        }
        return result;
    }

    /**
     * @return the cycleGeneration
     */
    public Map<String, Integer> getCycleGeneration() {
        return cycleGeneration;
    }

    /**
     * @param cycleGeneration the cycleGeneration to set
     */
    public void setCycleGeneration(Map<String, Integer> cycleGeneration) {
        this.cycleGeneration = cycleGeneration;
    }

    /**
     * @return the currentSequence
     */
    public List<Page> getCurrentSequence() {
        return currentSequence;
    }

}
