package thutconcrete.common.blocks;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import appeng.api.me.tiles.IGridTileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import thutconcrete.api.utils.IStampableBlock;
import thutconcrete.common.blocks.*;
import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.tileentity.TileEntityBlock16Fluid;
import thutconcrete.common.tileentity.TileEntityLiftAccess;
import thutconcrete.common.utils.IRebar;

public class BlockLiftRail extends BlockRebar implements ITileEntityProvider
{

	public Icon[] iconArray;
	
	public static BlockLiftRail staticBlock;

	public BlockLiftRail(int par1) 
	{
		super(par1);
		staticBlock = this;
		this.setUnlocalizedName("liftRail");
	//	this.setTickRandomly(true);
	}

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9)
    {
    	boolean placed = false;
    	ItemStack item = player.getHeldItem();
    	
    	world.scheduleBlockUpdate(x, y, z, blockID, 5);
    	if(item!=null)
    	{
	    	int itemID = item.itemID;
	    	if(side==0||side==1&&itemID<4095)
	    	{
		    	if(Block.blocksList[itemID] instanceof IRebar)
		    	{
			    	if(placeBlock(world, x, y, z, itemID, item.getItemDamage(), ForgeDirection.getOrientation(side)))
			    	{
			    		placed = true;
		    				if(!player.capabilities.isCreativeMode)
		    					player.inventory.consumeInventoryItem(item.itemID);
			    	}
		    	}
	    	}
    	}
        return placed;
    }
    
    /**
     * Called when the block is clicked by a player. Args: x, y, z, entityPlayer
     */
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) 
    {
    	boolean placed = false;
    	ItemStack item = player.getHeldItem();

    	
    	if(item!=null)
    	{
	    	int itemID = item.itemID;
	    	if(itemID<4095)
	    	{
		    	if(Block.blocksList[itemID] instanceof IRebar)
		    	{
		    		
		        	
		        	if(player.isSneaking())
		        	{
		        		boolean done = false;
		        		int num = item.stackSize;
		        		
		        		while(!done&&num>0)
		        		{
				    		placed = placeBlock(world, x, y, z, itemID, item.getItemDamage(), ForgeDirection.UP);
				    		done = !placed;
				    		if(placed)
				    		{
				    			num--;
				    		}
		    				if(!player.capabilities.isCreativeMode)
		    				{
		    					//player.addChatMessage("split");
		    					player.inventory.consumeInventoryItem(item.itemID);
		    				}
		        		}
		        	}
		        	else
			    	if(placeBlock(world, x, y, z, itemID, item.getItemDamage(), ForgeDirection.UP))
			    	{
			    		placed = true;
		    				if(!player.capabilities.isCreativeMode)
		    				{
		    					//player.addChatMessage("split");
		    					player.inventory.consumeInventoryItem(item.itemID);
		    				}
			    	}
		    	}
	    	}
	    	
    	}
    }
	
	@Override
	public TileEntity createNewTileEntity(World world) 
	{
		return new TileEntityLiftAccess();
	}
	
	//////////////////////////////////////////////////////RedStone stuff/////////////////////////////////////////////////
    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {

		TileEntityLiftAccess controller = (TileEntityLiftAccess)par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
		if(controller!=null)
		{
	//		System.out.println(controller.called);
			return controller.called?15:0;
		}
    
        return 0;
    }
    
    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return isProvidingWeakPower(par1IBlockAccess, par2, par3, par4, par5);
    }
	
	
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon("thutconcrete:liftRails");
	}

	
	/*/
	public boolean[] sides(IBlockAccess worldObj, int x, int y, int z) 
	{
		boolean[] side = new boolean[]{false, false, false, false, false, false};
    	int[][]sides = {{0,1,0},{0,-1,0}};
		for(int i = 0; i<2; i++)
		{
			int id = worldObj.getBlockId(x+sides[i][0], y+sides[i][1], z+sides[i][2]);
			Block block = Block.blocksList[id];
			side[4+i] = (block instanceof IRebar)||(block instanceof IGridTileEntity)||(block!=null&&block.isOpaqueCube());
		}
		
		return side;
	}
	//*/
	public boolean[] sides(IBlockAccess worldObj, int x, int y, int z) 
	{
		boolean[] side = new boolean[]{false, false, false, false, false, false};
    	int[][]sides = {{1,0,0},{-1,0,0},{0,0,1},{0,0,-1},{0,1,0},{0,-1,0}};
		for(int i = 0; i<6; i++)
		{
			int id = worldObj.getBlockId(x+sides[i][0], y+sides[i][1], z+sides[i][2]);
			Block block = Block.blocksList[id];
			TileEntity te = worldObj.getBlockTileEntity(x+sides[i][0], y+sides[i][1], z+sides[i][2]);
			if(i>3)
			{
				side[i] = (block instanceof IRebar)||(te instanceof IGridTileEntity);
			}
			else
			{
				side[i] = (te instanceof IGridTileEntity);
			}
		}
	//	System.out.println(Arrays.toString(side));
		return side;
	}
	
	@Override 
	public String getItemIconName(){
		return "thutconcrete:" + "liftRail";
	}
}
