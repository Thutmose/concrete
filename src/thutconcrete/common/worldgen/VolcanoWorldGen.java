package thutconcrete.common.worldgen;

import java.util.Random;

import thutconcrete.common.blocks.BlockWorldGen;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class VolcanoWorldGen implements IWorldGenerator{

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if(world.provider.isSurfaceWorld()){
			if(random.nextInt(1000)==1){
			int x = chunkX*16 + random.nextInt(16);
			int y = chunkZ*16 + random.nextInt(16);
			int z = 5;
			//TODO 
		//	System.out.println("Volcano");
			  world.setBlock(x, z, y, BlockWorldGen.instance.blockID, 0, 1);
			  world.scheduleBlockUpdate(x, z, y, BlockWorldGen.instance.blockID, 5);
			}
		}
		
	}

}
