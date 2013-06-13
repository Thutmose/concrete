package thutconcrete.common.blocks;

import java.util.List;
import java.util.Random;

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
import thutconcrete.common.blocks.*;
import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.tileentity.TileEntityBlock16Fluid;
import thutconcrete.common.tileentity.TileEntityLiftRail;
import thutconcrete.common.utils.IRebar;
import thutconcrete.common.utils.IStampableBlock;

public class BlockLiftRail extends BlockRebar implements ITileEntityProvider
{

	public Icon[] iconArray;
	
	public static BlockLiftRail instance;

	public BlockLiftRail(int par1) 
	{
		super(par1);
		instance = this;
		this.setUnlocalizedName("liftRail");
	//	this.setTickRandomly(true);
	}

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9)
    {
    	boolean placed = false;
    	ItemStack item = player.getHeldItem();
    	
    	int meta = world.getBlockMetadata(x, y, z);
    	
    	world.setBlockMetadataWithNotify(x, y, z, meta==0?1:0, 3);
    	
    	world.scheduleBlockUpdate(x, y, z, blockID, 5);
    	if(item!=null)
    	{
	    	int itemID = item.itemID;
	    	if(itemID<4095)
	    	{
		    	if(Block.blocksList[itemID] instanceof IRebar)
		    	{
			    	if(placeBlock(world, x, y, z, itemID, item.getItemDamage(), ForgeDirection.getOrientation(side)))
			    	{
			    		placed = true;
		    				if(!player.capabilities.isCreativeMode)
		    					item.splitStack(1);
			    	}
		    	}
	    	}
	    	
    	}
        return placed;
    }
	
	@Override
	public TileEntity createNewTileEntity(World world) 
	{
		return new TileEntityLiftRail();
	}
	
	@Override
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random)
	{
		List<Entity> lifts = worldObj.getEntitiesWithinAABB(EntityLift.class, AxisAlignedBB.getBoundingBox(x-1,y-0.5, z-1, x+1, y+0.5, z+1));
		if(lifts == null || lifts.isEmpty())
		{
	//		System.out.println("empty");
		}
		else
		{
	//		System.out.println("has lift");
		}
    }
	
	public int tickRate(World worldObj)
	{
		return 10;
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
    	
    	TileEntityLiftRail rail = (TileEntityLiftRail)par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
    	int meta = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
        return meta>0 ? 15 : 0;
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
		this.blockIcon = par1IconRegister.registerIcon("thutconcrete:dryConcrete_8");
		this.theIcon = par1IconRegister.registerIcon("thutconcrete:" + "rebarRusty");
		this.iconArray = new Icon[16];
    	for (int i = 0; i < this.iconArray.length; ++i)
        {
            this.iconArray[i] = par1IconRegister.registerIcon("thutconcrete:" + "dryConcrete_"+i);
        }
	}

	public boolean[] sides(IBlockAccess worldObj, int x, int y, int z) 
	{
		boolean[] side = new boolean[]{false, false, false, false, false, false};
    	int[][]sides = {{0,1,0},{0,-1,0}};
		for(int i = 0; i<2; i++)
		{
			int id = worldObj.getBlockId(x+sides[i][0], y+sides[i][1], z+sides[i][2]);
			Block block = Block.blocksList[id];
			side[4+i] = (block instanceof IRebar);
		}
		
		return side;
	}
}
