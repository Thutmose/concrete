package thutconcrete.common.worldgen;

import java.util.Random;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.BlockWorldGen;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class TrassWorldGen implements IWorldGenerator
{

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if(world.provider.isSurfaceWorld()){
			if(random.nextInt(250)==1){
			int x = chunkX*16 + random.nextInt(16);
			int y = chunkZ*16 + random.nextInt(16);
			int z = 20+random.nextInt(80);
			//TODO 
			  (new WorldGen(BlockWorldGen.instance.blockID,1, 1024)).generateSheet(world, random, x, z, y,1,10);
			}
		}

	}
}
