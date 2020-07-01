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

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author forrest0402
 * @Description
 * @date 2/12/2018
 */
@Service("MatrixGreedyImpl")
public class MatrixGreedyImpl implements LLengthRandomWalk {

    private static Logger logger = LoggerFactory.getLogger(MatrixGreedyImpl.class);

    private double bestSoFar = 0;
    private AbstractGraphNode bestNode = null;
    private double preFScore = 0.0, curFScore = 0.0;

    /**
     * @param graph
     * @param K     the size of target set
     * @param T     the budget
     * @param type
     * @return
     */
    public Collection<AbstractGraphNode> baseRun(AbstractGraphNode[] graph, final int K, final int T, final ProblemType type) {
        Collection<AbstractGraphNode> S = new HashSet<>(K);
        double preFScore = 0.0, curFScore = 0.0;
        for (int i = 1; i <= K; ++i) {
            AbstractGraphNode u = null;
            double optimal = 0;
            for (AbstractGraphNode v : graph) {
                if (!S.contains(v)) {
                    double marginalGain = calMarginGain(graph, v, T, S, preFScore);
                    if (optimal < marginalGain) {
                        optimal = marginalGain;
                        u = v;
                        curFScore = preFScore + marginalGain;
                    }
                    S.remove(v);
                }
            }
            S.add(u);
            preFScore = curFScore;
            logger.info("{}th best node: {} - {}", i, u.getName(), optimal);
        }
        return S;
    }

    private double calMarginGain(AbstractGraphNode[] graph, AbstractGraphNode curNode, int T, Collection<AbstractGraphNode> S, double fScore) {
        S.add(curNode);
        Map<AbstractGraphNode, Double> B = fastNI(graph, T, S);
        int num = 0;
        double tmp = 0.0;
        for (AbstractGraphNode key : B.keySet()) {
            if(key.getId()>0){
                tmp += B.get(key);
                num++;
            }
        }
        double marginalGain = num * T - tmp;
        S.remove(curNode);
        return marginalGain - fScore;
    }

