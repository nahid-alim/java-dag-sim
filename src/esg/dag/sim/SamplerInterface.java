package esg.dag.sim;

public interface SamplerInterface {

    public float getNextTransactionArrivalInterval();

    public float getNextMiningInterval(double hashPower);

    public float getNextTransactionValue();

    public long getNextTransactionSize();//in bytes

    public float getNextNodeElectricPower();

    public float getNextNodeHashPower();

    public float getNextNodeElectricityCost();

    public int getNextArrivalNode(int nNodes);

    public float getNextConnectionThroughput();

    public int getRandomNum(int min, int max);

//    void setSeed(long seed);
}
