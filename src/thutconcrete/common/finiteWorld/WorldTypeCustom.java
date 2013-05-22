package thutconcrete.common.finiteWorld;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.IChunkProvider;


public class WorldTypeCustom extends WorldType
{       
    public WorldTypeCustom(int id, String string) {
	        super(id, string);
	        // The first parameter is the id number of the WorldType,
	        // vanilla Minecraft uses 0, 1, 2, and 8 already. The max that can be used is 15,
	        // The second parameter is our textname for our WorldType, the case needs to
	        // match the "generator.void" localization in the "mod_" file.          
	}
	
	@Override
	public WorldChunkManager getChunkManager(World world) {
	        // This is our ChunkManager class, it controls rain, temp, biomes, and spawn location
	        return new WorldChunkManager(world);
	}
	
	@Override
	public IChunkProvider getChunkGenerator(World world, String generatorOptions) {
	        // This is our ChunkProvider, this generates the world terrain.
	        return new WorldChunkProviderFinite(world);
	}

}