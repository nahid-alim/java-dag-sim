package esg.dag.sim;

public class TangleSim {
    public static void main(String[] args) {
        TangleSim b = new TangleSim();
        String outputFile = "nodeset.ser";
        b.run(outputFile);
    }

    private void run(String outputFile){
        RandomNetwork n;
        Simulation s;
        NodeSet ns;
        TransactionSet ts;
        Sampler sampler;

        //Creating sampler
        sampler = new StandardSampler();
        sampler.setTxArrivalIntervalRate(Parameters.Lambda);
        sampler.setTxSizeMean(5000);//each tryte is 1300 bytes
        sampler.setTxSizeSD(500);
        sampler.setTxValueMean(50);
        sampler.setTxValueSD(12);
        sampler.setNodeHashPowerMean(Parameters.HashPowerMean);
        sampler.setNodeHashPowerSD(Parameters.HashPowerSD);
        sampler.setNodeElectricPowerMean(1375);
        sampler.setNodeElectricPowerSD(20);
        sampler.setNodeElectricCostMean((float) 0.1);
        sampler.setNodeElectricCostSD((float) 0.05);
        sampler.setNetThroughputMean(Parameters.NetThroughputMean);
        sampler.setNetThroughputSD(Parameters.NetThroughputSD);
        sampler.setCurrentDifficulty(Parameters.MWM);
        //If sampler Random object is instanced without sampler seed, the seed will be the same as the system time in milliseconds
        //        sampler.setSeed(2018);

        s = new Simulation(sampler);
        ns = new NodeSet(s);
        n = new RandomNetwork(ns,sampler);
        s.setNetwork(n);
        ns.addNodes(Parameters.NumofNodes); //a network where all nodes are honest
//        ns.addNodes(Parameters.NumofNodes, 1); //a network where 1 node is malicious
        s.setNodeSet(ns);
        ts = new TransactionSet(s, ns);
        ts.appendTransactions(Parameters.NumofHonestTxs);
        s.setTransactions(ts);
//        System.out.println("Victim Tx ID: " + s.getTransactions().getVictimTx().getID());
        s.setReport(new Report(s));
        try {
            Globals.createFileforWriter();
        }catch (Exception e){}

        long beginningTime = System.currentTimeMillis();

        s.run();

        long endTime = System.currentTimeMillis();
        long realTime = (endTime - beginningTime); // in Milli-Sec
        System.out.println("Real time(ms) is: " + realTime);
        System.out.println("And Simulation time(ms) is: " + Globals.currTime);
//        System.out.println("Malicious Node ID is: " + s.getNodeSet().getMaliciousNode().getID());
//        System.out.println("VictimTx ID: " + s.getTransactions().getVictimTx().getID());
//        System.out.println("MalTx ID: " + s.getTransactions().getMaliciousTx().getID());
        System.out.println("MalNodePropCounter: " + Node.malNodePropCounter);
        System.out.println("Hidden Chain Creation Time(ms): " + s.getNodeSet().getMaliciousNode().createhiddenChainTime);
        System.out.println("Hidden Chain Revealing Time(ms): " + s.getNodeSet().getMaliciousNode().revealhiddenChainTime);
        Globals.writer.close();
    }
}
