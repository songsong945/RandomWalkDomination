package au.edu.rmit.randomwalk.experiment;

import au.edu.rmit.randomwalk.Common;
import au.edu.rmit.randomwalk.algorithm.*;
import au.edu.rmit.randomwalk.io.FileManager;
import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import au.edu.rmit.randomwalk.model.OptimizationStrategy;
import au.edu.rmit.randomwalk.model.ProblemType;
import au.edu.rmit.randomwalk.util.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author forrest0402
 * @Description
 * @date 2018/4/2
 */
//@Service
public class Effectiveness extends AbstractBasicExp {

    private static Logger logger = LoggerFactory.getLogger(Effectiveness.class);
    StopWatch stopWatch = new StopWatch();

    final String BOUND_SEL_FILE_PATH_K = EXP_PREFIX + "boundsel.Effectiveness.K.txt";

    final String APPRO_GREEDY_FILE_PATH_K = EXP_PREFIX + "approximategreedy.Effectiveness.K.txt";

    final String DEGREE_GREEDY_FILE_PATH_K = EXP_PREFIX + "degreegreedy.Effectiveness.K.txt";

    final String BOUND_SEL_FILE_PATH_L = EXP_PREFIX + "boundsel.Effectiveness.L.txt";

    final String APPRO_GREEDY_FILE_PATH_L = EXP_PREFIX + "approximategreedy.Effectiveness.L.txt";

    final String DEGREE_GREEDY_FILE_PATH_L = EXP_PREFIX + "degreegreedy.Effectiveness.L.txt";

    final String RANDOM_FILE_PATH = EXP_PREFIX + "random.Effectiveness.txt";

    final String PAGERANK_FILE_PATH = EXP_PREFIX + "pagerank.Effectiveness.txt";

    FileManager fileManager = new FileManager();

    final int K = 120, L = 12, startNK = 50, endNK = 250, deltaK = 50,startNL = 3, endNL = 15, deltaL = 3, F = 1000, num=3000;

    private AbstractGraphNode[] readDataset() {
        return fileManager.readEUCoreNetwork();
    }

    private double getScore(AbstractGraphNode[] graph, Collection<AbstractGraphNode> S, int L) {
        double gain = 0.0;
        Map<AbstractGraphNode, Double> B = new HashMap<>();
        Map<AbstractGraphNode, Double> gamma = new HashMap<>(), gammaP = new HashMap<>();
        for (int t = 1; t <= L; ++t) {
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
                        for (AbstractGraphNode n : v.getNeighbors()) {
                            if (!S.contains(n)) {
                                value += 1.0 / v.getNeighbors().size() * gammaP.get(n);
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
        for (Double value : B.values()) {
            gain += value;
        }
        return gain / (graph.length * 1.0 - S.size());
    }

    @PostConstruct
    public void boundSel() {
        logger.info("Enter boundSel - K = {}", K);
        double[] timeList = new double[20];
        for (int K = 40; K <= 200; K += 40) {
            AbstractGraphNode[] V = readDataset();
            Collection<AbstractGraphNode> S = matrixGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);
            double result = getScore(V, S, L);
            timeList[(K - 40) / 40] += result;
        }

        fileManager.writeToFile(BOUND_SEL_FILE_PATH_K, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        logger.info("Exit boundSel");
    }

    @PostConstruct
    public void approximateGreedy() {
        logger.info("Enter approximateGreedy - K = {}", K);
        double[] timeList = new double[20];
        for (int K = 40; K <= 200; K += 40) {
            AbstractGraphNode[] V = readDataset();
            Collection<AbstractGraphNode> S = approximateGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);
            double result = getScore(V, S, L);
            timeList[(K - 40) / 40] += result;
        }
        fileManager.writeToFile(APPRO_GREEDY_FILE_PATH_K, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        logger.info("Exit approximateGreedy");
    }
}
