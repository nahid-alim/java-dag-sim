package esg.dag.sim;

public class NewTransactionArrival extends Event {
    private Transaction transaction;
    private Node node;

    public NewTransactionArrival(Transaction tx, Node n, float time) {
        this.node = n;
        this.transaction = tx;
        super.setTime(time);
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    @Override
    public void happen(Simulation sim) {
        super.happen(sim);
        node.receiveNewTransaction(transaction, getTime());
    }

    @Override
    public boolean isHonest() {
        if(transaction.getType().equals(Transaction.Type.HONEST))
            return true;
        else
            return false;
    }
}
