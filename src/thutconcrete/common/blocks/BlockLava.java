package thutconcrete.common.blocks;

import static net.minecraftforge.common.ForgeDirection.DOWN;
import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.UP;
import static net.minecraftforge.common.ForgeDirection.WEST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockLava extends Block16Fluid //implements ISaveable
	{
	public static Block[] instances = new BlockLava[16];
	public Block instance;
	public int typeid;
	static Material wetConcrete = (new Material(MapColor.stoneColor));
	Integer[][] data;

	@SideOnly(Side.CLIENT)
	private Icon iconFloating;
	
	public BlockLava(int par1, int x) {
		super(par1, Material.lava);
		typeid = x;
		this.setLightValue(1);
		setUnlocalizedName("Lava" + typeid);
		this.setResistance((float) 50.0);
		this.instance = this;
		this.rate = 0.95;
		this.instances[typeid] = this;
	}
	

	public static Block getInstance(int colorid)
	{
		return BlockLava.instances[colorid];
	}

	public void setData()
	{
			
		List<Integer> combinationList = new ArrayList<Integer>();
		List<Integer> desiccantList = new ArrayList<Integer>();
		List<Integer> configList = new ArrayList<Integer>();
		
		combinationList.add(4096*BlockLava.getInstance(typeid).blockID);
		combinationList.add(Block.waterMoving.blockID+4096*BlockLava.getInstance(typeid).blockID);
		combinationList.add(Block.waterStill.blockID+4096*BlockLava.getInstance(typeid).blockID);
		
		for(int i = 0;i<3;i++){
			combinationList.add(BlockLava.getInstance(i).blockID+4096*BlockLava.getInstance(typeid).blockID);
			combinationList.add(BlockSolidLava.getInstance(i).blockID+4096*BlockLava.getInstance(typeid).blockID);
		}

		combinationList.add(BlockConcrete.instance.blockID+4096*BlockLava.getInstance(typeid).blockID);

		combinationList.add(BlockLiquidConcrete.instance.blockID+4096*BlockLava.getInstance(typeid).blockID);
		
		int rate = 10;
		
		desiccantList.add(0+rate*4096);
		desiccantList.add(Block.dirt.blockID+rate*4096);
		desiccantList.add(Block.grass.blockID+rate*4096);
		desiccantList.add(Block.stone.blockID+rate*4096);
		desiccantList.add(Block.waterMoving.blockID+2*rate*4096);
		desiccantList.add(Block.waterStill.blockID+2*rate*4096);
		
		for(int i=0;i<3;i++){
			desiccantList.add(BlockSolidLava.getInstance(i).blockID+rate*20*4096);
		}
		
		//ORDER HERE MATTERS
		configList.add(0); //Add Return to
		int viscosity = 0;
		int fluidity = 2;
		int differential;
		if(typeid == 0)
		{
			viscosity = 1;
		}
		else if(typeid==1)
		{
			viscosity = 5;
		}
		else
		{
			fluidity = 1;
			viscosity = 15;
		}
		configList.add(viscosity);
		configList.add(BlockSolidLava.getInstance(typeid).blockID); //Add harden to
		configList.add(2); //Add Differential
		configList.add(typeid==0?4:10); //Add random Factor
		configList.add(2); //Make this a fluid
		configList.add(0);//no colour

		
		data = new Integer[][]{
				configList.toArray(new Integer[0]),
				desiccantList.toArray(new Integer[0]),
				combinationList.toArray(new Integer[0]),
			};
			fluid16Blocks.put(BlockLava.getInstance(typeid).blockID,data);
	}
	
   /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
	@Override
    public void onBlockPlacedBy(World worldObj,int x,int y,int z,EntityLiving entity, ItemStack item){
		worldObj.setBlockMetadataWithNotify(x, y, z, 15, 3);

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
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random){

		if(data==null){
			setData();
		}
		  if (worldObj.getGameRules().getGameRuleBooleanValue("doFireTick"))
	        {
	            Block base = Block.blocksList[worldObj.getBlockId(x, y - 1, z)];
	            boolean flag = (base != null && base.isFireSource(worldObj, x, y - 1, z, worldObj.getBlockMetadata(x, y - 1, z), UP));

                int l = worldObj.getBlockMetadata(x, y, z);

              
                boolean flag1 = worldObj.isBlockHighHumidity(x, y, z);
                byte b0 = 0;

                if (flag1)
                {
                    b0 = -50;
                }

                this.tryToCatchBlockOnFire(worldObj, x + 1, y, z, 300 + b0, par5Random, l, WEST );
                this.tryToCatchBlockOnFire(worldObj, x - 1, y, z, 300 + b0, par5Random, l, EAST );
                this.tryToCatchBlockOnFire(worldObj, x, y - 1, z, 250 + b0, par5Random, l, UP   );
                this.tryToCatchBlockOnFire(worldObj, x, y + 1, z, 250 + b0, par5Random, l, DOWN );
                this.tryToCatchBlockOnFire(worldObj, x, y, z - 1, 300 + b0, par5Random, l, SOUTH);
                this.tryToCatchBlockOnFire(worldObj, x, y, z + 1, 300 + b0, par5Random, l, NORTH);

                for (int i1 = x - 1; i1 <= x + 1; ++i1)
                {
                    for (int j1 = z - 1; j1 <= z + 1; ++j1)
                    {
                        for (int k1 = y - 1; k1 <= y + 4; ++k1)
                        {
                            if (i1 != x || k1 != y || j1 != z)
                            {
                                int l1 = 100;

                                if (k1 > y + 1)
                                {
                                    l1 += (k1 - (y + 1)) * 100;
                                }

                                int i2 = this.getChanceOfNeighborsEncouragingFire(worldObj, i1, k1, j1);

                                if (i2 > 0)
                                {
                                    int j2 = (i2 + 40 + worldObj.difficultySetting * 7) / (l + 30);

                                    if (flag1)
                                    {
                                        j2 /= 2;
                                    }

                                    if (j2 > 0 && par5Random.nextInt(l1) <= j2 && (!worldObj.isRaining() || !worldObj.canLightningStrikeAt(i1, k1, j1)) && !worldObj.canLightningStrikeAt(i1 - 1, k1, z) && !worldObj.canLightningStrikeAt(i1 + 1, k1, j1) && !worldObj.canLightningStrikeAt(i1, k1, j1 - 1) && !worldObj.canLightningStrikeAt(i1, k1, j1 + 1))
                                    {
                                        int k2 = l + par5Random.nextInt(5) / 4;

                                        if (k2 > 15)
                                        {
                                            k2 = 15;
                                        }

                                        worldObj.setBlock(i1, k1, j1, Block.fire.blockID, k2, 3);
                                    }
                                }
                            }
                        }
                    }
                }
	        }
		
		 super.updateTick(worldObj, x, y, z, par5Random);
	}
	
	
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:lava");
        this.iconFloating = par1IconRegister.registerIcon("thutconcrete:" + "floatingLava");
    }
	

	

    @Override
    public int quantityDropped(int meta, int fortune, Random random)
    {
        return 0;
    }
    
    
    
    private void tryToCatchBlockOnFire(World par1World, int x, int par3, int par4, int par5, Random par6Random, int par7, ForgeDirection face)
    {
        int j1 = 0;
        Block block = Block.blocksList[par1World.getBlockId(x, par3, par4)];
        if (block != null)
        {
            j1 = block.getFlammability(par1World, x, par3, par4, par1World.getBlockMetadata(x, par3, par4), face);
        }

        if (par6Random.nextInt(par5) < j1)
        {
            boolean flag = par1World.getBlockId(x, par3, par4) == Block.tnt.blockID;

            if (par6Random.nextInt(par7 + 10) < 5 && !par1World.canLightningStrikeAt(x, par3, par4))
            {
                int k1 = par7 + par6Random.nextInt(5) / 4;

                if (k1 > 15)
                {
                    k1 = 15;
                }

                par1World.setBlock(x, par3, par4, Block.fire.blockID, k1, 3);
            }
            else
            {
                par1World.setBlockToAir(x, par3, par4);
            }

            if (flag)
            {
                Block.tnt.onBlockDestroyedByPlayer(par1World, x, par3, par4, 1);
            }
        }
    }
    
    
    
    
    /**
     * Returns true if at least one block next to this one can burn.
     */
    private boolean canNeighborBurn(World par1World, int par2, int par3, int par4)
    {
        return canBlockCatchFire(par1World, par2 + 1, par3, par4, WEST ) ||
               canBlockCatchFire(par1World, par2 - 1, par3, par4, EAST ) ||
               canBlockCatchFire(par1World, par2, par3 - 1, par4, UP   ) ||
               canBlockCatchFire(par1World, par2, par3 + 1, par4, DOWN ) ||
               canBlockCatchFire(par1World, par2, par3, par4 - 1, SOUTH) ||
               canBlockCatchFire(par1World, par2, par3, par4 + 1, NORTH);
    }

    /**
     * Gets the highest chance of a neighbor block encouraging this block to catch fire
     */
    private int getChanceOfNeighborsEncouragingFire(World par1World, int par2, int par3, int par4)
    {
        byte b0 = 0;

        if (!par1World.isAirBlock(par2, par3, par4))
        {
            return 0;
        }
        else
        {
            int l = this.getChanceToEncourageFire(par1World, par2 + 1, par3, par4, b0, WEST);
            l = this.getChanceToEncourageFire(par1World, par2 - 1, par3, par4, l, EAST);
            l = this.getChanceToEncourageFire(par1World, par2, par3 - 1, par4, l, UP);
            l = this.getChanceToEncourageFire(par1World, par2, par3 + 1, par4, l, DOWN);
            l = this.getChanceToEncourageFire(par1World, par2, par3, par4 - 1, l, SOUTH);
            l = this.getChanceToEncourageFire(par1World, par2, par3, par4 + 1, l, NORTH);
            return l;
        }
    }

    /**
     * Side sensitive version that calls the block function.
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param face The side the fire is coming from
     * @return True if the face can catch fire.
     */
    public boolean canBlockCatchFire(IBlockAccess world, int x, int y, int z, ForgeDirection face)
    {
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if (block != null)
        {
            return block.isFlammable(world, x, y, z, world.getBlockMetadata(x, y, z), face);
        }
        return false;
    }

    /**
     * Side sensitive version that calls the block function.
     * 
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param oldChance The previous maximum chance.
     * @param face The side the fire is coming from
     * @return The chance of the block catching fire, or oldChance if it is higher
     */
    public int getChanceToEncourageFire(World world, int x, int y, int z, int oldChance, ForgeDirection face)
    {
        int newChance = 0;
        Block block = Block.blocksList[world.getBlockId(x, y, z)];
        if (block != null)
        {
            newChance = block.getFireSpreadSpeed(world, x, y, z, world.getBlockMetadata(x, y, z), face);
        }
        return (newChance > oldChance ? newChance : oldChance);
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

            return ((material == Material.air)||(block instanceof Block16Fluid&&meta!=15))? this.iconFloating : this.blockIcon;
            
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
    
    	return AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 0, 0, 0);
        
    }
	
    public void onEntityCollidedWithBlock(World worldObj,int x,int y, int z, Entity entity)
    {
    	if(entity instanceof EntityItem)
    	{
    		entity.setDead();
    	}
    	setTEUpdate(worldObj, x, y, z);
    }
    
}


