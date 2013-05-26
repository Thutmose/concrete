package thutconcrete.common.corehandlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidContainerData;
import net.minecraftforge.liquids.LiquidContainerRegistry;
import net.minecraftforge.liquids.LiquidDictionary;
import net.minecraftforge.oredict.OreDictionary;
import thutconcrete.common.blocks.BlockConcrete;
import thutconcrete.common.blocks.BlockLava;
import thutconcrete.common.blocks.BlockLiquidConcrete;
import thutconcrete.common.blocks.BlockLiquidREConcrete;
import thutconcrete.common.blocks.BlockRebar;
import thutconcrete.common.blocks.BlockSolidLava;
import thutconcrete.common.blocks.BlockWorldGen;
import thutconcrete.common.items.Items;
import cpw.mods.fml.common.registry.GameRegistry;

public class RecipeHandler
{
	
	// Empty fields for holding items
	public static Item[] items = ItemHandler.items;
	public static List<Item> itemList = ItemHandler.itemList;
	
	public static ItemStack[] brushes = ItemHandler.brushes;

	private static final String[] dyeNames = { "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite" };

	public RecipeHandler(ConfigHandler config) {
		registerOres();
	}
	
	public void registerRecipes()
	{
		registerSpecialRecipes();
		registerShapedRecipes();
		registerShapeless();
	}
	
	public void registerSpecialRecipes()
	{

		GameRegistry.addSmelting(Items.carbonate.itemID, Items.limeStack, 0);
	}

	public void registerShapedRecipes()
	{
		
		GameRegistry.addRecipe(Items.rebarStack,"x  "," x ","  x", 'x', Item.ingotIron);
		
		for (ItemStack steel : OreDictionary.getOres("ingotSteel")) 
		{
			GameRegistry.addRecipe(new ItemStack(BlockRebar.instance,8),"x  "," x ","  x", 'x', steel);
		}
		
		for (ItemStack steel : OreDictionary.getOres("ingotRefinedIron")) 
		{
			GameRegistry.addRecipe(new ItemStack(BlockRebar.instance,4),"x  "," x ","  x", 'x', steel);
		}
		
		for(int i = 0; i<16; i++)
		{
			GameRegistry.addRecipe(brushes[16].copy()," x "," y "," z ", 'x',new ItemStack(Block.cloth,1,i), 'y', Item.ingotIron, 'z', Item.stick);
		}
		
	}
	
	public void registerOres()
	{
		OreDictionary.registerOre("dustRockTiny", items[1]);
		OreDictionary.registerOre("dustCa(OH)2", items[2]);
		OreDictionary.registerOre("dustCaCO3", items[3]);
		OreDictionary.registerOre("dustCalciumHydroxide", items[2]);
		OreDictionary.registerOre("dustCalciumCarbonate", items[3]);
		OreDictionary.registerOre("dustRock", items[4]);
		OreDictionary.registerOre("fertilizer", items[1]);
		OreDictionary.registerOre("fertilizer", items[4]);
		OreDictionary.registerOre("dustCement", items[5]);
		OreDictionary.registerOre("oreChalk",new ItemStack(BlockWorldGen.instance,1,0));
		OreDictionary.registerOre("oreTrass",new ItemStack(BlockWorldGen.instance,1,1));
		OreDictionary.registerOre("oreLimestone",new ItemStack(BlockWorldGen.instance,1,2));
		
		LiquidDictionary.getOrCreateLiquid("mafic lava", Items.lava0);
		LiquidDictionary.getOrCreateLiquid("intermediate lava", Items.lava1);
		LiquidDictionary.getOrCreateLiquid("felsic lava", Items.lava2);
		LiquidDictionary.getOrCreateLiquid("concrete", Items.concrete);

		//*
		Items.lava0.canonical().setRenderingIcon(BlockLava.instances[0].getIcon(0, 0));
		Items.lava1.canonical().setRenderingIcon(BlockLava.instances[1].getIcon(0, 0));
		Items.lava2.canonical().setRenderingIcon(BlockLava.instances[2].getIcon(0, 0));
		Items.concrete.canonical().setRenderingIcon(BlockConcrete.instance.getIcon(0, 0));
		//*/
		//*
		LiquidContainerRegistry.registerLiquid(new LiquidContainerData(LiquidDictionary.getLiquid("mafic lava", LiquidContainerRegistry.BUCKET_VOLUME), new ItemStack(
				Item.bucketLava), new ItemStack(Item.bucketEmpty)));
		LiquidContainerRegistry.registerLiquid(new LiquidContainerData(LiquidDictionary.getLiquid("intermediate lava", LiquidContainerRegistry.BUCKET_VOLUME),
				new ItemStack(Item.bucketLava), new ItemStack(Item.bucketEmpty)));
		LiquidContainerRegistry.registerLiquid(new LiquidContainerData(LiquidDictionary.getLiquid("felsic lava", LiquidContainerRegistry.BUCKET_VOLUME),
				new ItemStack(Item.bucketLava), new ItemStack(Item.bucketEmpty)));
		LiquidContainerRegistry.registerLiquid(new LiquidContainerData(LiquidDictionary.getLiquid("concrete", LiquidContainerRegistry.BUCKET_VOLUME),
				Items.concreteBucketStack.copy(), new ItemStack(Item.bucketEmpty)));
		//*/
	}
	
	
	public void registerShapeless()
	{
		GameRegistry.addShapelessRecipe(Items.liquidConcreteStack,Items.cementStack, Items.gravelStack, Items.gravelStack, Items.gravelStack, Items.gravelStack, Items.sandStack, Items.sandStack, Items.sandStack, Items.waterStack);

		GameRegistry.addShapelessRecipe(Items.cementStack, Items.limeStack, Items.trassStack);
		GameRegistry.addShapelessRecipe(Items.cementStack, Items.limeStack, Items.dustStack, Items.dustStack, Items.dustStack, Items.dustStack, Items.dustStack, Items.dustStack, Items.dustStack, Items.dustStack);
		
		GameRegistry.addShapelessRecipe(Items.trassStack, Items.trassOreStack);
		GameRegistry.addShapelessRecipe(Items.dustStack, Items.solidLava0Stack);
		GameRegistry.addShapelessRecipe(Items.dustStack, Items.solidLava1Stack);
		GameRegistry.addShapelessRecipe(Items.dustStack, Items.solidLava2Stack);
		
		GameRegistry.addShapelessRecipe(Items.grinderStack, new ItemStack(Block.obsidian));
		
		GameRegistry.addShapelessRecipe(Items.carbonateStack, Items.chalkOreStack);
		GameRegistry.addShapelessRecipe(Items.carbonateStack, Items.limestoneOreStack);
		GameRegistry.addShapelessRecipe(Items.carbonateStack, Items.boneMealStack);
		
		
		for(int i = 0; i<16; i++)
		{
			for (ItemStack dye : OreDictionary.getOres(dyeNames[i])) 
			{
				for(ItemStack brush : brushes)
				{
					GameRegistry.addShapelessRecipe(brushes[i].copy(), brush.copy(), dye.copy() );
				}
			}
		}
	}
	
}
