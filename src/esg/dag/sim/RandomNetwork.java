package esg.dag.sim;

public class RandomNetwork extends Network{

    public RandomNetwork(NodeSet ns, Sampler s){
        Net = new float [Parameters.MAXNODES][Parameters.MAXNODES];
        this.sampler = s;
        CreateNetwork();
    }


private void CreateNetwork(){
    for (int i=1; i <= Parameters.NumofNodes; i++) {
        for (int j=1; j <= Parameters.NumofNodes; j++) {
            if(i!=j && Net[i][j] == 0)
            {
                Net[i][j] = (float) sampler.getNextConnectionThroughput(); //network throughput refers to how much data can be transferred from source to destination within a given time frame
                Net[j][i] = Net[i][j];
            }
        }
    }
}

    @Override
     public float getPropagationTime(int Origin, int Destination, float Size){
    	if(Origin < 0)
    		throw new ArithmeticException("Origin < 0");
    	if(Destination < 0)
    		throw new ArithmeticException("Destination < 0");
    	if(Size < 0)
    		throw new ArithmeticException("Size < 0");
        // Multiply by 8 because Size is in terms of bytes but throughput is in terms of bits.
    	// Multiply by 1000 because throughput is measured in bits/second but expected output is in terms of milliseconds.
        if(Net[Origin][Destination] == 0)
            return Float.POSITIVE_INFINITY;
        else
        return((Size * 8 * 1000)/Net[Origin][Destination]);
    }

    @Override
    public float getThroughput(int Origin, int Destination)
    {
    	if(Origin < 0)
    		throw new ArithmeticException("Origin < 0");
    	if(Destination < 0)
    		throw new ArithmeticException("Destination < 0");
    	return Net[Origin][Destination];
    }
    
    @Override
    public void setSampler(Sampler s) {
        sampler = s;
    }

    @Override
    public Sampler getSampler() {
        return (sampler);
    }
         
    
}
