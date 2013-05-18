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
import net.minecraftforge.common.ForgeDirection;

public class BlockConcrete extends Block16Fluid
{
	

	public static Block instance;
	public static int resistance = 10;
	public static float hardness = 1;
	Integer[][] data;
    @SideOnly(Side.CLIENT)
    private Icon[] iconArray;
	
	public BlockConcrete(int par1) {
		super(par1,Material.rock);
		setUnlocalizedName("concrete");
		this.rate = 1;
		this.instance = this;
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
	
	@Override
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random)
	{

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
	    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int par5)
	    {
		 TileEntityBlock16Fluid te = (TileEntityBlock16Fluid) par1IBlockAccess.getBlockTileEntity(x, y, z);
		 return this.iconArray[te.metaArray[par5]];
		 	
	    }
	
	 
	    /**
	     * Checks if the block is a solid face on the given side, used by placement logic.
	     *
	     * @param world The current world
	     * @param x X Position
	     * @param y Y position
	     * @param z Z position
	     * @param side The side to check
	     * @return True if the block is solid on the specified side.
	     */
	    public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
	    {
	        int meta = world.getBlockMetadata(x, y, z);
	        switch (side)
	        {
		            case UP:
		            {
		                    return (meta==15);
		            }
		            case DOWN:
		            {
		                    return true;
		            }
		            case NORTH:
		            {
		            	return (meta==15);
		            }
		            case SOUTH:
		            {
		            	return (meta==15);
		            }
		            case EAST:
		            {
		            	return (meta==15);
		            }
		            case WEST:
		            {
		            	return (meta==15);
		            }
		            default:
		            {
		            	return (meta==15);
		            }
         }
	    }
	 
	 
	
}
