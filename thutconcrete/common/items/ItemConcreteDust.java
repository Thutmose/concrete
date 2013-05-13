package thutconcrete.common.items;

import thutconcrete.common.ConcreteCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.world.World;

public class ItemConcreteDust extends Item {
	
	public ItemConcreteDust(int par1) {
		super(par1);
		this.maxStackSize = 64;
		this.setCreativeTab(ConcreteCore.tabThut);
		this.setUnlocalizedName("Concrete Dust");
	}

	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("thutconcrete:ConcreteDust");
    }
}
