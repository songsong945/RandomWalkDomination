package au.edu.rmit.randomwalk.experiment;

import au.edu.rmit.randomwalk.algorithm.DegreeImpl;
import au.edu.rmit.randomwalk.algorithm.LLengthRandomWalk;
import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import au.edu.rmit.randomwalk.model.OptimizationStrategy;
import au.edu.rmit.randomwalk.model.ProblemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mem extends AbstractBasicExp  {
    private static Logger logger = LoggerFactory.getLogger(VaryingK.class);

    private AbstractGraphNode[] readDataset() {
        return fileManager.readGrQcNetwork();
    }

    final int K = 60;
    public int L ;

    public Mem(int L){
        this.L = L;
    }

    public void matrixSel() {
        logger.info("Enter matrixSel - L = {}, T = {}", L);

        double startMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()) / 1024d / 1024d;

        AbstractGraphNode[] V = readDataset();
        matrixGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.BoundSel);

        double endMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()) / 1024d / 1024d;
        System.out.println("L = "+ L +" matrixSel memory :" + (endMemory - startMemory));

        logger.info("Exit matrixSel");
    }


    //@PostConstruct
    public void boundSel() {
        logger.info("Enter boundSel - L = {}, T = {}", L);

        double startMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()) / 1024d / 1024d;

                AbstractGraphNode[] V = readDataset();
                matrixGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);

        double endMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()) / 1024d / 1024d;
        System.out.println("L = "+ L +" boundSel memory :" + (endMemory - startMemory));

        logger.info("Exit boundSel");
    }

    //@PostConstruct
    public void approximateGreedy() {
        logger.info("Enter approximateGreedy - L = {}", L);

        double startMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()) / 1024d / 1024d;

                AbstractGraphNode[] V = readDataset();
                approximateGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);


        double endMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()) / 1024d / 1024d;
        System.out.println("L = "+ L +" approximateGreedy memory :" + (endMemory - startMemory));
        logger.info("Exit approximateGreedy");
    }

    //@PostConstruct
    public void dpGreedy() {
        logger.info("Enter dpGreedy - L = {}}", L);

        double startMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()) / 1024d / 1024d;

                AbstractGraphNode[] V = readDataset();
                dpGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.BoundSel);

        double endMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()) / 1024d / 1024d;
        System.out.println("L = "+ L +" dpGreedy memory :" + (endMemory - startMemory));


        logger.info("Exit dpGreedy");
    }

    public void degreeGreedy() {
        logger.info("Enter degreeGreedy - L = {}", L);

        double startMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()) / 1024d / 1024d;

        LLengthRandomWalk degreeGreedy = new DegreeImpl();
        AbstractGraphNode[] V = readDataset();
        degreeGreedy.run(V, K, L, ProblemType.PROBLEM1, OptimizationStrategy.REORDER);

        double endMemory = (Runtime.getRuntime().totalMemory() -  Runtime.getRuntime().freeMemory()) / 1024d / 1024d;
        System.out.println("L = "+ L +" degreeGreedy memory :" + (endMemory - startMemory));

        logger.info("Exit degreeGreedy");
    }
}
