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
 * change the degree of top-K nodes
 *
 * @author forrest0402
 * @Description
 * @date 2018/4/1
 */
//@Service
public class Degree extends AbstractBasicExp {

    private static Logger logger = LoggerFactory.getLogger(Degree.class);

    final String MATRIX_SEL_FILE_PATH = EXP_PREFIX + "matrixsel.Degree.txt";

    final String BOUND_SEL_FILE_PATH = EXP_PREFIX + "boundsel.Degree.txt";

    final String APPRO_GREEDY_FILE_PATH = EXP_PREFIX + "approximategreedy.Degree.txt";

    final int K = 20, L = 5, T = 5, startN = 0, endN = 500, deltaN = 50;

    private AbstractGraphNode[] readDataset(int degree) {
        return fileManager.readEpinionsSocialNetwork(K, degree);
    }

    @PostConstruct
    private void boundSel() {
        logger.info("Enter boundSel - K = {}, T = {}", K, T);
        long[] timeList = new long[20];
        for (int t = 1; t <= T; ++t) {
            for (int limit = startN; limit <= endN; limit += deltaN) {
                AbstractGraphNode[] V = readDataset(limit);
                stopWatch.start();
                matrixGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);
                long result = stopWatch.stop();
                timeList[(limit - startN) / deltaN] += result;
            }
        }
        for (int i = 0; i < timeList.length; i++) {
            timeList[i] /= T;
        }
        fileManager.writeToFile(BOUND_SEL_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        logger.info("Exit boundSel");
    }

    @PostConstruct
    private void approximateGreedy() {
        logger.info("Enter approximateGreedy - K = {}, T = {}", K, T);
        long[] timeList = new long[20];
        for (int t = 1; t <= T; ++t) {
            for (int limit = startN; limit <= endN; limit += deltaN) {
                AbstractGraphNode[] V = readDataset(limit);
                stopWatch.start();
                approximateGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);
                long result = stopWatch.stop();
                timeList[(limit - startN) / deltaN] += result;
            }
        }
        for (int i = 0; i < timeList.length; i++) {
            timeList[i] /= T;
        }
        fileManager.writeToFile(APPRO_GREEDY_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        logger.info("Exit approximateGreedy");
    }

}
