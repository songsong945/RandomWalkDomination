package au.edu.rmit.randomwalk.experiment;

import au.edu.rmit.randomwalk.algorithm.ApproximateGreedyImpl;
import au.edu.rmit.randomwalk.algorithm.DegreeImpl;
import au.edu.rmit.randomwalk.algorithm.LLengthRandomWalk;
import au.edu.rmit.randomwalk.algorithm.PageRankImpl;
import au.edu.rmit.randomwalk.io.FileManager;
import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import au.edu.rmit.randomwalk.model.OptimizationStrategy;
import au.edu.rmit.randomwalk.model.ProblemType;
import au.edu.rmit.randomwalk.util.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author forrest0402
 * @Description
 * @date 2018/3/20
 */
//@Service
public class VaryingK extends AbstractBasicExp {

    private static Logger logger = LoggerFactory.getLogger(VaryingK.class);

    final String MATRIX_SEL_FILE_PATH = EXP_PREFIX + "matrixsel.VaryingK.txt";

    final String BOUND_SEL_FILE_PATH = EXP_PREFIX + "boundsel.VaryingK.txt";

    final String APPRO_GREEDY_FILE_PATH = EXP_PREFIX + "approximategreedy.VaryingK.txt";

    final String DP_GREEDY_FILE_PATH = EXP_PREFIX + "dpgreedy.VaryingK.txt";

    final String DEGREE_GREEDY_FILE_PATH = EXP_PREFIX + "degreegreedy.VaryingK.txt";

    final String PageRank_FILE_PATH = EXP_PREFIX + "pagerank.VaryingK.txt";

    final String MATRIX_SEL_FILE_PATH_K = EXP_PREFIX + "matrixsel.Effectiveness.K.txt";

    final String BOUND_SEL_FILE_PATH_K = EXP_PREFIX + "boundsel.Effectiveness.K.txt";

    final String APPRO_GREEDY_FILE_PATH_K = EXP_PREFIX + "approximategreedy.Effectiveness.K.txt";

    final String DEGREE_GREEDY_FILE_PATH_K = EXP_PREFIX + "degreegreedy.Effectiveness.K.txt";

    final String DP_GREEDY_FILE_PATH_K = EXP_PREFIX + "dpgreedy.Effectiveness.K.txt";

    final String PageRank_FILE_PATH_K = EXP_PREFIX + "pagerank.Effectiveness.K.txt";

    final int L = 6, T = 1, startNK = 20, endNK = 100, deltaK = 20;

    private AbstractGraphNode[] readDataset() {
        return fileManager.readHepPhNetwork();
    }

