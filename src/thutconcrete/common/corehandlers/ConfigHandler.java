package thutconcrete.common.corehandlers;


import java.io.File;
import java.util.logging.Level;

import thutconcrete.common.ConcreteCore;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ConfigHandler {

    private int blockRange = 800;
    private int blockWorldRange = 252;
    private int biomeID = 252;
    private int itemRange = 7000;
    private int liquidID = 10;
    private int chunkSize = 1000;

	// Blocks
	public int IDBlock;
	public int IDWorldBlock;
	public int IDBiome;
	public int IDLiquid;
	public int ChunkSize;
	// Items
	public int IDItem;
	// Misc
	public static int renderId;

	public ConfigHandler(File configFile){
		// Loads The Configuration File into Forges Configuration
		Configuration conf = new Configuration(configFile);
		try{
			conf.load();
			
			// Load Block Ids

			Property blockOre = conf.getBlock("Block", blockRange);
			blockOre.comment = "the initial Block ID";
			IDBlock = blockOre.getInt();
			
			Property blockWorld = conf.getTerrainBlock("BlockWorld", "Block", blockWorldRange, "the Block ID for Worldgen blocks");
			IDWorldBlock = blockWorld.getInt();

			Property biome = conf.get("biomeID", "biomeID", biomeID,"the initial biome ID");
			IDBiome = biome.getInt();
			
			Property liquid = conf.get("liquidID", "liquidID", liquidID,"the initial liquid ID");
			IDLiquid = liquid.getInt();
			// Load Item Ids
			Property itemIdea = conf.getItem("Item", itemRange);
			itemIdea.comment = "The initial Item ID";
			IDItem = itemIdea.getInt();

			Property chunksize = conf.get("Chunk Size", "chunksize", chunkSize,"the size of wrapping chunks, If you set this less than 20 it will disabe the wrapping.");
			ChunkSize = chunksize.getInt();

		}catch(RuntimeException e){
			ConcreteCore.instance.log.log(Level.INFO, "Config file not found, creating new one");
		}finally{
			conf.save();
		}
	}

}