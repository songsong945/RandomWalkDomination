package au.edu.rmit.randomwalk.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author forrest0402
 * @Description
 * @date 2/5/2018
 */
@Component
public class BasicGraphNode extends AbstractGraphNode {

    private static Logger logger = LoggerFactory.getLogger(BasicGraphNode.class);

    List<AbstractGraphNode> neighbors = new ArrayList<>();
    List<Float> weights = new ArrayList<>();

    public BasicGraphNode() {

    }

    @Override
    public void addNeighbor(AbstractGraphNode neighbor, float weight) {
        for (AbstractGraphNode node : neighbors) {
            if (node.equals(neighbor))
                return;
        }
        this.neighbors.add(neighbor);
        this.weights.add(weight);
    }

    public BasicGraphNode(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void reBuild(int id) {
        this.id = id;
        this.name = id + "";
    }

    @Override
    public BigDecimal getAccuratePageRankScore() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void setAccuratePageRankScore(BigDecimal score) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public double getPageRankScore() {
        return pageRankScore;
    }

    @Override
    public void setPageRankScore(double score) {
        pageRankScore = score;
    }

    @Override
    public double getResidualScore() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public void setResidualScore(double score) {
        throw new UnsupportedOperationException("");
    }

    @Override
    public List<? extends AbstractGraphNode> getOutNeighbors() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public List<? extends AbstractGraphNode> getInNeighbors() {
        throw new UnsupportedOperationException("");
    }

    @Override
    public List<? extends AbstractGraphNode> getNeighbors() {
        return neighbors;
    }

    public List< Float> getWeights() {
        return weights;
    }


    public float getWeightSum() {
        return weightSum;
    }

    public void setWeightSum(float weightSum) {
        this.weightSum = weightSum;
    }

    public boolean isRealNode() {
        return isRealNode;
    }

    public void setRealNode(boolean realNode) {
        isRealNode = realNode;
    }

}
