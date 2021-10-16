package esg.dag.sim;

public class TransactionPropagation extends Event {
    private Transaction transaction;
    private Node node;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    TransactionPropagation(Transaction tx, Node n, float time){
        this.node = n;
        this.transaction = tx;
        super.setTime(time);
    }

    @Override
    public void happen(Simulation sim) {
        super.happen(sim);
        node.addTxtoTangle(transaction);
//        long b = System.currentTimeMillis();
//        System.out.println("addTxtoTangle for propagation" + b);
    }

    @Override
    public boolean isHonest() {
        if(transaction.getType().equals(Transaction.Type.HONEST))
            return true;
        else
            return false;
    }
}
