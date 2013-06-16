package thutconcrete.common.blocks;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import thutconcrete.client.render.BlockRenderHandler;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.corehandlers.ConfigHandler;
import thutconcrete.common.entity.*;
import thutconcrete.common.tileentity.TileEntityLaser;
import thutconcrete.common.utils.ExplosionCustom;
import thutconcrete.common.utils.IRebar;
import thutconcrete.common.utils.IStampableBlock;
import thutconcrete.common.utils.LinearAlgebra;
import thutconcrete.common.utils.ThreadSafeWorldOperations;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemCoal;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockMisc extends Block implements ITileEntityProvider, IStampableBlock, IRebar
{

	public static BlockMisc instance;
	ExplosionCustom boom = new ExplosionCustom();
	ThreadSafeWorldOperations safe = new ThreadSafeWorldOperations();
	
	public BlockMisc(int par1) {
		super(par1, Material.rock);
		
		if(ConfigHandler.debugPrints)
		this.setCreativeTab(ConcreteCore.tabThut);
		this.setResistance(100);
		this.setHardness(100);
		this.setUnlocalizedName("misc");
		instance = this;
	}
	
	
    @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
    	int meta = par1World.getBlockId(par2, par3, par4);
    	if(meta!=0)
    	{
    		return BlockREConcrete.instance.blockID;
    	}
    	
        return this.blockID;
    }
	
	
	
	
	   /**
     * Called when a user uses the creative pick block button on this block
     *
     * @param target The full target the player is looking at
     * @return A ItemStack to add to the player's inventory, Null if nothing should be added.
     */
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z)
    {
        int id = idPicked(world, x, y, z);

        if (id == 0)
        {
            return null;
        }

        Item item = Item.itemsList[id];
        if (item == null)
        {
            return null;
        }
        

        return new ItemStack(id, 1, getDamageValue(world, x, y, z));
        
    }

	 
    public boolean onBlockActivated(World worldObj, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9)
    {
    	ItemStack item = player.getHeldItem();
    	int meta = safe.safeGetMeta(worldObj,x,y,z);
    	
    	if(meta==0)
    	{
	    	if(item!=null&&item.getItem() instanceof ItemDye)
	    	{
		    	int meta1 = (15-item.getItemDamage())+1;
		    	boom.doExplosion(worldObj, x, y, z, Math.min(2*meta1*meta1,512), true);
		    	return true;
	    	}
	    	else if(!worldObj.isRemote&&item!=null&&item.getItem() instanceof ItemCoal)
	    	{
	    		EntityRocket rocket = new EntityRocket(worldObj, x,y+5,z);
	    		worldObj.spawnEntityInWorld(rocket);
	    	}
	    	else if(!worldObj.isRemote&&item!=null&&item.getItem() instanceof ItemSoup)
	    	{
	    		EntityTurret turret = new EntityTurret(worldObj, x+0.5, y+1.0, z+0.5);
	    		worldObj.spawnEntityInWorld(turret);
	    	}
	    	else if(!worldObj.isRemote&&item!=null&&item.getItem() instanceof ItemBook)
	    	{
	    		EntityLift turret = new EntityLift(worldObj, x+0.5, y+1.0, z+0.5,5);
	    		worldObj.spawnEntityInWorld(turret);
	    	}
	    	else if(!worldObj.isRemote&&item!=null&&item.getItem() instanceof ItemHoe)
	    	{
	    		EntitySine turret = new EntitySine(worldObj, x+0.5, y+2.0, z+0.5);
	    		worldObj.spawnEntityInWorld(turret);
	    	}
    	}
    	
        return false;
        
    }
	 
    
    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:" + this.getUnlocalizedName2());
    }


	@Override
	public Icon getSideIcon(IBlockAccess par1IBlockAccess, int x, int y, int z,
			int side) 
	{
		int meta = par1IBlockAccess.getBlockMetadata(x, y, z);
		if(meta==1)
		{
			return BlockREConcrete.instance.getSideIcon(par1IBlockAccess, x, y, z, side);
		}
		else
			return this.blockIcon;
	}


	@Override
	public int sideIconBlockId(IBlockAccess world, int x, int y, int z, int side) 
	{
		int meta = world.getBlockMetadata(x, y, z);
		if(meta==1)
		{
			return BlockREConcrete.instance.sideIconBlockId(world, x, y, z, side);
		}
		else
			return 0;
		
	}


	@Override
	public int sideIconMetadata(IBlockAccess world, int x, int y, int z,
			int side)
	{
		
		int meta = world.getBlockMetadata(x, y, z);
		if(meta==1)
		{
			return BlockREConcrete.instance.sideIconMetadata(world, x, y, z, side);
		}
		else
			return 0;
		
	}


	@Override
	public int sideIconSide(IBlockAccess world, int x, int y, int z, int side) 
	{
		
		int meta = world.getBlockMetadata(x, y, z);
		if(meta==1)
		{
			return BlockREConcrete.instance.sideIconSide(world, x, y, z, side);
		}
		else
			return 0;
		
	}


	@Override
	public boolean setBlockIcon(int id, int meta, int side, World worldObj,
			int x, int y, int z, Icon icon, int iconSide) 
	{
		int metahere = worldObj.getBlockMetadata(x, y, z);
		if(metahere==1)
		{	
			return BlockREConcrete.instance.setBlockIcon(id, meta, side, worldObj, x, y, z, icon, iconSide);
		}
		else
			return false;
		
	}
	
	 @SideOnly(Side.CLIENT)
    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
    {
		int meta = par1IBlockAccess.getBlockMetadata(x, y, z);
		if(meta==1)
		{
			return BlockREConcrete.instance.getSideIcon(par1IBlockAccess, x, y, z, side);
		}
		else
			return this.blockIcon;
    }
	 
	    @SideOnly(Side.CLIENT)

	    /**
	     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
	     */
	    public Icon getIcon(int par1, int par2)
	    {
	        return par2==0?this.blockIcon:BlockREConcrete.instance.getIcon(par1, par2);
	    }

	@Override
	public TileEntity createNewTileEntity(World world) 
	{
		return BlockREConcrete.instance.createNewTileEntity(world);
	}


	@Override
	public boolean[] sides(IBlockAccess worldObj, int x, int y, int z) {
		// TODO Auto-generated method stub
		return BlockREConcrete.instance.sides(worldObj, x, y, z);
	}


	@Override
	public boolean[] sides(World worldObj, int x, int y, int z) {
		// TODO Auto-generated method stub
		return BlockREConcrete.instance.sides(worldObj, x, y, z);
	}


	@Override
	public Icon getIcon(Block block) {
		// TODO Auto-generated method stub
		return BlockREConcrete.instance.getIcon(block);
	}
	
}
