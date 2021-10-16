package esg.dag.sim;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class TransactionSet {
    private ArrayList<Transaction> Txs = new ArrayList<>();
    private EventTimeComparator comp = new EventTimeComparator();
    private PriorityQueue<Event> queue = new PriorityQueue<>(comp);
    private Simulation sim;
    private NodeSet ns;
    private Sampler sampler;
    private Transaction victimTx;
    private Transaction maliciousTx;
    
    private long transactionCount = 0;
    private long transactionTotalValue = 0;
    private long transactionTotalSize = 0;
    private float timeEnd = 0;

    public long getTransactionNum() {
        return transactionCount;
    }

    public void setTransactionNum(long transactionNum) {
    	if(transactionNum < 0)
    		throw new ArithmeticException("transactionNum < 0");
        this.transactionCount = transactionNum;
    }

    public long getTransactionTotalVal() {
        return transactionTotalValue;
    }

    public void setTransactionTotalVal(long transactionTotalVal) {
    	if(transactionTotalVal < 0)
    		throw new ArithmeticException("transactionTotalVal < 0");
        this.transactionTotalValue = transactionTotalVal;
    }

    public long getTransactionTotalSize() {
        return transactionTotalSize;
    }

    public void setTransactionTotalSize(long transactionTotalSize) {
    	if(transactionTotalSize < 0)
    		throw new ArithmeticException("transactionTotalSize < 0");
        this.transactionTotalSize = transactionTotalSize;
    }

    public TransactionSet(Simulation s) {
        Txs = new ArrayList<>();
        comp = new EventTimeComparator();
        queue = new PriorityQueue<>(comp);
        sim = s;
    }
    
    public TransactionSet(Simulation sim, NodeSet ns) {
        Txs = new ArrayList<>();
        comp = new EventTimeComparator();
        queue = new PriorityQueue<>(comp);
        sampler = sim.getSampler();
        this.sim = sim;
        this.ns = ns;
    } 

    public PriorityQueue<Event> getQueue() {
        return queue;
    }

    public void addTransactions(long num, float startTime){
    	if(startTime < 0)
    		throw new ArithmeticException("startTime < 0");
    	if(num < 0)
    		throw new ArithmeticException("num < 0");
        float currTime = startTime;

        //add genesis txs to Txs
        for(Transaction tr : ns.getGenesisTxList()){
            Txs.add(tr);
            transactionCount++;
            transactionTotalSize += tr.getSize();
            transactionTotalValue += tr.getValue();
        }

        for (int i = 1; i <= num; i++){
            currTime += sampler.getNextTransactionArrivalInterval();
            addTransaction(currTime);
        }
        timeEnd = currTime;
        int index = Parameters.VictimTxIndex;
        setVictimTx(getTxs().get(index));
        for(Transaction t : getTxs()) {
            if (t.getID()==2000 || t.getID()==4000 || t.getID()==6000 || t.getID()==8000 || t.getID()==10000 /*||
                    t.getID()==17000 || t.getID()==17400 ||t.getID()==17441 || t.getID()==17442 || t.getID()==17443
                    || t.getID()==17444 || t.getID()==17445 || t.getID()==17446 || t.getID()==17447
                    || t.getID()==17448 || t.getID()==17449|| t.getID()==17450*//*% Parameters.TxID == 0*/)
                sim.getReport().logtxList.add(t);
        }
    }

    public void appendTransactions(long num){
    	if(num < 0)
    		throw new ArithmeticException("num < 0");
        addTransactions(num, timeEnd);
    }
   

    public ArrayList<Transaction> getTxs() {
        return Txs;
    }

    public void setTxs(ArrayList<Transaction> txs) {
        Txs = txs;
    }

    //related to Tangle
    public void addTransaction(float currTime){
        Transaction t;
        NewTransactionArrival e;
        Node node;

        //select a random node
        node = ns.pickArrivalNode();

        t = new Transaction(Transaction.getNextTxID(),
                currTime,
                sampler.getNextTransactionValue(),
                sampler.getNextTransactionSize());
        t.setType(Transaction.Type.HONEST);
        Txs.add(t);

        e = new NewTransactionArrival(t, node, t.getCreationTime());
        sim.schedule(e);

        transactionCount++;
        transactionTotalSize += t.getSize();
        transactionTotalValue += t.getValue();
    }

    public Sampler getSampler() {
        return sampler;
    }

    public void setSampler(Sampler sampler) {
        this.sampler = sampler;
    }

    public Transaction getVictimTx() {
        return victimTx;
    }

    public void setVictimTx(Transaction victimTx) {
        this.victimTx = victimTx;
    }

    public Transaction getMaliciousTx() {
        return maliciousTx;
    }

    public void setMaliciousTx(Transaction maliciousTx) {
        this.maliciousTx = maliciousTx;
    }

    public ArrayList<Transaction> getRandomTxs(float time) {
        ArrayList<Transaction> output = new ArrayList<>();
        ArrayList<Transaction> arrivedTxstoDAGs = getArrivedTxstoDAGs(time);
        if(arrivedTxstoDAGs.size() <= Parameters.NumofSim) {
            for (int i = 0; i < arrivedTxstoDAGs.size(); i++) {
                output.add(arrivedTxstoDAGs.get(i));
            }
        }else {
            while(output.size() < Parameters.NumofSim) {
                int rand = sampler.getRandomNum(0, arrivedTxstoDAGs.size() - 1);
                if (!output.contains(arrivedTxstoDAGs.get(rand))) {
                    output.add(arrivedTxstoDAGs.get(rand));
                }
            }
        }
        Report.sampleTxsCount = output.size();
        return output;
    }

    private ArrayList<Transaction> getArrivedTxstoDAGs(float time) {
        ArrayList<Transaction> output = new ArrayList<>();
        for (int i = Parameters.NumofFounders + 1; i < getTxs().size(); i++) {
            if(getTxs().get(i).getCreationTime() <= time)
                output.add(getTxs().get(i));
        }
        Report.arrivedTxsCount = output.size();
        return output;
    }

    public float getTimeEnd() {
        return timeEnd;
    }
}
