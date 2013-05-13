package thutconcrete.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.world.World;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.Block16Fluid;

public class ItemTrowel extends Item {

	public static final int MAX_USES = 128;
	
	public ItemTrowel(int par1) {
		super(par1);
		this.maxStackSize = 1;
		this.setMaxDamage(MAX_USES);
		this.setCreativeTab(ConcreteCore.tabThut);
		this.setUnlocalizedName("Smoother Thingy");
	}

    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if (!par2EntityPlayer.canPlayerEdit(x, y, z, side, par1ItemStack))
        {
            return false;
        }
        else
        {
        	int blockid;
        	
        	int minmeta = 30;
        	int maxmeta = 0;
        	
        	for(int locx = -1; locx < 2; locx++)
        	{
        		for(int locz = -1; locz < 2; locz++)
        		{
        			blockid = par3World.getBlockId(x + locx, y, z + locz);
        			if(Block.blocksList[blockid] instanceof Block16Fluid)
        			{
        				if(par3World.getBlockMetadata(x + locx, y, z + locz) < minmeta)
        				{
        					minmeta = par3World.getBlockMetadata(x + locx, y, z + locz);
        				}
        			}
        		}
        	}
        	
        	int modifyx = 0;
        	int modifyz = 0;
        	
        	for(int i = 0; i < 9; i++)
        	{
        		maxmeta = 0;
        		
            	for(int locx = -1; locx < 2; locx++)
            	{
            		for(int locz = -1; locz < 2; locz++)
            		{
            			blockid = par3World.getBlockId(x + locx, y, z + locz);
            			if(Block.blocksList[blockid] instanceof Block16Fluid)
            			{
            				if(par3World.getBlockMetadata(x + locx, y, z + locz) > maxmeta)
            				{
            					modifyx = x + locx;
            					modifyz = z + locz;
            					maxmeta = par3World.getBlockMetadata(x + locx, y, z + locz);
            				}
            			}
            		}
            	}
            	
            	if(MAX_USES - par1ItemStack.getItemDamage() >= maxmeta - minmeta)
            	{
            		par3World.setBlockMetadataWithNotify(modifyx, y, modifyz, minmeta, 3);
            		par1ItemStack.damageItem(maxmeta - minmeta, par2EntityPlayer);
            	}
            	else
            	{
            		par3World.setBlockMetadataWithNotify(modifyx, y, modifyz, MAX_USES - par1ItemStack.getItemDamage(), 3);
            		par1ItemStack.damageItem(MAX_USES - par1ItemStack.getItemDamage() + 1, par2EntityPlayer);
            		return true;
            	}
        	}
        	
        	return true;
        }
    }
    
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("thutconcrete:ItemSmoother");
    }
}
