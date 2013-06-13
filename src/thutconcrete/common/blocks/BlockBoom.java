package thutconcrete.common.blocks;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import thutconcrete.client.render.BlockRenderHandler;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.corehandlers.ConfigHandler;
import thutconcrete.common.entity.*;
import thutconcrete.common.tileentity.TileEntityLaser;
import thutconcrete.common.utils.ExplosionCustom;
import thutconcrete.common.utils.LinearAlgebra;
import thutconcrete.common.utils.ThreadSafeWorldOperations;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBook;
import net.minecraft.item.ItemCoal;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockBoom extends Block //implements ITileEntityProvider
{

	public static BlockBoom instance;
	ExplosionCustom boom = new ExplosionCustom();
	ThreadSafeWorldOperations safe = new ThreadSafeWorldOperations();
	
	public BlockBoom(int par1) {
		super(par1, Material.rock);
		
		if(ConfigHandler.debugPrints)
		this.setCreativeTab(ConcreteCore.tabThut);
		this.setResistance(10);
		this.setUnlocalizedName("uGoBoom");
		instance = this;
	}
	 
	 
    public boolean onBlockActivated(World worldObj, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9)
    {
    	ItemStack item = player.getHeldItem();
    	int meta = safe.safeGetMeta(worldObj,x,y,z);
    	if(item!=null&&item.getItem() instanceof ItemDye)
    	{
	    	int meta1 = (15-item.getItemDamage())+1;
	    	boom.doExplosion(worldObj, x, y, z, Math.min(2*meta1*meta1,512), true);
	    	return true;
    	}
    	else if(!worldObj.isRemote&&item!=null&&item.getItem() instanceof ItemCoal)
    	{
    		EntityRocket rocket = new EntityRocket(worldObj, x,y+5,z);
    		worldObj.spawnEntityInWorld(rocket);
    	}
    	else if(!worldObj.isRemote&&item!=null&&item.getItem() instanceof ItemSoup)
    	{
    		EntityTurret turret = new EntityTurret(worldObj, x+0.5, y+1.0, z+0.5);
    		worldObj.spawnEntityInWorld(turret);
    	}
    	else if(!worldObj.isRemote&&item!=null&&item.getItem() instanceof ItemBook)
    	{
    		EntityLift turret = new EntityLift(worldObj, x+0.5, y+2.0, z+0.5);
    		worldObj.spawnEntityInWorld(turret);
    	}
        return false;
        
    }
	 
    
    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:" + this.getUnlocalizedName2());
    }
	
}
