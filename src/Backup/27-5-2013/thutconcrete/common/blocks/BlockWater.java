package thutconcrete.common.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.corehandlers.ConfigHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.liquids.ILiquid;

public class BlockWater extends Block16Fluid implements ILiquid
{
	public static BlockWater instance;
	Integer[][] data;
	
	public BlockWater(int par1) {
		super(par1,Material.water);
		setUnlocalizedName("b16fWater");
		this.instance = this;
		this.rate = 1;
		this.setTickRandomly(true);
		setData();
		instance = this;
	//	this.setLightOpacity(0);
		this.wanderer = true;
	}
	
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:water");
    }
	
    public boolean isBlockNormalCube(World world, int x, int y, int z)
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return 0;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 0;
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int par2, int par3, int par4, Random par5Random)
    {
    	Block.waterStill.randomDisplayTick(par1World, par2, par3, par4, par5Random);
    }
    
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
    	this.setBoundsByMeta(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 0, 0, 0);
    }

	public void setData()
	{
			
		List<Integer> combinationList = new ArrayList<Integer>();
		List<Integer> desiccantList = new ArrayList<Integer>();
		List<Integer> configList = new ArrayList<Integer>();
		
		combinationList.add(4096*this.blockID);
		
		combinationList.add(this.blockID+4096*this.blockID);
		
		combinationList.add(Block.lavaMoving.blockID+4096*Block.cobblestone.blockID);
		combinationList.add(Block.lavaStill.blockID+4096*BlockLava.obsidian.blockID);
		
		//ORDER HERE MATTERS
		configList.add(0);
		int viscosity = 0;
		int fluidity = 2;
		int differential = 0;
		configList.add(viscosity);
		configList.add(null); //Add harden to
		configList.add(differential); //Add Differential
		configList.add(0); //Add random Factor
		configList.add(fluidity); //Make this a fluid
		configList.add(0);//no colour

		
		data = new Integer[][]{
				configList.toArray(new Integer[0]),
				desiccantList.toArray(new Integer[0]),
				combinationList.toArray(new Integer[0]),
			};
			fluid16Blocks.put(this.blockID,data);
	}

	@Override
	public int stillLiquidId() {
		return this.blockID;
	}

	@Override
	public boolean isMetaSensitive() {
		return false;
	}

	@Override
	public int stillLiquidMeta() {
		return 0;
	}
	
    @SideOnly(Side.CLIENT)

    /**
     * Returns which pass should this block be rendered on. 0 for solids and 1 for alpha
     */
    public int getRenderBlockPass()
    {
        return 1;
    }
	
}
