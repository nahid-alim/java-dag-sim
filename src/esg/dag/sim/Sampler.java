package esg.dag.sim;

import java.io.Serializable;

public abstract class Sampler implements SamplerInterface,Serializable {

    protected float txArrivalIntervalRate; 

    protected float txSizeMean;

    protected float txSizeSD;

    protected float txValueMean;

    protected float txValueSD;
    
    protected float nodeHashPowerMean;

    protected float nodeHashPowerSD;

    protected float nodeElectricPowerMean;

    protected float nodeElectricPowerSD;

    protected float nodeElectricCostMean;

    protected float nodeElectricCostSD;

    protected float netThroughputMean;

    protected float netThroughputSD;
    
    protected double currentDifficulty;

    public double getCurrentDifficulty() {
        return currentDifficulty;
    }

    public void setCurrentDifficulty(double currentDifficulty) {
        this.currentDifficulty = currentDifficulty;
    }

    public Sampler() {
    }

    public Sampler(float txArrivalIntervalRate, float txSizeMean, float txSizeSD,
            float txValueMean, float txValueSD,
            float nodeHashPowerMean, float nodeHashPowerSD,
            float nodeElectricPowerMean, float nodeElectricPowerSD,
            float nodeElectricCostMean, float nodeElectricCostSD,
            double difficulty) {
        this.txArrivalIntervalRate = txArrivalIntervalRate;
        this.txSizeMean = txSizeMean;
        if(txSizeSD < 0)
    		throw new ArithmeticException("Transaction Size Standard Deviation < 0");
        this.txSizeSD = txSizeSD;
        if(txValueMean < 0)
    		throw new ArithmeticException("Transaction Value Mean < 0");
        this.txValueMean = txValueMean;
        if(txValueSD < 0)
    		throw new ArithmeticException("Transaction Value Standard Deviation < 0");
        this.txValueSD = txValueSD;
        if(nodeHashPowerMean < 0)
    		throw new ArithmeticException("Node Hash Power Mean < 0");
        this.nodeHashPowerMean = nodeHashPowerMean;
        if(nodeHashPowerSD < 0)
    		throw new ArithmeticException("Node Hash Power Standard Deviation < 0");
        this.nodeHashPowerSD = nodeHashPowerSD;
        if(nodeElectricPowerMean < 0)
    		throw new ArithmeticException("Node Electric Power Mean < 0");
        this.nodeElectricPowerMean = nodeElectricPowerMean;
        if(nodeElectricPowerSD < 0)
    		throw new ArithmeticException("Node Electric Power Standard Deviation < 0");
        this.nodeElectricPowerSD = nodeElectricPowerSD;
        if(nodeElectricCostMean < 0)
    		throw new ArithmeticException("Node Electric Cost Mean < 0");
        this.nodeElectricCostMean = nodeElectricCostMean;
        if(nodeElectricCostSD < 0)
    		throw new ArithmeticException("Node Electric Cost Standard Deviation < 0");
        this.nodeElectricCostSD = nodeElectricCostSD;
        if(netThroughputMean < 0)
    		throw new ArithmeticException("Network Throughput Mean < 0");
        this.netThroughputMean = netThroughputMean;
        if(netThroughputSD < 0)
    		throw new ArithmeticException("Network Throughput Standard Deviation < 0");
        this.netThroughputSD = netThroughputSD;
        
        this.currentDifficulty = difficulty;
    }

    public float getTxArrivalIntervalRate() {
        return txArrivalIntervalRate;
    }

    public void setTxArrivalIntervalRate(float txArrivalIntervalRate) {
    	if(txArrivalIntervalRate < 0)
    		throw new ArithmeticException("Transaction Arrival Interval Rate < 0");
        this.txArrivalIntervalRate = txArrivalIntervalRate;
    }

    public float getTxSizeMean() {
        return txSizeMean;
    }

    public void setTxSizeMean(float txSizeMean) {
    	if(txSizeMean < 0)
    		throw new ArithmeticException("Transaction size mean < 0");
        this.txSizeMean = txSizeMean;
    }

    public float getTxSizeSD() {
        return txSizeSD;
    }

