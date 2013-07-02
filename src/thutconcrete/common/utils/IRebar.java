package thutconcrete.common.utils;

import net.minecraft.block.Block;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IRebar {
	abstract boolean[] sides(IBlockAccess worldObj, int x, int y, int z);
	
	abstract Icon getIcon(Block block);
}
