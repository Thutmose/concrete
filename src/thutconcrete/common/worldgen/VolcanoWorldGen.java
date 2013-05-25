package thutconcrete.common.worldgen;

import java.util.Random;

import thutconcrete.common.blocks.BlockVolcano;
import thutconcrete.common.blocks.BlockWorldGen;
import thutconcrete.common.corehandlers.ConfigHandler;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class VolcanoWorldGen implements IWorldGenerator{

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if(world.provider.isSurfaceWorld()&&ConfigHandler.volcanos){
			if(random.nextInt(ConfigHandler.VolcRate)==1){
			int x = chunkX*16 + random.nextInt(16);
			int y = chunkZ*16 + random.nextInt(16);
			int z = 5;
			  world.setBlock(x, z, y, BlockVolcano.instance.blockID, 0, 3);
			}
		}
		
	}

}
