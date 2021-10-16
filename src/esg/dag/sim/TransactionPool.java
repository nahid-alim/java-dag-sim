package esg.dag.sim;

import java.io.Serializable;
import java.util.ArrayList;


/** The list that contains all unvalidated transactions present at sampler node.
 *
 * @author Sotirios
 */
public class TransactionPool implements Serializable{
    private ArrayList<Transaction> pool;

    /**Constructor for the TransactionPool class.
     * 
     */
    public TransactionPool() {
        pool = new ArrayList<>();
    }
    
    /** Adds sampler transaction to the list of transactions.
     * 
     * @param tx The transaction to be added to the list.
     */
    public void addToPool(Transaction tx){
        pool.add(tx);
    }

    public Transaction removeFirstTxFromPool(){
        return pool.remove(0);
    }

    
    /** Remove transactions contained in sampler given block from the list.
     * 
     * @param b The block with transactions to be removed from the list.
     */
//    public void removeBlockFromPool(Block b){
//        for (Transaction t : b.getTxSet()){
//            pool.remove(t);
//        }
//
//    }

    /** Returns the list of transactions.
     * 
     * @return List of transactions.
     */
    public ArrayList<Transaction> getTransactions() {
        return pool;
    }

    /**Sets the transaction list.
     * 
     * @param Pool The transaction list.
     */
    public void setPool(ArrayList<Transaction> Pool) {
        this.pool = Pool;
    }

    /**Remove sampler transaction from the list.
     * 
     * @param t The transaction to be removed from the list.
     */
    void removeTxFromPool(Transaction t) {
        pool.remove(t);
    }
    
    
    /** Returns all transaction ids contained in the list for debugging.
     * 
     * @return String containing all transaction ids present in the transaction list.
     */
    public String debugPrintPoolTx(){
        String s = "";
        for (Transaction t : pool){
            s = s + t.getID() + ", ";
        }
        return (s);
    }
    
}
