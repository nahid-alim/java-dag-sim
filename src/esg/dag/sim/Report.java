package esg.dag.sim;

import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
//import java.util.stream.Stream;
//import java.util.stream.StreamSupport;

/**
 * Created by Asus on 5/13/2020.
 */
public class Report {
    private ArrayList<Node> nodeList;
    private ArrayList<Transaction> txList;
    private Simulation s;
    public static ArrayList<ArrayList<Float>> logCumulativeWeight = new ArrayList<>();
    public static ArrayList<ArrayList<Float>> logFinalCumulativeWeights = new ArrayList<>();
    public static ArrayList<ArrayList<Float>> logOrphans = new ArrayList<>();
    public static ArrayList<ArrayList<Float>> logNoofTips = new ArrayList<>();
    public static ArrayList<ArrayList<Float>> logVictimMaliciousTxsConf = new ArrayList<>();
    public static ArrayList<ArrayList<Float>> log3DMatrix = new ArrayList<>();
    public static ArrayList<ArrayList<Float>> logNodesInfo = new ArrayList<>();
    public static ArrayList<ArrayList<Float>> logEdgesInfo = new ArrayList<>();
    public static ArrayList<Transaction> logtxList = new ArrayList<>();
    public static ArrayList<ArrayList<Float>> hList = new ArrayList<>();
    public static double previousCounter = 0.1;
    public static double counter = 0;
    public static int arrivedTxsCount = 0;
    public static int sampleTxsCount = 0;

    public Report(Simulation sim){
        s = sim;
    }

    public void doTimeBasedReports(int counter, float currTime){
        //report of honest network
//        fillCumulativeWeightList(time);
//        fillNoofTipsList(time);

        //report of the network for throughput
        if(counter % Parameters.Mod == 0) {
            nodeList = s.getNodeSet().getRandomNodes();
            txList = s.getTransactions().getRandomTxs(currTime);
            System.out.println("\n Event number: " + counter);
//            System.out.println("-----------------------------------------------------------------");
//            try {
                fill3DMatrix_seq(currTime);
//                fill3DMatrix_parallel(currTime);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("fill 3DMatrix Done.----------------------------------------------");
        }

        //report of the network for security
//        if(counter % Parameters.Mod == 0) {
//            nodeList = s.getNodeSet().getRandomNodes();
//            fillVictimMaliciousTxsConf(currTime);
////            securityLogs(currTime);
//            System.out.println(counter);
//        }
    }

    public void doOtherReports(){
//        fillTxsCumulativeWeight(s.getTransactions(), s.getNodeSet());
//        fillNumofOrphans(s.getNodeSet());

        //for R visualization
//        fillNodes();
//        fillEdges();

        // tangle visualization
//        Node random = s.getNodeSet().getNodes().get(s.getSampler().getRandomNum(0, s.getNodeSet().getNodes().size()-1));
//        String dotFormat = createDotFormatTangle(random.getTangle().getDAG());
//        createDotGraph(dotFormat, "DotGraph");

        // write the reports to text files
        writeToTxtFiles();

// Plots in java
//        try {
//            String[] arguments = new String[]{};
//            //plot diagrams
//            PlotCumulativeWeight plot = new PlotCumulativeWeight();
//            plot.main(arguments);
//        }
//        catch (Exception e){
//            System.out.println(e.getMessage());
//        }
    }

