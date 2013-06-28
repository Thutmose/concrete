package thutconcrete.common.blocks;

import static net.minecraftforge.common.ForgeDirection.UP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import thutconcrete.api.utils.Vector3;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.items.ItemConcreteDust;
import thutconcrete.common.utils.ThreadSafeWorldOperations;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
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
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.IPlantable;


public class BlockDust extends Block16Fluid
{
	public static Block instance;
	static Integer[][] data;
	private static int thisID;
	long time = 0;
	
	@SideOnly(Side.CLIENT)
	private Icon iconFloatingDust;

    public BlockDust(int par1)
    {
    	super(par1, Material.ground);
		setUnlocalizedName("dust");
		setCreativeTab(ConcreteCore.tabThut);
		setHardness(0.1f);
		setResistance(0.0f);
		instance=this;
		this.thisID = par1;
		this.dust = true;
    }
  
	public void setData(){
		
		List<Integer> combinationList = new ArrayList<Integer>();
		
		combinationList.add(BlockDust.instance.blockID+4096*BlockDust.instance.blockID);
		combinationList.add(4096*BlockDust.instance.blockID);
		
		for(int i = 0;i<4;i++){
			combinationList.add(BlockLava.getInstance(i).blockID+4096*BlockLava.getInstance(i).blockID);
		}

		
		data = new Integer[][]{
				{
					0,//ID that this returns when meta hits -1, 
					2,//the viscosity factor,
					this.thisID,//a secondary ID that this can turn into used for hardening,
					1,//The hardening differential that prevents things staying liquid forever.,
					0,//a randomness coefficient, this is multiplied by a random 0-10 then added to the hardening differential and viscosity.,
					0,//The will fall of edges factor, this is 0 or 1,
					0,//0 = not colourable, 1 = colourable.
				},
				{},
				combinationList.toArray(new Integer[0]),
				{Block.leaves.blockID}
			};
			fluid16Blocks.put(this.thisID,data);
	}
    
    @Override
    public void onBlockAdded(World worldObj, int x, int y, int z)
    {
		worldObj.scheduleBlockUpdate(x, y, z, worldObj.getBlockId(x, y, z), 5);
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
            return ((material == Material.air)||(block instanceof Block16Fluid&&meta!=0))? this.iconFloatingDust : this.blockIcon;

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
        int l = 15-par1World.getBlockMetadata(par2, par3, par4);
        int id = par1World.getBlockId(par2, par3 - 1, par4);
        Block block = Block.blocksList[id];
        float f = 0.0625F;
        if(!(Vector3.isLiquid(par1World, new Vector3(par2,par3-1,par4))||
        		par1World.isAirBlock(par2, par3-1, par4)||(block instanceof Block16Fluid&&meta!=0))){
        return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ, (double)par2 + this.maxX, (double)((float)par3 + (float)l * f), (double)par4 + this.maxZ);
        }
        else{
        	return AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 0, 0, 0);
        }
    }
    
	@Override
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random)
	{ 

		if(data==null){
			setData();
		}
		doFluidTick(worldObj, x, y, z);
		
		if(Math.random()>0.999&&worldObj.getBlockMetadata(x, y, z)==0)
		{
			int idUp = worldObj.getBlockId(x, y+1, z);
			int metaUp = worldObj.getBlockMetadata(x, y+1, z);
			if(idUp==thisID&&metaUp==0)
			{
				worldObj.setBlock(x, y, z, BlockWorldGen.instance.blockID, 1, 2);
			}
		}
    }
    
    ////////////////////////////////////////////Plant stuff////////////////////////////////////////////////////////////////
    

    /**
     * Determines if this block can support the passed in plant, allowing it to be planted and grow.
     * Some examples:
     *   Reeds check if its a reed, or if its sand/dirt/grass and adjacent to water
     *   Cacti checks if its a cacti, or if its sand
     *   Nether types check for soul sand
     *   Crops check for tilled soil
     *   Caves check if it's a colid surface
     *   Plains check if its grass or dirt
     *   Water check if its still water
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z position
     * @param direction The direction relative to the given position the plant wants to be, typically its UP
     * @param plant The plant that wants to check
     * @return True to allow the plant to be planted/stay.
     */
    public boolean canSustainPlant(World world, int x, int y, int z, ForgeDirection direction, IPlantable plant)
    {
       return world.getBlockMetadata(x, y, z)==0;
    }

    /**
     * Checks if this soil is fertile, typically this means that growth rates
     * of plants on this soil will be slightly sped up.
     * Only vanilla case is tilledField when it is within range of water.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z position
     * @return True if the soil should be considered fertile.
     */
    public boolean isFertile(World world, int x, int y, int z)
    {
    	 return world.getBlockMetadata(x, y, z)==0;
    }

    
    /**
     * Return true from this function if the player with silk touch can harvest this block directly, and not it's normal drops.
     *
     * @param world The world
     * @param player The player doing the harvesting
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param metadata The metadata
     * @return True if the block can be directly harvested using silk touch
     */
    public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata)
    {
        return false;
    }
    
    
}