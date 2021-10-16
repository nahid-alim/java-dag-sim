package esg.dag.sim;


public class Event {
    private float time;

    public void setTime(float time) {
    	if(time < 0)
    		throw new ArithmeticException("Time < 0");
        this.time = time;
    }

    public float getTime() {
        return time;
    }
    
    public void happen(Simulation sim){
        String evtType ="";
//        if (this instanceof NewTransactionArrival) evtType = "NewTransactionArrival";
//        if (this instanceof TransactionValidation) evtType = "TransactionValidation";
//        if (this instanceof TransactionPropagation) evtType = "TransactionPropagation";
//        System.out.print("Time:" + String.format("%.4f", this.getTime()) + " s \t Event:" + evtType + "\n");
    }

    public boolean isHonest() {
        return true;
    }
}
