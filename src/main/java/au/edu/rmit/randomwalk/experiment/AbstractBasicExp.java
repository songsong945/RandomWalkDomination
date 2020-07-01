package au.edu.rmit.randomwalk.experiment;

import au.edu.rmit.randomwalk.algorithm.ApproximateGreedyImpl;
import au.edu.rmit.randomwalk.algorithm.GreedyAlgorithmImpl;
import au.edu.rmit.randomwalk.algorithm.LLengthRandomWalk;
import au.edu.rmit.randomwalk.algorithm.MatrixGreedyImpl;
import au.edu.rmit.randomwalk.io.FileManager;
import au.edu.rmit.randomwalk.util.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author forrest0402
 * @Description
 * @date 2018/3/20
 */
@Component
public abstract class AbstractBasicExp {

    public final String EXP_PREFIX = "exp/HepPh/";

    ApproximateGreedyImpl approximateGreedy = new ApproximateGreedyImpl();
    MatrixGreedyImpl matrixGreedy = new MatrixGreedyImpl();
    GreedyAlgorithmImpl dpGreedy = new GreedyAlgorithmImpl();
    StopWatch stopWatch = new StopWatch();
    FileManager fileManager = new FileManager();


    @Autowired
    @Qualifier("MonteCarloImpl")
    LLengthRandomWalk sampleGreedy;

    @Autowired
    @Qualifier("DegreeImpl")
    LLengthRandomWalk degreeGreedy;

    @Autowired
    @Qualifier("RandomImpl")
    LLengthRandomWalk randomImpl;


}
