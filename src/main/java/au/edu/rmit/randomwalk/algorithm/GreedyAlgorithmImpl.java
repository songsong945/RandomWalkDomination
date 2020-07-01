package au.edu.rmit.randomwalk.algorithm;

import au.edu.rmit.randomwalk.Common;
import au.edu.rmit.randomwalk.datastructure.FibonacciHeap;
import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import au.edu.rmit.randomwalk.model.GreedyType;
import au.edu.rmit.randomwalk.model.OptimizationStrategy;
import au.edu.rmit.randomwalk.model.ProblemType;
import au.edu.rmit.randomwalk.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author forrest0402
 * @Description
 * @date 2/5/2018
 */
@Service("GreedyAlgorithmImpl")
public class GreedyAlgorithmImpl implements LLengthRandomWalk {

    private static Logger logger = LoggerFactory.getLogger(GreedyAlgorithmImpl.class);

    private double bestSoFar = 0;
    private AbstractGraphNode bestNode = null;
    private double preFScore = 0.0, curFScore = 0.0;

    /**
     * Expectation of u to S
     *
     * @param u
     * @param S
     * @param L
     * @param cacheH When applying Dynamic programming, I use cacheH to store the results of sub-problems
     * @return
     */
    private double h(AbstractGraphNode u, Collection<AbstractGraphNode> S, int L, Map<String, Double> cacheH) {
        if (S.contains(u)) return 0.0;
        if (L == 0) return 0.0;
        double score = 1.0, degree = u.getNeighbors().size();
        for (AbstractGraphNode neighborNode : u.getNeighbors()) {
            String key = neighborNode.getName() + " " + (L - 1);
            Double v = cacheH.get(key);
            if (v == null) {
                v = h(neighborNode, S, L - 1, cacheH);
                if (Common.INSTANCE.GREEDY_MODE != GreedyType.WITHOUT_CACHE)
                    cacheH.put(key, v);
            }
            score += 1.0 / degree * v;
        }
        return score;
    }

    private BigDecimal accurateH(AbstractGraphNode u, Collection<AbstractGraphNode> S, int L, Map<String, BigDecimal> cacheH) {
        if (S.contains(u)) return new BigDecimal(0.0);
        if (L == 0) return new BigDecimal(0.0);
        BigDecimal score = new BigDecimal(1.0);
        double degree = u.getNeighbors().size();
        for (AbstractGraphNode neighborNode : u.getNeighbors()) {
            String key = neighborNode.getName() + " " + (L - 1);
            BigDecimal v = cacheH.get(key);
            if (v == null) {
                v = accurateH(neighborNode, S, L - 1, cacheH);
                if (Common.INSTANCE.GREEDY_MODE != GreedyType.ACCU_WITHOUT_CACHE)
                    cacheH.put(key, v);
            }
            score = score.add(v.multiply(new BigDecimal(1.0 / degree)));
        }
        return score;
    }

    private double p(AbstractGraphNode node, Collection<AbstractGraphNode> S, int L, Map<String, Double> cacheP) {
        if (S.contains(node)) return 1.0;
        if (L == 0) return 0.0;
        double score = 0, degree = node.getNeighbors().size();
        for (AbstractGraphNode neighborNode : node.getNeighbors()) {
            String key = neighborNode.getName() + " " + (L - 1);
            Double v = cacheP.get(key);
            if (v == null) {
                v = p(neighborNode, S, L - 1, cacheP);
                cacheP.put(key, v);
            }
            score += 1 / degree * v;
        }
        return score;
    }

    /**
     * Expectation of u \in {graph - S} to S
     *
     * @param graph
     * @param S
     * @param L
     * @return
     */
    private double F1(AbstractGraphNode[] graph, Collection<AbstractGraphNode> S, int L) {
        double score = graph.length * L;
        Map<String, Double> cache = new HashMap<>();
        for (int i = 0; i < graph.length; i++) {
            AbstractGraphNode u = graph[i];
            if (Common.INSTANCE.GREEDY_MODE == GreedyType.INSIDE_CACHE)
                cache = new HashMap<>();
            if (!S.contains(u)) {
                double dis = h(u, S, L, cache);
                score -= dis;
            }
        }
        return score;
    }

    private double F2(AbstractGraphNode[] graph, Collection<AbstractGraphNode> S, int L) {
        double pScore = 0;
        Map<String, Double> cache = new HashMap<>();
        for (int i = 0; i < graph.length; i++) {
            AbstractGraphNode curNode = graph[i];
            if (!S.contains(curNode)) {
                Set<String> visited = new HashSet<>();
                visited.add(curNode.getName());
                pScore += p(curNode, S, L, cache);
            }
        }
        return pScore;
    }


    /**
     * Expectation of u \in {graph - S} to S
     *
     * @param graph
     * @param S
     * @param L
     * @return
     */
    private double accurateF1(AbstractGraphNode[] graph, Collection<AbstractGraphNode> S, int L) {
        BigDecimal score = new BigDecimal(graph.length * L);
        Map<String, BigDecimal> cache = new HashMap<>();
        for (int i = 0; i < graph.length; i++) {
            AbstractGraphNode u = graph[i];
            if (Common.INSTANCE.GREEDY_MODE == GreedyType.ACCU_INSIDE_CACHE)
                cache = new HashMap<>();
            if (!S.contains(u)) {
                BigDecimal dis = accurateH(u, S, L, cache);
                score = score.subtract(dis);
            }
        }
        return score.doubleValue();
    }

