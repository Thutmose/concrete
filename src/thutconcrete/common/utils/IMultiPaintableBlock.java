package thutconcrete.common.utils;

import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public interface IMultiPaintableBlock {

	public abstract Icon[] getIcon(IBlockAccess world, int x, int y, int z, ForgeDirection side);
	
}
