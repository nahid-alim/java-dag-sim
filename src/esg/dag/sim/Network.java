package esg.dag.sim;

import java.io.Serializable;

public abstract class Network implements Serializable {

    protected Sampler sampler;

    protected float[][] Net;

    public abstract void setSampler(Sampler s);

    public abstract Sampler getSampler();

    public abstract float getPropagationTime(int Origin, int Destination, float Size);

    public abstract float getThroughput(int Origin, int Destination);

}
