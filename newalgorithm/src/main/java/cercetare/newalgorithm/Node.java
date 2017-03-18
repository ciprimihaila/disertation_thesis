/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cercetare.newalgorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ciprian
 */
public class Node {

    private String category;
    private double probability;
    private List<Node> children;
    private int weight;

    private List<Date> dates;
    private List<Integer> times;
    private List<Integer> consecutiveAccessess;

    private Node parent;

    public Node(Integer categoryId, String category, double probability, List<Node> children) {
        this.category = category;
        this.probability = probability;
        this.children = children;
        this.weight = 1;
    }

    public Node(String category, Date date, List<Node> children) {
        if (dates == null) {
            dates = new ArrayList<>();
        }
        if (times == null) {
            times = new ArrayList<>();
        }
        if (consecutiveAccessess == null) {
            consecutiveAccessess = new ArrayList<>();
        }
        dates.add(date);
        this.category = category;
        this.children = children;
        this.weight = 1;
        this.probability = 0;
    }

    public Node(Integer categoryId, String category, List<Node> children) {
        this.category = category;
        this.children = children;
        this.weight = 1;
    }

    public void addTime(int diff) {
        times.add(diff);
    }

    public void addAccess(int accessNr) {
        consecutiveAccessess.add(accessNr);
    }

    public List<Integer> getConsecutiveAccessessList() {
        return consecutiveAccessess;
    }

    public List<Integer> getTimes() {
        return times;
    }

    public void addDate(Date date) {
        getDates().add(date);
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the probability
     */
    public double getProbability() {
        return probability;
    }

    /**
     * @param probability the probability to set
     */
    public void setProbability(double probability) {
        this.probability = probability;
    }

    /**
     * @return the children
     */
    public List<Node> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(List<Node> children) {
        this.children = children;
    }

    /**
     * @return the weight
     */
    public int getWeight() {
        return weight;
    }

    public void incWeight() {
        weight++;
    }

    public int getChildrenWSum() {
        int result = 0;
        if (children.size() == 0) {
            return 1;
        }
        for (Node n : children) {
            result += n.getWeight();
        }
        return result;
    }

    public void addChildren(Node node) {
        children.add(node);

        for (Node n : children) {
            n.setProbability((double) n.getWeight() / getChildrenWSum());
            //System.out.println(n.getCategory() + "  " + n.getProbability() + " w: " + n.getWeight() + " s" + getChildrenWSum());
        }

    }

    /**
     * @return the parent
     */
    public Node getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * @return the dates
     */
    public List<Date> getDates() {
        return dates;
    }

}
