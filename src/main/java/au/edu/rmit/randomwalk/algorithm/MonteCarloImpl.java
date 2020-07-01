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
@Service("MonteCarloImpl")
public class MonteCarloImpl implements LLengthRandomWalk {

    private static Logger logger = LoggerFactory.getLogger(MonteCarloImpl.class);

    private double bestSoFar = 0;
    private AbstractGraphNode bestNode = null;
    private double preFScore = 0.0, curFScore = 0.0;

    private double calMarginGain(AbstractGraphNode[] graph, AbstractGraphNode curNode, Set<AbstractGraphNode> S, int L, ProblemType type, double fScore) {
        S.add(curNode);
        double marginGain = 0;
        SecureRandom random = new SecureRandom();
        for (AbstractGraphNode node : graph) {
            if (!S.contains(node)) {
                double avgHitLength = 0;
                for (int i = 0; i < Common.INSTANCE.RANDOM_WALK_TIMES; ++i) {
                    AbstractGraphNode nextNode = node;
                    int hitNumber = L;
                    for (int t = 0; t <= L; ++t) {
                        if (S.contains(nextNode)) {
                            hitNumber = t;
                            break;
                        }
                        if (nextNode.getNeighbors().size() > 0)
                            nextNode = nextNode.getNeighbors().get(random.nextInt(nextNode.getNeighbors().size()));
                    }
                    avgHitLength += hitNumber;
                }
                marginGain += avgHitLength / Common.INSTANCE.RANDOM_WALK_TIMES;
            }
        }
        marginGain = graph.length * L - marginGain - fScore;
        S.remove(curNode);
        return marginGain;
    }

    @Override
    public Collection<AbstractGraphNode> run(AbstractGraphNode[] graph, int K, int L, ProblemType type, OptimizationStrategy strategy) {
        logger.info("Enter MonteCarloImpl/run() K = {}, L = {}", K, L);
        Set<AbstractGraphNode> S = new HashSet<>();
        FibonacciHeap<AbstractGraphNode> fibHeap = new FibonacciHeap();
        //choose the first node
        final ReentrantLock mgLock = new ReentrantLock();
        ExecutorService threadPool = Utils.getThreadPool();
        for (int j = 0; j < graph.length; j++) {
            AbstractGraphNode curNode = graph[j];
            final int process = j;
            threadPool.execute(() -> {
                if (process % 50 == 0)
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
            while (fibHeap.size() > 0 && bestSoFar <= -fibHeap.min().getPriority()) {
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
        logger.info("Exit MonteCarloImpl/run()");
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
