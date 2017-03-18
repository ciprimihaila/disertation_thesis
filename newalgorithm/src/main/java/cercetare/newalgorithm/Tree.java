/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.newalgorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * class for history tree representation
 *
 * @author ciprian
 */
public class Tree {

    private Node root;

    private List<Node> nodes;

    private List<Integer> timeDifferences;
    private List<String> categories;

    private List<Node> currentSequence;

    private long sum;
    private List<Integer> delta;

    private StatisticsValues statisticValues;

    private List<Integer> accessedNumberPages = new ArrayList<>();

    public Tree() {
        root = new Node(-1, "START", 1, new ArrayList<>());
        timeDifferences = new ArrayList<>();
        categories = new ArrayList<>();
        delta = new ArrayList<>();
    }

    public Tree(Node root) {
        this.root = root;
    }

    /**
     * @return the root
     */
    public Node getRoot() {
        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot(Node root) {
        this.root = root;
    }

    public void addToSum(long s) {
        sum += s;
    }

    public void addDelta(List<Integer> deltaNew) {
        delta.addAll(deltaNew);
    }

    public StatisticsValues computeStatisticsValues() {
        double mean = (double) sum / delta.size();
        double devs = Utils.computeStandardDeviation(delta, mean);
        return new StatisticsValues(mean, devs);
    }

    public void addDiff(String category, int d) {
        getTimeDifferences().add(d);
        getCategories().add(category);
    }

    public void addDiffs(List<String> categs, List<Integer> diffs) {
        timeDifferences.addAll(diffs);
        categories.addAll(categs);
    }

    private Node checkExistingChildren(Node newn, Node dest) {
        for (Node child : dest.getChildren()) {
            if (child.getCategory().equals(newn.getCategory())) {
                child.incWeight();
                if (newn.getTimes().size() > 0) {
                    child.addTime(newn.getTimes().get(0));
                }
                child.addDate(newn.getDates().get(0));
                return child;
            }
        }
        return null;
    }

    private Node addChild(Node newn, Node dest) {
        Node exChild = checkExistingChildren(newn, dest);
        if (exChild != null) {
            for (Node child : dest.getChildren()) {
                child.setProbability((double) child.getWeight() / dest.getChildrenWSum());
            }
            return exChild;
        }
        dest.addChildren(newn);
        return newn;
    }

    /**
     *
     *
     * @param sequence
     * @param last
     * @return last node added
     */
    public Node addSequence(List<Node> sequence, boolean last) {
        if (last) {
            currentSequence = sequence;
            return currentSequence.get(currentSequence.size() - 1);
        } else {
            Node dest = root;
            for (Node node : sequence) {
                dest = addChild(node, dest);
            }
            return dest;
        }
    }

    public List<Node> getNodes() {
        nodes = new ArrayList<>();
        walk(root);
        return nodes;
    }

    public List<Node> getNodes(String nodeName) {
        nodes = new ArrayList<>();
        walkForNodes(root, nodeName);
        return nodes;
    }

    private void walkForNodes(Node node, String category) {
        if (node.getCategory().equals(category)) {
            nodes.add(node);
        }
        for (Node child : node.getChildren()) {
            walkForNodes(child, category);
        }
    }

    public List<Integer> getAccessedPagesFromNode(String category) {
        accessedNumberPages = new ArrayList<>();
        walkForPages(root, category);
        return accessedNumberPages;
    }

    private void walkForPages(Node node, String category) {
        if (node.getCategory().equals(category)) {
            int pagesNr = 0;
            for (Node child : node.getChildren()) {
                pagesNr += child.getDates().size();
            }
            accessedNumberPages.add(pagesNr);
        }
        for (Node child : node.getChildren()) {
            walkForPages(child, category);
        }
    }

    public Node getChild(Node dest, Node pathNode) {
        for (Node child : dest.getChildren()) {
            if (child.getCategory().equals(pathNode.getCategory())) {
                return child;
            }
        }
        return null;
    }

    private void walk(Node node) {
        if (!node.getCategory().equals("START")) {
            nodes.add(node);
        }
        for (Node child : node.getChildren()) {
            walk(child);
        }
    }

    /**
     * @return the statisticValues
     */
    public StatisticsValues getStatisticValues() {
        return statisticValues;
    }

    /**
     * @param mean
     * @param devs
     */
    public void setStatisticValues(double mean, double devs) {
        this.statisticValues = new StatisticsValues(mean, devs);
    }

    /**
     * @return the currentSequence
     */
    public List<Node> getCurrentSequence() {
        return currentSequence;
    }

    /**
     * @return the timeDifferences
     */
    public List<Integer> getTimeDifferences() {
        return timeDifferences;
    }

    /**
     * @return the categories
     */
    public List<String> getCategories() {
        return categories;
    }
}
