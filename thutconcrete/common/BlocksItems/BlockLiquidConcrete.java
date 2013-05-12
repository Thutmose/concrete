package thutconcrete.common.BlocksItems;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLiquidConcrete extends Block16Fluid {

	public static Block instance;
	static Material wetConcrete = (new Material(MapColor.stoneColor)).setReplaceable();
	static Integer[][] data;
	
	public BlockLiquidConcrete(int par1) {
		super(par1, wetConcrete);
		setUnlocalizedName("concreteLiquid");
		this.setResistance((float) 0.0);
		this.instance = this;
	}
	
	
	/////////////////////////////////////////Block Bounds Stuff//////////////////////////////////////////////////////////
	
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
    	this.setBoundsByMeta(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
		return AxisAlignedBB.getAABBPool().getAABB(0, 0.0, 0, 0, 0.0, 0);
    }
	
   /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    
    
    
	@Override
    public void onBlockPlacedBy(World worldObj,int x,int y,int z,EntityLiving entity, ItemStack item){
		worldObj.setBlockMetadataWithNotify(x, y, z, 15, 3);

		if(data==null){
			setData();
			}
    	this.setTickRandomly(true);
    	super.onBlockPlacedBy(worldObj, x, y, z, entity, item);
    }
	
	@Override
    public void onBlockAdded(World worldObj, int x, int y, int z) {
		if(data==null){
			setData();
			}
		
    	this.setTickRandomly(true);
    }
	
	@Override
	public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity entity) {
		entity.motionX*=0.5;
		entity.motionZ*=0.5;
		if(par1World.getBlockMetadata(par2, par3, par4)>6)
		entity.motionY*=0.5;
	}
	
	@Override
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random){

		if(data==null){
			setData();
			}
		 super.updateTick(worldObj, x, y, z, par5Random);
	}
	
	private void setData(){
		data = new Integer[][]{
				{BlockLiquidConcrete.instance.blockID,0,BlockConcrete.instance.blockID},
				{BlockRebar.instance.blockID,BlockLiquidREConcrete.instance.blockID, BlockREConcrete.instance.blockID},
				{BlockLiquidREConcrete.instance.blockID,0,0,BlockConcrete.instance.blockID},
				{BlockConcrete.instance.blockID,BlockConcrete.instance.blockID,BlockConcrete.instance.blockID,BlockConcrete.instance.blockID,0}
			};
			fluid16Blocks.put(BlockLiquidConcrete.instance.blockID,data);
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:" + this.getUnlocalizedName2());
    }
	
	
	
	 @SideOnly(Side.CLIENT)

	    /**
	     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	     */
	    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	    {
	           return this.blockIcon;
	    }
	

	    @Override
	    public int quantityDropped(int meta, int fortune, Random random)
	    {
	        return 0;
	    }
}
