package thutconcrete.common.blocks;

import java.util.List;

import powercrystals.minefactoryreloaded.api.rednet.IConnectableRedNet;
import powercrystals.minefactoryreloaded.api.rednet.RedNetConnectionType;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thutconcrete.api.utils.Vector3;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.items.ItemLiftController;
import thutconcrete.common.tileentity.TileEntityLiftAccess;
import thutconcrete.common.tileentity.TileEntityLimekiln;
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

public class BlockLift extends Block implements ITileEntityProvider, IConnectableRedNet
{

	public static BlockLift instance;
	
	public Icon[] faces;
	
	public int size = 5;
	
	public BlockLift(int par1) 
	{
		super(par1, Material.iron);
		setHardness(3.5f);
		setCreativeTab(ConcreteCore.tabThut);
		this.setUnlocalizedName("Block");
		instance = this;
	}
	
	@Override
	public void onBlockPlacedBy(World worldObj, int x, int y, int z, EntityLiving entity, ItemStack itemStack)
	{
		int meta = worldObj.getBlockMetadata(x, y, z);
		 if(meta==1)
		 {
			 TileEntityLiftAccess te = (TileEntityLiftAccess)worldObj.getBlockTileEntity(x, y, z);
			 if(te!=null)
			 {
				ForgeDirection side =  getFacingfromEntity(entity);
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
	
	public boolean onBlockActivated(World worldObj, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
		 int meta = worldObj.getBlockMetadata(x, y, z);
		 if(player.isSneaking())
		 {
			 player.addChatMessage("sneaky");
		 }

		 if(meta==1)
		 {
			 TileEntityLiftAccess te = (TileEntityLiftAccess)worldObj.getBlockTileEntity(x, y, z);
			 if(te!=null&&side!=te.side)
			 {
				 if(player.getHeldItem()!=null&&(player.getHeldItem().itemID==BlockMisc.instance.blockID
						 ||player.getHeldItem().getItem().getUnlocalizedName().toLowerCase().contains("wrench")
						 ||player.getHeldItem().getItem().getUnlocalizedName().toLowerCase().contains("screwdriver")
						 ||player.getHeldItem().itemID==ItemLiftController.instance.itemID
						 ))
					 {
						 te.setSide(side);
						 return true;
					 }
			 }
		 }
		 worldObj.markBlockForRenderUpdate(x, y, z);
		 boolean ret = false;
		 int id;
		 boolean rails = false;
		 
		 if(meta == 0)
		 {
			 ret = checkRailsForSpawn(worldObj, true, x, y, z);
			 if(!ret)
			 {
				 ret = checkRailsForSpawn(worldObj, false, x, y, z);
				 if(ret)
					 rails = true;
			 }
			 if(!ret&&worldObj.isRemote)
			 {
				 player.addChatMessage("complete rails not found");
			 }
			 if(ret)
				 ret = checkBlocks(worldObj, x, y, z);
			 
			if(ret&&!worldObj.isRemote)
			{
				removeBlocks(worldObj, x, y, z);
				EntityLift lift = new EntityLift(worldObj, x+0.5, y, z+0.5, size);
				worldObj.spawnEntityInWorld(lift);
			}
			if(!ret&&rails&&worldObj.isRemote)
			{
				player.addChatMessage("complete base not found");
			}
		 }
		 else if(meta == 1)
		 {
			 TileEntityLiftAccess te = (TileEntityLiftAccess)worldObj.getBlockTileEntity(x, y, z);

			 if(te!=null)
				 te.doButtonClick(side, hitX, hitY, hitZ);
			 ret = true;
		 }
		 
		return ret;
    }
	 
	public boolean checkRailsForSpawn(World worldObj, boolean axis, int x, int y, int z)
	{
		int[] sizes = {5,3,1};
		
		boolean ret = false;
		
		for(int j = 0; j<3; j++)
		{
			boolean bool = true;
			
			int rail =(int)(1+Math.floor(sizes[j]/2));
			int colmn = (int)(sizes[j]/2);
			
			int[][] sides = {{rail,0},{-rail,0},{0,rail},{0,-rail}};
			int[][] colm =  {{colmn,0},{-colmn,0},{0,colmn},{0,-colmn}};
			
			
			for(int i = 0; i<5; i++)
			{
				bool = bool&&worldObj.getBlockId(x+sides[axis?2:0][0],y+i,z+sides[axis?2:0][1])==BlockLiftRail.staticBlock.blockID;
				bool = bool&&worldObj.getBlockId(x+sides[axis?3:1][0],y+i,z+sides[axis?3:1][1])==BlockLiftRail.staticBlock.blockID;
				
				
				if(i!=0&&colmn!=0)
				{
					bool = bool&&(worldObj.getBlockId(x+colm[axis?2:0][0],y+i,z+colm[axis?2:0][1])==Block.blockIron.blockID);
					bool = bool&&(worldObj.getBlockId(x+colm[axis?3:1][0],y+i,z+colm[axis?3:1][1])==Block.blockIron.blockID);
				}
				
			}
			if(bool)
			{
				size = sizes[j];
				ret = bool;
				break;
			}
		}
		
		return ret;
	}
	
	public boolean checkBlocks(World worldObj, int x, int y, int z)
	{
		boolean ret = true;
		
		int rad = (size/2);
		
		for(int i = -rad; i<=rad;i++)
			for(int j = -rad;j<=rad;j++)
			{
				if(!(i==0&&j==0))
				{
					ret = ret && worldObj.getBlockId(x+i, y, z+j)==Block.blockIron.blockID;
				}
				ret = ret && worldObj.getBlockId(x+i, y+4, z+j)==Block.blockIron.blockID;
			}
		return ret;
	}
	
	public void removeBlocks(World worldObj, int x, int y, int z)
	{
		int rad = (size/2);
		
		for(int i = -rad; i<=rad;i++)
			for(int j = -rad;j<=rad;j++)
				for(int k = 0; k<5; k++)
				{
					worldObj.setBlock(x+i, y+k, z+j,0,0,3);
				}
		
	}

    @SideOnly(Side.CLIENT)
	public Icon icon;
    @SideOnly(Side.CLIENT)
	public Icon icon2;
    
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		blockIcon = iconRegister.registerIcon("thutconcrete:blockLift");
		icon = iconRegister.registerIcon("thutconcrete:liftControl");
		faces = new Icon[17];
		icon2 = iconRegister.registerIcon("thutconcrete:controlPanel_1");
	}
	
    @SideOnly(Side.CLIENT)
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(int par1, int par2)
    {
        return par2==0?blockIcon:icon;
    }
    
	 @SideOnly(Side.CLIENT)

	    /**
	     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	     */
	    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int par5)
	    {
		 int meta = par1IBlockAccess.getBlockMetadata(x, y, z);
		 if(meta==0)
		 {
			 return blockIcon;
		 }
		 TileEntityLiftAccess te = (TileEntityLiftAccess)par1IBlockAccess.getBlockTileEntity(x, y, z);
		 if(te!=null)
		 {
			 if(par5 == te.side)//&&te.floor>=0&&te.floor<=16
				 return icon2;
		 }
		 return this.icon;
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
	        return metadata==1;
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
	        if (metadata==1)
	        {
	            return new TileEntityLiftAccess();
	        }
	        return null;
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
    public int isProvidingWeakPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {

    	int meta = par1IBlockAccess.getBlockMetadata(par2, par3, par4);
    	if(meta==1)
    	{
    		TileEntityLiftAccess controller = (TileEntityLiftAccess)par1IBlockAccess.getBlockTileEntity(par2, par3, par4);
    		if(controller!=null)
    		{
    			return controller.called?15:0;
    		}
    	}
        return 0;
    }
    
    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return isProvidingWeakPower(par1IBlockAccess, par2, par3, par4, par5);
    }
	
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    @SideOnly(Side.CLIENT)

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int j = 0; j < 2; j++)
        {
            par3List.add(new ItemStack(par1, 1, j));
        }
    }

    protected ItemStack createStackedBlock(int par1)
    {
        return new ItemStack(this.blockID, 1, par1);
    }
   
    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int par1)
    {
        return par1;
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
    	if(worldObj.getBlockTileEntity(x, y, z)!=null && worldObj.getBlockTileEntity(x, y, z) instanceof TileEntityLiftAccess)
    	{
    		TileEntityLiftAccess te = (TileEntityLiftAccess)worldObj.getBlockTileEntity(x, y, z);
    		te.setSide(axis.ordinal());
    	}
        return RotationHelper.rotateVanillaBlock(this, worldObj, x, y, z, axis);
    }

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityLiftAccess();
	}

	///////////////////////////////////////////////MFR Rednet Compatibility stuff///////////////////////////////////////////////////
	@Override
	public RedNetConnectionType getConnectionType(World world, int x, int y,
			int z, ForgeDirection side) {
		
		TileEntityLiftAccess te = (TileEntityLiftAccess)world.getBlockTileEntity(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if(meta==1&&te!=null)
		{
			ForgeDirection teSide = ForgeDirection.getOrientation(te.side);
			if(teSide!=side)
			{
				return RedNetConnectionType.PlateAll;
			}
		}
		
		return RedNetConnectionType.None;
	}

	@Override
	public int[] getOutputValues(World world, int x, int y, int z,
			ForgeDirection side) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getOutputValue(World world, int x, int y, int z,
			ForgeDirection side, int subnet) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onInputsChanged(World world, int x, int y, int z,
			ForgeDirection side, int[] inputValues) {
		TileEntityLiftAccess te = (TileEntityLiftAccess)world.getBlockTileEntity(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if(meta==1&&te!=null)
		{
			ForgeDirection teSide = ForgeDirection.getOrientation(te.side);
			
			boolean hex = inputValues[2] == 13;
			boolean binary = (inputValues[0]==13||inputValues[1]==13)&&!hex;
			
			if(hex)
			{
				int yPos = inputValues[0]+16*inputValues[1];
				te.callYValue(yPos);
				return;
			}
			
			if(binary)
			{
				int yPos = 0;
				for(int i = 0; i<inputValues.length; i++)
				{
					yPos += inputValues[i]==0?0:Math.pow(2,i);
				}
				te.callYValue(yPos);
				return;
			}
			if(!(binary||hex))
			{
				for(int i = 0; i<inputValues.length; i++)
				{
					if(inputValues[i]==15)
					{
						te.buttonPress(i+1);
						break;
					}
					if(inputValues[i]==14)
					{
						te.buttonPress(te.floor);
						break;
					}
				}
				return;
			}
			
		}
	}

	@Override
	public void onInputChanged(World world, int x, int y, int z,
			ForgeDirection side, int inputValue) {
		// TODO Auto-generated method stub
		
	}
}
