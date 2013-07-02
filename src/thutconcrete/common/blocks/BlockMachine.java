package thutconcrete.common.blocks;

import java.util.Arrays;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thutconcrete.client.render.BlockRenderHandler;
import thutconcrete.client.render.RenderSensorDisplay;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.corehandlers.ConfigHandler;
import thutconcrete.common.items.ItemDataLinker;
import thutconcrete.common.tileentity.TileEntityLiftAccess;
import thutconcrete.common.tileentity.TileEntityRTG;
import thutconcrete.common.tileentity.TileEntitySensors;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.RotationHelper;

public class BlockMachine extends Block implements ITileEntityProvider
{
	public static Block instance;
	
    public static final String[] names = {
		"display",
    	"seismicSensor",
    	"RTG",
    	"NISS",
    };

    public static final int MAX_META = 4;
    Icon[] icons = new Icon[4];
    Icon blank;
	
	public BlockMachine(int par1) {
		super(par1, Material.rock);
		this.setCreativeTab(ConcreteCore.tabThut);
		
		this.setResistance(100);
		this.setHardness(1);
		this.setUnlocalizedName("seismicMonitor");
		instance = this;
	}
	
    @SideOnly(Side.CLIENT)

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int j = 0; j < MAX_META; j++)
        {
            par3List.add(new ItemStack(par1, 1, j));
        }
    }
    
	
	@Override
	public void onBlockPlacedBy(World worldObj, int x, int y, int z, EntityLiving entity, ItemStack itemStack)
	{
		int meta = worldObj.getBlockMetadata(x, y, z);
		 if(meta==0)
		 {
			 TileEntitySensors te = (TileEntitySensors)worldObj.getBlockTileEntity(x, y, z);
			 if(te!=null)
			 {
				ForgeDirection side =  getFacingfromEntity(entity);
				System.out.println("set: "+side+" "+entity.rotationYaw);
				te.setSide(side.getOpposite().ordinal());
			 }
		 }
	}
	
	public ForgeDirection getFacingfromEntity(EntityLiving e)
	{
		ForgeDirection side = null;
		double angle = e.rotationYaw%360;
			
		if(angle>315||angle<=45)
		{
			return ForgeDirection.SOUTH;
		}
		if(angle>45&&angle<=135)
		{
			return ForgeDirection.WEST;
		}
		if(angle>135&&angle<=225)
		{
			return ForgeDirection.NORTH;
		}
		if(angle>225&&angle<=315)
		{
			return ForgeDirection.EAST;
		}
		
		return side;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntitySensors();
	}
	
	  /**
     * Called throughout the code as a replacement for block instanceof BlockContainer
     * Moving this to the Block base class allows for mods that wish to extend vinella
     * blocks, and also want to have a tile entity on that block, may.
     *
     * Return true from this function to specify this block has a tile entity.
     *
     * @param metadata Metadata of the current block
     * @return True if block has a tile entity, false otherwise
     */
    public boolean hasTileEntity(int metadata)
    {
        return true;
    }

    /**
     * Called throughout the code as a replacement for ITileEntityProvider.createNewTileEntity
     * Return the same thing you would from that function.
     * This will fall back to ITileEntityProvider.createNewTileEntity(World) if this block is a ITileEntityProvider
     *
     * @param metadata The Metadata of the current block
     * @return A instance of a class extending TileEntity
     */
    public TileEntity createTileEntity(World world, int metadata)
    {
        if (metadata==0||metadata==1)
        {
        	return createNewTileEntity(world);
        }
        if (metadata==2||metadata==3)
        {
            return new TileEntityRTG();
        }
        return null;
    }

	 public boolean onBlockActivated(World worldObj, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	 {

		 TileEntity te = worldObj.getBlockTileEntity(x, y, z);
		 if(te!=null&&te instanceof TileEntitySensors)
		 {
		
			 if(player.getHeldItem()!=null&&player.getHeldItem().itemID==Item.arrow.itemID)
			 {
				 rotateBlock(worldObj, x, y, z, ForgeDirection.getOrientation(side));
			 }
			 if(player.getHeldItem()==null||(player.getHeldItem()!=null&&player.getHeldItem().itemID!=ItemDataLinker.instance.itemID))
			 {
				 ((TileEntitySensors)te).doButtonClick(side, hitX, hitY, hitZ);
				 return true;
			 }
		 }
		 if(te!=null && te instanceof TileEntityRTG)
		 {
			 player.addChatMessage(Boolean.toString(((TileEntityRTG)te).checkSides()));
		 }
		 
		 return false;
	 }
	
    /**
     * Rotate the block. For vanilla blocks this rotates around the axis passed in (generally, it should be the "face" that was hit).
     * Note: for mod blocks, this is up to the block and modder to decide. It is not mandated that it be a rotation around the
     * face, but could be a rotation to orient *to* that face, or a visiting of possible rotations.
     * The method should return true if the rotation was successful though.
     *
     * @param worldObj The world
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param axis The axis to rotate around
     * @return True if the rotation was successful, False if the rotation failed, or is not possible
     */
    public boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis)
    {
    	if(axis==ForgeDirection.DOWN||axis==ForgeDirection.UP)
    	{
    		return false;
    	}
    	if(worldObj.getBlockTileEntity(x, y, z)!=null && worldObj.getBlockTileEntity(x, y, z) instanceof TileEntitySensors)
    	{
    		TileEntitySensors te = (TileEntitySensors)worldObj.getBlockTileEntity(x, y, z);
    		te.setSide(axis.ordinal());
    		return true;
    	}
    	
        return RotationHelper.rotateVanillaBlock(this, worldObj, x, y, z, axis);
    }
	    
    
    
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		blockIcon = iconRegister.registerIcon("thutconcrete:misc");
		blank = iconRegister.registerIcon("thutconcrete:blank");
		for(int i = 0; i<4; i++)
		{
			icons[i] = iconRegister.registerIcon("thutconcrete:controlPanel_"+i);
		}
			
	}
    
    
	@SideOnly(Side.CLIENT)

    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
    {
	 int meta = par1IBlockAccess.getBlockMetadata(x, y, z);
	 	if(meta==0&&side==1)
	 	{
	 		TileEntitySensors te = (TileEntitySensors)par1IBlockAccess.getBlockTileEntity(x, y, z);
			 if(te!=null)
			 {
				 return icons[Math.min(Math.max(te.side-2,0),3)];
			 }
			 
	 		return BlockLift.instance.icon2;
	 	}
	 	if(meta==2)
	 	{
	 		return blank;
	 	}
	 	return BlockLift.instance.icon;
    }
	 
		//////////////////////////////////////////////////////RedStone stuff/////////////////////////////////////////////////
	    /**
	     * Can this block provide power. Only wire currently seems to have this change based on its state.
	     */
	    public boolean canProvidePower()
	    {
	        return true;
	    }

	    /**
	     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	     */
	    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int side)
	    {

	    	int meta = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
	    	if(meta==1||meta==0&&(side==1||side==0))
	    	{
	    		TileEntitySensors controller = (TileEntitySensors)par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
	    		if(controller!=null)
	    		{
	    			return controller.emitRedstone?15:0;
	    		}
	    	}
	        return 0;
	    }
	    
	    /**
	     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
	     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	     */
	    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int side)
	    {
	        return isProvidingWeakPower(par1IBlockAccess, par2, par3, par4, side);
	    }
		
	    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	    public String getUnlocalizedName(int par1){
	    	return names[par1];
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
	     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	     */
	    public boolean renderAsNormalBlock()
	    {
	        return false;
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
	    	return world.getBlockMetadata(x, y, z)!=2;
	    }
}
