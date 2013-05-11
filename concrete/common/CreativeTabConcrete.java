package concrete.common;

import concrete.common.BlocksItems.BlockRebar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;

public class CreativeTabConcrete extends CreativeTabs{

	public CreativeTabConcrete() {
		super("tabConcrete");
	}

	@SideOnly(Side.CLIENT)
	public int getTabIconItemIndex(){
        return BlockRebar.instance.blockID;
    }

	@SideOnly(Side.CLIENT)
    public String getTabLabel(){
        return "Concrete";
    }

	@SideOnly(Side.CLIENT)
    public String getTranslatedTabLabel(){
        return this.getTabLabel();
    }
}
