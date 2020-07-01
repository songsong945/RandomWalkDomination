package au.edu.rmit.randomwalk.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author forrest0402
 * @Description
 * @date 2/6/2018
 */
@Component
@Aspect
public class TimeAspect {

    private static Logger logger = LoggerFactory.getLogger(TimeAspect.class);

    @Around("execution(public * au.edu.rmit.randomwalk.algorithm.LLengthRandomWalk.*(..))")
    public Object printExecTime(ProceedingJoinPoint joinPoint) throws Throwable {
        double startMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024d / 1024d;
        long begin = System.nanoTime();
        Object o = joinPoint.proceed();
        long end = System.nanoTime();
        double endMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024d / 1024d;
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(joinPoint.getTarget().getClass()).append(".").append(joinPoint.getSignature().getName())
                .append(" : ").append((end - begin) / 1000000).append(" ms");
        logger.info(strBuilder.toString());
        logger.info("memory : {} Megabyte", endMemory - startMemory);
        return o;
    }
}
