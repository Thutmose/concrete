package thutconcrete.common.utils;

import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public interface IMultiPaintableTE {

	public Icon[][] iconArrayArray = new Icon[6][256];
	public int[][] metaArrayArray = new int[6][256];
	
	public abstract Icon[] getIcon(ForgeDirection side);
	
	public abstract void setColour(ForgeDirection side, int colour);
	
	public abstract void setColour(int subu, int subv,  ForgeDirection side, int colour);
}
