package thutconcrete.common.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.tileEntities.TileEntityVolcano;
import thutconcrete.common.utils.ExplosionCustom;
import thutconcrete.common.utils.ThreadSafeWorldOperations;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockBoom extends Block
{

	public static BlockBoom instance;
	
	ExplosionCustom boom = new ExplosionCustom();
	ThreadSafeWorldOperations safe = new ThreadSafeWorldOperations();
	
	public BlockBoom(int par1) {
		super(par1, Material.rock);
		this.setTickRandomly(true);
		this.setCreativeTab(ConcreteCore.tabThut);
		this.setUnlocalizedName("uGoBoom");
		instance = this;
		this.setBlockBounds(0, 0, 0, 1, 1, 1);
	}
	
	 public void onBlockAdded(World worldObj, int par2, int par3, int par4)  { }

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
	 
	 
	public void updateTick(World worldObj, int xCoord, int yCoord, int zCoord, Random par5Random)
	{
		worldObj.setBlockToAir(xCoord, yCoord, zCoord);
	}
	
    public boolean isAirBlock(World world, int x, int y, int z)
    {
        return true;
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
