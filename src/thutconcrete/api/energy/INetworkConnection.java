package thutconcrete.api.energy;

import net.minecraftforge.common.ForgeDirection;

public interface INetworkConnection 
{
	public boolean canConnect(ForgeDirection side);
	
	public double getResistance();

	public double getMaxVoltage();
}
