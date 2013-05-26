package thutconcrete.common.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.liquids.LiquidStack;
import thutconcrete.common.blocks.BlockLava;
import thutconcrete.common.blocks.BlockLiquidConcrete;
import thutconcrete.common.blocks.BlockRebar;
import thutconcrete.common.blocks.BlockSolidLava;
import thutconcrete.common.blocks.BlockWorldGen;
import thutconcrete.common.corehandlers.ItemHandler;

public class Items 
{
	public static Item[] items = ItemHandler.items;
	public static List<Item> itemList = ItemHandler.itemList;
	
	public static ItemStack[] brushes = ItemHandler.brushes;
	
	public static ItemStack liquidConcreteStack = new ItemStack(BlockLiquidConcrete.instance,8);
	public static ItemStack limeStack = new ItemStack(items[2]);
	public static ItemStack trassStack = new ItemStack(items[4]);
	public static ItemStack dustStack = new ItemStack(items[1]);
	public static ItemStack cementStack = new ItemStack(items[5]);
	public static ItemStack sandStack = new ItemStack(Block.sand);
	public static ItemStack gravelStack = new ItemStack(Block.gravel);
	public static ItemStack waterStack = new ItemStack(Item.bucketWater);
	public static ItemStack concreteBucketStack = new ItemStack(ItemBucketConcrete.instance);
	public static ItemStack trassOreStack = new ItemStack(BlockWorldGen.instance,1,1);
	public static ItemStack limestoneOreStack = new ItemStack(BlockWorldGen.instance,1,2);
	public static ItemStack chalkOreStack = new ItemStack(BlockWorldGen.instance,1,0);
	public static ItemStack rebarStack = new ItemStack(BlockRebar.instance,2,0);
	public static ItemStack carbonateStack = new ItemStack(items[3]);
	public static ItemStack boneMealStack = new ItemStack(Item.dyePowder,1,15);
	public static ItemStack grinderStack = new ItemStack(items[0]);

	public static ItemStack solidLava0Stack = new ItemStack(BlockSolidLava.getInstance(0),1,0);
	public static ItemStack solidLava1Stack = new ItemStack(BlockSolidLava.getInstance(1),1,0);
	public static ItemStack solidLava2Stack = new ItemStack(BlockSolidLava.getInstance(2),1,0);

	
	public static Item lime = items[2];
	public static Item trass = items[4];
	public static Item dust = items[1];
	public static Item cement = items[5];
	public static Item carbonate = items[3];
	public static Item grinder = items[0];

	
	public static LiquidStack lava0 = new LiquidStack(BlockLava.getInstance(0),1);
	public static LiquidStack lava1 = new LiquidStack(BlockLava.getInstance(1),1);
	public static LiquidStack lava2 = new LiquidStack(BlockLava.getInstance(2),1);
	
	public static LiquidStack concrete = new LiquidStack(BlockLiquidConcrete.instance,1);
}
