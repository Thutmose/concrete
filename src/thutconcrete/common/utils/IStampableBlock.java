package thutconcrete.common.utils;

import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public interface IStampableBlock 
{
	public Icon getSideIcon(IBlockAccess par1IBlockAccess, int x, int y, int z, int side);

	public int sideIconBlockId(IBlockAccess world, int x, int y, int z, int side);
	public int sideIconMetadata(IBlockAccess world, int x, int y, int z, int side);
	public int sideIconSide(IBlockAccess world, int x, int y, int z, int side);
	
	 public boolean setBlockIcon(int id, int meta, int side, World worldObj, int x, int y, int z, Icon icon, int iconSide);
}
