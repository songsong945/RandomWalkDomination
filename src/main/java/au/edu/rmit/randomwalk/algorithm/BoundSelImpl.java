package au.edu.rmit.randomwalk.algorithm;

import au.edu.rmit.randomwalk.datastructure.FibonacciHeap;
import au.edu.rmit.randomwalk.experiment.Effectiveness;
import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import au.edu.rmit.randomwalk.model.Debug;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class BoundSelImpl {

    private static Logger logger = LoggerFactory.getLogger(MatrixGreedyImpl.class);

    private double bestSoFar = 0;
    private AbstractGraphNode bestNode = null;
    private double preFScore = 0.0, curFScore = 0.0;

    private double fastNI3(AbstractGraphNode[] graph, final int T, Collection<AbstractGraphNode> S , AbstractGraphNode s) {
        Map<AbstractGraphNode, Double> B = new HashMap<>();
        Map<AbstractGraphNode, Double> gamma = new HashMap<>(), gammaP = new HashMap<>();
        for (AbstractGraphNode v : graph) {
            if (!S.contains(v)) {
                if(v==s)
                    gammaP.put(v, 1.0);
                else
                    gammaP.put(v, 0.0);
            }
        }
        for (int t = 1; t <= T; ++t) {
            for (AbstractGraphNode v : graph) {
                if (!S.contains(v)) {
                    gamma.put(v, 0.0);
                }
            }
            for (AbstractGraphNode v : graph) {
                if (!S.contains(v)) {
                    if (v.getNeighbors().size() != 0) {
                        for (AbstractGraphNode n : v.getNeighbors()) {
                            if (!S.contains(n)) {
                                double value = gamma.get(n);
                                gamma.put(n, value + 1.0 / v.getNeighbors().size() * gammaP.get(v));
                                //exactValue = exactValue.add(new BigDecimal(1.0 / v.getNeighbors().size()).multiply(new BigDecimal(gammaP.get(n))));
                            }
                        }
                    }else {
                        double value = gamma.get(v);
                        gamma.put(v, value + gammaP.get(v));
                    }
                }
            }
            for (Map.Entry<AbstractGraphNode, Double> entry : gamma.entrySet()) {
                if (!S.contains(entry.getKey())) {
                    Double v = B.get(entry.getKey());
                    if (v == null) {
                        B.put(entry.getKey(), entry.getValue());
                    } else {
                        B.put(entry.getKey(), v + entry.getValue());
                    }
                    gammaP.put(entry.getKey(), entry.getValue());
                }
            }
        }
/*        double va = 0.0;
        for(Map.Entry<AbstractGraphNode, Double> entry : B.entrySet()){
            Double v = B.get(entry.getKey());
            va += v;
        }
        System.out.println(s.getId()+"  va= "+va+"  B.s = "+B.get(s));*/
        return B.get(s);
    }

    private Map<AbstractGraphNode, Double> fastNI1(AbstractGraphNode[] graph, final int T, Collection<AbstractGraphNode> S) {
        Map<AbstractGraphNode, Double> B = new HashMap<>();
        Map<AbstractGraphNode, Double> gamma = new HashMap<>(), gammaP = new HashMap<>();
        for (AbstractGraphNode v : graph) {
            if (!S.contains(v)) {
                gammaP.put(v, 1.0);
                //gammaP.put(v, ((double)graph.length-S.size()-1)/((double)graph.length-S.size()));
            }
        }
        for (int t = 1; t <= T; ++t) {
            for (AbstractGraphNode v : graph) {
                if (!S.contains(v)) {
                    gamma.put(v, 0.0);
                }
            }
            for (AbstractGraphNode v : graph) {
                if (!S.contains(v)) {
                    if (v.getNeighbors().size() != 0) {
                        for (AbstractGraphNode n : v.getNeighbors()) {
                            if (!S.contains(n)) {
                                double value = gamma.get(n);
                                gamma.put(n, value + 1.0 / v.getNeighbors().size() * gammaP.get(v));
                                //exactValue = exactValue.add(new BigDecimal(1.0 / v.getNeighbors().size()).multiply(new BigDecimal(gammaP.get(n))));
                            }
                        }
                    }else {
                        double value = gamma.get(v);
                        gamma.put(v, value + gammaP.get(v));
                    }
                }
            }
            for (Map.Entry<AbstractGraphNode, Double> entry : gamma.entrySet()) {
                if (!S.contains(entry.getKey())) {
                    Double v = B.get(entry.getKey());
                    if (v == null) {
                        B.put(entry.getKey(), entry.getValue());
                    } else {
                        B.put(entry.getKey(), v + entry.getValue());
                    }
                    gammaP.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return B;
    }

    private Map<AbstractGraphNode, Double> fastNI(AbstractGraphNode[] graph, final int T, Collection<AbstractGraphNode> S) {
        Map<AbstractGraphNode, Double> B = new HashMap<>();
        Map<AbstractGraphNode, Double> gamma = new HashMap<>(), gammaP = new HashMap<>();
        for (int t = 0; t <= T; ++t) {
            if (t == 0) {
                for (AbstractGraphNode v : graph) {
                    if (!S.contains(v)) {
                        gamma.put(v, 1.0);
                        //gamma.put(v, ((double)graph.length-S.size()-1)/((double)graph.length-S.size()));
                    }
                }
            } else {
                for (AbstractGraphNode v : graph) {
                    if (!S.contains(v)) {
                        double value = 0.0;
                        //BigDecimal exactValue = new BigDecimal(0.0);
                        for (AbstractGraphNode n : v.getNeighbors()) {
                            if (!S.contains(n)) {
                                value += 1.0 / v.getNeighbors().size() * gammaP.get(n);
                                //exactValue = exactValue.add(new BigDecimal(1.0 / v.getNeighbors().size()).multiply(new BigDecimal(gammaP.get(n))));
                            }
                        }
                        gamma.put(v, value);
                    }
                }
            }
            for (Map.Entry<AbstractGraphNode, Double> entry : gamma.entrySet()) {
                if (!S.contains(entry.getKey())) {
                    Double v = B.get(entry.getKey());
                    if (v == null) {
                        B.put(entry.getKey(), entry.getValue());
                    } else {
                        B.put(entry.getKey(), v + entry.getValue());
                    }
                    gammaP.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return B;
    }


    private double fastNI2(AbstractGraphNode[] graph, final int T, Collection<AbstractGraphNode> S, AbstractGraphNode s) {
        Map<AbstractGraphNode, Double> B = new HashMap<>();
        Map<AbstractGraphNode, Double> gamma = new HashMap<>(), gammaP = new HashMap<>();
        Set<AbstractGraphNode> hs = new HashSet();
        hs.add(s);
        for (int t = 1; t <= T; ++t) {
            if (t == 1) {
                for (AbstractGraphNode v : graph) {
                    if(v==s)
                        gamma.put(v, 1.0);
                }
            } else {
                Iterator it = hs.iterator();
                Set<AbstractGraphNode> hsP = new HashSet();
                while (it.hasNext()) {
                    AbstractGraphNode v =  (AbstractGraphNode)it.next();
                    if (!S.contains(v)) {
                        double value = 1.0 / v.getNeighbors().size() * gammaP.get(v);
                        //BigDecimal exactValue = new BigDecimal(0.0);
                        for (AbstractGraphNode n : v.getNeighbors()) {
                            if (!S.contains(n)) {
                                Double va = gamma.get(n);
                                if (va == null) {
                                    gamma.put(n, value);
                                } else {
                                    gamma.put(n, va + value);
                                }
                                hsP.add(n);
                            }
                        }
                    }
                }
                Iterator itP = hsP.iterator();
                while(itP.hasNext()){
                    hs.add((AbstractGraphNode)itP.next());
                }
            }
            for (Map.Entry<AbstractGraphNode, Double> entry : gamma.entrySet()) {
                if (!S.contains(entry.getKey())) {
                    Double v = B.get(entry.getKey());
                    if (v == null) {
                        B.put(entry.getKey(), entry.getValue());
                    } else {
                        B.put(entry.getKey(), v + entry.getValue());
                    }
                    gammaP.put(entry.getKey(), entry.getValue());
                    gamma.put(entry.getKey(), 0.);
                }
            }
        }
        return B.get(s);
    }

    private Map<AbstractGraphNode, Double> calUpperBound(AbstractGraphNode[] graph, int T, Collection<AbstractGraphNode> S) {
        Map<AbstractGraphNode, Double> B = new HashMap<>();
        Map<AbstractGraphNode, Double> NI= fastNI(graph,T, S);
        Map<AbstractGraphNode, Double> IN= fastNI1(graph,T, S);
        double count =0.,count2 = 0.;
        for (AbstractGraphNode v : graph) {
            B.put(v, NI.get(v)+IN.get(v));
            //System.out.println("v = "+v.getId()+" "+"NI = "+NI.get(v)+" "+"IN = "+IN.get(v));
            count += IN.get(v);
            count2 += NI.get(v);
        }
        System.out.println("NI = "+count2+"   IN = "+count);
        return B;
    }

    private double calMarginGain(AbstractGraphNode[] graph, AbstractGraphNode curNode, int T, Collection<AbstractGraphNode> S ,
                                 Map<AbstractGraphNode, Double> NI, Map<AbstractGraphNode, Double> IN) {
        double value = fastNI3(graph,T,S,curNode);
        double marginalGain = NI.get(curNode)+IN.get(curNode)-value;
        //System.out.println(curNode.getId()+"   NI = "+NI.get(curNode)+" "+"IN = "+ IN.get(curNode) +" "+ "value = "+ value);
        return marginalGain ;
    }

    public Collection<AbstractGraphNode> run(AbstractGraphNode[] graph, final int K, final int T) {
        logger.info("Enter MatrixGreedyImpl/run() K = {}, T = {}", K, T);
        final Set<AbstractGraphNode> S = new HashSet<>();
        int calculation = 0;
        final FibonacciHeap<AbstractGraphNode> fibHeap = new FibonacciHeap();
        //choose the first node
        List<Map.Entry<AbstractGraphNode, Double>> upperBound;
        upperBound = new ArrayList<>(calUpperBound(graph, T, S).entrySet());
        upperBound.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        for(int i =0 ; i< upperBound.size(); ++i){
            System.out.println(upperBound.get(i));
        }

        for (int index = 0; index < upperBound.size(); ++index) {
            Map.Entry<AbstractGraphNode, Double> entry = upperBound.get(index);
            fibHeap.enqueue(entry.getKey(), -(entry.getValue()));//fibHeap is a min-heap
        }

/*        Map<AbstractGraphNode, Double> NI2= fastNI(graph,T, S);
        Map<AbstractGraphNode, Double> IN2= fastNI1(graph,T, S);
        double count2=0.;
        for(AbstractGraphNode v:graph){
            double marginGain = calMarginGain(graph,v, T, S, NI2,IN2);
            count2+=marginGain;
        }
        System.out.println(count2);*/

        Queue<BoundSelImpl.RankPair> repo = new LinkedList<>();
        for (int i = 1; i <= K; ++i) {
            bestSoFar = 0;
            int count = 0;
            Map<AbstractGraphNode, Double> NI= fastNI(graph,T, S);
            Map<AbstractGraphNode, Double> IN= fastNI1(graph,T, S);
            while (fibHeap.size() > 0 && bestSoFar <= -fibHeap.min().getPriority()) {
                ++count;
                FibonacciHeap.Entry<AbstractGraphNode> entry = fibHeap.dequeueMin();
                AbstractGraphNode curNode = entry.getValue();
                if (!S.contains(curNode)) {
                    double marginGain = calMarginGain(graph,curNode, T, S, NI,IN);
                    repo.add(new RankPair(curNode, marginGain));
                    if (marginGain > bestSoFar) {
                        bestSoFar = marginGain;
                        bestNode = curNode;
                    }
                }
            }
            S.add(bestNode);
            while (repo.size() > 0) {
                BoundSelImpl.RankPair pair = repo.poll();
                fibHeap.enqueue(pair.getKey(), -pair.getValue());
            }
            logger.info("{}th best node: {} - {} - calculation times: {}", i, bestNode.getName(), bestSoFar, count);
            calculation +=count;
//            if(Debug.ENABLE){
//                if(i%10 == 0){
//                    Effectiveness effectiveness = new Effectiveness();
//                    double result = effectiveness.getScore(graph,S,T);
//                    System.out.println("L = "+T+"    "+"k = "+i+"    "+ "Score = "+ result);
//                }
//            }
        }
        System.out.println("total calculation times = "+calculation+"    "+"Avg calculation times =  "+ (double)calculation/K+"    "+"K =  "+ K);
        bestSoFar = 0;
        bestNode = null;
        preFScore = 0.0;
        curFScore = 0.0;
        logger.info("Exit MatrixGreedyImpl/run()");
        return S;
    }

    class RankPair implements au.edu.rmit.randomwalk.model.Pair<AbstractGraphNode, Double> {

        @Override
        public AbstractGraphNode getKey() {
            return this.key;
        }

        @Override
        public java.lang.Double getValue() {
            return this.value;
        }

        private RankPair(AbstractGraphNode key, Double value) {
            this.key = key;
            this.value = value;
        }

        private AbstractGraphNode key;
        private Double value;
    }

}
