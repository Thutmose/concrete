package thutconcrete.common.blocks;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import thutconcrete.client.render.BlockRenderHandler;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.corehandlers.TSaveHandler;
import thutconcrete.common.items.ItemConcreteDust;
import thutconcrete.common.tileentity.TileEntityBlock16Fluid;
import thutconcrete.common.utils.ISaveable;
import thutconcrete.common.utils.IStampableBlock;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockConcrete extends Block16Fluid implements ITileEntityProvider, IStampableBlock
{
	

	public static Block instance;
	public static int resistance = 10;
	public static float hardness = 30;
	Integer[][] data;
	
	public BlockConcrete(int par1) {
		super(par1,Material.rock);
		setUnlocalizedName("concrete");
		this.rate = 10;
		this.instance = this;
		this.setStepSound(soundStoneFootstep);
		setCreativeTab(ConcreteCore.tabThut);
		this.solid = true;
		this.stampable = true;
		setData();
	}
	
	public void setData(){
		if(data==null)
		{
			data = new Integer[][]
				{
					{
						0,//ID that this returns when meta hits -1, 
						15,//the viscosity factor,
						null,//a secondary ID that this can turn into used for hardening,
						15,//The hardening differential that prevents things staying liquid forever.,
						15,//a randomness coefficient, this is multiplied by a random 0-10 then added to the hardening differential and viscosity.,
						0,//The will fall of edges factor, this is 0 or 1,
						1,//0 = not colourable, 1 = colourable.
					},
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
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random){}
	
	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
    	int meta = par1World.getBlockMetadata(par2, par3, par4);
    	
        int l = 15-par1World.getBlockMetadata(par2, par3, par4);
        float f = 0.0625F;
        return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ,
        								(double)par2 + this.maxX, (double)((float)par3 + (float)l * f), (double)par4 + this.maxZ);
    }
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.iconArray = new Icon[16];
        //super.registerIcons(par1IconRegister);
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:" + "dryConcrete_"+8);
        for (int i = 0; i < this.iconArray.length; ++i)
        {
            this.iconArray[i] = par1IconRegister.registerIcon("thutconcrete:" + "dryConcrete_"+i);
        }
    }
	
	public void onBlockClicked(World worldObj, int x, int y, int z, EntityPlayer player){
		this.setResistanceByMeta(worldObj.getBlockMetadata(x, y, z));
	}
	
	protected void setResistanceByMeta(int meta){
		int j = 15-meta;
        float f = (float)((1 + j)) / 16.0F;
        this.setResistance(f*resistance);
        this.setHardness(f*hardness);
	}
	protected float getBlastResistanceByMeta(int meta){
		int j = 15-meta;
        float f = (float)((1 + j)) / 16.0F;
        return (f*resistance);
	}
	protected float getHardnessByMeta(int meta){
		int j = 15-meta;
        float f = (float)((1 + j)) / 16.0F;
        return (f*hardness);
	}
	
	
	 @SideOnly(Side.CLIENT)
	    /**
	     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	     */
	    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
	    {
		 	return getSideIcon(par1IBlockAccess, x, y, z, side);
	    }
	
	    /**
	     * Returns the ID of the items to drop on destruction.
	     */
	    public int idDropped(int par1, Random par2Random, int par3)
	    {
	        return ItemConcreteDust.instance.itemID;
	    }
	    
	    public int quantityDropped(int meta, int fortune, Random random)
	    {
	        return (int) ((16-meta)*random.nextDouble());
	    }

		 public TileEntity createNewTileEntity(World world)
		 {
		    return new TileEntityBlock16Fluid();
		 }

		@Override
		public Icon getSideIcon(IBlockAccess par1IBlockAccess, int x, int y,
				int z, int side) {
			 TileEntityBlock16Fluid te = (TileEntityBlock16Fluid) par1IBlockAccess.getBlockTileEntity(x, y, z);
			 if(te.icons[side]==null)
			 {
				 te.icons[side]=this.iconArray[te.metaArray[side]&15];
			 }
			 return te.icons[side];
		}

		@Override
		public int sideIconBlockId(IBlockAccess world, int x, int y, int z,
				int side) {
			TileEntityBlock16Fluid te = (TileEntityBlock16Fluid) world.getBlockTileEntity(x, y, z);
			
			return te.iconIDs[side];
		}

		@Override
		public int sideIconMetadata(IBlockAccess world, int x, int y, int z,
				int side) {
			TileEntityBlock16Fluid te = (TileEntityBlock16Fluid) world.getBlockTileEntity(x, y, z);
			
			return te.metaArray[side];
		}

		@Override
		public int sideIconSide(IBlockAccess world, int x, int y, int z,
				int side) {
			TileEntityBlock16Fluid te = (TileEntityBlock16Fluid) world.getBlockTileEntity(x, y, z);
			
			return te.sideArray[side];
		}
}
