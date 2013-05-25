package thutconcrete.common.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.corehandlers.ConfigHandler;
import thutconcrete.common.items.ItemConcreteDust;
import thutconcrete.common.utils.ThreadSafeWorldOperations;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;

public class BlockSolidLava extends Block16Fluid
{
	
	public static BlockSolidLava[] instances = new BlockSolidLava[16];
	ThreadSafeWorldOperations safe = new ThreadSafeWorldOperations();
	public List<Integer> turnto = new ArrayList<Integer>();
	
	public BlockSolidLava instance;
	public int typeid;
	public static int resistance = 10;
	public static float hardness = 5;
	public int totalProb = 0;
	Integer[][] data;
	
	public BlockSolidLava(int par1, int par2) {
		super(par1,Material.rock);
		typeid = par2;
		setUnlocalizedName("solidLava" + typeid);
		this.instance = this;
		this.rate = 1;
		this.instances[typeid] = this;
		this.setTickRandomly(true);
		setData();
	}

	public static BlockSolidLava getInstance(int colorid)
	{
		return BlockSolidLava.instances[colorid];
	}
	
	public void setData()
	{
		if(data==null){
			data = new Integer[][]{
					{
						0,//ID that this returns when meta hits -1, 
						15,//the viscosity factor,
						null,//a secondary ID that this can turn into used for hardening,
						15,//The hardening differential that prevents things staying liquid forever.,
						15,//a randomness coefficient, this is multiplied by a random 0-10 then added to the hardening differential and viscosity.,
						0,//The will fall of edges factor, this is 0 or 1,
						0,//0 = not colourable, 1 = colourable.
					},
					{},
					{BlockSolidLava.getInstance(typeid).blockID+4096*BlockSolidLava.getInstance(typeid).blockID}
			};
			fluid16Blocks.put(BlockSolidLava.getInstance(typeid).blockID,data);
			
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
		this.blockIcon = par1IconRegister.registerIcon("thutconcrete:"+"solidLava" + typeid);
    }
	
	@Override
	public void updateTick(World worldObj, int xCoord, int yCoord, int zCoord, Random par5Random)
	{
		if(safe.safeGetMeta(worldObj,xCoord, yCoord, zCoord)==15)
		{
			{
				for(Integer i : BlockSolidLava.getInstance(typeid).turnto)
				{
					int id = i & 4095;
					int probability = i>>12;
					int meta = i>>22;
					if(Math.random()<((double)probability)/((double)BlockSolidLava.getInstance(typeid).totalProb))
					{
						safe.safeSet(worldObj, xCoord, yCoord, zCoord, id, meta);
					}
				}
			}
		}
	}
	
	public void onBlockClicked(World worldObj, int x, int y, int z, EntityPlayer player){
		this.setResistanceByMeta(worldObj.getBlockMetadata(x, y, z));
	}
	
	protected void setResistanceByMeta(int meta){
		int j = meta & 15;
        float f = (float)((1 + j)) / 16.0F;
        this.setResistance(f*resistance*(1+typeid));
        this.setHardness(f*hardness);
	}
	protected float getBlastResistanceByMeta(int meta){
		int j = meta & 15;
        float f = (float)((1 + j)) / 16.0F;
        return (f*resistance*(1+typeid));
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
	           return this.blockIcon;
	    }
	
	   @Override
	    public int quantityDropped(int meta, int fortune, Random random)
	    {
	        return (meta & 15) + 1;
	    }
	    
}