    private void fillVictimMaliciousTxsConf(float currTime) {
        Transaction victimTx = s.getTransactions().getVictimTx();
        Transaction malicious = s.getTransactions().getMaliciousTx();

        if(malicious != null) {
            ArrayList temp = new ArrayList();
            double victimConfRate, malConfRate;

            Globals.writer.println("***************************************************************************************");
            Globals.writer.println("Current Time(ms): " + currTime + "\n");

            for(Node n : nodeList){
                Site victimSite = n.getTangle().findSiteWithTx(victimTx);
                Site malSite = n.getTangle().findSiteWithTx(malicious);

                Globals.writer.println("Node " + n.getID() + " Type: " + n.getType());
                ArrayList<Transaction> txsInDAG = n.getTangle().getTxsInDAG();
                Globals.writer.println("Number of the Transactions Arrived to the DAG:" + txsInDAG.size());
                Globals.writer.println("Transactions Arrived to the DAG:");
                for(Transaction tx : txsInDAG){
                    Globals.writer.print(tx.getID() + " ");
                }
                Globals.writer.println("");
                ArrayList<Site> tips = n.getTangle().getTips();
                Globals.writer.println("\nNumber of the Tips in the DAG:" + tips.size());
                Globals.writer.println("\nTips in the DAG:");
                for(Site s : tips){
                    Globals.writer.print(s.getTx().getID() + " ");
                }
                Globals.writer.println("");

                victimConfRate = n.getTangle().calcConfidenceRate(victimSite);
                Globals.writer.println("\nVictim Transaction Confidence Rate:" + victimConfRate);
                Globals.writer.println("Tips Referring to the Victim Transaction:");
                for(Site s : n.getTangle().getTipsReferringtoVictimTx()){
                    Globals.writer.print(s.getTx().getID() + " ");
                }
                Globals.writer.println("");
                Globals.writer.println("Tips selected to Calculate the Victim Transaction Confidence Rate:");
                for(Site s : n.getTangle().tipsForConfRateCalc){
                    Globals.writer.print(s.getTx().getID() + " ");
                }
                Globals.writer.println("");

                malConfRate = n.getTangle().calcConfidenceRate(malSite);
                Globals.writer.println("\nMalicious Transaction Confidence Rate:" + malConfRate);
                Globals.writer.println("Tips Referring to the Malicious Transaction:");
                for(Site s : n.getTangle().getTipsReferringtoMaliciousTx()){
                    Globals.writer.print(s.getTx().getID() + " ");
                }
                Globals.writer.println("");
                Globals.writer.println("\nTips selected to Calculate the Malicious Transaction Confidence Rate:");
                for(Site s : n.getTangle().tipsForConfRateCalc){
                    Globals.writer.print(s.getTx().getID() + " ");
                }
                Globals.writer.println("\n------------------------------------------------------------------------\n");

                temp.add(currTime);
                temp.add(n.getID());
                temp.add(victimTx.getID());
                temp.add(victimTx.getCreationTime());
                temp.add(victimConfRate);
                temp.add(malicious.getID());
                temp.add(malicious.getCreationTime());
                temp.add(malConfRate);
                logVictimMaliciousTxsConf.add(temp);
                temp = new ArrayList();
            }
        }
    }

//    private void securityLogs(float currTime) {
//        Transaction victimTx = s.getTransactions().getVictimTx();
//        Transaction maliciousTx = s.getTransactions().getMaliciousTx();
//        if(maliciousTx != null) {
//            double victimConfRate, malConfRate;
//            Globals.writer.println("***************************************************************************************");
//            Globals.writer.println("Current Time(ms): " + currTime + "\n");
//            for(Node n : nodeList){
//                Site victimSite = n.getTangle().findSiteWithTx(victimTx);
//                Site malSite = n.getTangle().findSiteWithTx(maliciousTx);
//
//                Globals.writer.println("Node " + n.getID() + " Type: " + n.getType());
//                Globals.writer.println("Number of the Transactions Arrived to the DAG:" + n.getTangle().getTxsInDAG().size());
//                Globals.writer.println("Transactions Arrived to the DAG:");
//                for(Transaction tx : n.getTangle().getTxsInDAG()){
//                    Globals.writer.print(tx.getID() + " ");
//                }
//                Globals.writer.println("");
//                Globals.writer.println("\nTips in the DAG:");
//                for(Site s : n.getTangle().getTips()){
//                    Globals.writer.print(s.getTx().getID() + " ");
//                }
//                Globals.writer.println("");
//
//                victimConfRate = n.getTangle().calcConfidenceRate(victimSite);
//                Globals.writer.println("\nVictim Transaction Confidence Rate:" + victimConfRate);
//                Globals.writer.println("Tips Referring to the Victim Transaction:");
//                for(Site s : n.getTangle().getTipsReferringtoVictimTx()){
//                    Globals.writer.print(s.getTx().getID() + " ");
//                }
//                Globals.writer.println("");
//                Globals.writer.println("Tips selected to Calculate the Victim Transaction Confidence Rate:");
//                for(Site s : n.getTangle().tipsForConfRateCalc){
//                    Globals.writer.print(s.getTx().getID() + " ");
//                }
//                Globals.writer.println("");
//
//                malConfRate = n.getTangle().calcConfidenceRate(malSite);
//                Globals.writer.println("\nMalicious Transaction Confidence Rate:" + malConfRate);
//                Globals.writer.println("Tips Referring to the Malicious Transaction:");
//                for(Site s : n.getTangle().getTipsReferringtoMaliciousTx()){
//                    Globals.writer.print(s.getTx().getID() + " ");
//                }
//                Globals.writer.println("");
//                Globals.writer.println("\nTips selected to Calculate the Malicious Transaction Confidence Rate:");
//                for(Site s : n.getTangle().tipsForConfRateCalc){
//                    Globals.writer.print(s.getTx().getID() + " ");
//                }
//                Globals.writer.println("\n------------------------------------------------------------------------\n");
//            }
//        }
//    }

