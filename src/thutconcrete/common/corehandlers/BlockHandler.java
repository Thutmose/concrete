package thutconcrete.common.corehandlers;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import thutconcrete.common.blocks.*;

public class BlockHandler {

	private ConfigHandler config;

	
	
	public static Block[] blocks;
	private static List<Block> blockList = new ArrayList<Block>();

	public BlockHandler(ConfigHandler configHandler){
		config = configHandler;
		initBlocks();
	}

	public void initBlocks(){
		
		int id = config.IDBlock;
		
		blockList.add(new BlockDust(id++));
		blockList.add(new BlockRebar(id++));
		blockList.add(new BlockFullSolidREConcrete(id++));
		blockList.add(new BlockFullSolidConcrete(id++));
		
		for(int i = 0; i<16; i++){
			blockList.add(new BlockREConcrete(id++,i));
			blockList.add(new BlockLiquidREConcrete(id++,i));
			blockList.add(new BlockConcrete(id++,i));
			blockList.add(new BlockLiquidConcrete(id++,i));
		}
		
		for(int i = 0; i<3; i++){
			blockList.add(new BlockLava(id++,i));
			blockList.add(new BlockSolidLava(id++,i));
		}

		blockList.add(new BlockLavaSpawner(id++));
		blocks = blockList.toArray(new Block[0]);

		registerBlocks();
	}

	public void registerBlocks(){
		for(Block block : blocks){
			GameRegistry.registerBlock(block, block.getLocalizedName().substring(5));
			LanguageRegistry.addName(block, block.getLocalizedName().substring(5));
		}
		
	}

}
