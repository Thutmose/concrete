package thutconcrete.common.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.utils.ISaveable;

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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;

public class BlockLiquidConcrete extends Block16Fluid implements ISaveable {

	public static Block[] instances = new BlockLiquidConcrete[16];
	public Block instance;
	public int colourid;
	static Material wetConcrete = (new Material(MapColor.stoneColor));
	Integer[][] data;
	
	public BlockLiquidConcrete(int par1, int par2) {
		super(par1, wetConcrete);
		colourid = par2;
		setUnlocalizedName("concreteLiquid" + colourid);
		this.setResistance((float) 0.0);
		this.instance = this;
		this.instances[colourid] = this;
		ConcreteCore.instance.saveList.addSavedData(this);
	}
	

	public static Block getInstance(int colorid)
	{
		return BlockLiquidConcrete.instances[colorid];
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
		
		List<Integer> combinationList = new ArrayList<Integer>();
		List<Integer> desiccantList = new ArrayList<Integer>();
		
		//Rebar to make this colour.
		combinationList.add(BlockRebar.instance.blockID+4096*BlockLiquidREConcrete.getInstance(colourid).blockID);
		//Air to make this colour.
		combinationList.add(4096*BlockLiquidConcrete.getInstance(colourid).blockID);
		
		//Normal Concrete to make this colour
		combinationList.add(BlockLiquidConcrete.getInstance(colourid).blockID+4096*BlockLiquidConcrete.getInstance(colourid).blockID);
		combinationList.add(BlockConcrete.getInstance(colourid).blockID+4096*BlockLiquidConcrete.getInstance(colourid).blockID);
		//RE Concrete to make this colour
		combinationList.add(BlockLiquidREConcrete.getInstance(colourid).blockID+4096*BlockLiquidREConcrete.getInstance(colourid).blockID);
		combinationList.add(BlockREConcrete.getInstance(colourid).blockID+4096*BlockLiquidREConcrete.getInstance(colourid).blockID);
		
		
		for(int i = 0;i<16;i++){
			int key = Math.max(colourid, i)*16+Math.min(colourid, i);
			int colour = i;
			if(ConcreteCore.colourMap.containsKey(key)){
				colour = ConcreteCore.colourMap.get(key);
				//Normal Concrete Mix
				combinationList.add(BlockLiquidConcrete.getInstance(i).blockID+4096*BlockLiquidConcrete.getInstance(colour).blockID);
				combinationList.add(BlockConcrete.getInstance(i).blockID+4096*BlockLiquidConcrete.getInstance(colour).blockID);
				//RE Concrete Mix
				combinationList.add(BlockLiquidREConcrete.getInstance(i).blockID+4096*BlockLiquidREConcrete.getInstance(colour).blockID);
				combinationList.add(BlockREConcrete.getInstance(i).blockID+4096*BlockLiquidREConcrete.getInstance(colour).blockID);
			}else{
				//Normal Concrete Mix
				combinationList.add(BlockLiquidConcrete.getInstance(colour).blockID+4096*BlockLiquidConcrete.getInstance(colour).blockID);
				combinationList.add(BlockConcrete.getInstance(colour).blockID+4096*BlockLiquidConcrete.getInstance(colour).blockID);
				//RE Concrete Mix
				combinationList.add(BlockLiquidREConcrete.getInstance(colour).blockID+4096*BlockLiquidREConcrete.getInstance(colour).blockID);
				combinationList.add(BlockREConcrete.getInstance(colour).blockID+4096*BlockLiquidREConcrete.getInstance(colour).blockID);
			}
		}
		
		for(int i = 0;i<3;i++){
			combinationList.add(BlockLiquidConcrete.getInstance(colourid).blockID+4096*BlockLava.getInstance(i).blockID);
		}
		

		desiccantList.add(0+4096);
		desiccantList.add(BlockFullSolidREConcrete.instance.blockID+4096*100);
		
		for(int i=0;i<16;i++){
			desiccantList.add(BlockREConcrete.getInstance(i).blockID+4096*4);
			desiccantList.add(BlockConcrete.getInstance(i).blockID+4096*4);
		}
		
		data = new Integer[][]{
				{	
					0,
					0,
					BlockConcrete.getInstance(colourid).blockID,
					1,
					0,
					1,
				},
				desiccantList.toArray(new Integer[0]),
				combinationList.toArray(new Integer[0]),
			};
			fluid16Blocks.put(BlockLiquidConcrete.getInstance(colourid).blockID,data);
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:wetConcrete_"+colourid);
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


		@Override
		public void save(NBTTagCompound par1nbtTagCompound) {
			
		}


		@Override
		public void load(NBTTagCompound par1nbtTagCompound) {
			
		}

		
		@Override
		public String getName() {
			return "LiquidConcrete";
		}
}
