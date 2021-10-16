package esg.dag.sim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Transaction  implements Serializable{

    private int ID;
    private float size;
    private float value;
    private float creationTime;
    public static int currID = 2 + Parameters.NumofFounders;// 1, 2-11 are genesis and founders in the tangle

    //related to Tangle
    public enum Type {HONEST, MALICIOUS, FAKE};
    private Type type;
    private ArrayList<Transaction> parents;
    private ArrayList<Site> siteList;

    public float getCreationTime() {
        return creationTime;
    }

    public Transaction(int ID, float time, float value, float size) {
    	if(time < 0)
    		throw new ArithmeticException("Time < 0");
        creationTime = time;
        parents = new ArrayList<>();
        siteList = new ArrayList<>();
        this.ID = ID;
        if(value < 0)
    		throw new ArithmeticException("Value < 0");
        this.value = value;
        if(size < 0)
    		throw new ArithmeticException("Size < 0");
        this.size = size;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
    	if(value < 0)
    		throw new ArithmeticException("Value < 0");
        this.value = value;
    }

    public void setSize(float size) {
        if(size < 0)
    		throw new ArithmeticException("Size < 0");
        this.size = size;
    }

    public float getSize() {
        return (size);
    }

    public static int getNextTxID(){
        return(currID++);
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
       return(ID);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ArrayList<Transaction> getParents() {
        return parents;
    }

    public void setParents(ArrayList<Transaction> parents) {
        this.parents = parents;
    }

    public ArrayList<Site> getSiteList() {
        return siteList;
    }
}
