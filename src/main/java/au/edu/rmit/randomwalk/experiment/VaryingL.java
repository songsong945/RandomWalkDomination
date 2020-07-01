package au.edu.rmit.randomwalk.experiment;

import au.edu.rmit.randomwalk.algorithm.DegreeImpl;
import au.edu.rmit.randomwalk.algorithm.LLengthRandomWalk;
import au.edu.rmit.randomwalk.algorithm.PageRankImpl;
import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import au.edu.rmit.randomwalk.model.OptimizationStrategy;
import au.edu.rmit.randomwalk.model.ProblemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
//@Component
public class VaryingL extends AbstractBasicExp {

    private static Logger logger = LoggerFactory.getLogger(VaryingL.class);

    final String MATRIX_SEL_FILE_PATH = EXP_PREFIX + "matrixsel.VaryingL.txt";

    final String BOUND_SEL_FILE_PATH = EXP_PREFIX + "boundsel.VaryingL.txt";

    final String APPRO_GREEDY_FILE_PATH = EXP_PREFIX + "approximategreedy.VaryingL.txt";

    final String DP_GREEDY_FILE_PATH = EXP_PREFIX + "dpgreedy.VaryingL.txt";

    final String DEGREE_GREEDY_FILE_PATH = EXP_PREFIX + "degreegreedy.VaryingL.txt";

    final String PageRank_FILE_PATH = EXP_PREFIX + "pagerank.VaryingL.txt";

    final String MATRIX_SEL_FILE_PATH_L = EXP_PREFIX + "matrixsel.Effectiveness.L.txt";

    final String BOUND_SEL_FILE_PATH_L = EXP_PREFIX + "boundsel.Effectiveness.L.txt";

    final String APPRO_GREEDY_FILE_PATH_L = EXP_PREFIX + "approximategreedy.Effectiveness.L.txt";

    final String DEGREE_GREEDY_FILE_PATH_L = EXP_PREFIX + "degreegreedy.Effectiveness.L.txt";

    final String DP_GREEDY_FILE_PATH_L = EXP_PREFIX + "dpgreedy.Effectiveness.L.txt";

    final String PageRank_FILE_PATH_L = EXP_PREFIX + "pagerank.Effectiveness.L.txt";

    final int K = 60, T = 1, startNL = 2, endNL = 10, deltaL = 2;

