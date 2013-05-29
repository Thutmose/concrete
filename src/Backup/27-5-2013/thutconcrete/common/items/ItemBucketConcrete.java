package thutconcrete.common.items;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.Block16Fluid;
import thutconcrete.common.blocks.BlockLiquidConcrete;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

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
    public boolean tryPlaceContainedLiquid(World worldObj, double par2, double par4, double par6, int x, int y, int z)
    {
        if (this.isFull <= 0)
        {
            return false;
        }
        else
        {
			int id = worldObj.getBlockId(x, y, z);
			Block b = Block.blocksList[id];
			if(id==0||worldObj.getBlockMaterial(x, y, z).isReplaceable())
			{
				worldObj.setBlock(x, y, z, BlockLiquidConcrete.instance.blockID, 0, 3);
				return true;
			}
	    	if(b instanceof Block16Fluid)
	    	{
		    	int meta = worldObj.getBlockMetadata(x, y, z);
	    		Block16Fluid block = (Block16Fluid)b;
		    	if(meta!=0&&block.willCombine(BlockLiquidConcrete.instance.blockID, id))
		    	{
		    		return placedStack(worldObj,x, y, z, ForgeDirection.getOrientation(1), block);
		    	}
	    	}
        	return false;
        	
        }
    }
    
    public boolean placedStack(World worldObj, int x, int y, int z, ForgeDirection side, Block16Fluid block)
    {
    	int id = worldObj.getBlockId(x, y, z);
    	int id1 = worldObj.getBlockId(x+side.offsetX,y+side.offsetY, z+side.offsetZ);
    	
    	int itemID = block.blockID;
    	
    	int meta = worldObj.getBlockMetadata(x, y, z);
    	
    	int meta1 = worldObj.getBlockMetadata(x+side.offsetX,y+side.offsetY, z+side.offsetZ);
    	int placementamount = block.placeamount;
    	
    	int initialamount = 16-meta;
    	
    	int newMeta = 16-(placementamount + initialamount);
    	

    	int remainder = (placementamount - (16-meta));
    	
    	Block block1 = Block.blocksList[id1];
    	
    	if(id1==0||block1.blockMaterial.isReplaceable())
    	{
    		worldObj.setBlock(x,y, z, block.getCombineID(worldObj, id, itemID), Math.max(newMeta,0), 3);
    		if(newMeta<0)
    		worldObj.setBlock(x+side.offsetX,y+side.offsetY, z+side.offsetZ, itemID, remainder, 3);
    		return true;
    	}
    	
    	return false;
    }
	
}
