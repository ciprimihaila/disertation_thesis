package cercetare.std.recomandation;

public class CollaborativeFiltering {

    private final int rows;
    private final int cols;
    private final int matrix[][];
    double itemsSimilarities[][];

    public CollaborativeFiltering(int[][] matrix) {
        this.matrix = matrix;
        this.cols = matrix[0].length;
        this.rows = matrix.length;
        itemsSimilarities = new double[cols][cols];
    }

    private void computeItemsSimilarities() {
        for (int m = 1; m < cols; m++) {
            for (int m2 = m + 1; m2 < cols; m2++) {
                int it1[] = new int[rows];
                int it2[] = new int[rows];
                int i = 0;
                for (int u = 1; u < rows; u++) {
                    if (matrix[u][m] != 0 && matrix[u][m2] != 0) {
                        it1[i] = matrix[u][m];
                        it2[i] = matrix[u][m2];
                        i++;
                    }
                }
                if (i > 0) {
                    double cos = Similarity.cosineSimilarity(it1, it2, i);
                    itemsSimilarities[m][m2] = itemsSimilarities[m2][m] = cos;
                }
            }
        }
    }

    public int[][] collaborativeFilteringUsers() {
        this.computeItemsSimilarities();
        double similarities[][] = itemsSimilarities;
        int result[][] = new int[rows][cols];
        int ru[] = new int[cols];
        for (int u = 1; u < rows; u++) {
            for (int m = 1; m < cols; m++) {
                double sump = 0;
                double sum = 0;
                for (int m2 = 1; m2 < cols; m2++) {
                    if (similarities[m2][m] > 0 && matrix[u][m2] != 0) {
                        sump += similarities[m2][m] * matrix[u][m2];
                        sum += similarities[m2][m];
                    }
                }
                result[u][m] = (int) Math.round(sump / sum);

            }
        }
        return result;
    }

}
