package esg.dag.sim;

public interface NodeInterface {

    public void setHashPower(float hashpower);

    public float getHashpower();

    public void setElectricPower(float electricPower);

    public float getElectricPower();

    public void setElectricityCost(float electricityCost);
    
    public float getElectricityCost();

    public void setNetwork(Network network);
    
    public Network getNetwork();
}
