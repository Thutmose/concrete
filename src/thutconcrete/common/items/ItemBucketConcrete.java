package thutconcrete.common.items;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.BlockLiquidConcrete;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ItemBucketConcrete extends ItemBucket{

	public static ItemBucketConcrete instance;
	private int isFull;
	public ItemBucketConcrete(int par1) {
		super(par1, BlockLiquidConcrete.instance.blockID);
		isFull = BlockLiquidConcrete.instance.blockID;
		this.setCreativeTab(ConcreteCore.tabThut);
		instance = this;
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		return "Bucket of Concrete";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister par1IconRegister)
	{
		this.itemIcon = par1IconRegister.registerIcon("thutconcrete:bucket_concrete");
	}
	
	
	 /**
     * Attempts to place the liquid contained inside the bucket.
     */
    public boolean tryPlaceContainedLiquid(World par1World, double par2, double par4, double par6, int par8, int par9, int par10)
    {
        if (this.isFull <= 0)
        {
            return false;
        }
        else if (!par1World.isAirBlock(par8, par9, par10) && par1World.getBlockMaterial(par8, par9, par10).isSolid())
        {
            return false;
        }
        else
        {
          
	        par1World.setBlock(par8, par9, par10, this.isFull, 15, 3);
	        par1World.scheduleBlockUpdate(par8, par9, par10, isFull, 5);
	    
            return true;
        }
    }
	
}
