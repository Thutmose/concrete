package thutconcrete.common.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.corehandlers.TSaveHandler;
import thutconcrete.common.utils.ISaveable;
import thutconcrete.common.utils.ThreadSafeWorldOperations;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.World;
import net.minecraft.item.*;

public class BlockLiquidConcrete extends Block16Fluid implements ISaveable {

	public static Block instance;
	static Material wetConcrete = (new Material(MapColor.stoneColor));
	Integer[][] data;
    @SideOnly(Side.CLIENT)
    private Icon[] iconArray;
    public static ConcurrentHashMap<String, Byte> metaData = new ConcurrentHashMap<String, Byte>();
	public BlockLiquidConcrete(int par1) {
		super(par1, wetConcrete);
		setUnlocalizedName("concreteLiquid");
		this.setResistance((float) 0.0);
		this.instance = this;
		ConcreteCore.instance.saveList.addSavedData(this);

		superMetaData.put(par1, metaData);
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
    public int tickRate(World par1World)
    {
        return 10;
    }
    /**
     * Called upon block activation (right click on the block.)
     */
    /*
    public boolean onBlockActivated(World worldObj, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9)
    {
		System.out.println("Paint Attmept");
		int colour = (this.getMetaData(worldObj, x, y, z)+1)%16;
		this.setColourMetaData(worldObj, x, y, z, (byte) colour);
		worldObj.markBlockForRenderUpdate(x, y, z);
        return true;
    }
    //*/
    
	@Override
    public void onBlockPlacedBy(World worldObj,int x,int y,int z,EntityLiving entity, ItemStack item){
		worldObj.setBlockMetadataWithNotify(x, y, z, 15, 3);
		//TODO add set colour based on item
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
		combinationList.add(BlockRebar.instance.blockID+4096*BlockLiquidREConcrete.instance.blockID);
		//Air to make this colour.
		combinationList.add(4096*BlockLiquidConcrete.instance.blockID);

		//Normal Concrete to make this colour
		
		combinationList.add(BlockLiquidConcrete.instance.blockID + 4096*BlockLiquidConcrete.instance.blockID);
		
		
		combinationList.add(BlockConcrete.instance.blockID+4096*BlockLiquidConcrete.instance.blockID);
		//RE Concrete to make this colour
		combinationList.add(BlockLiquidREConcrete.instance.blockID+4096*BlockLiquidREConcrete.instance.blockID);
		combinationList.add(BlockREConcrete.instance.blockID+4096*BlockLiquidREConcrete.instance.blockID);

		for(int i = 0;i<3;i++){
	//		combinationList.add(BlockLiquidConcrete.instance.blockID+4096*BlockLava.getInstance(i).blockID);
		}


		desiccantList.add(0+4096);
		desiccantList.add(BlockFullSolidREConcrete.instance.blockID+4096*100);

		desiccantList.add(BlockREConcrete.instance.blockID+4096*4);
		
		desiccantList.add(BlockConcrete.instance.blockID+4096*4);

		data = new Integer[][]{
				{	
					0,
					0,
					BlockConcrete.instance.blockID,
					1,
					0,
					1,
					1,
				},
				desiccantList.toArray(new Integer[0]),
				combinationList.toArray(new Integer[0]),
			};
			fluid16Blocks.put(BlockLiquidConcrete.instance.blockID,data);

	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.iconArray = new Icon[16];
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:" + "wetConcrete_"+8);
        for (int i = 0; i < this.iconArray.length; ++i)
        {
            this.iconArray[i] = par1IconRegister.registerIcon("thutconcrete:" + "wetConcrete_"+i);
        }
    }
	
	
	
	 @SideOnly(Side.CLIENT)

	    /**
	     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	     */
	    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	    {
		 	if(superMetaData.get(this.blockID).containsKey(coordsToString(par2,par3,par4)))
	           return this.iconArray[superMetaData.get(this.blockID).get(coordsToString(par2,par3,par4))%16];//TODO find why this is not always the case.
		 	else
		 	{
		 		superMetaData.get(this.blockID).put(coordsToString(par2,par3,par4),(byte) 8);
		 		return this.iconArray[superMetaData.get(this.blockID).get(coordsToString(par2,par3,par4))];
		 	}
	    }
	
	

	    @Override
	    public int quantityDropped(int meta, int fortune, Random random)
	    {
	        return 0;
	    }

		@Override
		public void save(NBTTagCompound par1nbtTagCompound) 
		{
			if(superMetaData.get(this.blockID).size()>0)
			{
				TSaveHandler.saveSBHashMap(par1nbtTagCompound, superMetaData.get(this.blockID));
			}
		}


		@Override
		public void load(NBTTagCompound par1nbtTagCompound) 
		{
			metaData = TSaveHandler.readSBHashMap(par1nbtTagCompound);
			superMetaData.replace(this.blockID, metaData);
		}

		
		@Override
		public String getName() 
		{
			return "BlockLiquidConcrete";
		}
}