    /**
     * the margin is not the real margin, but I think the result should be same as F1(graph, S, L) - F1(graph, S - curNode, L)
     * the result is negative because I want to define the lower bound for each node
     *
     * @param graph
     * @param curNode
     * @param S
     * @param L
     * @param type
     * @return
     */
    private double calMarginGain(AbstractGraphNode[] graph, AbstractGraphNode curNode, Set<AbstractGraphNode> S, int L, ProblemType type, double fScore) {
        //System.out.println(curNode.getName());
        S.add(curNode);
        double marginGain = 0;
        switch (type) {
            case PROBLEM1:
                if (Common.INSTANCE.GREEDY_MODE == GreedyType.OUT_CACHE
                        || Common.INSTANCE.GREEDY_MODE == GreedyType.INSIDE_CACHE
                        || Common.INSTANCE.GREEDY_MODE == GreedyType.WITHOUT_CACHE)
                    marginGain = F1(graph, S, L) - fScore;//F is non-decreasing F1(S, u) - F1(S)
                else
                    marginGain = accurateF1(graph, S, L) - fScore;//F is non-decreasing F1(S, u) - F1(S)
                break;
            case PROBLEM2:
                marginGain = F2(graph, S, L) - fScore;
                break;
        }
        S.remove(curNode);
        return marginGain;
    }

    @Override
    public Collection<AbstractGraphNode> run(AbstractGraphNode[] graph, int K, int L, ProblemType type, OptimizationStrategy strategy) {
        logger.info("Enter GreedyAlgorithmImpl/run() K = {}, L = {}, mode = {}, optStrategy = {}", K, L, Common.INSTANCE.GREEDY_MODE, strategy);
        if (strategy == OptimizationStrategy.NONE)
            return baseRun(graph, K, L, type);
        Set<AbstractGraphNode> S = new HashSet<>();
        FibonacciHeap<AbstractGraphNode> fibHeap = new FibonacciHeap();
        //choose the first node
        final ReentrantLock mgLock = new ReentrantLock();
        ExecutorService threadPool = Utils.getThreadPool();
        for (int j = 0; j < graph.length; j++) {
            AbstractGraphNode curNode = graph[j];
            final int process = j;
            threadPool.execute(() -> {
                if (process % 10 == 0)
                    logger.info("process: {}/{}", process, graph.length);
                final Set<AbstractGraphNode> SPrime = new HashSet<>(S);
                double marginGain = calMarginGain(graph, curNode, SPrime, L, type, preFScore);
                try {
                    mgLock.lock();
                    fibHeap.enqueue(curNode, -marginGain);//fibHeap is a min-heap
                    if (marginGain > bestSoFar) {
                        bestSoFar = marginGain;
                        bestNode = curNode;
                        curFScore = preFScore + marginGain;
                    }
                } finally {
                    mgLock.unlock();
                }
            });
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            logger.error("{}", e);
        }
        S.add(bestNode);
        preFScore = curFScore;
        logger.info("first best node: {} - {}", bestNode.getName(), bestSoFar);
        Queue<RankPair> repo = new LinkedList<>();
        for (int i = 2; i <= K; ++i) {
            bestSoFar = 0;
            while (fibHeap.size() > 0 && bestSoFar <= -fibHeap.min().getPriority()) {//&& bestSoFar > fibHeap.min().getPriority()
                FibonacciHeap.Entry<AbstractGraphNode> entry = fibHeap.dequeueMin();
                AbstractGraphNode curNode = entry.getValue();
                if (!S.contains(curNode)) {
                    double marginGain = calMarginGain(graph, curNode, S, L, type, preFScore);
                    repo.add(new RankPair(curNode, marginGain));
                    if (marginGain > bestSoFar) {
                        bestSoFar = marginGain;
                        bestNode = curNode;
                        curFScore = preFScore + marginGain;
                    }
                }
            }
            S.add(bestNode);
            preFScore = curFScore;
            while (repo.size() > 0) {
                RankPair pair = repo.poll();
                fibHeap.enqueue(pair.getKey(), -pair.getValue());
            }
            logger.info("{}th best node: {} - {}", i, bestNode.getName(), bestSoFar);
        }
        bestSoFar = 0;
        bestNode = null;
        preFScore = 0.0;
        curFScore = 0.0;
        logger.info("Exit GreedyAlgorithmImpl/run()");
        return S;
    }

    public Collection<AbstractGraphNode> baseRun(AbstractGraphNode[] graph, int K, int L, ProblemType type) {
        logger.info("Enter GreedyAlgorithmImpl/run()");
        Set<AbstractGraphNode> S = new HashSet<>();
        //Map<AbstractGraphNode, Double> upperBound = new HashMap<>();
        AbstractGraphNode bestNode = graph[0];
        double preFScore = 0.0, curFScore = 0.0;
        //choose the first node
        for (int i = 1; i <= K; ++i) {
            double bestSoFar = 0;
            for (int j = 0; j < graph.length; j++) {
                AbstractGraphNode curNode = graph[j];
                if (!S.contains(curNode)) {
                    double marginGain = calMarginGain(graph, curNode, S, L, type, preFScore);
                    if (marginGain > bestSoFar) {
                        bestSoFar = marginGain;
                        bestNode = curNode;
                        curFScore = preFScore + marginGain;
                    }
                }
            }
            S.add(bestNode);
            preFScore = curFScore;
            logger.info("first best node: {} - {}", bestNode.getName(), bestSoFar);
        }
        return S;
    }


    class RankPair implements au.edu.rmit.randomwalk.model.Pair<AbstractGraphNode, Double> {

        @Override
        public AbstractGraphNode getKey() {
            return this.key;
        }

        @Override
        public java.lang.Double getValue() {
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