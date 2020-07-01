package au.edu.rmit.randomwalk.experiment;

import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import au.edu.rmit.randomwalk.model.OptimizationStrategy;
import au.edu.rmit.randomwalk.model.ProblemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author forrest0402
 * @Description
 * @date 2018/3/23
 */
@Service
public class Scalability extends AbstractBasicExp {

    private static Logger logger = LoggerFactory.getLogger(Scalability.class);

    final String MATRIX_SEL_FILE_PATH = EXP_PREFIX + "matrixsel.Scalability.txt";

    final String BOUND_SEL_FILE_PATH = EXP_PREFIX + "boundsel.Scalability.txt";

    final String APPRO_GREEDY_FILE_PATH = EXP_PREFIX + "approximategreedy.Scalability.txt";

    final String DP_GREEDY_FILE_PATH = EXP_PREFIX + "dpgreedy.Scalability.txt";

    final int K = 120, L = 12, T = 1;
    int startN = 250000, endN = 250000, deltaN = 250000;

    public Scalability(int limit){
        startN = limit;
        endN = limit;
        deltaN =limit;
    }

    private AbstractGraphNode[] readDataset(int limit) {
        return fileManager.readYoutubeNetwork(limit, Integer.MAX_VALUE);
    }

    //@PostConstruct
    private void matrixSel() {
        int T = 2;
        logger.info("Enter matrixSel - K = {}, T = {}", K, T);
        long[] timeList = new long[10];
        for (int t = 1; t <= T; ++t) {
            for (int limit = startN; limit <= endN; limit += deltaN) {
                AbstractGraphNode[] V = readDataset(limit);
                stopWatch.start();
                matrixGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.BoundSel);
                long result = stopWatch.stop();
                timeList[(limit - startN) / deltaN] += result;
            }
        }
        for (int i = 0; i < timeList.length; i++) {
            timeList[i] /= T;
        }
        fileManager.writeToFile(MATRIX_SEL_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        logger.info("Exit matrixSel");
    }


    //@PostConstruct
    public void boundSel() {
        logger.info("Enter boundSel - K = {}, T = {}", K, T);
        long[] timeList = new long[10];
        for (int t = 1; t <= T; ++t) {
            for (int limit = startN; limit <= endN; limit += deltaN) {

                double startMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()) / 1024d / 1024d;

                AbstractGraphNode[] V = readDataset(limit);
                stopWatch.start();
                matrixGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);
                long result = stopWatch.stop();
                timeList[(limit - startN) / deltaN] += result;

                double endMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()) / 1024d / 1024d;
                System.out.println("limit = "+ startN +" matrixSel memory :" + (endMemory - startMemory));
                System.out.println("limit = "+ startN +" time :" + result);
            }
        }
//        for (int i = 0; i < timeList.length; i++) {
//            timeList[i] /= T;
//        }
//        fileManager.writeToFile(BOUND_SEL_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        logger.info("Exit boundSel");
    }

    //@PostConstruct
    public void approximateGreedy() {
        logger.info("Enter approximateGreedy - K = {}, T = {}", K, T);
        long[] timeList = new long[10];
        for (int t = 1; t <= T; ++t) {
            for (int limit = startN; limit <= endN; limit += deltaN) {

                double startMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()) / 1024d / 1024d;


                AbstractGraphNode[] V = readDataset(limit);
                stopWatch.start();
                approximateGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);
                long result = stopWatch.stop();
                timeList[(limit - startN) / deltaN] += result;

                double endMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()) / 1024d / 1024d;
                System.out.println("limit = "+ startN +" matrixSel memory :" + (endMemory - startMemory));
                System.out.println("limit = "+ startN +" time :" + result);
            }
        }
//        for (int i = 0; i < timeList.length; i++) {
//            timeList[i] /= T;
//        }
//        fileManager.writeToFile(APPRO_GREEDY_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        logger.info("Exit approximateGreedy");
    }
}
