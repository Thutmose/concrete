package thutconcrete.api.utils;

import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public interface IStampableTE {
	
	/**
	 * Returns the Icon which corresponds to the given side.
	 * @param side
	 * @return
	 */

	public abstract Icon getIcon(ForgeDirection side);
	
	/**
	 * @param side - The side of this block that is changed.
	 * @param meta - The metadata of the block that the icon came from
	 * @param id - the blockId of the block that the icon came from
	 * @param icon - the icon of the block that the icon came from
	 * @param iconSide - the side of the block that the icon came from
	 */
	
	public abstract void setIcon(int side,int meta,int id, Icon icon, int iconSide);
	
	
	/**
	 *  This needs to set the icon array to the value when read from NBT
	 */
	
	public abstract void setIconArray();

	public abstract int[] getMetaArray();
	public abstract int[] getIdArray();
	public abstract int[] getSideArray();
	
	public abstract void setArrays(int[] meta, int[] id, int[] side);
	
}
