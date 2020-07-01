package au.edu.rmit.randomwalk.algorithm;

import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import au.edu.rmit.randomwalk.model.SortByPageRankScore;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PageRankImpl {
    double orignalResult[] = new double[3300000];
    public double Result[] = new double[3300000];

    public Collection<AbstractGraphNode> run(AbstractGraphNode[] graph, int K, double DampingFactor) {
        Set<AbstractGraphNode> S = new HashSet<>();
        dopageRank(graph, DampingFactor);

        for (int i = 0; i < graph.length; i++) {
            graph[i].setPageRankScore(Result[graph[i].getId()]);
        }

        SortByPageRankScore sortByPageRankScore = new SortByPageRankScore();
        Arrays.sort(graph,sortByPageRankScore);

        for(int i=graph.length-1; i>=graph.length - K; i--){
            S.add(graph[i]);
            System.out.print(graph[i].getId()+"，"+graph[i].getPageRankScore()+" ");
        }
        System.out.println();

        return S;
    }

    private void dopageRank(AbstractGraphNode[] graph, double DampingFactor) {
        //初始化
        for (int i = 0; i < graph.length; i++)
            orignalResult[graph[i].getId()] = 1.0;


        while (true) {
            boolean key = true;
            for (int i = 0; i < graph.length; i++)
                Result[graph[i].getId()] = 0;

            //迭代
            for (int i = 0; i < graph.length; i++) {
                if (graph[i].getNeighbors().size() != 0) {
                    int j = 0;
                    for (AbstractGraphNode neighborNode : graph[i].getNeighbors()) {
                        Result[neighborNode.getId()] += (1.0 - DampingFactor) * orignalResult[graph[i].getId()] * graph[i].getWeights().get(j++) / graph[i].getWeightSum();
                    }
                } else {
                    Result[graph[i].getId()] += (1.0 - DampingFactor) * orignalResult[i];
                }
                Result[graph[i].getId()] += DampingFactor;

            }

            //判断是否结束
            for (int i = 0; i < graph.length; i++)
                if (Math.abs(Result[graph[i].getId()] - orignalResult[graph[i].getId()]) > 0.001 * graph.length)
                    key = false;
            if (key)
                break;

            //重置orignalResult
            for (int i = 0; i < graph.length; i++)
                orignalResult[graph[i].getId()] = Result[graph[i].getId()];
        }
    }
}