    public double getScore(AbstractGraphNode[] graph, Collection<AbstractGraphNode> S, int L) {
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
                        int i = 0;
                        for (AbstractGraphNode n : v.getNeighbors()) {
                            if (!S.contains(n)) {
                                value += v.getWeights().get(i) / v.getWeightSum() * gammaP.get(n);
                                i++;
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
        int num = 0;
        for (AbstractGraphNode key : B.keySet()) {
            if(key.isRealNode()){
                if(B.get(key)> L){
                    gain += L;
                }else{
                    gain += B.get(key);
                }
                num++;
            }
        }
        return gain / num;
    }

    //@PostConstruct
    public void matrixSel() {
        logger.info("Enter matrixSel - L = {}, T = {}", L, T);
        long[] timeList = new long[30];
        double[] List = new double[30];
        for (int t = 1; t <= T; ++t) {
            for (int K = startNK; K <= endNK; K += deltaK) {
                AbstractGraphNode[] V = readDataset();
                stopWatch.start();
                Collection<AbstractGraphNode> S = matrixGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.BoundSel);
                long result = stopWatch.stop();
                timeList[K / startNK - 1] += result;
                double Result = getScore(V, S, L);
                List[(K - startNK) / deltaK] += Result;
            }
        }
        for (int i = 0; i < timeList.length; i++) {
            timeList[i] /= T;
            List[i] /= T;
        }
        fileManager.writeToFile(MATRIX_SEL_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        fileManager.writeToFile(MATRIX_SEL_FILE_PATH_K, Arrays.stream(List).boxed().collect(Collectors.toList()), false);
        logger.info("Exit matrixSel");
    }


    //@PostConstruct
    public void boundSel() {
        logger.info("Enter boundSel - L = {}, T = {}", L, T);
        long[] timeList = new long[30];
        double[] List = new double[30];
        for (int t = 1; t <= T; ++t) {
            for (int K = startNK; K <= endNK; K += deltaK) {
                AbstractGraphNode[] V = readDataset();
                stopWatch.start();
                Collection<AbstractGraphNode> S = matrixGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);
                long result = stopWatch.stop();
                timeList[K / startNK - 1] += result;
                System.out.println("time = " + result);
                double Result = getScore(V, S, L);
                List[(K - startNK) / deltaK] += Result;
                System.out.println("effect = " + Result);
            }
        }
        for (int i = 0; i < timeList.length; i++) {
            timeList[i] /= T;
            List[i] /= T;
        }
        fileManager.writeToFile(BOUND_SEL_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        fileManager.writeToFile(BOUND_SEL_FILE_PATH_K, Arrays.stream(List).boxed().collect(Collectors.toList()), false);
        logger.info("Exit boundSel");
    }

    //@PostConstruct
    public void approximateGreedy() {
        logger.info("Enter approximateGreedy - L = {}, T = {}", L, T);
        long[] timeList = new long[30];
        double[] List = new double[30];
        for (int t = 1; t <= T; ++t) {
            for (int K = startNK; K <= endNK; K += deltaK) {
                AbstractGraphNode[] V = readDataset();
                stopWatch.start();
                Collection<AbstractGraphNode> S = approximateGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);
                long result = stopWatch.stop();
                timeList[K / startNK - 1] += result;
                double Result = getScore(V, S, L);
                List[(K - startNK) / deltaK] += Result;
            }
        }
        for (int i = 0; i < timeList.length; i++) {
            timeList[i] /= T;
            List[i] /= T;
        }
        System.out.println("effect = " + List[0]);
        fileManager.writeToFile(APPRO_GREEDY_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        fileManager.writeToFile(APPRO_GREEDY_FILE_PATH_K, Arrays.stream(List).boxed().collect(Collectors.toList()), false);
        logger.info("Exit approximateGreedy");
    }

    //@PostConstruct
    public void dpGreedy() {
        logger.info("Enter dpGreedy - L = {}, T = {}", L, T);
        long[] timeList = new long[30];
        double[] List = new double[30];
        for (int t = 1; t <= T; ++t) {
            for (int K = startNK; K <= endNK; K += deltaK) {
                AbstractGraphNode[] V = readDataset();
                stopWatch.start();
                Collection<AbstractGraphNode> S = dpGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.BoundSel);
                long result = stopWatch.stop();
                timeList[K / startNK - 1] += result;
                double Result = getScore(V, S, L);
                List[(K - startNK) / deltaK] += Result;
            }
        }
        for (int i = 0; i < timeList.length; i++) {
            timeList[i] /= T;
            List[i] /= T;
        }
        fileManager.writeToFile(DP_GREEDY_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        fileManager.writeToFile(DP_GREEDY_FILE_PATH_K, Arrays.stream(List).boxed().collect(Collectors.toList()), false);
        logger.info("Exit dpGreedy");
    }

    public void degreeGreedy() {
        logger.info("Enter degreeGreedy - L = {}, T = {}", L, T);
        double[] timeList = new double[30];
        double[] List = new double[30];
        LLengthRandomWalk degreeGreedy = new DegreeImpl();
        for (int K = startNK; K <= endNK; K += deltaK) {
            AbstractGraphNode[] V = readDataset();
            stopWatch.start();
            Collection<AbstractGraphNode> S = degreeGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);
            long result = stopWatch.stop();
            double Result = getScore(V, S, L);
            timeList[(K - startNK) / deltaK] += result;
            List[(K - startNK) / deltaK] += Result;

        }
        fileManager.writeToFile(DEGREE_GREEDY_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        fileManager.writeToFile(DEGREE_GREEDY_FILE_PATH_K, Arrays.stream(List).boxed().collect(Collectors.toList()), false);
        logger.info("Exit degreeGreedy");
    }

    public void pagerank() {
        logger.info("Enter pagerank - L = {}, T = {}", L, T);
        double[] timeList = new double[30];
        double[] List = new double[30];
        PageRankImpl pageRank = new PageRankImpl();
        for (int K = startNK; K <= endNK; K += deltaK) {
            AbstractGraphNode[] V = readDataset();
            stopWatch.start();
            Collection<AbstractGraphNode> S = pageRank.run(V, K, 0.15);
            long result = stopWatch.stop();
            double Result = getScore(V, S, L);
            timeList[(K - startNK) / deltaK] += result;
            List[(K - startNK) / deltaK] += Result;
            System.out.println("effect = " + result);
        }
        fileManager.writeToFile(PageRank_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        fileManager.writeToFile(PageRank_FILE_PATH_K, Arrays.stream(List).boxed().collect(Collectors.toList()), false);
        logger.info("Exit degreeGreedy");
    }
}
