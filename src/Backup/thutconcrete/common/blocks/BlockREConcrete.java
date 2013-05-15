package thutconcrete.common.blocks;

import java.util.Random;

import thutconcrete.client.BlockRenderHandler;
import thutconcrete.common.utils.IRebar;

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

public class BlockREConcrete extends Block16Fluid implements IRebar{
	
	public static Block[] instances = new BlockREConcrete[16];
	public Block instance;
	public int colourid;
	public static int resistance = 100;
	public static float hardness = 1;
	Integer[][] data;

	public BlockREConcrete(int par1, int par2) {
		super(par1,Material.rock);
		colourid = par2;
		setUnlocalizedName("REconcrete" + colourid);
		this.instance = this;
		this.instances[colourid] = this;
	}
	

	public static Block getInstance(int colorid)
	{
		return BlockREConcrete.instances[colorid];
	}
	
	@Override
    public void onBlockAdded(World worldObj, int x, int y, int z) {
		
		if(data==null){
			data = new Integer[][]{
					{0,15,null,null,0,0},
					{},
					{BlockREConcrete.getInstance(colourid).blockID+4096*BlockREConcrete.getInstance(colourid).blockID}
			};
			fluid16Blocks.put(BlockREConcrete.getInstance(colourid).blockID,data);
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
    	EntityPlayer player;
        int l = par1World.getBlockMetadata(par2, par3, par4) & 15;
        float f = 0.0625F;
        return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ,
        								(double)par2 + this.maxX, (double)((float)par3 + (float)l * f), (double)par4 + this.maxZ);
    }
	
	@Override
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random){
		
		
		int meta = worldObj.getBlockMetadata(x, y, z);
		
		if(meta<10)
			for(int i=0;i<10-meta;i++)
		if(Math.random()>(1-SOLIDIFY_CHANCE)){
			 worldObj.setBlock(x, y, z, 0, 0, 3);
		 }
		
		if(meta==15)worldObj.setBlock(x, y, z, BlockFullSolidREConcrete.instance.blockID, colourid, 2);
		
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
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon("thutconcrete:dryConcrete_"+colourid);
		this.theIcon = par1IconRegister.registerIcon("thutconcrete:" + "rebarRusty");
	}
	
	@SideOnly(Side.CLIENT)
	
	/**
	* Returns the texture index of the thin side of the pane.
	*/
	public Icon getSideTextureIndex()
	{
	return this.theIcon;
	}
	
	@SideOnly(Side.CLIENT)
	public Icon theIcon;
	
	@SideOnly(Side.CLIENT)
	
	/**
	* Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	*/
	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	{
	return this.blockIcon;
	}

    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType()
    {
        return BlockRenderHandler.ID;
    }
    
	public boolean[] sides(IBlockAccess worldObj, int x, int y, int z) {
		boolean[] side = new boolean[6];
    	int[][]sides = {{1,0,0},{-1,0,0},{0,0,1},{0,0,-1},{0,1,0},{0,-1,0}};
		for(int i = 0; i<6; i++){
			int id = worldObj.getBlockId(x+sides[i][0], y+sides[i][1], z+sides[i][2]);
			Block block = Block.blocksList[id];
			side[i] = (block instanceof IRebar);
		}
		return side;
	}

	@Override
	public boolean[] sides(World worldObj, int x, int y, int z) {

		boolean[] side = new boolean[6];
    	int[][]sides = {{1,0,0},{-1,0,0},{0,0,1},{0,0,-1},{0,1,0},{0,-1,0}};
		for(int i = 0; i<6; i++){
			int id = worldObj.getBlockId(x+sides[i][0], y+sides[i][1], z+sides[i][2]);
			Block block = Block.blocksList[id];
			side[i] = (block instanceof IRebar);
		}
		return side;
	}

	@Override
	public Icon getIcon(Block block) {
		return this.blockIcon;
	}
	
	
 
}
