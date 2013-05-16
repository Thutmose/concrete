package thutconcrete.common.blocks;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.corehandlers.TSaveHandler;
import thutconcrete.common.utils.ISaveable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockConcrete extends Block16Fluid implements ISaveable{
	

	public static Block instance;
	public static int resistance = 10;
	public static float hardness = 1;
	public static ConcurrentHashMap<String, Byte> metaData = new ConcurrentHashMap<String, Byte>();
	Integer[][] data;
    @SideOnly(Side.CLIENT)
    private Icon[] iconArray;
	
	public BlockConcrete(int par1) {
		super(par1,Material.rock);
		setUnlocalizedName("concrete");
		this.instance = this;
		ConcreteCore.instance.saveList.addSavedData(this);

		superMetaData.put(par1, metaData);
	}

	
	@Override
    public void onBlockAdded(World worldObj, int x, int y, int z) {
		if(data==null){
			data = new Integer[][]{
					{0,15,null,null,0,0,1},
					{},
					{BlockConcrete.instance.blockID+4096*BlockConcrete.instance.blockID}
			};
			fluid16Blocks.put(BlockConcrete.instance.blockID,data);
			
			}
    }
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
    	this.setBoundsByMeta(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    	this.setResistanceByMeta(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }
	
	@Override
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
        return getBlastResistanceByMeta(world.getBlockMetadata(x, y, z));
    }
	
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
    	int meta = par1World.getBlockMetadata(par2, par3, par4);
    	
        int l = par1World.getBlockMetadata(par2, par3, par4) & 15;
        float f = 0.0625F;
        return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ,
        								(double)par2 + this.maxX, (double)((float)par3 + (float)l * f), (double)par4 + this.maxZ);
    }
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.iconArray = new Icon[16];

        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:" + "dryConcrete_"+8);
        for (int i = 0; i < this.iconArray.length; ++i)
        {
            this.iconArray[i] = par1IconRegister.registerIcon("thutconcrete:" + "dryConcrete_"+i);
        }
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
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random){

		 if(worldObj.getTotalWorldTime()%10000==1)
			 cleanUp(worldObj, this.blockID);
	}
	
	public void onBlockClicked(World worldObj, int x, int y, int z, EntityPlayer player){
		this.setResistanceByMeta(worldObj.getBlockMetadata(x, y, z));
	}
	
	protected void setResistanceByMeta(int meta){
		int j = meta & 15;
        float f = (float)((1 + j)) / 16.0F;
        this.setResistance(f*resistance);
        this.setHardness(f*hardness);
	}
	protected float getBlastResistanceByMeta(int meta){
		int j = meta & 15;
        float f = (float)((1 + j)) / 16.0F;
        return (f*resistance);
	}
	protected float getHardnessByMeta(int meta){
		int j = meta & 15;
        float f = (float)((1 + j)) / 16.0F;
        return (f*hardness);
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
		public void save(NBTTagCompound par1nbtTagCompound) {
			if(superMetaData.get(this.blockID).size()>0)
			{
				TSaveHandler.saveSBHashMap(par1nbtTagCompound, superMetaData.get(this.blockID));
			}
		}


		@Override
		public void load(NBTTagCompound par1nbtTagCompound) {
			metaData = TSaveHandler.readSBHashMap(par1nbtTagCompound);
			superMetaData.replace(this.blockID, metaData);
		}

		
		@Override
		public String getName() {
			return "BlockConcrete";
		}
	
}