    private void firstNodeSelectionWithMultiThreads(AbstractGraphNode[] graph, FibonacciHeap<AbstractGraphNode> fibHeap,
                                                    Set<AbstractGraphNode> S, int T) {
        final ReentrantLock mgLock = new ReentrantLock();
        ExecutorService threadPool = Utils.getThreadPool();
        for (int j = 0; j < graph.length; j++) {
            final AbstractGraphNode curNode = graph[j];
            if(!curNode.isRealNode()){
                fibHeap.enqueue(curNode, 0);
                continue;
            }
            final int process = j;
            threadPool.execute(() -> {
                if (process % 100 == 0)
                    logger.info("process: {}/{}", process, graph.length);
                final Set<AbstractGraphNode> SPrime = new HashSet<>(S);
                double marginGain = calMarginGain(graph, curNode, T, SPrime, preFScore);
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
    }

    @Override
    public Collection<AbstractGraphNode> run(AbstractGraphNode[] graph, final int K, final int T, final ProblemType type, OptimizationStrategy strategy) {
        logger.info("Enter MatrixGreedyImpl/run() K = {}, T = {}, optStrategy = {}", K, T, strategy);
        if (strategy == OptimizationStrategy.NONE)
            return baseRun(graph, K, T, null);
        final Set<AbstractGraphNode> S = new HashSet<>();
        final FibonacciHeap<AbstractGraphNode> fibHeap = new FibonacciHeap();
        //choose the first node
        List<Map.Entry<AbstractGraphNode, Double>> upperBound;
        if (strategy == OptimizationStrategy.REORDER) {
            upperBound = new ArrayList<>(calUpperBound(graph, T, S).entrySet());
            upperBound.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

            int index = 0;
            for (; index < upperBound.size()&&upperBound.get(index).getKey().getId()>=0; ++index) {
                Map.Entry<AbstractGraphNode, Double> entry = upperBound.get(index);
                double marginGain = entry.getValue();//graph.length * T -
                if (bestSoFar > marginGain || index>1000) {
                    logger.info("calculation times: {}", index);
                    break;
                }
                if(!entry.getKey().isRealNode()){
                    fibHeap.enqueue(entry.getKey(), 0);
                    continue;
                }
                marginGain = calMarginGain(graph, entry.getKey(), T, S, preFScore);
                //System.out.println(entry.getKey().getName() + ", " + marginGain);
                fibHeap.enqueue(entry.getKey(), -marginGain);//fibHeap is a min-heap
                if (marginGain > bestSoFar) {
                    bestSoFar = marginGain;
                    bestNode = entry.getKey();
                    curFScore = preFScore + marginGain;
                }
            }
            for (; index < upperBound.size()&&upperBound.get(index).getKey().getId()>=0; ++index) {
                Map.Entry<AbstractGraphNode, Double> entry = upperBound.get(index);
                fibHeap.enqueue(entry.getKey(), -(entry.getValue()));//fibHeap is a min-heap
            }
        } else
            firstNodeSelectionWithMultiThreads(graph, fibHeap, S, T);
        S.add(bestNode);
        preFScore = curFScore;
        logger.info("first best node: {} - {}", bestNode.getName(), bestSoFar);
        Queue<RankPair> repo = new LinkedList<>();
        for (int i = 2; i <= K; ++i) {
            bestSoFar = 0;
            int count = 0;
            while (fibHeap.size() > 0 && bestSoFar <= -fibHeap.min().getPriority()) {
                ++count;
                FibonacciHeap.Entry<AbstractGraphNode> entry = fibHeap.dequeueMin();
                AbstractGraphNode curNode = entry.getValue();
                if (!S.contains(curNode)&&curNode.isRealNode()) {
                    double marginGain = calMarginGain(graph, curNode, T, S, preFScore);
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
            logger.info("{}th best node: {} - {} - calculation times: {}", i, bestNode.getName(), bestSoFar, count);
        }
        bestSoFar = 0;
        bestNode = null;
        preFScore = 0.0;
        curFScore = 0.0;
        logger.info("Exit MatrixGreedyImpl/run()");
        return S;
    }

    private Map<AbstractGraphNode, Double> fastNI(AbstractGraphNode[] graph, final int T, Collection<AbstractGraphNode> S) {
        Map<AbstractGraphNode, Double> B = new HashMap<>();
        Map<AbstractGraphNode, Double> gamma = new HashMap<>(), gammaP = new HashMap<>();
        for (int t = 1; t <= T; ++t) {
            if (t == 1) {
                for (AbstractGraphNode v : graph) {
                    if (!S.contains(v)) {
                        gamma.put(v, 1.0);
                    }
                }
            } else {
                for (AbstractGraphNode v : graph) {
                    if (!S.contains(v)) {
                        double value = 0.0;
                        //BigDecimal exactValue = new BigDecimal(0.0);
                        int i = 0;
                        for (AbstractGraphNode n : v.getNeighbors()) {
                            if (!S.contains(n)) {
                                value += v.getWeights().get(i) / v.getWeightSum() * gammaP.get(n);
                                i++;
                                //exactValue = exactValue.add(new BigDecimal(1.0 / v.getNeighbors().size()).multiply(new BigDecimal(gammaP.get(n))));
                            }
                        }
                        gamma.put(v, value);
                    }
                }
            }
            for (Map.Entry<AbstractGraphNode, Double> entry : gamma.entrySet()) {
                if (!S.contains(entry.getKey())) {
                    Double v = B.get(entry.getKey());
                    if (v == null) {
                        B.put(entry.getKey(), entry.getValue());
                    } else {
                        B.put(entry.getKey(), v + entry.getValue());
                    }
                    gammaP.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return B;
    }

    private Map<AbstractGraphNode, Double> calUpperBound(AbstractGraphNode[] graph, int T, Collection<AbstractGraphNode> S) {
        Map<AbstractGraphNode, Double> B = new HashMap<>();
        Map<AbstractGraphNode, Double> gamma = new HashMap<>(), gammaP = new HashMap<>();
        for (AbstractGraphNode v : graph) {
            gammaP.put(v, 1.0);
        }
        for (int t = 1; t <= T; ++t) {
            for (AbstractGraphNode v : graph) {
                double value = 0.0;
                int i = 0;
                for (AbstractGraphNode n : v.getNeighbors()) {
                    if (v.getName().equals(n.getName())) continue;
                    value += gammaP.get(n) * v.getWeights().get(i) / n.getWeightSum();
                }
                gamma.put(v, value);
            }
            for (AbstractGraphNode v : graph) {
                Double value = B.get(v);
                if (value == null) {
                    B.put(v, gamma.get(v));
                } else {
                    B.put(v, value + gamma.get(v) * t);
                }
                gammaP.put(v, gamma.get(v));
            }
        }
//        B.entrySet().forEach(entry -> {
//            entry.setValue( graph.length * T - entry.getValue());
//        });
        return B;
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
