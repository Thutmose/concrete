package thutconcrete.common.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import thutconcrete.common.ConcreteCore;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLiftBlocks extends ItemBlock{
	
	private final static String[] names = {
		"liftSpawner",
    	"liftController",
    };
	public static ItemBlock instance;
	
	
	public ItemLiftBlocks(int par1)
    {
        super(par1);
    //    this.setMaxDamage(0);
        this.setHasSubtypes(true);
		this.setUnlocalizedName("Block");
		this.setCreativeTab(ConcreteCore.tabThut);
		instance = this;
    }
	
	@Override
	public int getMetadata (int damageValue) {
		return damageValue;
	}
	
	   /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        int i = par1ItemStack.getItemDamage()%names.length;
        return super.getUnlocalizedName() + "." + names[i];
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int j = 0; j < names.length; ++j)
        {
            par3List.add(new ItemStack(par1, 1, j));
        }
    }


}
