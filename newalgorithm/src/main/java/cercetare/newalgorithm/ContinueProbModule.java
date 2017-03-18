/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.newalgorithm;

import java.util.List;
import java.util.Map;

/**
 *
 * @author ciprian
 */
public class ContinueProbModule {

    private final Map<String, Integer> categories;

    public ContinueProbModule(Map<String, Integer> categories) {
        this.categories = categories;
    }

    private Node getChild(Node dest, Node pathNode) {
        for (Node child : dest.getChildren()) {
            if (child.getCategory().equals(pathNode.getCategory())) {
                return child;
            }
        }
        return null;
    }

    public double[] getCategoriesProbabilities(List<Node> currentSequence,
            Node root) {

        double[] sequenceProbabilities = new double[categories.size() + 1];
        Node dest = root;
        boolean sequenceProb = false;
        for (int i = 0; i < currentSequence.size(); i++) {
            Node r = getChild(dest, currentSequence.get(i));
            if (r != null) {
                dest = r;
                sequenceProbabilities[categories.get(r.getCategory())] = r.getProbability();
                sequenceProb = true;
            } else {
                dest = null;
                break;
            }
        }

        boolean nextProbability = false;
        double[] nextProbabilities = new double[categories.size()];
        if (dest != null) {
            for (Node next : dest.getChildren()) {
                if (next.getProbability() > 0) {
                    nextProbability = true;
                }
                nextProbabilities[categories.get(next.getCategory())] = next.getProbability();
            }
        }

        if (nextProbability) {
            return nextProbabilities;
        } else if (sequenceProb) {
            return sequenceProbabilities;
        } else {//return the first level probabilities
            double[] rootProbabilities = new double[categories.size()];
            for (Node child : root.getChildren()) {
                rootProbabilities[categories.get(child.getCategory())] = child.getProbability();
            }
            return rootProbabilities;
        }
    }
}
