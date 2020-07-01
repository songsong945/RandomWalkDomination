package au.edu.rmit.randomwalk.model;

import java.util.Comparator;

public class SortByPageRankScore implements Comparator<AbstractGraphNode> {
    @Override
    public int compare(AbstractGraphNode n1, AbstractGraphNode n2) {
        if(n1.getPageRankScore() > n2.getPageRankScore())
            return 1;
        else if (n1.getPageRankScore() < n2.getPageRankScore())
            return -1;
        else
            return 0;
    }
}
