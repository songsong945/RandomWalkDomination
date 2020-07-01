package au.edu.rmit.randomwalk.io;

import au.edu.rmit.randomwalk.model.AbstractGraphNode;
import au.edu.rmit.randomwalk.model.BasicGraphNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author forrest0402
 * @Description
 * @date 2/5/2018
 */
@Service
public class FileManager {

    private static Logger logger = LoggerFactory.getLogger(FileManager.class);

    private static final String PREFIX = "data/";

    /**
     * read as undirected graph
     *
     * @param fileName
     * @return
     */
    private AbstractGraphNode[] readData(String fileName, boolean directed, boolean weighted, int nodelimit, int edgelimit) {
        logger.info("Enter readData");
        int edgeNum = 0, maxNodeID = 0, minNodeID = 0;
        Map<String, AbstractGraphNode> nodeMap = new HashMap<>();
        Set<String> existEdge = new HashSet<>();
        float weight = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                //Files.lines(Paths.get(FileManager.class.getClassLoader().getResource(fileName).toURI())).forEach(line -> {
                if (nodeMap.size() >= nodelimit) break;
                if (existEdge.size() >= edgelimit) break;
                if (line.contains("#")||line.contains("%")) continue;
                line = line.trim();
                String splitor = " ";
                if (line.contains("\t"))
                    splitor = "\t";
                String[] array = line.split(splitor);
                if(weighted)
                    weight = Float.parseFloat(array[2]);
                else
                    weight = 1;
                AbstractGraphNode node1 = nodeMap.get(array[0]);
                if (node1 == null) {
                    int nodeID1 = Integer.parseInt(array[0]);
                    node1 = new BasicGraphNode(array[0], nodeID1);
                    nodeMap.put(array[0], node1);
                    if (nodeID1 > maxNodeID) maxNodeID = nodeID1;
                    if (nodeID1 < minNodeID) minNodeID = nodeID1;
                }
                AbstractGraphNode node2 = nodeMap.get(array[1]);
                if (node2 == null) {
                    int nodeID2 = Integer.parseInt(array[1]);
                    node2 = new BasicGraphNode(array[1], nodeID2);
                    nodeMap.put(array[1], node2);
                    if (nodeID2 > maxNodeID) maxNodeID = nodeID2;
                    if (nodeID2 < minNodeID) minNodeID = nodeID2;
                }
                node1.addNeighbor(node2, weight);
                existEdge.add(node1.getName() + "," + node2.getName());
                if (!directed) {
                    node2.addNeighbor(node1, weight);
                    existEdge.add(node2.getName() + "," + node1.getName());
                }
            }
            //});
        } catch (IOException e) {
            logger.error("{}", e);
        }
        for (AbstractGraphNode graphNode : nodeMap.values()) {
            edgeNum += graphNode.getNeighbors().size();
        }
        logger.info("Exit readData - graph size: node = {}/{}, edge = {}", nodeMap.size(), maxNodeID + 1, edgeNum);
        AbstractGraphNode[] res = new AbstractGraphNode[maxNodeID - minNodeID + 1];
        for (int i = 0; i <= maxNodeID - minNodeID ; i++) {
            res[i] = nodeMap.get(String.valueOf(i + minNodeID));
            if(res[i]!=null){
                if(i+ minNodeID >= 0 ){
                    res[i].setRealNode(true);
                }else{
                    res[i].setRealNode(false);
                }
            }
        }
        //re-organize graph
        Map<Integer, Integer> IDMap = new HashMap<>();
        int idSequence = 0;
        for (int i = 0; i < res.length; i++) {
            if (res[i] == null) continue;
            IDMap.put(idSequence++, i);
        }
        res = new AbstractGraphNode[nodeMap.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = nodeMap.get(String.valueOf(IDMap.get(i)+ minNodeID));
            //System.out.println(i);
            res[i].reBuild(i);
            float sum = 0;
            for(float f : res[i].getWeights()){
                sum += f;
            }
            res[i].setWeightSum(sum);
        }
        return res;
    }

    public void BuildCounterPart(String fileName, boolean weighted) {
        logger.info("Enter BuildCounterPart");
        int num = 1;
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))) {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File("data/HepPh5.txt")));
            BufferedWriter writer2 = new BufferedWriter(new FileWriter(new File("data/HepPh5-w.txt")));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("#")||line.contains("%")){
                    writer.write(line+"\n");
                    continue;
                }

                line = line.trim();
                String splitor = " ";
                if (line.contains("\t"))
                    splitor = "\t";
                String[] array = line.split(splitor);

                int r = (int)(1+Math.random()*9);

                if(weighted){
                    r = Integer.parseInt(array[2]);
                }else{
                    writer2.write(array[0]+splitor+array[1]+splitor+r+"\n");
                }
                int s = num;
                int e = num+r-2;
                num = num+r-1;
                if(r == 1)
                    writer.write(array[0]+splitor+array[1]+"\n");
                else {
                    writer.write(array[0]+splitor+(-s)+"\n");
                    writer.write((-e)+splitor+array[1]+"\n");
                    while (s!=e){
                        writer.write((-s)+splitor+(-s-1)+"\n");
                        s++;
                    }
                }
            }
            writer.close();
            writer2.close();
        } catch (IOException e) {
            logger.error("{}", e);
        }
    }


