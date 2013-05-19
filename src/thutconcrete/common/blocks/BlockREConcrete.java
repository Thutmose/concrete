package thutconcrete.common.blocks;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import thutconcrete.client.BlockRenderHandler;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.corehandlers.TSaveHandler;
import thutconcrete.common.utils.IRebar;
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

public class BlockREConcrete extends Block16Fluid implements IRebar
{
	
	public static Block instance;
	public int colourid;
	public static int resistance = 100;
	public static float hardness = 1;
	public static ConcurrentHashMap<String, Byte> metaData = new ConcurrentHashMap<String, Byte>();
	Integer[][] data;
	boolean[] side = new boolean[6];

	public BlockREConcrete(int par1) {
		super(par1,Material.rock);
		setUnlocalizedName("REconcrete");
		this.setTickRandomly(true);
		this.rate = 1;
		this.instance = this;
	}
	
	
	@Override
    public void onBlockAdded(World worldObj, int x, int y, int z) {
		
		if(data==null){
			data = new Integer[][]{
					{
						0,//ID that this returns when meta hits -1, 
						15,//the viscosity factor,
						null,//a secondary ID that this can turn into used for hardening,
						null,//The hardening differential that prevents things staying liquid forever.,
						null,//a randomness coefficient, this is multiplied by a random 0-10 then added to the hardening differential and viscosity.,
						0,//The will fall of edges factor, this is 0 or 1,
						1,//0 = not colourable, 1 = colourable.
					},
					{},
					{BlockREConcrete.instance.blockID+4096*BlockREConcrete.instance.blockID}
			};
			fluid16Blocks.put(BlockREConcrete.instance.blockID,data);
			}
    }
	
	
	
	//*
    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
	@Override
    public void addCollisionBoxesToList(World worldObj, int x, int y, int z, AxisAlignedBB aaBB, List list, Entity par7Entity)
    {
		side = sides(worldObj,x,y,z);
		
		if(!(side[0]||side[1]||side[2]||side[3]||side[4]||side[5]))
			side = new boolean[] {true, true, true, true, false, false};
		
    	AxisAlignedBB aabb = this.getCollisionBoundingBoxFromPool(worldObj, x, y, z);
    	if (aaBB.intersectsWith(aabb))
            list.add(aabb);
    	int n = 5;
    	
        for (ForgeDirection fside : ForgeDirection.VALID_DIRECTIONS)
        {
                AxisAlignedBB coll = getBoundingBoxForSide(fside).offset(x, y, z);
                if (aaBB.intersectsWith(coll)&&this.side[n])
                        list.add(coll);
                n--;
        }
    }

	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int x, int y, int z)
    {
		side = sides(par1IBlockAccess,x,y,z);
		
		if(!(side[0]||side[1]||side[2]||side[3]||side[4]||side[5]))
			side = new boolean[] {true, true, true, true, false, false};
		setBlockBounds(0.35F, 0.35F, 0.35F, 0.65F, 0.65F, 0.65F);

    	this.setBoundsByMeta(par1IBlockAccess.getBlockMetadata(x, y, z));
    	this.setResistanceByMeta(par1IBlockAccess.getBlockMetadata(x, y, z));
    }

    public AxisAlignedBB getBoundingBoxForSide(ForgeDirection fside)
    {
            switch (fside)
            {
                    case UP:
                    {
                            return AxisAlignedBB.getBoundingBox(0.35F, 0.4F, 0.35F, 0.65F, 1F, 0.65F);
                    }
                    case DOWN:
                    {
                            return AxisAlignedBB.getBoundingBox(0.35F, 0.0F, 0.35F, 0.65F, 0.6F, 0.65F);
                    }
                    case NORTH:
                    {
                            return AxisAlignedBB.getBoundingBox(0.35F, 0.35F, 0.0F, 0.65F, 0.65F, 0.6F);
                    }
                    case SOUTH:
                    {
                            return AxisAlignedBB.getBoundingBox(0.35F, 0.35F, 0.4F, 0.65F, 0.65F, 1F);
                    }
                    case EAST:
                    {
                            return AxisAlignedBB.getBoundingBox(0.4F, 0.35F, 0.35F, 1F, 0.65F, 0.65F);
                    }
                    case WEST:
                    {
                            return AxisAlignedBB.getBoundingBox(0.0F, 0.35F, 0.35F, 0.60F, 0.65F, 0.65F);
                    }
                    default:
                    {
                            return AxisAlignedBB.getBoundingBox(0f, 0f, 0f, 1f, 1f, 1f);
                    }
            }
    }
    
    private void setBlockBoundsForSide(int x, int y, int z, ForgeDirection side)
    {
            switch (side)
	        {
		            case UP:
		            {
		                    setBlockBounds(0.35F, 0.4F, 0.35F, 0.65F, 1F, 0.65F);
		                    break;
		            }
		            case DOWN:
		            {
		                    setBlockBounds(0.35F, 0.0F, 0.35F, 0.65F, 0.6F, 0.65F);
		                    break;
		            }
		            case NORTH:
		            {
		                    setBlockBounds(0.35F, 0.35F, 0.0F, 0.65F, 0.65F, 0.6F);
		                    break;
		            }
		            case SOUTH:
		            {
		                    setBlockBounds(0.35F, 0.35F, 0.4F, 0.65F, 0.65F, 1F);
		                    break;
		            }
		            case EAST:
		            {
		                    setBlockBounds(0.4F, 0.35F, 0.35F, 1F, 0.65F, 0.65F);
		                    break;
		            }
		            case WEST:
		            {
		                    setBlockBounds(0.0F, 0.35F, 0.35F, 0.60F, 0.65F, 0.65F);
		                    break;
		            }
		            default:
		            {
		                    setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f);
		                    break;
		            }
            }
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
    public float getExplosionResistance(Entity par1Entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ)
    {
        return getBlastResistanceByMeta(world.getBlockMetadata(x, y, z));
    }
	

	
	@Override
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random){
		
		
		int meta = worldObj.getBlockMetadata(x, y, z);
		
		if(meta<10)
		{
			for(int i=0;i<10-meta;i++)
			{
				if(Math.random()>(1-SOLIDIFY_CHANCE*100))
				{
					 worldObj.setBlock(x, y, z, 0, 0, 3);
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
    private Icon[] iconArray;
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.blockIcon = par1IconRegister.registerIcon("thutconcrete:dryConcrete_"+colourid);
		this.theIcon = par1IconRegister.registerIcon("thutconcrete:" + "rebarRusty");
		this.iconArray = new Icon[16];
    	for (int i = 0; i < this.iconArray.length; ++i)
        {
            this.iconArray[i] = par1IconRegister.registerIcon("thutconcrete:" + "dryConcrete_"+i);
        }
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
