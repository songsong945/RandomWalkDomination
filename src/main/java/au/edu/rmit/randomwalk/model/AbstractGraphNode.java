package au.edu.rmit.randomwalk.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author forrest0402
 * @Description
 * @date 2/5/2018
 */
@Component
public abstract class AbstractGraphNode {

    int id;

    String name;

    double pageRankScore;

    float weightSum;

    public abstract boolean isRealNode();

    public abstract void setRealNode(boolean realNode) ;

    boolean isRealNode;

    public abstract float getWeightSum();

    public abstract void setWeightSum(float weightSum);

    public abstract int getId();

    public abstract String getName();

    public abstract void reBuild(int id);

    public abstract BigDecimal getAccuratePageRankScore();

    public abstract void setAccuratePageRankScore(BigDecimal score);

    public abstract double getPageRankScore();

    public abstract void setPageRankScore(double score);

    public abstract double getResidualScore();

    public abstract void setResidualScore(double score);

    public abstract List<? extends AbstractGraphNode> getOutNeighbors();

    public abstract List<? extends AbstractGraphNode> getInNeighbors();

    public abstract List<? extends AbstractGraphNode> getNeighbors();

    public abstract List<Float> getWeights();

    public abstract void addNeighbor(AbstractGraphNode neighbor, float weight);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicGraphNode that = (BasicGraphNode) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "[" + id + ", " + name + "]";
    }
}
