package thutconcrete.common.blocks;

import java.util.Arrays;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thutconcrete.client.render.BlockRenderHandler;
import thutconcrete.client.render.RenderSeismicMonitor;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.corehandlers.ConfigHandler;
import thutconcrete.common.items.ItemSeismicLinker;
import thutconcrete.common.tileentity.TileEntityLiftAccess;
import thutconcrete.common.tileentity.TileEntitySeismicMonitor;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.RotationHelper;

public class BlockSeismicMonitor extends Block implements ITileEntityProvider
{
	public static Block instance;
	
	public BlockSeismicMonitor(int par1) {
		super(par1, Material.rock);
		this.setCreativeTab(ConcreteCore.tabThut);
		this.setResistance(100);
		this.setHardness(100);
		this.setUnlocalizedName("seismicMonitor");
		instance = this;
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntitySeismicMonitor();
	}

	 public boolean onBlockActivated(World worldObj, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
	 {

		 TileEntity te = worldObj.getBlockTileEntity(x, y, z);
		 if(te!=null&&te instanceof TileEntitySeismicMonitor)
		 {
		
			 if(player.getHeldItem()!=null&&player.getHeldItem().itemID==Item.arrow.itemID)
			 {
				// System.out.println(ForgeDirection.getOrientation(side));
				 rotateBlock(worldObj, x, y, z, ForgeDirection.getOrientation(side));
			 }
			 if(player.getHeldItem()==null||(player.getHeldItem()!=null&&player.getHeldItem().itemID!=ItemSeismicLinker.instance.itemID))
			 {
				 ((TileEntitySeismicMonitor)te).doButtonClick(side, hitX, hitY, hitZ);
				 return true;
			 }
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
	    	if(worldObj.getBlockTileEntity(x, y, z)!=null && worldObj.getBlockTileEntity(x, y, z) instanceof TileEntitySeismicMonitor)
	    	{
	    		TileEntitySeismicMonitor te = (TileEntitySeismicMonitor)worldObj.getBlockTileEntity(x, y, z);
	    		te.setSide(axis.ordinal());
	    		return true;
	    	}
	        return RotationHelper.rotateVanillaBlock(this, worldObj, x, y, z, axis);
	    }
	    
	/**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     * /
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    //*/
    
    Icon[] icons = new Icon[4];
    
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		blockIcon = iconRegister.registerIcon("thutconcrete:blockLift");
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
		 	if(side==1)
		 	{
		 		TileEntitySeismicMonitor te = (TileEntitySeismicMonitor)par1IBlockAccess.getBlockTileEntity(x, y, z);
				 if(te!=null)
				 {
					 return icons[Math.min(Math.max(te.side-2,0),3)];
				 }
				 
		 		return BlockLift.instance.icon2;
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
	    	if((side==1||side==0))
	    	{
	    		TileEntitySeismicMonitor controller = (TileEntitySeismicMonitor)par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
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
	   
}
