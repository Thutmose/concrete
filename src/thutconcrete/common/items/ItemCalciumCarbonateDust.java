package thutconcrete.common.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.entity.EntityBeam;
import thutconcrete.common.network.TCPacket;
import thutconcrete.common.utils.ExplosionCustom;
import thutconcrete.common.utils.ExplosionCustom.Cruncher;
import thutconcrete.common.utils.Vector3;
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

}