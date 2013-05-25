package thutconcrete.common.blocks;

import thutconcrete.common.tileEntities.TileEntityCanvas;
import thutconcrete.common.utils.ThreadSafeWorldOperations;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockCanvas extends Block implements ITileEntityProvider
{

	public ThreadSafeWorldOperations safe = new ThreadSafeWorldOperations();
	
	public BlockCanvas(int par1) 
	{
		super(par1, Material.cloth);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityCanvas();
	}

	public boolean onBlockActivated(World worldObj, int x, int y, int z, EntityPlayer player, int side, float X, float Y, float Z)
    {
    	ItemStack item = player.getHeldItem();
    	TileEntityCanvas te = (TileEntityCanvas)safe.safeGetTE(worldObj, x, y, z);
    	int meta = safe.safeGetMeta(worldObj,x,y,z);
    	if(item!=null&&item.getItem() instanceof ItemDye)
    	{
	    	
    	}
        return false;
        
    }
	
	
	
	
}
