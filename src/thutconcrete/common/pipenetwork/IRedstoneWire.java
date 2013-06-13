package thutconcrete.common.pipenetwork;

import net.minecraftforge.common.ForgeDirection;

public interface IRedstoneWire 
{
	public void onSignalChange(ForgeDirection side, int newSignal);
}
