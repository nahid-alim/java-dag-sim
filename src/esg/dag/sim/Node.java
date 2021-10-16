package esg.dag.sim;

import java.io.Serializable;
import java.util.*;

public class Node implements NodeInterface,Serializable {

    private int ID;
    private Simulation sim;
    private Network network;
    private float hashpower; //in trials/s
    private float electricPower;
    private float electricityCost;
    private static int currID = 1;
    public static int malNodePropCounter = 0;
    public static float createhiddenChainTime = 0;
    public static float revealhiddenChainTime = 0;
    public boolean attackingMode = false;
    private ArrayList<Transaction> fakeTxList;
    private Transaction lastTxinParasiteChain;

    public enum Type {HONEST, MALICIOUS};
    private Type type;
    private Tangle tangle;
    private TransactionPool pool;
    private boolean isBusyMining = false;
    private ArrayList<TransactionPropagation> malTxPropagationEvents;

    public Node(Simulation sim, List<Transaction> txList) {
        this.sim = sim;
        pool = new TransactionPool();
        setNetwork(sim.getNetwork());
        ID = getNextNodeID();
        tangle = new Tangle(this, txList);
        fakeTxList = new ArrayList<>();
        malTxPropagationEvents = new ArrayList<>();
    }

    public int getID() {
        return ID;
    }

    public static int getNextNodeID(){
        return(currID++);
    }

    @Override
    public void setHashPower(float hashpower) {
    	if(hashpower < 0 )
    		throw new ArithmeticException("Hash Power < 0");
        this.hashpower = hashpower;
    }

    @Override
    public void setElectricPower(float electricPower) {
    	if(electricPower < 0 )
    		throw new ArithmeticException("Electric Power < 0");
        this.electricPower = electricPower;
    }

    @Override
    public void setElectricityCost(float electricityCost) {
    	if(electricityCost < 0 )
    		throw new ArithmeticException("Electricity Cost < 0");
        this.electricityCost = electricityCost;
    }
       
    @Override
    public float getHashpower() {
        return hashpower;
    }

    @Override
    public float getElectricPower() {
        return electricPower;
    }

    @Override
    public float getElectricityCost() {
        return electricityCost;
    }
        
    @Override
    public void setNetwork(Network net) {
        this.network = net;
    }

    @Override
    public Network getNetwork() {
        return network;
    }

    public void addTxtoTangle(Transaction t) {
        double victimConfRate = 0;
        tangle.addToTangle(t);
        //if the victim tx arrives at the malicious node, this node starts creating a double-spend tx and a hidden parasite chain
        if(this.getType().equals(Type.MALICIOUS)){
            if(t.equals(sim.getTransactions().getVictimTx())) {
                createHiddenParasiteChain(t, this.getSim());
                attackingMode = true;
            }

            //calc victimConfRate before revealing the malicious chain
            if(!sim.isParasiteChainRevealed) {
//                Site victimSite = tangle.findSiteWithTx(sim.getTransactions().getVictimTx());
                victimConfRate = getVictimTxAvgConfRate();
//                System.out.println(victimConfRate);
            }
            if (victimConfRate >= Parameters.ConfThreshold) {
                sim.isParasiteChainRevealed = true;
                revealhiddenChainTime = Globals.currTime;
            }
            if(sim.isParasiteChainRevealed)
                revealTheParasiteChain();
        }
    }

    private void revealTheParasiteChain() {
        malNodePropCounter += malTxPropagationEvents.size();
        for(TransactionPropagation tp : malTxPropagationEvents){
            float inter = sim.getNetwork().getPropagationTime(sim.getNodeSet().getMaliciousNode().getID(), tp.getNode().getID(), tp.getTransaction().getSize());
            tp.setTime(Globals.currTime + inter);
            sim.schedule(tp);
        }
        malTxPropagationEvents = new ArrayList<>();
    }

    private void createHiddenParasiteChain(Transaction victimTx, Simulation sim) {
        ArrayList<Transaction> temp_tipList = new ArrayList<>();
        float currTime = Globals.currTime;
//        currTime += sim.getSampler().getNextTransactionArrivalInterval(); //comment this line later
        //first create the double-spending tx
        Transaction maliciousTx = new Transaction(Transaction.getNextTxID(),
                currTime,
                sim.getTransactions().getVictimTx().getValue(),
                sim.getTransactions().getVictimTx().getSize());
        //add to pool
//        System.out.println("Mal Tx ID: " + maliciousTx.getID());
        maliciousTx.setParents(victimTx.getParents());
        maliciousTx.setType(Transaction.Type.MALICIOUS);
        sim.getTransactions().setMaliciousTx(maliciousTx);
        fakeTxList.add(maliciousTx);
        temp_tipList.add(maliciousTx);

        //create fake txs which will verify the double-spending tx
        for (int i = 1; i <= Parameters.NumberofFakeTxs; i++){
//            currTime += sim.getSampler().getNextTransactionArrivalInterval(); //comment this line later
            Transaction tx = new Transaction(Transaction.getNextTxID(),
                    currTime,
                    sim.getSampler().getNextTransactionValue(),
                    sim.getSampler().getNextTransactionSize());

            tx.setParents(temp_tipList);
            tx.setType(Transaction.Type.FAKE);
            fakeTxList.add(tx);
            //set the parent for next fake tx
            temp_tipList = new ArrayList<>();
            temp_tipList.add(tx);
        }
        lastTxinParasiteChain = fakeTxList.get(fakeTxList.size()-1);
        for(Transaction tx : fakeTxList){
            Event e = new NewTransactionArrival(tx, this, tx.getCreationTime());
            sim.schedule(e);
        }
        createhiddenChainTime = Globals.currTime;
    }

