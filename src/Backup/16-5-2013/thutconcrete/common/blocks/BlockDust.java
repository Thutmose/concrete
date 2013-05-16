package thutconcrete.common.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import thutconcrete.common.items.ItemConcreteDust;
import thutconcrete.common.utils.ThreadSafeWorldOperations;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.*;


public class BlockDust extends Block16Fluid
{
	public static Block instance;
	static Integer[][] data;
	private static int thisID;
	
	@SideOnly(Side.CLIENT)
	private Icon iconFloatingDust;

    public BlockDust(int par1)
    {
    	super(par1, Material.ground);
		setUnlocalizedName("dust");
		setHardness(0.1f);
		setResistance(0.0f);
		instance=this;
		this.thisID = par1;
		if(data==null){
			setData();
		}
    }
    
    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    @Override
    public void onBlockAdded(World worldObj, int x, int y, int z) {
    	if(data==null){
			setData();
			}
    	tickSides(worldObj,x,y,z);
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
		data = new Integer[][]{
				{
					0,
					2,
					this.thisID,
					1,
					0,
					0,
				},
				{},
				{

					BlockDust.instance.blockID+4096*BlockDust.instance.blockID,
					4096*BlockDust.instance.blockID,
					
				}
			};
			fluid16Blocks.put(this.thisID,data);
	}
	
	
    
    
    ///////////////////////////////////////////////////////////////////Block Ticking Stuff Above Here///////////////////////////////////////
    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return par5 == 1 ? true : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:" + this.getUnlocalizedName2());
        this.iconFloatingDust = par1IconRegister.registerIcon("thutconcrete:" + "dustCloud");
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
            Material material = par1IBlockAccess.getBlockMaterial(par2, par3 - 1, par4);

            int id = par1IBlockAccess.getBlockId(par2, par3 - 1, par4);
            int meta = par1IBlockAccess.getBlockMetadata(par2, par3 - 1, par4);
            Block block = Block.blocksList[id];
            return ((material == Material.air)||(block instanceof Block16Fluid&&meta!=15))? this.iconFloatingDust : this.blockIcon;

    }
    
    @Override
    public int quantityDropped(int meta, int fortune, Random random)
    {
        return (meta & 15) + 1;
    }
    
    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return ItemConcreteDust.instance.itemID;
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
    	int meta = par1World.getBlockMetadata(par2, par3, par4);
    	ThreadSafeWorldOperations safe = new ThreadSafeWorldOperations();
        int l = par1World.getBlockMetadata(par2, par3, par4) & 15;
        int id = par1World.getBlockId(par2, par3 - 1, par4);
        Block block = Block.blocksList[id];
        float f = 0.0625F;
        if(!(safe.isLiquid(par1World, par2,par3-1,par4)||
        		par1World.isAirBlock(par2, par3-1, par4)||(block instanceof Block16Fluid&&meta!=15))){
        return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ, (double)par2 + this.maxX, (double)((float)par3 + (float)l * f), (double)par4 + this.maxZ);
        }
        else{
        	return AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 0, 0, 0);
        }
    }
    
}