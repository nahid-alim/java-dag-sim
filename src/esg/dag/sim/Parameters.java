package esg.dag.sim;

public class Parameters {
    public static final int MAXNODES = 20000;
//-----------------------------------------------------------------------
    public static final double MWM = 1e5; //Minumum Weight Magnitude - difficulty of the tangle in trials/sec

    public static final int Lambda = 5; //tx arrival rate in tx/s

    public static  final double ALPHA = 0.001; //α > 0 is a parameter to be chosen - randomness factor for weighted random walk

    public static  final int NumofNodes = 10;

    public static  final int NumofHonestTxs = 50;

    public static final int NetThroughputMean = 10000000; //high load (bits/sec) - bandwidth
//    public static final int NetThroughputMean = 250000000; //low load

    public static final int NetThroughputSD = 1000;

    public static final int Mod = ((NumofHonestTxs * NumofNodes) + NumofHonestTxs) / 100; // for report

    public static final int CalcConfRounds = 100; //number of rounds to calculate a tx confidence rate

    public static  final int K = 2; //number of tips to be approved by a new issued tx

    public static  final int h = 1000; //default time in millisecond for the PoW in the tangle

    public static final float HashPowerMean = (float) 12e9;

    public static final float HashPowerSD = (float) 1e9;

    public static final int NumofFounders = 10; //number of founders after genesis in the tangle

    public static final int NumofParticles = 10; //number of particles in MCMC algorithm

    public static  final float beginofCutset = (float) 0;

    public static  final float endofCutset = (float) 0.5;

    public static final int NumofSim = 30;

    public static final int TxID = 500; //for reporting cumulative weight of some of the transactions

//    -------------------------------------------------------------------
//Malicious part
    public static final int VictimTxIndex = 29;

    public static final int NumberofFakeTxs = 8000; //number of fake txs in the parasite chain

    public static final double MaliciousHPPercentage = 0.8; //percentage of malicious hash power in the network

    public static final double ConfThreshold = 0.6;
//    -------------------------------------------------------------------
}