    public void validateTransaction(Transaction transaction, float time) {
        selectTips(transaction);
        // why first propagate and then add to the tangle: if mal tx validation becomes the last event in the queue, then the mal tx propagation condition is not get checked anymore
        propagateTransaction(transaction, time);
//        long a = System.currentTimeMillis();
//        System.out.println("propagateTransaction " + a);
        addTxtoTangle(transaction);
//        long b = System.currentTimeMillis();
//        System.out.println("addTxtoTangle " + b);
        setBusyMining(false);
        setUpNextValidationEvent(time);
    }

    private void setUpNextValidationEvent(float time) {
        if(getPool().getTransactions().size() > 0){
            Transaction transaction = getPool().removeFirstTxFromPool();
//            float h = Parameters.h;
            float h = sim.getSampler().getNextMiningInterval(getHashpower());
            TransactionValidation e = new TransactionValidation(transaction, this, time + h);
            sim.schedule(e);
            setBusyMining(true);
//            getSim().getReport().fillHList(time, this, transaction, h);
        }
    }

    private void propagateTransaction(Transaction transaction, float time) {
        NodeSet nodes = sim.getNodeSet();
        ArrayList<Node> ns_list = nodes.getNodes();
        for (Node n : ns_list) {
            if (!n.equals(this)){
                float inter = sim.getNetwork().getPropagationTime(this.getID(), n.getID(), transaction.getSize());
                TransactionPropagation e = new TransactionPropagation(transaction, n, time + inter);
                if(attackingMode)
                    malTxPropagationEvents.add(e);
                else
                    sim.schedule(e);
            }
        }
    }

    public void receiveNewTransaction(Transaction transaction, float time) {
        //for the sake of the simulation time, todo: check for validity
        if(attackingMode && transaction.getType().equals(Transaction.Type.HONEST))
            sim.NumofHonestEvents -= (Parameters.NumofNodes - 1);
//        if(node.getType().equals(Node.Type.MALICIOUS) && transaction.getType().equals(Transaction.Type.HONEST) && transaction.getID() != sim.getTransactions().getVictimTx().getID()){
//
//        }
//        else {
        if(!isBusyMining()) {
//            float h = Parameters.h;
            float h = sim.getSampler().getNextMiningInterval(getHashpower());
            TransactionValidation e = new TransactionValidation(transaction, this, time + h);
            sim.schedule(e);
            setBusyMining(true);
//            getSim().getReport().fillHList(time, this, transaction, h);
        }
        else{
            getPool().addToPool(transaction);
        }
//        }
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public Simulation getSim() {
        return sim;
    }

    public void setSim(Simulation sim) {
        this.sim = sim;
    }

    public void setHashpower(float hashpower) {
        this.hashpower = hashpower;
    }

    public static int getCurrID() {
        return currID;
    }

    public static void setCurrID(int currID) {
        Node.currID = currID;
    }

    public void selectTips(Transaction transaction) {
        tangle.selectTips(transaction);
    }

    public Tangle getTangle() {
        return tangle;
    }

    public void setTangle(Tangle tangle) {
        this.tangle = tangle;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public TransactionPool getPool() {
        return pool;
    }

    public void setPool(TransactionPool pool) {
        this.pool = pool;
    }

    public ArrayList<Transaction> getFakeTxList() {
        return fakeTxList;
    }

    public void setFakeTxList(ArrayList<Transaction> fakeTxList) {
        this.fakeTxList = fakeTxList;
    }

    public ArrayList<TransactionPropagation> getMalTxPropagationEvents() {
        return malTxPropagationEvents;
    }

    public void setMalTxPropagationEvents(ArrayList<TransactionPropagation> malTxPropagationEvents) {
        this.malTxPropagationEvents = malTxPropagationEvents;
    }

    public boolean isBusyMining() {
        return isBusyMining;
    }

    public void setBusyMining(boolean busyMining) {
        this.isBusyMining = busyMining;
    }

    public Transaction getLastTxinParasiteChain() {
        return lastTxinParasiteChain;
    }

    public void setLastTxinParasiteChain(Transaction lastTxinParasiteChain) {
        this.lastTxinParasiteChain = lastTxinParasiteChain;
    }

    public double getVictimTxAvgConfRate(){
        double result = 0;
        for(Node n : sim.getNodeSet().getNodes()){
            if(n.type.equals(Type.HONEST)) {
                Site victimSite = n.getTangle().findSiteWithTx(sim.getTransactions().getVictimTx());
                result += n.getTangle().calcConfidenceRate(victimSite);
            }
        }
        return result/sim.getNodeSet().getNodes().size();
    }
}



