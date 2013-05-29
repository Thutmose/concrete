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
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
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
	public static int resistance = 5;
	public static float hardness = 1;
	public int totalProb = 0;
	Integer[][] data;
	ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
	
	public BlockSolidLava(int par1, int par2) {
		super(par1,Material.rock);
		typeid = par2;
		setUnlocalizedName("solidLava" + typeid);
		this.instance = this;
		this.rate = 1;
		this.instances[typeid] = this;
		this.setTickRandomly(true);
		this.setStepSound(soundStoneFootstep);
		this.placeamount = 1;
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
    	
        int l = 15-par1World.getBlockMetadata(par2, par3, par4);
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
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random)
	{
	}
	
    /**
     * This returns a complete list of items dropped from this block.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param metadata Current metadata
     * @param fortune Breakers fortune level
     * @return A ArrayList containing all items this block drops
     */
    public ArrayList<ItemStack> getBlockDropped(World worldObj, int x, int y, int z, int metadata, int fortune)
    {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        
        return ret;
    }
    
    private void initDrops()
    {
        for(Integer i : BlockSolidLava.getInstance(typeid).turnto)
		{
			int id = i & 4095;
			int probability = i>>12;
			int meta = i>>22;
			for(int j = 0; j<probability; j++)
			{
				drops.add(new ItemStack(Block.blocksList[id], 1, meta));
			}
		}
    }
    
    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(World worldObj, int x, int y, int z, int thismeta, float par6, int par7)
    {
        if (!worldObj.isRemote)
        {
            ArrayList<ItemStack> items = new ArrayList<ItemStack>();
            
            if(drops.size()==0)
            {
            	initDrops();
            }
            
            if(thismeta==0)
            {
            	items = drops;
            }
            else
            {
            	items.add(new ItemStack(Block.blocksList[blockID], 16-thismeta, 0));
            }
            
            int i = (new Random()).nextInt(items.size());

            ItemStack item = items.get(i);
            
            this.dropBlockAsItem_do(worldObj, x, y, z, item);
            
        }
    }
    
	public int tickRate(World worldObj)
	{
		return 10;
	}
	
	public void onBlockClicked(World worldObj, int x, int y, int z, EntityPlayer player){
		this.setResistanceByMeta(worldObj.getBlockMetadata(x, y, z));
	}
	
	protected void setResistanceByMeta(int meta){
		int j = 15-meta;
        float f = (float)((1 + j)) / 16.0F;
        this.setResistance(f*resistance*(1+typeid));
        this.setHardness(f*hardness);
	}
	protected float getBlastResistanceByMeta(int meta){
		int j = 15-meta;
        float f = (float)((1 + j)) / 16.0F;
        return (f*resistance*(1+typeid));
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
	    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	    {
	           return this.blockIcon;
	    }
	    
	    public void onBlockPlacedBy(World worldObj,int x,int y,int z,EntityLiving entity, ItemStack item)
	    {
	    	worldObj.setBlockMetadataWithNotify(x, y, z, 15, 3);
	    	merge(worldObj, x, y, z, x, y-1, z);
	    }
	   
	    public boolean checkSides(World worldObj, int x, int y, int z){
	    	int[][]sides = {{1,0,0},{-1,0,0},{0,0,1},{0,0,-1},{0,1,0},{0,-1,0}};
	        for(int i=0;i<6;i++){
	        	int id = safe.safeGetID(worldObj,x+sides[i][0], y+sides[i][1], z+sides[i][2]);
	        	int meta = safe.safeGetMeta(worldObj,x+sides[i][0], y+sides[i][1], z+sides[i][2]);
	        	Block block = safe.safeGetBlock(worldObj,x+sides[i][0], y+sides[i][1], z+sides[i][2]);

	        	if(block instanceof BlockSolidLava && meta!=0)
	        	{
	        		return false;
	        	}
	        	if(id==0||safe.isLiquid(worldObj, x, y, z))
	        	{
	        		return false;
	        	}
	        }
	        return true;
	   }
}
