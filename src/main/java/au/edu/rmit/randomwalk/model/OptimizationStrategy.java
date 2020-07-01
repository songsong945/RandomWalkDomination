package au.edu.rmit.randomwalk.model;

/**
 * @author forrest0402
 * @Description
 * @date 3/8/2018
 */
public enum OptimizationStrategy {
    NONE,
    BoundSel, //lazy forward
    REORDER // re-order all nodes in the first round by estimating the upperbound of each node
}
