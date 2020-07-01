package au.edu.rmit.randomwalk.algorithm;

import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import au.edu.rmit.randomwalk.model.OptimizationStrategy;
import au.edu.rmit.randomwalk.model.ProblemType;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * @author forrest0402
 * @Description
 * @date 2/5/2018
 */
@Component
public interface LLengthRandomWalk {

    /**
     *
     * @param graph
     * @param K the number of seeds
     * @param L length of walk
     * @param type
     * @param strategy
     * @return
     */
    Collection<AbstractGraphNode> run(AbstractGraphNode[] graph, int K, int L, ProblemType type, OptimizationStrategy strategy);
}
