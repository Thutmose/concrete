package thutconcrete.common.corehandlers;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;
import thutconcrete.common.blocks.BlockLift;
import thutconcrete.common.blocks.BlockLiquidConcrete;
import thutconcrete.common.blocks.BlockLiquidREConcrete;
import thutconcrete.common.blocks.BlockRebar;
import thutconcrete.common.blocks.BlockSolidLava;
import thutconcrete.common.blocks.BlockWorldGen;
import thutconcrete.common.items.*;

public class ItemHandler {

	// For shorter referencing to the config handler
	private ConfigHandler config;

	// Empty fields for holding items
	public static Item[] items;
	public static List<Item> itemList = new ArrayList<Item>();
	
	public static ItemStack[] brushes = new ItemStack[17];
	
	public ItemHandler(ConfigHandler handler){
		config = handler;
		// Initalizes all mod items
		initItems();
	}

	public void initItems(){
		int id = config.IDItem;
		
		itemList.add(new ItemGrinder(id++));
		itemList.add(new ItemConcreteDust(id++));
		itemList.add(new ItemQuickLimeDust(id++));
		itemList.add(new ItemCalciumCarbonateDust(id++));
		itemList.add(new ItemTrassDust(id++));
		itemList.add(new ItemCement(id++));
		itemList.add(new ItemBucketConcrete(id++));
		itemList.add(new ItemStamper(id++));
		itemList.add(new ItemLiftController(id++));
		
		items = itemList.toArray(new Item[0]);

		registerItems();
		
		for(int i = 0; i<17;i++)
		{
			Item painter = new ItemPaintBrush(id++, i);
			ItemStack stack = new ItemStack(painter, 1,0);
			brushes[i] = stack;
		}
		ItemPaintBrush.emptyBrushID = id;
		itemList.add(new ItemSeismicLinker(id++));
	}
	
	public void registerItems(){
		for(Item item: items){
			GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
		}
	}
	
}
