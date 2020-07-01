package au.edu.rmit.randomwalk.experiment;

import au.edu.rmit.randomwalk.algorithm.ApproximateGreedyImpl;
import au.edu.rmit.randomwalk.io.FileManager;
import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import au.edu.rmit.randomwalk.model.OptimizationStrategy;
import au.edu.rmit.randomwalk.model.ProblemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class VaryingR  {
    private static Logger logger = LoggerFactory.getLogger(VaryingL.class);
    public final String EXP_PREFIX = "exp/";
    FileManager fileManager = new FileManager();
    final String APPRO_GREEDY_FILE_PATH = EXP_PREFIX + "approximategreedy.VaryingR.txt";
    final int K = 10, L = 100, startN = 50, endN = 500, delta = 50;

    private AbstractGraphNode[] readDataset() {
        return fileManager.readGraphData1000();
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

    //@PostConstruct
//    public void approximateGreedy() {
//        ApproximateGreedyImpl approximateGreedy = new ApproximateGreedyImpl();
//        double[] timeList = new double[20];
//        AbstractGraphNode[] V = readDataset();
//        for (int R = startN; R <= endN; R += delta) {
//            logger.info("Enter approximateGreedy - R = {}", R);
//            Collection<AbstractGraphNode> S = approximateGreedy.run2(V, K, L, R, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);
//            double result = getScore(V, S, L);
//            System.out.println(result);
//            timeList[(R - startN) / delta] += result;
//        }
//        fileManager.writeToFile(APPRO_GREEDY_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
//        logger.info("Exit approximateGreedy");
//    }
}
