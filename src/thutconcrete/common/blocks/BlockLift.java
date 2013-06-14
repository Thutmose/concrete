package thutconcrete.common.blocks;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.items.ItemLiftController;
import thutconcrete.common.tileentity.TileEntityLiftAccess;
import thutconcrete.common.tileentity.TileEntityLimekiln;
import thutconcrete.common.utils.Vector3;
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

public class BlockLift extends Block implements ITileEntityProvider
{

	public static BlockLift instance;
	
	public BlockLift(int par1) 
	{
		super(par1, Material.iron);
		setHardness(3.5f);
		setCreativeTab(ConcreteCore.tabThut);
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
		 
		 if(player.getHeldItem()!=null&&(player.getHeldItem().itemID==Item.axeWood.itemID
				 ||player.getHeldItem().getItem().getUnlocalizedName().toLowerCase().contains("wrench")
				 ||player.getHeldItem().getItem().getUnlocalizedName().toLowerCase().contains("screwdriver")))
		 {
			 if(meta==1)
			 {
				 TileEntityLiftAccess te = (TileEntityLiftAccess)worldObj.getBlockTileEntity(x, y, z);
				 if(te!=null)
				 {
					 te.setSide(side);
				 }
			 }
		 }
		 worldObj.markBlockForRenderUpdate(x, y, z);
		 boolean ret = false;
		 int id;
		 
		 if(meta == 0)
		 {
			 ret = checkRailsForSpawn(worldObj, true, x, y, z);
			 if(!ret)
			 {
				 ret = checkRailsForSpawn(worldObj, false, x, y, z);
			 }
			 if(!ret)
			 {
				 player.addChatMessage("complete rails not found");
			 }
	
			ret = checkBlocks(worldObj, x, y, z);
			 
			if(ret&&!worldObj.isRemote)
			{
				removeBlocks(worldObj, x, y, z);
				EntityLift lift = new EntityLift(worldObj, x+0.5, y, z+0.5);
				worldObj.spawnEntityInWorld(lift);
			}
			if(!ret)
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
		int[][] sides = {{3,0},{-3,0},{0,3},{0,-3}};
		
		boolean ret = true;
		
		for(int i = 0; i<4; i++)
		{
			ret = ret&&worldObj.getBlockId(x+sides[axis?2:0][0],y+i,z+sides[axis?2:0][1])==BlockLiftRail.staticBlock.blockID;
			ret = ret&&worldObj.getBlockId(x+sides[axis?3:1][0],y+i,z+sides[axis?3:1][1])==BlockLiftRail.staticBlock.blockID;
		}
		
		return ret;
	}
	
	public boolean checkBlocks(World worldObj, int x, int y, int z)
	{
		boolean ret = true;
		for(int i = -2; i<=2;i++)
			for(int j = -2;j<=2;j++)
			{
				if(!(i==0&&j==0))
				{
					ret = ret && worldObj.getBlockId(x+i, y, z+j)==Block.blockIron.blockID;
				}
			}
		return ret;
	}
	
	public void removeBlocks(World worldObj, int x, int y, int z)
	{
		for(int i = -2; i<=2;i++)
			for(int j = -2;j<=2;j++)
			{
				System.out.println("set air"+" "+(x+i)+" "+y+" "+(z+j));
				worldObj.setBlock(x+i, y, z+j,0,0,3);
			}
		
	}

    @SideOnly(Side.CLIENT)
	public Icon icon;
    @SideOnly(Side.CLIENT)
	public Icon icon2;
    
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		blockIcon = iconRegister.registerIcon("thutconcrete:liftSpawner");
		icon = iconRegister.registerIcon("thutconcrete:liftControl");
		icon2 = iconRegister.registerIcon("thutconcrete:controlPanel");
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
			 if(par5 == te.side)
				 return icon2;
		 }
		 return this.icon;
	    }

	@Override
	public TileEntity createNewTileEntity(World world) 
	{
		return new TileEntityLiftAccess();
	}
	
    
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
    
}
