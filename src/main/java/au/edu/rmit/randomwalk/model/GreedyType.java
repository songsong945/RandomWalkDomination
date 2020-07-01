package au.edu.rmit.randomwalk.model;

import org.springframework.stereotype.Component;

/**
 * @author forrest0402
 * @Description
 * @date 2/27/2018
 */
public enum GreedyType {

    //using BigDecimal

    ACCU_OUT_CACHE,
    ACCU_INSIDE_CACHE,
    ACCU_WITHOUT_CACHE,

    //using double
    OUT_CACHE,
    INSIDE_CACHE,
    WITHOUT_CACHE;
}
