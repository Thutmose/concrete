package thutconcrete.common.corehandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.world.World;
import thutconcrete.common.blocks.*;
import thutconcrete.common.items.ItemWorldGenBlock;

public class BlockHandler {

	private ConfigHandler config;

	
	
	public static Block[] blocks;
	public static Map<Block, ItemBlock> itemBlocks = new HashMap<Block, ItemBlock>();
	private static List<Block> blockList = new ArrayList<Block>();

	public BlockHandler(ConfigHandler configHandler){
		config = configHandler;
		initBlocks();
	}

	public void initBlocks(){
		
		int id = config.IDBlock;
		int idWorld = config.IDWorldBlock;
		blockList.add(new BlockDust(id++));
		blockList.add(new BlockRebar(id++));
		blockList.add(new BlockFullSolidREConcrete(id++));
		
		blockList.add(new BlockREConcrete(id++));
		blockList.add(new BlockLiquidREConcrete(id++));
		

		blockList.add(new BlockConcrete(id++));
		blockList.add(new BlockLiquidConcrete(id++));
		for(int i = 0; i<3; i++){
			blockList.add(new BlockLava(id++,i));
			blockList.add(new BlockSolidLava(id++,i));
		}
		blocks = blockList.toArray(new Block[0]);

		registerBlocks();
		
		BlockWorldGen block = new BlockWorldGen(idWorld);
		blockList.add(block);
		blocks = blockList.toArray(new Block[0]);
		
		registerBlockDrops(block);
	}

	public void registerBlocks(){
		for(Block block : blocks){
			GameRegistry.registerBlock(block, block.getLocalizedName().substring(5));
			LanguageRegistry.addName(block, block.getLocalizedName().substring(5));
				
		}
		
	}
	
	public void registerBlockDrops(Block block){
			GameRegistry.registerBlock(block, ItemWorldGenBlock.class, "Test");
			LanguageRegistry.addName(block, "Test");
	}

}
