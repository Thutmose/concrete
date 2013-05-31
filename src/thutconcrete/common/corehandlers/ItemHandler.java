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
	
	public static final String[] names = 
		{
			"Grinder",
			"Dust",
			"Quick Lime",
			"Calcium Carbonate",
			"Trass Dust",
			"Cement",
			"Bucket of Concrete",
			"Stamper",
		};
	
	private final static String[] genNames = {
		"Chalk",
    	"Trass",
    	"Limestone",
    };
	
	private static final String[] dyeNames = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite" };
	 
	
	private final static String[] colourNames = { 
		"Black",
		"Red", 
		"Green",
		"Brown", 
		"Blue",
		"Purple",
		"Cyan",
		"Light Gray",
		"Gray",
		"Pink",
		"Lime",
		"Yellow",
		"Light Blue",
		"Magenta",
		"Orange",
		"White", 
		"",
		};


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
		
		items = itemList.toArray(new Item[0]);

		registerItems();
		registerNames();
		
		Item item = new ItemWorldGenBlock(BlockWorldGen.instance.blockID-256);
		
		for(int i = 0;i<genNames.length;i++)
		{
			ItemStack stack = new ItemStack(item, 1, i);
			LanguageRegistry.addName(stack, genNames[i]);
		}
		
		for(int i = 0; i<17;i++)
		{
			Item painter = new ItemPaintBrush(id++, i);
			ItemStack stack = new ItemStack(painter, 1,0);
			LanguageRegistry.addName(stack, colourNames[i]+" Brush");
			brushes[i] = stack;
		}
		ItemPaintBrush.emptyBrushID = id;
	}
	
	public void registerItems(){
		for(Item item: items){
			GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
		}
	}
	public void registerNames(){
		int n = 0;
		for(Item item: items){
			LanguageRegistry.addName(item, names[n]);
			n++;
		}
	}
	
}
