package esg.dag.sim;

import java.util.Random;

public class StandardSampler extends Sampler {
    private Random random = new Random();
//    private long seed;
//
//    public long getSeed() {
//        return seed;
//    }
//
//    public void setSeed(long seed) {
//        this.seed = seed;
//        random.setSeed(seed);
//    }

    public StandardSampler() {
    }

    public StandardSampler(float txArrivalIntervalRate,
            float txSizeMean, float txSizeSD,
            float txValueMean, float txValueSD,
            float nodeHashPowerMean, float nodeHashPowerSD,
            float nodeElectricPowerMean, float nodeElectricPowerSD,
            float nodeElectricCostMean, float nodeElectricCostSD,
            float netThroughputMean, float netThroughputSD,
            double difficulty) {
        this.txArrivalIntervalRate = txArrivalIntervalRate;
        this.txSizeMean = txSizeMean;
        this.txSizeSD = txSizeSD;
        this.txValueMean = txValueMean;
        this.txValueSD = txValueSD;
        this.nodeHashPowerMean = nodeHashPowerMean;
        this.nodeHashPowerSD = nodeHashPowerSD;
        this.nodeElectricPowerMean = nodeElectricPowerMean;
        this.nodeElectricPowerSD = nodeElectricPowerSD;
        this.nodeElectricCostMean = nodeElectricCostMean;
        this.nodeElectricCostSD = nodeElectricCostSD;
        this.netThroughputMean = netThroughputMean;
        this.netThroughputSD = netThroughputSD;
        this.currentDifficulty = difficulty;
    }

    public double getPoissonInterval(float lambda) {
    	if(lambda < 0)
    		throw new ArithmeticException("lambda < 0");
		double p = random.nextDouble();
		while (p == 0.0){
			p = random.nextDouble();
		}
        return (double) (Math.log(1-p)/(-lambda));
    }
    
    private float getGaussian(float mean, float deviation) {
    	if(deviation < 0)
    		throw new ArithmeticException("Standard deviation < 0");
    	float gaussianValue = mean + (float) random.nextGaussian() * deviation;
    	while(gaussianValue <= 0) {
    		gaussianValue = mean + (float) random.nextGaussian() * deviation;
    	}
    	return gaussianValue;
    }
 
    
    @Override
    public float getNextTransactionArrivalInterval() {
    	return (float) getPoissonInterval(txArrivalIntervalRate)*1000;
    }

    @Override
    public int getRandomNum(int min, int max) {
        return(random.nextInt((max - min) + 1) + min);
    }


    @Override
    public float getNextMiningInterval(double hashPower) {

        if(hashPower < 0)
            throw new ArithmeticException("hashPower < 0");

        return((float) getNextMiningIntervalMiliSeconds(hashPower, currentDifficulty));
    }

    public double getNextMiningIntervalTrials(double difficulty) {
    	if(difficulty < 0)
    		throw new ArithmeticException("difficulty < 0");
        return ((double) (Math.log(1-Math.random())/Math.log1p(- 1.0/difficulty)));
    }

    public double getNextMiningIntervalSeconds(double hashPower, double difficulty) {
     	if(hashPower < 0)
    		throw new ArithmeticException("hashPower < 0");

    	return((double) getNextMiningIntervalTrials(difficulty) / hashPower);
    }

    public double getNextMiningIntervalMiliSeconds(double hashPower, double difficulty) {
    	return((double) getNextMiningIntervalSeconds(hashPower,difficulty)*1000);
    }

    @Override
    public float getNextTransactionValue() {
        return(getGaussian(txValueMean, txValueSD));
    }

    @Override
    public long getNextTransactionSize() {
        return(long) (getGaussian(txSizeMean, txSizeSD));
    }

    @Override
    public float getNextNodeElectricPower() {
        return (getGaussian(nodeElectricPowerMean, nodeElectricPowerSD));
    }

    @Override
    public float getNextNodeHashPower() {
        return (getGaussian(nodeHashPowerMean, nodeHashPowerSD));
    }

    @Override
    public float getNextNodeElectricityCost() {
        return (getGaussian(nodeElectricCostMean, nodeElectricCostSD));
    }

    @Override
    public int getNextArrivalNode(int nNodes) {
        return(random.nextInt(nNodes));
    }

    @Override
    public float getNextConnectionThroughput() {
        return (getGaussian(netThroughputMean, netThroughputSD));
    }

}