    private AbstractGraphNode[] readDataset() {
        return fileManager.readHepPhNetwork();
    }

//    public double getScore(AbstractGraphNode[] graph, Collection<AbstractGraphNode> S, int L) {
//        double gain = 0.0;
//        Map<AbstractGraphNode, Double> B = new HashMap<>();
//        Map<AbstractGraphNode, Double> gamma = new HashMap<>(), gammaP = new HashMap<>();
//        for (int t = 1; t <= L; ++t) {
//            if (t == 1) {
//                for (AbstractGraphNode v : graph) {
//                    if (!S.contains(v)) {
//                        gamma.put(v, 1.0);
//                    }
//                }
//            } else {
//                for (AbstractGraphNode v : graph) {
//                    if (!S.contains(v)) {
//                        double value = 0.0;
//                        for (AbstractGraphNode n : v.getNeighbors()) {
//                            if (!S.contains(n)) {
//                                value += 1.0 / v.getNeighbors().size() * gammaP.get(n);
//                            }
//                        }
//                        gamma.put(v, value);
//                    }
//                }
//            }
//            for (Map.Entry<AbstractGraphNode, Double> entry : gamma.entrySet()) {
//                if (!S.contains(entry.getKey())) {
//                    Double v = B.get(entry.getKey());
//                    if (v == null) {
//                        B.put(entry.getKey(), entry.getValue());
//                    } else {
//                        B.put(entry.getKey(), v + entry.getValue());
//                    }
//                    gammaP.put(entry.getKey(), entry.getValue());
//                }
//            }
//        }
//        for (Double value : B.values()) {
//            gain += value;
//        }
//        return gain / (graph.length * 1.0 - S.size());
//    }

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
        return gain / (num);
    }


    //@PostConstruct
    public void matrixSel() {
        logger.info("Enter matrixSel - K = {}, T = {}", K, T);
        long[] timeList = new long[25];
        double[] List = new double[25];
        for (int t = 1; t <= T; ++t) {
            for (int L = startNL; L <= endNL; L += deltaL) {
                AbstractGraphNode[] V = readDataset();
                stopWatch.start();
                Collection<AbstractGraphNode> S = matrixGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.BoundSel);
                long result = stopWatch.stop();
                timeList[L / startNL - 1] += result;
                double Result = getScore(V, S, L);
                List[(L - startNL) / deltaL] += Result;
            }
        }
        for (int i = 0; i < timeList.length; i++) {
            timeList[i] /= T;
            List[i] /= T;
        }
        fileManager.writeToFile(MATRIX_SEL_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        fileManager.writeToFile(MATRIX_SEL_FILE_PATH_L, Arrays.stream(List).boxed().collect(Collectors.toList()), false);
        logger.info("Exit matrixSel");
    }


    //@PostConstruct
    public void boundSel() {
        logger.info("Enter boundSel - K = {}, T = {}", K, T);
        long[] timeList = new long[25];
        double[] List = new double[25];
        for (int t = 1; t <= T; ++t) {
            for (int L = startNL; L <= endNL; L += deltaL) {
                AbstractGraphNode[] V = readDataset();
                stopWatch.start();
                Collection<AbstractGraphNode> S = matrixGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);
                long result = stopWatch.stop();
                timeList[L / startNL - 1] += result;
                System.out.println("time = " + result);
                double Result = getScore(V, S, L);
                List[(L - startNL) / deltaL] += Result;
                System.out.println("effect = " + Result);
            }
        }
        for (int i = 0; i < timeList.length; i++) {
            timeList[i] /= T;
            List[i] /= T;
        }
        fileManager.writeToFile(BOUND_SEL_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        fileManager.writeToFile(BOUND_SEL_FILE_PATH_L, Arrays.stream(List).boxed().collect(Collectors.toList()), false);
        logger.info("Exit boundSel");
    }

   // @PostConstruct
    public void approximateGreedy() {
        logger.info("Enter approximateGreedy - K = {}, T = {}", K, T);
        long[] timeList = new long[25];
        double[] List = new double[25];
        for (int t = 1; t <= T; ++t) {
            for (int L = startNL; L <= endNL; L += deltaL) {
                AbstractGraphNode[] V = readDataset();
                stopWatch.start();
                Collection<AbstractGraphNode> S = approximateGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);
                long result = stopWatch.stop();
                timeList[L / startNL - 1] += result;
                double Result = getScore(V, S, L);
                List[(L - startNL) / deltaL] += Result;
            }
        }
        for (int i = 0; i < timeList.length; i++) {
            timeList[i] /= T;
            List[i] /= T;
        }
        fileManager.writeToFile(APPRO_GREEDY_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        fileManager.writeToFile(APPRO_GREEDY_FILE_PATH_L, Arrays.stream(List).boxed().collect(Collectors.toList()), false);
        logger.info("Exit approximateGreedy");
    }

    //@PostConstruct
    public void dpGreedy() {
        logger.info("Enter dpGreedy - K = {}, T = {}", K, T);
        long[] timeList = new long[25];
        double[] List = new double[25];
        for (int t = 1; t <= T; ++t) {
            for (int L = startNL; L <= endNL; L += deltaL) {
                AbstractGraphNode[] V = readDataset();
                stopWatch.start();
                Collection<AbstractGraphNode> S = dpGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.BoundSel);
                long result = stopWatch.stop();
                timeList[L / startNL - 1] += result;
                double Result = getScore(V, S, L);
                List[(L - startNL) / deltaL] += Result;
            }
        }
        for (int i = 0; i < timeList.length; i++) {
            timeList[i] /= T;
            List[i] /= T;
        }
        fileManager.writeToFile(DP_GREEDY_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        fileManager.writeToFile(DP_GREEDY_FILE_PATH_L, Arrays.stream(List).boxed().collect(Collectors.toList()), false);
        logger.info("Exit dpGreedy");
    }

    public void degreeGreedy() {
        logger.info("Enter degreeGreedy - K = {}, T = {}", K, T);
        double[] timeList = new double[25];
        double[] List = new double[25];
        LLengthRandomWalk degreeGreedy = new DegreeImpl();
        for (int L = startNL; L <= endNL; L += deltaL) {
            AbstractGraphNode[] V = readDataset();
            stopWatch.start();
            Collection<AbstractGraphNode> S = degreeGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);
            long result = stopWatch.stop();
            double Result = getScore(V, S, L);
            timeList[(L - startNL) / deltaL] += result;
            List[(L - startNL) / deltaL] += Result;
        }
        fileManager.writeToFile(DEGREE_GREEDY_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        fileManager.writeToFile(DEGREE_GREEDY_FILE_PATH_L, Arrays.stream(List).boxed().collect(Collectors.toList()), false);
        logger.info("Exit degreeGreedy");
    }

    public void pagerank() {
        logger.info("Enter pagerank - L = {}, T = {}", K, T);
        double[] timeList = new double[30];
        double[] List = new double[25];
        PageRankImpl pageRank = new PageRankImpl();
        for (int L = startNL; L <= endNL; L += deltaL) {
            AbstractGraphNode[] V = readDataset();
            stopWatch.start();
            Collection<AbstractGraphNode> S = pageRank.run(V, K, 0.15);
            long result = stopWatch.stop();
            double Result = getScore(V, S, L);
            timeList[(L - startNL) / deltaL] += result;
            List[(L - startNL) / deltaL] += Result;
        }
        fileManager.writeToFile(PageRank_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        fileManager.writeToFile(PageRank_FILE_PATH_L, Arrays.stream(List).boxed().collect(Collectors.toList()), false);
        logger.info("Exit pagerank");
    }
}
