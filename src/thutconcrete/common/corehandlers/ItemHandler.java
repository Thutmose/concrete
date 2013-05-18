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
import thutconcrete.common.blocks.BlockWorldGen;
import thutconcrete.common.items.*;

public class ItemHandler {

	// For shorter referencing to the config handler
	private ConfigHandler config;

	// Empty fields for holding items
	public static Item[] items;
	public static List<Item> itemList = new ArrayList<Item>();
	
	public static final String[] names = 
		{
			"Grinder",
			"Concrete Dust",
			"Quick Lime",
			"Calcium Carbonate",
			"Trass Dust",
			"Cement",
			"Bucket of Concrete",
			"Chalk",
	    	"lava",
	    	"Trass",
	    	"Limestone",
		};
	
	private final static String[] genNames = {
		"Chalk",
    	"lava",
    	"Trass",
    	"Limestone",
    };

	public ItemHandler(ConfigHandler handler){
		config = handler;
		// Initalizes all mod items
		initItems();
	}

	public void initItems(){
		int id = config.IDItem;
		
		itemList.add(new ItemTrowel(id++));
		itemList.add(new ItemConcreteDust(id++));
		itemList.add(new ItemQuickLimeDust(id++));
		itemList.add(new ItemCalciumCarbonateDust(id++));
		itemList.add(new ItemTrassDust(id++));
		itemList.add(new ItemCement(id++));
		itemList.add(new ItemBucketConcrete(id++));
		
		items = itemList.toArray(new Item[0]);

		registerItems();
		registerNames();
		
		Item item = new ItemWorldGenBlock(BlockWorldGen.instance.blockID-256);
		
		for(int i = 0;i<4;i++)
		{
			ItemStack stack = new ItemStack(item, 1, i);
			LanguageRegistry.addName(stack, genNames[i]);
		}
		
		registerRecipes();
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
	
	public void registerRecipes(){
		
		OreDictionary.registerOre("dustConcrete", items[1]);
		OreDictionary.registerOre("dustCa(OH)2", items[2]);
		OreDictionary.registerOre("dustCaCO3", items[3]);
		OreDictionary.registerOre("dustRock", items[4]);
		OreDictionary.registerOre("dustCement", items[5]);
		
		ItemStack liquidConcrete = new ItemStack(BlockLiquidConcrete.instance,8);
		ItemStack lime = new ItemStack(items[2]);
		ItemStack trass = new ItemStack(items[4]);
		ItemStack dust = new ItemStack(items[1]);
		ItemStack cement = new ItemStack(items[5]);
		ItemStack sand = new ItemStack(Block.sand);
		ItemStack gravel = new ItemStack(Block.gravel);
		ItemStack water = new ItemStack(Item.bucketWater);
		ItemStack trassOre = new ItemStack(BlockWorldGen.instance,1,2);
		ItemStack limestoneOre = new ItemStack(BlockWorldGen.instance,1,3);
		ItemStack chalkOre = new ItemStack(BlockWorldGen.instance,1,0);
		ItemStack rebar = new ItemStack(BlockRebar.instance);
		ItemStack carbonate = new ItemStack(items[3]);
		ItemStack boneMeal = new ItemStack(Item.dyePowder,1,15);
		
		GameRegistry.addShapelessRecipe(liquidConcrete,cement, gravel, gravel, gravel, gravel, sand, sand, sand, water);

		GameRegistry.addShapelessRecipe(cement, lime, trass);
		GameRegistry.addShapelessRecipe(cement, lime, dust, dust, dust, dust, dust, dust, dust, dust);

		GameRegistry.addShapelessRecipe(trass, trassOre);
		
		GameRegistry.addShapelessRecipe(carbonate, chalkOre);
		GameRegistry.addShapelessRecipe(carbonate, limestoneOre);
		GameRegistry.addShapelessRecipe(carbonate, boneMeal);
		
		GameRegistry.addShapelessRecipe(new ItemStack(BlockLiquidREConcrete.instance), liquidConcrete, rebar);
		
		GameRegistry.addRecipe(rebar,"x  "," x ","  x", 'x', Item.ingotIron);
		
		
		
		GameRegistry.addSmelting(items[3].itemID, lime, 0);
	//	FurnaceRecipes.smelting().addSmelting(items[3].itemID, lime, 0);
		
		
	}
}