//    private AbstractGraphNode[] readData_T(String fileName, boolean directed, int nodelimit, int edgelimit) {
//        logger.info("Enter readData");
//        int edgeNum = 0, maxNodeID = 0;
//        Map<String, AbstractGraphNode> nodeMap = new HashMap<>();
//        Set<String> existEdge = new HashSet<>();
//        try (BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                //Files.lines(Paths.get(FileManager.class.getClassLoader().getResource(fileName).toURI())).forEach(line -> {
//                if (nodeMap.size() >= nodelimit) break;
//                if (existEdge.size() >= edgelimit) break;
//                if (line.contains("#")) continue;
//                line = line.trim();
//                String splitor = " ";
//                if (line.contains("\t"))
//                    splitor = "\t";
//                String[] array = line.split(splitor);
//                AbstractGraphNode node1 = nodeMap.get(array[0]);
//                if (node1 == null) {
//                    int nodeID1 = Integer.parseInt(array[0]);
//                    node1 = new BasicGraphNode(array[0], nodeID1);
//                    nodeMap.put(array[0], node1);
//                    if (nodeID1 > maxNodeID) maxNodeID = nodeID1;
//                }
//                AbstractGraphNode node2 = nodeMap.get(array[1]);
//                if (node2 == null) {
//                    int nodeID2 = Integer.parseInt(array[1]);
//                    node2 = new BasicGraphNode(array[1], nodeID2);
//                    nodeMap.put(array[1], node2);
//                    if (nodeID2 > maxNodeID) maxNodeID = nodeID2;
//                }
//                node2.addNeighbor(node1);
//                existEdge.add(node2.getName() + "," + node1.getName());
//            }
//            //});
//        } catch (IOException e) {
//            logger.error("{}", e);
//        }
//        for (AbstractGraphNode graphNode : nodeMap.values()) {
//            edgeNum += graphNode.getNeighbors().size();
//        }
//        logger.info("Exit readData - graph size: node = {}/{}, edge = {}", nodeMap.size(), maxNodeID + 1, edgeNum);
//        AbstractGraphNode[] res = new AbstractGraphNode[maxNodeID + 1];
//        for (int i = 0; i <= maxNodeID; i++) {
//            res[i] = nodeMap.get(String.valueOf(i));
//        }
//        //re-organize graph
//        Map<Integer, Integer> IDMap = new HashMap<>();
//        int idSequence = 0;
//        for (int i = 0; i < res.length; i++) {
//            if (res[i] == null) continue;
//            IDMap.put(idSequence++, i);
//        }
//        res = new AbstractGraphNode[nodeMap.size()];
//        for (int i = 0; i < res.length; i++) {
//            res[i] = nodeMap.get(String.valueOf(IDMap.get(i)));
//            res[i].reBuild(i);
//        }
//        return res;
//    }

    /**
     * email-Eu-core network
     * http://snap.stanford.edu/data/email-Eu-core.html
     */
    public AbstractGraphNode[] readEUCoreNetwork() {
        logger.info("Enter readEUNetwork");
        return readData(PREFIX + "email-Eu-core.txt", true,false, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }


    /**
     * email-Eu-core network
     * http://snap.stanford.edu/data/email-Eu-core.html
     */
    public AbstractGraphNode[] readMorenoNetwork() {
        logger.info("Enter MorenoNetwork");
        return readData(PREFIX + "moreno_health_health.txt", false,true, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public AbstractGraphNode[] readEUCoreCPNetwork() {
        logger.info("Enter readEUNetwork");
        return readData(PREFIX + "email-Eu-core-CP.txt", true,false, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public AbstractGraphNode[] readAdvogatoCPNetwork() {
        logger.info("Enter AdvogatoCPNetwork");
        return readData(PREFIX + "advogato-CP.txt", false, false, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public AbstractGraphNode[] readMorenoCPNetwork() {
        logger.info("Enter MorenoCPNetwork");
        return readData(PREFIX + "moreno_health_health-CP.txt", false, false, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * email-Euall network
     * http://snap.stanford.edu/data/email-EuAll.html
     */
    public AbstractGraphNode[] readEuAllNetwork() {
        logger.info("Enter readEuAllNetwork");
        return readData(PREFIX + "Email-EuAll.txt", false, false, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * p2p-Gnutella30
     * http://snap.stanford.edu/data/p2p-Gnutella30.html
     */
    public AbstractGraphNode[] readGnutella30Network() {
        logger.info("Enter p2p-Gnutella30");
        return readData(PREFIX + "p2p-Gnutella30.txt", false, false, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Wiki-Vote
     * http://snap.stanford.edu/data/wiki-Vote.html
     */
    public AbstractGraphNode[] readWikiVoteNetwork() {
        logger.info("Enter Wiki-Vote");
        return readData(PREFIX + "Wiki-Vote.txt", false, false, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * CA-HepPh
     * http://snap.stanford.edu/data/ca-HepPh.html
     */
    public AbstractGraphNode[] readHepPhNetwork() {
        logger.info("Enter CA-HepPh");
        return readData(PREFIX + "CA-HepPh.txt", true, false, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public AbstractGraphNode[] readHepPhNetwork(int delta) {
        logger.info("Enter CA-HepPh {}",delta);
        return readData(PREFIX + "HepPh"+delta+".txt", true, false, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * CA-HepPh
     * http://snap.stanford.edu/data/ca-HepPh.html
     */
    public AbstractGraphNode[] readHepPhCPNetwork() {
        logger.info("Enter CA-HepPh");
        return readData(PREFIX + "CA-HepPh-CP.txt", true, false, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * advogato
     * http://snap.stanford.edu/data/ca-HepPh.html
     */
    public AbstractGraphNode[] readAdvogatoNetwork() {
        logger.info("Enter advogato");
        return readData(PREFIX + "advogato.txt", false, true, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * CA-GrQc
     * http://snap.stanford.edu/data/ca-GrQc.html
     */
    public AbstractGraphNode[] readGrQcNetwork() {
        logger.info("Enter CA-GrQc");
        return readData(PREFIX + "CA-GrQc.txt", true,false, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public AbstractGraphNode[] readGrQcNetwork(int delta) {
        logger.info("Enter CA-GrQc {}", delta);
        return readData(PREFIX + "GrQc"+delta+".txt", true,false, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * CA-GrQc
     * http://snap.stanford.edu/data/ca-GrQc.html
     */
    public AbstractGraphNode[] readGrQcCPNetwork() {
        logger.info("Enter CA-GrQc");
        return readData(PREFIX + "CA-GrQc-CP.txt", true, false,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }


    /**
     * ca-CondMat
     * http://snap.stanford.edu/data/ca-CondMat.html
     */
    public AbstractGraphNode[] readCondMatNetwork() {
        logger.info("Enter CA-CondMat");
        return readData(PREFIX + "CA-CondMat.txt", true, false,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * email-Eu-Email network
     * http://snap.stanford.edu/data/email-Enron.html
     */
    public AbstractGraphNode[] readEnronEmailNetwork() {
        logger.info("Enter readEnronEmailNetwork");
        return readData(PREFIX + "Email-Enron.txt", true, false,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public AbstractGraphNode[] readEnronEmailWNetwork() {
        logger.info("Enter readEnronEmailNetwork");
        return readData(PREFIX + "Email-Enron-w.txt", true, true, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public AbstractGraphNode[] readEnronEmailCPNetwork() {
        logger.info("Enter readEnronEmailNetwork");
        return readData(PREFIX + "Email-Enron-CP.txt", true, false, Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

//    public AbstractGraphNode[] readEnronEmailNetwork_T() {
//        logger.info("Enter readEnronEmailNetwork");
//        return readData_T(PREFIX + "Email-Enron.txt", true, Integer.MAX_VALUE, Integer.MAX_VALUE);
//    }

    public AbstractGraphNode[] readTestData() {
        logger.info("Enter readTestData");
        return readData(PREFIX + "test-data.txt", true, false,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public AbstractGraphNode[] readKarateData() {
        logger.info("Enter readTestData");
        return readData(PREFIX + "karate-graph.txt", false, false,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * a graph generated by GraphStream
     *
     * @return
     */
    public AbstractGraphNode[] readGraphData100() {
        logger.info("Enter readGraphData100");
        return readData(PREFIX + "graph100.txt", true, false,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * a graph generated by GraphStream
     *
     * @return
     */
    public AbstractGraphNode[] readGraphData300() {
        logger.info("Enter readGraphData300");
        return readData(PREFIX + "graph300.txt", true, false,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * a graph generated by GraphStream
     *
     * @return
     */
    public AbstractGraphNode[] readGraphData1000() {
        logger.info("Enter readGraphData1000");
        return readData(PREFIX + "graph1000.txt", false, false,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * a graph generated by GraphStream
     *
     * @return
     */
    public AbstractGraphNode[] readGraphData30000() {
        logger.info("Enter readGraphData30000");
        return readData(PREFIX + "graph30000.txt", false, false,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * a graph from http://snap.stanford.edu/data/soc-Epinions1.html
     *
     * @return
     */
    public AbstractGraphNode[] readEpinionsSocialNetwork() {
        logger.info("Enter readEpinionsSocialNetwork");
        return readData(PREFIX + "soc-Epinions1.txt", false, false,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * a graph from http://snap.stanford.edu/data/soc-sign-epinions.html
     *
     * @return
     */
    public AbstractGraphNode[] readSignedEpinionsSocialNetwork() {
        logger.info("Enter readSignedEpinionsSocialNetwork");
        return readData(PREFIX + "soc-sign-epinions.txt", false, false,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * a graph from http://snap.stanford.edu/data/cit-HepTh.html
     *
     * @return
     */
    public AbstractGraphNode[] readPhysicsTheoryCitationNetwork() {
        logger.info("Enter readPhysicsTheoryCitationNetwork");
        return readData(PREFIX + "Cit-HepTh.txt", false, false,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * a graph from http://snap.stanford.edu/data/soc-sign-Slashdot081106.html
     *
     * @return
     */
    public AbstractGraphNode[] readSlashdotNetwork() {
        logger.info("Enter readSlashdotNetwork");
        return readData(PREFIX + "soc-sign-Slashdot081106.txt", false, false,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public AbstractGraphNode[] readSlashdotWNetwork() {
        logger.info("Enter readSlashdotNetwork");
        return readData(PREFIX + "soc-sign-Slashdot081106-w.txt", false, true,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public AbstractGraphNode[] readSlashdotCPNetwork() {
        logger.info("Enter readSlashdotNetwork");
        return readData(PREFIX + "soc-sign-Slashdot081106-CP.txt", false, false,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * a graph from http://snap.stanford.edu/data/com-Youtube.html
     *
     * @return
     */
    public AbstractGraphNode[] readYoutubeNetwork(int nodelimit, int edgelimit) {
        logger.info("Enter readYoutubeNetwork");
        return readData(PREFIX + "com-youtube.ungraph.txt", false, false,nodelimit, edgelimit);
    }

    /**
     * a graph from http://snap.stanford.edu/data/com-DBLP.html
     *
     * @return
     */
    public AbstractGraphNode[] readDblpNetwork(int nodelimit, int edgelimit) {
        logger.info("Enter readDblpNetwork");
        return readData(PREFIX + "com-dblp.ungraph.txt", false, false,nodelimit, edgelimit);
    }

    /**
     * a graph from http://snap.stanford.edu/data/loc-brightkite.html
     *
     * @return
     */
    public AbstractGraphNode[] readBrightkiteNetwork() {
        logger.info("Enter readBrightkiteNetwork");
        return readData(PREFIX + "Brightkite_edges.txt", false, false,Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    public AbstractGraphNode[] readEpinionsSocialNetwork(int k, int delta) {
        logger.info("Enter readEpinionsSocialNetwork - k = {} delta = {}", k, delta);
        AbstractGraphNode[] originGraph = readEpinionsSocialNetwork();
        PriorityQueue<AbstractGraphNode> topkHeap = new PriorityQueue<>((v1, v2) -> Integer.compare(v1.getNeighbors().size(), v2.getNeighbors().size()));
        for (int i = 0; i < originGraph.length; i++) {
            AbstractGraphNode node = originGraph[i];
            topkHeap.add(node);
            if (topkHeap.size() > k) topkHeap.poll();
        }
        Random random = new Random();
        while (topkHeap.size() > 0) {
            AbstractGraphNode node = topkHeap.poll();
            for (int i = 0; i < delta; ++i) {
                while (true) {
                    int id = random.nextInt(originGraph.length);
                    if (id == node.getId()) continue;
                    boolean flag = false;
                    for (AbstractGraphNode abstractGraphNode : node.getNeighbors()) {
                        if (abstractGraphNode.getId() == id) {
                            flag = true;
                            continue;
                        }
                    }
                    if (flag) continue;
                    node.addNeighbor(originGraph[id],1);
                    originGraph[id].addNeighbor(node,1);
                    break;
                }
            }
        }
        return originGraph;
    }


    public void writeToFile(String filePath, List<? extends Object> data, boolean append) {
        System.out.println("start writeToFile");
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try (BufferedWriter out = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(filePath, append), StandardCharsets.UTF_8))) {
            for (Object lineStr : data) {
                out.write(String.valueOf(lineStr));
                out.newLine();
            }
            out.flush();
        } catch (FileNotFoundException e) {
            logger.error("{}", e);
        } catch (IOException e) {
            logger.error("{}", e);
        }
    }

}
