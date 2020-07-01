package au.edu.rmit.randomwalk.algorithm;

import au.edu.rmit.randomwalk.Common;
import au.edu.rmit.randomwalk.datastructure.FibonacciHeap;
import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import au.edu.rmit.randomwalk.model.OptimizationStrategy;
import au.edu.rmit.randomwalk.model.ProblemType;
import au.edu.rmit.randomwalk.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author forrest0402
 * @Description
 * @date 3/14/2018
 */
@Service("DegreeImpl")
public class DegreeImpl implements LLengthRandomWalk {

    private static Logger logger = LoggerFactory.getLogger(DegreeImpl.class);

    private double bestSoFar = 0;
    private AbstractGraphNode bestNode = null;
    private double preFScore = 0.0, curFScore = 0.0;

    @Override
    public Collection<AbstractGraphNode> run(AbstractGraphNode[] graph, int K, int L, ProblemType type, OptimizationStrategy strategy) {
        logger.info("Enter DegreeImpl/run() K = {}, L = {}", K, L);
        Set<AbstractGraphNode> S = new HashSet<>();
        PriorityQueue<AbstractGraphNode> topkHeap = new PriorityQueue<>((v1, v2) -> Integer.compare(v1.getNeighbors().size(), v2.getNeighbors().size()));
        for (int i = 0; i < graph.length; i++) {
            AbstractGraphNode node = graph[i];
            topkHeap.add(node);
            if (topkHeap.size() > K) topkHeap.poll();
        }
        while (topkHeap.size() > 0) {
            S.add(topkHeap.poll());
        }
        logger.info("Exit DegreeImpl/run()");
        return S;
    }

    public Collection<AbstractGraphNode> run2(AbstractGraphNode[] graph, Collection<AbstractGraphNode> C, int K, int L, ProblemType type, OptimizationStrategy strategy) {
        logger.info("Enter DegreeImpl/run() K = {}, L = {}", K, L);
        Set<AbstractGraphNode> S = new HashSet<>();
        PriorityQueue<AbstractGraphNode> topkHeap = new PriorityQueue<>((v1, v2) -> Integer.compare(getSize(C,v1), getSize(C,v2)));
        for (int i = 0; i < graph.length; i++) {
            AbstractGraphNode node = graph[i];
            topkHeap.add(node);
            if (topkHeap.size() > K) topkHeap.poll();
        }
        while (topkHeap.size() > 0) {
            S.add(topkHeap.poll());
        }
        logger.info("Exit DegreeImpl/run()");
        return S;
    }

    private int getSize(Collection<AbstractGraphNode> C, AbstractGraphNode u){
        int size = 0;
        for(AbstractGraphNode v:u.getNeighbors()){
            if(C.contains(v))
                size++;
        }
        return size;
    }

    class RankPair implements au.edu.rmit.randomwalk.model.Pair<AbstractGraphNode, Double> {

        @Override
        public AbstractGraphNode getKey() {
            return this.key;
        }

        @Override
        public Double getValue() {
            return this.value;
        }

        private RankPair(AbstractGraphNode key, Double value) {
            this.key = key;
            this.value = value;
        }

        private AbstractGraphNode key;
        private Double value;
    }
}
