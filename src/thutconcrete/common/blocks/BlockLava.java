package thutconcrete.common.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import thutconcrete.common.ConcreteCore;

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

public class BlockLava extends Block16Fluid {

	public static Block[] instances = new BlockLava[16];
	public Block instance;
	public int typeid;
	static Material wetConcrete = (new Material(MapColor.stoneColor));
	Integer[][] data;
	
	public BlockLava(int par1, int par2) {
		super(par1, Material.lava);
		typeid = par2;
		this.setLightValue(1);
		setUnlocalizedName("Lava" + typeid);
		this.setResistance((float) 0.0);
		this.instance = this;
		this.instances[typeid] = this;
	}
	

	public static Block getInstance(int colorid)
	{
		return BlockLava.instances[colorid];
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
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random){

		if(data==null){
			setData();
		}
		 super.updateTick(worldObj, x, y, z, par5Random);
	}
	
	private void setData(){
		
		List<Integer> combinationList = new ArrayList<Integer>();
		List<Integer> desiccantList = new ArrayList<Integer>();
		
		combinationList.add(4096*BlockLava.getInstance(typeid).blockID);
		combinationList.add(Block.waterMoving.blockID+4096*BlockLava.getInstance(typeid).blockID);
		combinationList.add(Block.waterStill.blockID+4096*BlockLava.getInstance(typeid).blockID);
		
		for(int i = 0;i<3;i++){
			combinationList.add(BlockLava.getInstance(i).blockID+4096*BlockLava.getInstance(typeid).blockID);
		}
		
		combinationList.add(BlockSolidLava.getInstance(0).blockID+4096*BlockLava.getInstance(typeid).blockID);
		
		desiccantList.add(0+4096);
		desiccantList.add(Block.waterMoving.blockID+50*4096);
		desiccantList.add(Block.waterStill.blockID+40*4096);
		
		for(int i=0;i<1;i++){
			desiccantList.add(BlockSolidLava.getInstance(0).blockID+5*4096);//TODO add the rock types here
		}
		
		data = new Integer[][]{
				{	
					0,
					typeid*2,
					BlockSolidLava.getInstance(0).blockID,
					1*2,
					10,
				},
				desiccantList.toArray(new Integer[0]),
				combinationList.toArray(new Integer[0]),
			};
			fluid16Blocks.put(BlockLava.getInstance(typeid).blockID,data);
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:lava");
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
