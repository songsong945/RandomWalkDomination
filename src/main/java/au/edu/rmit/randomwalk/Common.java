package au.edu.rmit.randomwalk;

import au.edu.rmit.randomwalk.model.GreedyType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author forrest0402
 * @Description
 * @date 2/25/2018
 */

public enum Common {

    INSTANCE;

    public final int TRHEAD_NUMBER = 1;

    public final int RANDOM_WALK_TIMES = 500;

    public final int DIRECTED_GRAPH = 1;

    /**
     * 1:accurate outside cache
     * 2:accurate inside cache
     * 3:accurate without cache
     * 4:outside cache
     * 5:inside cache
     * 6:without cache
     */
    public final GreedyType GREEDY_MODE = GreedyType.INSIDE_CACHE;

}
