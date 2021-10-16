package esg.dag.sim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NodeSet implements Serializable {
    private final ArrayList<Node> nodes;
    private Simulation sim;
    private TransactionSet ts;
    private Sampler sampler;
    private Node maliciousNode;
    private List<Transaction> genesisTxList = new ArrayList<>();
    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public NodeSet(Simulation s) {
        nodes = new ArrayList<>();
        sim = s;
        sampler = sim.getSampler();
    }

    public String debugPrintNodeSet(){
        String s = "";
        for(int i = 0; i< nodes.size();i++){
            s = s + "Node ID:" + nodes.get(i).getID() + 
                    "\t Hashpower: " + nodes.get(i).getHashpower() + " (H/sec)" +
                    "\n";
        }
        return (s);
    }

    public int getNodeSetSize(){
        return (nodes.size());
    }

    public void addNodes(int num){
        List<Transaction> baseTxList = genesis_founders_TXs();
        if(num < 0)
            throw new ArithmeticException("num < 0");
        for (int i = 1; i<=num; i++){
            Node o = new Node(sim, baseTxList);
            o.setHashPower (sampler.getNextNodeHashPower());
            o.setElectricPower(sampler.getNextNodeElectricPower());
            o.setElectricityCost(sampler.getNextNodeElectricityCost());
            o.setType(Node.Type.HONEST);
            nodes.add(o);
        }
    }

    public void addNodes(int num, int numMalNodes){
        float sumHP = 0;
        float sumHonestHP;
        float maliciousHP;
        List<Transaction> baseTxList = genesis_founders_TXs();

        if(num < 0)
        	throw new ArithmeticException("num < 0");
        for (int i = 1; i <= num; i++){
            Node o = new Node(sim, baseTxList);
            o.setType(Node.Type.HONEST);
            o.setHashPower (sampler.getNextNodeHashPower());
            sumHP += o.getHashpower();
            nodes.add(o);
        }

        //select a random node as the malicious node
        Node maliciousNode = pickArrivalNode();
//        System.out.println("Mal Node ID: " + maliciousNode.getID());
        maliciousNode.setType(Node.Type.MALICIOUS);
        setMaliciousNode(maliciousNode);
        sumHonestHP = sumHP - maliciousNode.getHashpower();
        //maliciousNode hash power is the MaliciousHPPercentage of total hash power in the network
        maliciousHP = (float) ((Parameters.MaliciousHPPercentage * sumHonestHP) /(1 - Parameters.MaliciousHPPercentage));
        maliciousNode.setHashPower(maliciousHP);
    }

    public Node pickArrivalNode() {
        int nInx = sampler.getNextArrivalNode(nodes.size());
        return (nodes.get(nInx));
    }

    public Node getMaliciousNode() {
        return maliciousNode;
    }

    public void setMaliciousNode(Node maliciousNode) {
        this.maliciousNode = maliciousNode;
    }

    public TransactionSet getTs() {
        return ts;
    }

    public void setTs(TransactionSet ts) {
        this.ts = ts;
    }

    public List<Transaction> genesis_founders_TXs(){
        Transaction genesis = new Transaction(1, Globals.currTime, 21000000,
                sim.getSampler().getNextTransactionSize());
        genesisTxList.add(genesis);
        for(int i = 0; i< Parameters.NumofFounders; i++){
            Transaction founder = new Transaction(i+2, Globals.currTime, sim.getSampler().getNextTransactionValue(),
                    sim.getSampler().getNextTransactionSize());
            founder.getParents().add(genesis);
            genesisTxList.add(founder);
        }
        return genesisTxList;
    }

    public List<Transaction> getGenesisTxList() {
        return genesisTxList;
    }

    public void setGenesisTxList(List<Transaction> genesisTxList) {
        this.genesisTxList = genesisTxList;
    }

    public ArrayList<Node> getRandomNodes(){
        ArrayList<Node> output = new ArrayList<>();
        if(getNodes().size() <= Parameters.NumofSim) {
            for (int i = 0; i < getNodes().size(); i++)
                output.add(getNodes().get(i));
        }else {
            while(output.size() < Parameters.NumofSim) {
                int rand = sampler.getRandomNum(0, getNodes().size() - 1);
                if (!output.contains(getNodes().get(rand)))
                    output.add(getNodes().get(rand));
            }
        }
        return output;
    }
}