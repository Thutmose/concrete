package thutconcrete.common.corehandlers;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.block.Block;
import thutconcrete.common.blocks.BlockConcrete;
import thutconcrete.common.blocks.BlockDust;
import thutconcrete.common.blocks.BlockLiquidConcrete;
import thutconcrete.common.blocks.BlockLiquidREConcrete;
import thutconcrete.common.blocks.BlockREConcrete;
import thutconcrete.common.blocks.BlockRebar;

public class BlockHandler {

	private ConfigHandler config;

	
	
	public static Block[] blocks;

	public BlockHandler(ConfigHandler configHandler){
		config = configHandler;
		initBlocks();
	}

	public void initBlocks(){
		
		int id = config.IDBlock;
		blocks = new Block[]{
				new BlockDust(id++),
				new BlockRebar(id++),
				new BlockConcrete(id++),
				new BlockLiquidConcrete(id++),
				new BlockREConcrete(id++),
				new BlockLiquidREConcrete(id++),
			};

		registerBlocks();
	}

	public void registerBlocks(){
		for(Block block : blocks){
			GameRegistry.registerBlock(block, block.getLocalizedName().substring(5));
			LanguageRegistry.addName(block, block.getLocalizedName().substring(5));
		}
		
	}

}
