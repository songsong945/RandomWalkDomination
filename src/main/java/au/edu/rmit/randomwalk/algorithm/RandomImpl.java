package au.edu.rmit.randomwalk.algorithm;

import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import au.edu.rmit.randomwalk.model.OptimizationStrategy;
import au.edu.rmit.randomwalk.model.ProblemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service("RandomImpl")
public class RandomImpl implements LLengthRandomWalk{
    private static Logger logger = LoggerFactory.getLogger(ApproximateGreedyImpl.class);

    @Override
    public Collection<AbstractGraphNode> run(AbstractGraphNode[] graph, int K, int L, ProblemType type, OptimizationStrategy strategy) {
        logger.info("Enter RandomImpl/run() K = {}, L = {}", K, L);
        Set<AbstractGraphNode> S = new HashSet<>();
        Random random = new Random();
        for(int i = 0 ; i < K ; i++){
            int rand = Math.abs(random.nextInt()%graph.length);
            System.out.print(rand+" ");
            if(S==null&&!S.contains(graph[rand]))
                S.add(graph[rand]);
        }
        System.out.println();
        return S;
    }

}
