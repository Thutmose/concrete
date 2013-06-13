package thutconcrete.common.utils;

import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public interface IStampableTE {

	public abstract Icon getIcon(ForgeDirection side);
	public abstract void setIcon(int side,int meta,int id, Icon icon, int iconSide);
	public abstract void setIconArray();
	
}