    public void getConfRate(Node n, Transaction tx, float currTime){
        ArrayList temp = new ArrayList();
        double confRate;
//        long b1 = System.currentTimeMillis();
//        System.out.println("before 1st findSiteWithTx "+b1);
        Site site = n.getTangle().findSiteWithTx(tx);
//        long e1 = System.currentTimeMillis();
//        System.out.println("after 1st findSiteWithTx "+e1);
        if(site != null) {
            confRate = n.getTangle().calcConfidenceRate(site);
            temp.add(currTime);
            temp.add(n.getID());
            temp.add(tx.getID());
            temp.add(tx.getCreationTime());
            temp.add(confRate);
            temp.add(site.getCumulativeWeight());
            temp.add(Report.sampleTxsCount);
            temp.add(Report.arrivedTxsCount);
            log3DMatrix.add(temp);
        }
    }

    private void fill3DMatrix_parallel(float currTime) throws InterruptedException {
        //multiThread implementation with ForkJoinPool
        counter = 0;
        previousCounter = 0.1;
        ForkJoinPool forkJoinPool = new ForkJoinPool(7); //n CPUs or threads
        MyRecursiveAction myRecursiveAction = new MyRecursiveAction(s, nodeList, txList, currTime);
        forkJoinPool.invoke(myRecursiveAction);

        //continue logging until finished
        while (counter != previousCounter) {
            previousCounter = counter;
            Thread.sleep(500);
        }
    }

    private void fill3DMatrix_seq(float currTime){
        //nested loop implementation
        for(Node n : nodeList){
            for(Transaction tx : txList){
                getConfRate(n, tx, currTime);
            }
        }
    }

    private void fillNodes() {
        ArrayList temp = new ArrayList();
        for(Node n : s.getNodeSet().getNodes()){
            temp.add(n.getID());
            temp.add(n.getHashpower());
            temp.add(n.getType());
            logNodesInfo.add(temp);
            temp = new ArrayList();
        }
    }

    private void fillEdges() {
        Node n = s.getNodeSet().getNodes().get(s.getSampler().getRandomNum(0, s.getNodeSet().getNodes().size()-1));
        ArrayList temp = new ArrayList();
        for (Iterator it = n.getTangle().getDAG().iterator(); it.hasNext(); ) {
            Site site = (Site)it.next();
            Set<DefaultEdge> edges = (Set<DefaultEdge>)n.getTangle().getDAG().outgoingEdgesOf(site);
            if(edges.size() != 0) {
                for (DefaultEdge e : edges) {
                    Site target = (Site) n.getTangle().getDAG().getEdgeTarget(e);
                    temp.add(site.getTx().getID());
                    temp.add(target.getTx().getID());
                    logEdgesInfo.add(temp);
                    temp = new ArrayList();
                }
            }
        }
    }

    private void fillNoofTipsList(float currTime) {
        int counter = 0; // average number of tips
        ArrayList temp = new ArrayList();
        for (Node n : s.getNodeSet().getNodes()) {
            for (Iterator it = n.getTangle().getDAG().iterator(); it.hasNext(); ) {
                Site s = (Site) it.next();
                if (s.getCumulativeWeight() == 1)
                    counter++;
            }
        }
        temp.add(currTime);
        temp.add((float) counter/s.getNodeSet().getNodes().size());
        logNoofTips.add(temp);
    }

