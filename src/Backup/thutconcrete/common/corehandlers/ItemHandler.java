package thutconcrete.common.corehandlers;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thutconcrete.common.blocks.BlockWorldGen;
import thutconcrete.common.items.*;

public class ItemHandler {

	// For shorter referencing to the config handler
	private ConfigHandler config;

	// Empty fields for holding items
	public static Item[] items;
	public static List<Item> itemList = new ArrayList<Item>();

	public ItemHandler(ConfigHandler handler){
		config = handler;
		// Initalizes all mod items
		initItems();
	}

	public void initItems(){
		int id = config.IDItem;
	//	itemList.add(new ItemWorldGenBlock(id++));
		itemList.add(new ItemTrowel(id++));
		itemList.add(new ItemConcreteDust(id++));
		
		items = itemList.toArray(new Item[0]);

		Item item = new ItemWorldGenBlock(id++);
		
		for(int i = 0; i<BlockWorldGen.MAX_META; i++){
			ItemStack stack = new ItemStack(item,1,i);
			LanguageRegistry.addName(stack, item.getUnlocalizedName(stack));
		}
		registerItems();
	}
	
	public void registerItems(){
		for(Item item: items){
			GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
			LanguageRegistry.addName(item, item.getUnlocalizedName().substring(5));
		}
	}
}
