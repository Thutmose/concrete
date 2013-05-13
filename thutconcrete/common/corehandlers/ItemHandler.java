package thutconcrete.common.corehandlers;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.item.Item;
import thutconcrete.common.items.ItemTrowel;

public class ItemHandler {

	// For shorter referencing to the config handler
	private ConfigHandler config;

	// Empty fields for holding items
	public static Item[] items;

	public ItemHandler(ConfigHandler handler){
		config = handler;
		// Initalizes all mod items
		initItems();
	}

	public void initItems(){
		int id = config.IDItem;
		items = new Item[]{
	//			new ItemRebar(id++),
				new ItemTrowel(id++),
		};
		
		registerItems();
	}
	
	public void registerItems(){
		for(Item item : items){
			GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
			LanguageRegistry.addName(item, item.getUnlocalizedName().substring(5));
		}
	}
}
