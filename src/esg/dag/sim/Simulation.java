package esg.dag.sim;

import java.io.Serializable;
import java.util.PriorityQueue;

public class Simulation implements Serializable {
    private final EventTimeComparator comp = new EventTimeComparator();
    private PriorityQueue<Event> queue = new PriorityQueue<>(comp);
    private RandomNetwork net;
    private NodeSet nodes;
    private TransactionSet transactions;
    private Sampler sampler;
    private Report report;
    public static boolean isParasiteChainRevealed = false;

    public static int NumofHonestEvents = (Parameters.NumofNodes + 1) * Parameters.NumofHonestTxs;
    public Simulation(Sampler a){
        this.sampler = a;
    }

    public RandomNetwork getNetwork() {
        return net;
    }

    public void setNetwork(RandomNetwork net){
        this.net = net;
        if(nodes !=null)
        {
        	for(Node n:nodes.getNodes())
        		n.setNetwork(net);
        }
    }

    public TransactionSet getTransactions() {
        return transactions;
    }

    public void setTransactions(TransactionSet transactions) {
        this.transactions = transactions;
    }

    public void setNodes(NodeSet nodes) {
        this.nodes = nodes;
    }

    public Sampler getSampler() {
        return sampler;
    }

    public void setSampler(Sampler sampler) {
        this.sampler = sampler;
    }

    void setNodeSet(NodeSet ns) {
        nodes = ns;
        if(net!=null)
        {
        	for(Node n:nodes.getNodes())
        		n.setNetwork(net);
        }
    }

    NodeSet getNodeSet() {
        return(this.nodes);
    }

    public void setInitialTransactionSet(TransactionSet ts){
        queue = ts.getQueue();
    }

    public void schedule(Event e){
        queue.add(e);
    }

    void removeEventFromQueue(Event e) {
        queue.remove(e);
    }

    public void run(){
        //MainLoop
        int eventCounter = 0;
        Event e;
        while (!queue.isEmpty()){
            e = queue.poll(); //it removes the last element of the queue
            if(NumofHonestEvents > 0) {
                Globals.currTime = e.getTime();
                e.happen(this);
                if (e.isHonest()) {
                    NumofHonestEvents--;
                }
                eventCounter++;
                report.doTimeBasedReports(eventCounter, Globals.currTime);
            } else {
                System.out.println("End of the simulation time.");
            }
        }
        report.doOtherReports();
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }
}






