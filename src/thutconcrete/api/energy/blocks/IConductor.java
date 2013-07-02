package thutconcrete.api.energy.blocks;

import thutconcrete.api.energy.INetworkConnection;

public interface IConductor extends INetworkConnection
{
	public double getMaxPowerDissipation();
	
	public void onOverVoltage();
	
	public void onOverHeat();
}
