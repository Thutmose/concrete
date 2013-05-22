package thutconcrete.common.finiteWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;

public class WorldChunkManagerFinite extends WorldChunkManager
{
	private World world;
    private List biomesToSpawnIn;

    public WorldChunkManagerFinite() {
            biomesToSpawnIn = new ArrayList();
            biomesToSpawnIn.add(BiomeGenBase.plains); // Which biomes can you spawn in?
    }

    public WorldChunkManagerFinite(World world) {
            this();
            this.world = world; // save the world value if you need it later.
    }

    // Gets the list of valid biomes for the player to spawn in.
    public List getBiomesToSpawnIn() {
            return biomesToSpawnIn;
    }

    // Returns the BiomeGenBase related to the x, z position on the world.
    public BiomeGenBase getBiomeGenAt(int x, int z) {
            return BiomeGenBase.plains; // Always plains here
    }

    // checks given Chunk's Biomes against List of allowed ones
    public boolean areBiomesViable(int par1, int par2, int par3, List par4List) {
            return true;
    }

    // Finds a valid position within a range, that is one of the listed biomes.
    public ChunkPosition findBiomePosition(int i, int j, int k, List list, Random random) {
            // With the integration of the server into the client, the spawn location is randomized a bit.
            // EntityPlayerMP will adjust the location you give here randomly in the range of -10 to +9
            // This is done to both the X and Z locations.
            // The Y location is ideally just above the top solid block at that location.
            // NOTE:  If the location given does not have a grass block, the location will be further randomized.
            return new ChunkPosition(7, 0, 7);
    }
}
