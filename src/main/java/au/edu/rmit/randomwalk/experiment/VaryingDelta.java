package au.edu.rmit.randomwalk.experiment;

import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import au.edu.rmit.randomwalk.model.OptimizationStrategy;
import au.edu.rmit.randomwalk.model.ProblemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class VaryingDelta extends AbstractBasicExp {

    private static Logger logger = LoggerFactory.getLogger(VaryingL.class);

    final String MATRIX_SEL_FILE_PATH = EXP_PREFIX + "matrixsel.VaryingL.txt";

    final String BOUND_SEL_FILE_PATH = EXP_PREFIX + "boundsel.VaryingL.txt";

    final int K = 60, T = 1, L = 6;

    private AbstractGraphNode[] readDataset(int delta) {
        return fileManager.readHepPhNetwork(delta);
    }

    public void matrixSel() {
        logger.info("Enter matrixSel - K = {}, T = {}", K, T);
        long[] timeList = new long[25];
        //double[] List = new double[25];
        for (int t = 1; t <= T; ++t) {
            for (int delta = 5; delta <= 25; delta += 5) {
                AbstractGraphNode[] V = readDataset(delta);
                stopWatch.start();
                Collection<AbstractGraphNode> S = matrixGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.BoundSel);
                long result = stopWatch.stop();
                timeList[delta / 5 - 1] += result;
            }
        }
        for (int i = 0; i < timeList.length; i++) {
            timeList[i] /= T;
        }
        fileManager.writeToFile(MATRIX_SEL_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        logger.info("Exit matrixSel");
    }


    public void boundSel() {
        logger.info("Enter boundSel - K = {}, T = {}", K, T);
        long[] timeList = new long[25];
        //double[] List = new double[25];
        for (int t = 1; t <= T; ++t) {
            for (int delta = 5; delta <= 25; delta += 5) {
                AbstractGraphNode[] V = readDataset(delta);
                stopWatch.start();
                Collection<AbstractGraphNode> S = matrixGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);
                long result = stopWatch.stop();
                timeList[delta / 5 - 1] += result;
            }
        }
        for (int i = 0; i < timeList.length; i++) {
            timeList[i] /= T;
        }
        fileManager.writeToFile(BOUND_SEL_FILE_PATH, Arrays.stream(timeList).boxed().collect(Collectors.toList()), false);
        logger.info("Exit boundSel");
    }
}
