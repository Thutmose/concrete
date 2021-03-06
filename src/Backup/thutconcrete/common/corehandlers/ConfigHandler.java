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

	// Blocks
	public int IDBlock;
	public int IDWorldBlock;
	public int IDBiome;
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

			Property biome = conf.get("biomeID", "biomeID", biomeID,"the initial Block ID");
			IDBiome = biome.getInt();
			// Load Item Ids
			Property itemIdea = conf.getItem("Item", itemRange);
			itemIdea.comment = "The initial Item ID";
			IDItem = itemIdea.getInt();


		}catch(RuntimeException e){
			ConcreteCore.instance.log.log(Level.INFO, "Config file not found, creating new one");
		}finally{
			conf.save();
		}
	}

}