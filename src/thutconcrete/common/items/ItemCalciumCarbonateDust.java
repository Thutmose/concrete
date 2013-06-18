package thutconcrete.common.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.entity.EntityBeam;
import thutconcrete.common.network.TCPacket;
import thutconcrete.common.utils.ExplosionCustom;
import thutconcrete.common.utils.ExplosionCustom.Cruncher;
import thutconcrete.common.utils.Vector3;
import cpw.mods.fml.common.network.FMLPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCalciumCarbonateDust extends Item {
	
	public ItemCalciumCarbonateDust(int par1) {
		super(par1);
		this.maxStackSize = 64;
		this.setCreativeTab(ConcreteCore.tabThut);
		this.setUnlocalizedName("dustCaCO3");
	}

	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("thutconcrete:dustCaCO3");
    }

	
    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     * /
    public ItemStack onItemRightClick(ItemStack itemstack, World worldObj, EntityPlayer player)
    {
    	boolean ret = false;
    	
    	for(Item item: Item.itemsList)
    	{
    		if(item!=null&&item.getUnlocalizedName()!=null)
    		{
	    		ret = ret||item.getUnlocalizedName().toLowerCase().contains("greenorb");
	    		if(ret)
	    			break;
    		}
    	}
    	
    	if(ret&&!worldObj.isRemote)
    	{
    		EntityRayquaza mob = new EntityRayquaza(worldObj);
    		mob.setPosition(player.posX, player.posY+2, player.posZ);
    		mob.setEntityHealth(10);
    		worldObj.spawnEntityInWorld(mob);
    	}
    	
    	return itemstack;
    }
	//*/
}