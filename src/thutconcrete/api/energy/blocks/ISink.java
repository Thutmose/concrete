package thutconcrete.api.energy.blocks;

import thutconcrete.api.energy.EnergyPack;
import thutconcrete.api.energy.INetworkConnection;

public interface ISink extends INetworkConnection
{
	public double getMinVoltage();
	
	public void onEnergyRecieved(EnergyPack energy);
}