    public void fillHList(float currTime, Node n, Transaction tx, float h) {
        ArrayList temp = new ArrayList();
        temp.add(currTime);
        temp.add(n.getID());
        temp.add(tx.getID());
        temp.add(tx.getCreationTime());
        temp.add(h);
        hList.add(temp);
    }

    private void fillCumulativeWeightList(float currTime) {
        ArrayList temp;
        int cw;
        for (Transaction t : logtxList) {
            cw = 0;
            for (Node n : s.getNodeSet().getNodes()) {
                Site site = n.getTangle().findSiteWithTx(t);
                if (site != null) {
                   cw += (float) site.getCumulativeWeight();
                }
            }
            temp = new ArrayList();
            temp.add((float) t.getID());
            temp.add(t.getCreationTime());
            temp.add(currTime);
            temp.add(cw/s.getNodeSet().getNodes().size()); //average cumulative weight
            logCumulativeWeight.add(temp);
        }
    }

    private void fillNumofOrphans(NodeSet ns) {
        ArrayList temp;
        for(Node node : ns.getNodes()){
            temp = new ArrayList();
            temp.add(node.getID());
            temp.add(node.getTangle().getOrphans().size());
            logOrphans.add(temp);
//            System.out.println("N" + node.getID() + " Orphans size: " + node.getTangle().getOrphans().size());
        }
    }

    private void fillTxsCumulativeWeight(TransactionSet ts, NodeSet ns) {
        ArrayList temp;
        int cumulativeWeight = 0;
//        System.out.println("");
        for(Transaction transaction : ts.getTxs()){
            temp = new ArrayList();
            temp.add(transaction.getID());
            temp.add(transaction.getCreationTime());
//            System.out.print("Tx" + transaction.getID() + ": ");
            for(Node node : ns.getNodes()){
                Site s = node.getTangle().findSiteWithTx(transaction);
                if(s != null){
                    cumulativeWeight = s.getCumulativeWeight();
                }
                else {
                    cumulativeWeight = 0; //not found
                }
//                System.out.print(cumulativeWeight + "|");
                temp.add(cumulativeWeight);
                temp.add("|");
            }
            logFinalCumulativeWeights.add(temp);
//            System.out.println("");
        }
    }

    private String createDotFormatTangle(DirectedAcyclicGraph dag) {
        String result = "";
        for (Iterator it = dag.iterator(); it.hasNext(); ) {
            Site site = (Site)it.next();
            Set<DefaultEdge> edges = (Set<DefaultEdge>)dag.outgoingEdgesOf(site);
            if(edges.size() != 0) {
                for (DefaultEdge e : edges) {
                    Site target = (Site) dag.getEdgeTarget(e);
                    result += site.getTx().getID() + "->" + target.getTx().getID()+";";
                }
            }
        }
        return result;
    }

    public static void createDotGraph(String dotFormat,String fileName) {
        GraphViz gv=new GraphViz();
        gv.addln(gv.start_graph());
        gv.add(dotFormat);
        gv.addln(gv.end_graph());
        // String type = "gif";
        String type = "jpg";
        // gv.increaseDpi();
        gv.decreaseDpi();
        gv.decreaseDpi();
        File out = new File(fileName+"."+ type);
        gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out );
    }

    private void writeToTxtFiles(){
        CreateTxt ce = new CreateTxt();
        try {
//            ce.createTxtFile(logCumulativeWeight, "logCumulativeWeight");
//            ce.createTxtFile(logNoofTips, "logNoofTips");
            ce.createTxtFile(logVictimMaliciousTxsConf, "logLegMalTxsConf");
            ce.createTxtFile(log3DMatrix, "log3DMatrix");
//            ce.createTxtFile(logNodesInfo, "logNodesInfo");
//            ce.createTxtFile(logEdgesInfo, "logEdgesInfo");
//            ce.createTxtFile(logOrphans, "logOrphans");
//            ce.createTxtFile(logFinalCumulativeWeights, "logFinalCumulativeWeights");
//            ce.createTxtFile(hList, "logHList");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
