package thutconcrete.common.corehandlers;

import net.minecraft.item.Item;

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
		};
	}
}
