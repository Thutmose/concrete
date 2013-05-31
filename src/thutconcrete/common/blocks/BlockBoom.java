package thutconcrete.common.blocks;

import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.tileentity.TileEntityBlock16Fluid;
import thutconcrete.common.tileentity.TileEntityVolcano;
import thutconcrete.common.utils.ExplosionCustom;
import thutconcrete.common.utils.LinearAlgebra;
import thutconcrete.common.utils.ThreadSafeWorldOperations;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockBoom extends Block implements ITileEntityProvider
{

	public static BlockBoom instance;
	double[] pointing = {1, 0, 0};
	ExplosionCustom boom = new ExplosionCustom();
	ThreadSafeWorldOperations safe = new ThreadSafeWorldOperations();
	
	public BlockBoom(int par1) {
		super(par1, Material.rock);
		this.setCreativeTab(ConcreteCore.tabThut);
		//this.setTickRandomly(true);
		this.setUnlocalizedName("uGoBoom");
		instance = this;
		this.setBlockBounds(0, 0, 0, 1, 1, 1);
	}
	
	 public void onBlockAdded(World worldObj, int par2, int par3, int par4)  
	 { 
	 }

    public boolean onBlockActivated(World worldObj, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9)
    {
    	ItemStack item = player.getHeldItem();
    	int meta = safe.safeGetMeta(worldObj,x,y,z);
    	if(item!=null)
    	{
	    	int meta1 = (15-item.getItemDamage())+1;
	    	boom.doExplosion(worldObj, x, y, z, Math.min(2*meta1*meta1,512), true);
    	}
        return false;
        
    }
	 
    @Override
    public void addCollisionBoxesToList(World worldObj, int x, int y, int z, AxisAlignedBB aaBB, List list, Entity par7Entity)
    {
    	
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

	@Override
	public TileEntity createNewTileEntity(World world) {
		// TODO Auto-generated method stub
		return new TileEntityBlock16Fluid(true);
	}
	
}
