package au.edu.rmit.randomwalk.algorithm;

import au.edu.rmit.randomwalk.Common;
import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import au.edu.rmit.randomwalk.model.OptimizationStrategy;
import au.edu.rmit.randomwalk.model.ProblemType;
import au.edu.rmit.randomwalk.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.*;

/**
 * @author forrest0402
 * @Description
 * @date 3/14/2018
 */
@Service("ApproximateGreedyImpl")
public class ApproximateGreedyImpl implements LLengthRandomWalk {

    private static Logger logger = LoggerFactory.getLogger(ApproximateGreedyImpl.class);

    /**
     * Implementation of Algorihtm 2 in "Random-walk Domination in Large Graphs"
     *
     * @param graph
     * @param L
     * @param R
     * @return
     */
    private Index invertedIndex(AbstractGraphNode[] graph, int L, int R) {
        Entry[][] invertedIndex = new Entry[R][graph.length];
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < graph.length; i++) {
            if (i % 5000 == 0)
                logger.debug("i = {}", i);
            AbstractGraphNode w = graph[i];
            if(!w.isRealNode())
                continue;
            for (int t = 0; t < R; ++t) {
                boolean[] visit = new boolean[graph.length];
                AbstractGraphNode u = w;
                visit[u.getId()] = true;
                for (int j = 1; j <= L; ++j) {
                    AbstractGraphNode v = u;
                    if (u.getNeighbors().size() > 0) {
                        v = u.getNeighbors().get(random.nextInt(u.getNeighbors().size()));
                        //v = u.getNeighbors().get(Utils.getRandomNumber() % u.getNeighbors().size());
                    }
                    if (!visit[v.getId()]) {
                        visit[v.getId()] = true;
                        Entry entry = invertedIndex[t][v.getId()];
                        Entry head = new Entry(w.getId(), j);
                        invertedIndex[t][v.getId()] = head;
                        head.next = entry;
                    }
                    u = v;
                }
            }
        }
        return new Index(invertedIndex);
    }

    /**
     * Implementation of Algorithm 3 in "Random-walk Domination in Large Graphs"
     *
     * @param I
     * @param D
     * @param u
     * @param R
     * @return
     */
    private double approxGain(Index I, double[][] D, AbstractGraphNode u, int R) {
        double theta = 0;
        for (int i = 0; i < R; ++i) {
            theta += D[i][u.getId()];
            Entry entry = I.getEntry(i, u.getId());
            if (entry != null) {
                while (entry.next != null) {
                    if (entry.weight < D[i][entry.id]) {
                        theta = theta + D[i][entry.id] - entry.weight;
                    }
                    entry = entry.next;
                }
            }
        }
        theta /= R;
        return theta;
    }

    /**
     * Implementation of Algorihtm 4 in "Random-walk Domination in Large Graphs"
     *
     * @param I
     * @param D
     * @param u
     * @param R
     */
    private void update(Index I, double[][] D, AbstractGraphNode u, int R) {
        for (int i = 0; i < R; ++i) {
            D[i][u.getId()] = 0;
            Entry entry = I.getEntry(i, u.getId());
            if (entry != null) {
                while (entry.next != null) {
                    if (entry.weight < D[i][entry.id]) {
                        D[i][entry.id] = entry.weight;
                    }
                    entry = entry.next;
                }
            }
        }
    }

    @Override
    public Collection<AbstractGraphNode> run(AbstractGraphNode[] graph, int K, int L, ProblemType type, OptimizationStrategy strategy) {
        logger.info("Enter ApproximateGreedyImpl/run() K = {}, L = {}", K, L);
        int R = Common.INSTANCE.RANDOM_WALK_TIMES;
        Index I = invertedIndex(graph, L, R);
        logger.info("finish creating index");
        Set<AbstractGraphNode> S = new HashSet<>();
        double[][] D = new double[R][graph.length];
        for (int i = 0; i < R; ++i)
            for (int j = 0; j < graph.length; ++j)
                D[i][j] = L;

        for (int i = 1; i <= K; ++i) {
            AbstractGraphNode v = null;
            double maxScore = -Double.MAX_VALUE;
            for (AbstractGraphNode u : graph) {
                if (!S.contains(u)) {
                    double score = approxGain(I, D, u, R);
                    if (score > maxScore) {
                        maxScore = score;
                        v = u;
                    }
                }
            }
            S.add(v);
            logger.debug("{}th best node: {} - {}", i, v.getName(), maxScore);
            update(I, D, v, R);
        }
        logger.info("Exit ApproximateGreedyImpl/run()");
        return S;
    }

    public Collection<AbstractGraphNode> run2(AbstractGraphNode[] graph, int K, int L, int R, ProblemType type, OptimizationStrategy strategy) {
        logger.info("Enter ApproximateGreedyImpl/run() K = {}, L = {}, R = {}", K, L, R);
        Index I = invertedIndex(graph, L, R);
        logger.info("finish creating index");
        Set<AbstractGraphNode> S = new HashSet<>();
        double[][] D = new double[R][graph.length];
        for (int i = 0; i < R; ++i)
            for (int j = 0; j < graph.length; ++j)
                D[i][j] = L;

        for (int i = 1; i <= K; ++i) {
            AbstractGraphNode v = null;
            double maxScore = -Double.MAX_VALUE;
            for (AbstractGraphNode u : graph) {
                if (!S.contains(u)) {
                    double score = approxGain(I, D, u, R);
                    if (score > maxScore) {
                        maxScore = score;
                        v = u;
                    }
                }
            }
            S.add(v);
            logger.debug("{}th best node: {} - {}", i, v.getName(), maxScore);
            update(I, D, v, R);
        }
        logger.info("Exit ApproximateGreedyImpl/run()");
        return S;
    }

    class Entry {
        public final int id;
        public final int weight;

        public Entry next;

        Entry(int id, int weight) {
            this.id = id;
            this.weight = weight;
        }


    }

    class Index {
        private final Entry[][] invertedIndex;

        public Index(Entry[][] index) {
            this.invertedIndex = index;
        }

        public Entry getEntry(int i, int j) {
            return invertedIndex[i][j];
        }
    }
}
