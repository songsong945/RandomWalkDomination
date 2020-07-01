package au.edu.rmit.randomwalk.experiment;

import au.edu.rmit.randomwalk.io.FileManager;
import au.edu.rmit.randomwalk.model.AbstractGraphNode;

public class DegreeCount {
    public FileManager fileManager = new FileManager();

    int MaxDegree=0;
    double avgDegree=0.0;

    private AbstractGraphNode[] readDataset() {
        return fileManager.readAdvogatoNetwork();
    }

    public void run(){
        AbstractGraphNode[] V = readDataset();
        for(AbstractGraphNode u : V){
            int num = u.getNeighbors().size();
            if(num>MaxDegree)
                MaxDegree = num;
            avgDegree += num;
        }
        avgDegree /= V.length;
        System.out.println("MaxDegree = "+MaxDegree);
        System.out.println("avgDegree = "+avgDegree);
    }
}
