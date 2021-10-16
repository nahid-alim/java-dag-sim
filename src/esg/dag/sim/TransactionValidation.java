package esg.dag.sim;


public class TransactionValidation extends Event {
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

    TransactionValidation(Transaction tx, Node n, float time){
        this.node = n;
        this.transaction = tx;
        super.setTime(time);
    }

    @Override
    public void happen(Simulation sim) {
        super.happen(sim);
        node.validateTransaction(transaction, getTime());
    }

    @Override
    public boolean isHonest() {
        if(transaction.getType().equals(Transaction.Type.HONEST))
            return true;
        else
            return false;
    }
}
