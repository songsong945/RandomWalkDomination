package au.edu.rmit.randomwalk.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author forrest0402
 * @Description
 * @date 3/9/2018
 */
@Component
public class StopWatch {

    private static Logger logger = LoggerFactory.getLogger(StopWatch.class);

    private long startTime = -1;

    public void start() {
        startTime = System.nanoTime();
    }

    public long stop() {
        if (startTime == -1)
            throw new IllegalStateException("call start first");
        long duration = System.nanoTime() - startTime;
        startTime = -1;
        return duration;
    }
}
