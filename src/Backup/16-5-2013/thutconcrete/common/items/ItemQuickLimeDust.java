package thutconcrete.common.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import thutconcrete.common.ConcreteCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemQuickLimeDust extends Item {
	
	public ItemQuickLimeDust(int par1) {
		super(par1);
		this.maxStackSize = 64;
		this.setCreativeTab(ConcreteCore.tabThut);
		this.setUnlocalizedName("dustCa(OH)2");
	}

	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("thutconcrete:dustCa(OH)2");
    }
}