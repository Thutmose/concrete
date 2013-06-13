package thutconcrete.common.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.corehandlers.ConfigHandler;
import thutconcrete.common.tileentity.TileEntityLimekiln;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockLimekiln extends Block implements ITileEntityProvider
{
	private Icon faceIconUnlit;
	private Icon faceIconLit;
	
	public static BlockLimekiln instance;
	
	
	public BlockLimekiln(int par1) {
		super(par1, Material.rock);
		
		setUnlocalizedName("blockLimekiln");
		setStepSound(Block.soundStoneFootstep);
		setHardness(3.5f);
		setCreativeTab(ConcreteCore.tabThut);
		instance = this;
	}
	
	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z)
	{
		return (world.getBlockMetadata(x, y, z) == 0 ? 0 : 15); 
	}
	
	 public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	    {
	        if(player.isSneaking())
	            return false;
	         
	        TileEntityLimekiln tileEntity = (TileEntityLimekiln)world.getBlockTileEntity(x, y, z);
	         
	        if(tileEntity != null)
	        {
	        	if(tileEntity.facing!=ForgeDirection.getOrientation(par6).getOpposite())
	        	{
	        		tileEntity.facing=ForgeDirection.getOrientation(par6).getOpposite();
	        	}

	            if(!tileEntity.getIsValid())
	            {
	                if(tileEntity.checkIfProperlyFormed())
	                {
	                    tileEntity.convertDummies();
	                }
	            }
	            // Check if the multi-block structure has been formed.
	            if(tileEntity.getIsValid())
	                player.openGui(ConcreteCore.instance, ConfigHandler.GUIIDs.limekiln, world, x, y, z);
	        }
	         
	        return true;
	    }
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		blockIcon = iconRegister.registerIcon("thutconcrete:brick");
		faceIconUnlit = iconRegister.registerIcon("thutconcrete:Kiln_Front_Unlit");
		faceIconLit = iconRegister.registerIcon("thutconcrete:Kiln_Front_Lit");
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity, ItemStack itemStack)
	{
		TileEntityLimekiln te = (TileEntityLimekiln)world.getBlockTileEntity(x, y, z);
		ForgeDirection side =  getFacingfromEntity(entity);
		if(te==null)
		{
			te = new TileEntityLimekiln();
			world.setBlockTileEntity(x, y, z, te);
		}
		
		te.facing = side;
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
	
	 @SideOnly(Side.CLIENT)
    /**
     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
     */
    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int side)
    {
		 ForgeDirection dir = ForgeDirection.getOrientation(side);
		 int meta = par1IBlockAccess.getBlockMetadata(x, y, z);
		 
		return side>1?meta>0? faceIconLit:faceIconUnlit:blockIcon;
    }

    @SideOnly(Side.CLIENT)

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    public void randomDisplayTick(World par1World, int x, int y, int z, Random par5Random)
    {
		boolean isActive = par1World.getBlockMetadata(x, y, z) >0;
		TileEntityLimekiln te = (TileEntityLimekiln)par1World.getBlockTileEntity(x, y, z);
        if (isActive)
        {
            int l = te.facing.ordinal()-2;
            float f = (float)x + 0.5F;
            float f1 = (float)y + 0.0F + par5Random.nextFloat() * 6.0F / 16.0F;
            float f2 = (float)z + 0.5F;
            float f3 = 0.52F;
            float f4 = par5Random.nextFloat() * 0.6F - 0.3F;

         //   if (l == 4)
            {
                par1World.spawnParticle("smoke", (double)(f - f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
                par1World.spawnParticle("flame", (double)(f - f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
            }
        //    else if (l == 5)
            {
                par1World.spawnParticle("smoke", (double)(f + f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
                par1World.spawnParticle("flame", (double)(f + f3), (double)f1, (double)(f2 + f4), 0.0D, 0.0D, 0.0D);
            }
       //     else if (l == 2)
            {
                par1World.spawnParticle("smoke", (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0D, 0.0D, 0.0D);
                par1World.spawnParticle("flame", (double)(f + f4), (double)f1, (double)(f2 - f3), 0.0D, 0.0D, 0.0D);
            }
        //    else if (l == 3)
            {
                par1World.spawnParticle("smoke", (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0D, 0.0D, 0.0D);
                par1World.spawnParticle("flame", (double)(f + f4), (double)f1, (double)(f2 + f3), 0.0D, 0.0D, 0.0D);
            }
        }
    }
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return  new TileEntityLimekiln();
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6)
	{
		TileEntityLimekiln tileEntity = (TileEntityLimekiln)world.getBlockTileEntity(x, y, z);
		
		if(tileEntity != null)
			tileEntity.invalidateMultiblock();
		
		dropItems(world, x, y, z);
		super.breakBlock(world, x, y, z, par5, par6);
	}
	
	private static int getSideFromFacing(int facing)
	{
		return ForgeDirection.getOrientation(facing+2).ordinal();
	}
	
	private void dropItems(World world, int x, int y, int z)
	{
		Random prng = new Random();
		
		TileEntityLimekiln tileEntity = (TileEntityLimekiln)world.getBlockTileEntity(x, y, z);
		if(tileEntity == null)
			return;
		
		for(int slot = 0; slot < tileEntity.getSizeInventory(); slot++)
		{
			ItemStack item = tileEntity.getStackInSlot(slot);
			
			if(item != null && item.stackSize > 0)
			{
				float rx = prng.nextFloat() * 0.8f + 0.1f;
				float ry = prng.nextFloat() * 0.8f + 0.1f;
				float rz = prng.nextFloat() * 0.8f + 0.1f;
				
				EntityItem entityItem = new EntityItem(world, x + rx, y + ry, z + rz, item.copy());
				world.spawnEntityInWorld(entityItem);
				item.stackSize = 0;
			}
		}
	}

}
