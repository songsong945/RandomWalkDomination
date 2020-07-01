package au.edu.rmit.randomwalk.util;

import au.edu.rmit.randomwalk.Common;
import au.edu.rmit.randomwalk.io.FileManager;
import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author forrest0402
 * @Description
 * @date 3/9/2018
 */
public class Utils {

    private static Logger logger = LoggerFactory.getLogger(Utils.class);

    private static FileManager fileManager = new FileManager();

    public static synchronized ExecutorService getThreadPool() {
        return new ThreadPoolExecutor(Common.INSTANCE.TRHEAD_NUMBER, Common.INSTANCE.TRHEAD_NUMBER, 6000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }

    public static void printDegreeDistributionOfGraph(AbstractGraphNode[] graph) {
        //key for degree, value for the number of nodes
        Map<Integer, Integer> map = new HashMap<>();
        List<String> data = new ArrayList<>();
        for (AbstractGraphNode node : graph) {
            Integer nodeNumber = map.get(node.getNeighbors().size());
            if (nodeNumber == null) {
                map.put(node.getNeighbors().size(), 1);
            } else {
                map.put(node.getNeighbors().size(), nodeNumber + 1);
            }
            data.add(node.getNeighbors().size() + "");
        }
//        map.entrySet().forEach(entry -> {
//            data.add(entry.getKey() + "," + entry.getValue());
//        });
        fileManager.writeToFile("statistics.csv", data, false);
    }

    private static int[] randomArray = null;
    private static final int MAX_SIZE = 75000 * 500 * 5;
    private static int randomIndex = 0;

    public static int getRandomNumber() {
        if (randomArray == null) {
            randomArray = new int[MAX_SIZE];
            Random random = new Random();
            for (int i = 0; i < randomArray.length; i++) {
                randomArray[i] = random.nextInt(MAX_SIZE);
            }
        }
        if (randomIndex == MAX_SIZE) randomIndex = 0;
        return randomArray[randomIndex++];
    }
}