    public void setTxSizeSD(float txSizeSD) {
    	if(txSizeSD < 0)
    		throw new ArithmeticException("Transaction Size Standard Deviation < 0");
        this.txSizeSD = txSizeSD;
    }

    public float getTxValueMean() {
        return txValueMean;
    }

    public void setTxValueMean(float txValueMean) {
    	if(txValueMean < 0)
    		throw new ArithmeticException("Transaction Value Mean < 0");
        this.txValueMean = txValueMean;
    }

    public float getTxValueSD() {
        return txValueSD;
    }

    public void setTxValueSD(float txValueSD) {
    	if(txValueSD < 0)
    		throw new ArithmeticException("Transaction Value Standard Deviation < 0");
        this.txValueSD = txValueSD;
    }

    public float getNodeHashPowerMean() {
        return nodeHashPowerMean;
    }

    public void setNodeHashPowerMean(float nodeHashPowerMean) {
    	if(nodeHashPowerMean < 0)
    		throw new ArithmeticException("Node Hash Power Mean < 0");
        this.nodeHashPowerMean = nodeHashPowerMean;
    }

    public float getNodeHashPowerSD() {
        return nodeHashPowerSD;
    }

    public void setNodeHashPowerSD(float nodeHashPowerSD) {
    	if(nodeHashPowerSD < 0)
    		throw new ArithmeticException("Node Hash Power Standard Deviation < 0");
        this.nodeHashPowerSD = nodeHashPowerSD;
    }

    public float getNodeElectricPowerMean() {
        return nodeElectricPowerMean;
    }

    public void setNodeElectricPowerMean(float nodeElectricPowerMean) {
    	if(nodeElectricPowerMean < 0)
    		throw new ArithmeticException("Node Electric Power Mean < 0");
        this.nodeElectricPowerMean = nodeElectricPowerMean;
    }

    public float getNodeElectricPowerSD() {
        return nodeElectricPowerSD;
    }

    public void setNodeElectricPowerSD(float nodeElectricPowerSD) {
    	if(nodeElectricPowerSD < 0)
    		throw new ArithmeticException("Node Electric Power Standard Deviation < 0");
    }

    public float getNodeElectricCostMean() {
        return nodeElectricCostMean;
    }

    public void setNodeElectricCostMean(float nodeElectricCostMean) {
    	if(nodeElectricCostMean < 0)
    		throw new ArithmeticException("Node Electric Cost Mean < 0");
        this.nodeElectricCostMean = nodeElectricCostMean;
    }

    public float getNodeElectricCostSD() {
        return nodeElectricCostSD;
    }

    public void setNodeElectricCostSD(float nodeElectricCostSD) {
    	if(nodeElectricCostSD < 0)
    		throw new ArithmeticException("Node Electric Cost Standard Deviation < 0");
        this.nodeElectricCostSD = nodeElectricCostSD;
    }

    public float getNetThroughputMean() {
        return netThroughputMean;
    }

    public void setNetThroughputMean(float netThroughputMean) {
    	if(netThroughputMean < 0)
    		throw new ArithmeticException("Network Throughput Mean < 0");
        this.netThroughputMean = netThroughputMean;
    }

    public float getNetThroughputSD() {
        return netThroughputSD;
    }

    public void setNetThroughputSD(float netThroughputSD) {
    	if(netThroughputSD < 0)
    		throw new ArithmeticException("Network Throughput Standard Deviation < 0");
        this.netThroughputSD = netThroughputSD;
    }

    @Override
    public abstract float getNextTransactionArrivalInterval();

    @Override
    public abstract float getNextMiningInterval(double hashPower);

    @Override
    public abstract float getNextTransactionValue();

    @Override
    public abstract long getNextTransactionSize();

    @Override
    public abstract float getNextNodeElectricPower();

    @Override
    public abstract float getNextNodeHashPower();

    @Override
    public abstract float getNextNodeElectricityCost();

    @Override
    public abstract int getNextArrivalNode(int nNodes);

    @Override
    public abstract float getNextConnectionThroughput();

    @Override
    public abstract int getRandomNum(int min, int max);

}
