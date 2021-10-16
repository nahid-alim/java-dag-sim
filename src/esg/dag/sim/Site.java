package esg.dag.sim;

import java.io.Serializable;
import java.util.ArrayList;

public class Site implements Serializable {
    private Transaction tx;
    private Node node;
    private Integer ownWeight;
    private Integer cumulativeWeight;
    private Integer depth;
    private Integer height;
    private Integer score;


    public Site(Node n, Transaction tx) {
        this.setOwnWeight(1);
        this.setCumulativeWeight(ownWeight);
        this.node = n;
        this.tx = tx;
    }
    public Transaction getTx() {
        return tx;
    }

    public void setTx(Transaction tx) {
        this.tx = tx;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Integer getOwnWeight() {
        return ownWeight;
    }

    public void setOwnWeight(Integer ownWeight) {
        this.ownWeight = ownWeight;
    }

    public Integer getCumulativeWeight() {
        return cumulativeWeight;
    }

    public void setCumulativeWeight(Integer cumulativeWeight) {
        this.cumulativeWeight = cumulativeWeight;
    }

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

}
