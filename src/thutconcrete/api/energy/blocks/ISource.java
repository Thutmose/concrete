package thutconcrete.api.energy.blocks;

import thutconcrete.api.energy.EnergyPack;
import thutconcrete.api.energy.INetworkConnection;

public interface ISource extends INetworkConnection
{
	public double getMaxOutputVoltage();
	
	public void onEnergySent(EnergyPack energy);
}
