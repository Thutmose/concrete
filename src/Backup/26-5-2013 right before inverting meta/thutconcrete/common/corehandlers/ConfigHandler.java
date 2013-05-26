package thutconcrete.common.corehandlers;


import java.io.File;
import java.util.logging.Level;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.BlockLiquidConcrete;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ConfigHandler {

    private int blockRange = 800;
    private int blockWorldRange = 252;
    private int biomeID = 252;
    private int itemRange = 7000;
    private int liquidID = 10;
    private int chunkSize = 500;
    private double coolrate = 0.9998;
    private boolean volcano = true;

    private int volcanoRate = 5000;

	// Blocks
	public int IDBlock;
	public int IDWorldBlock;
	public int IDBiome;
	public int IDLiquid;
	public int ChunkSize;
	public static double CoolRate;
	public static boolean volcanos;
	public static int VolcRate;
	public static int worldID;
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
			
			Property WorldID = conf.get("finiteID", "finiteID", 15,"The id of the Finite WorldType");
			worldID = WorldID.getInt();
			
			Property liquid = conf.get("liquidID", "liquidID", liquidID,"the initial liquid ID");
			IDLiquid = liquid.getInt();
			////////////////////Concrete Stuff////////////////////////////////////////
			
			Property concreteDryRate = conf.get("Drying Rate", "Drying Rate", BlockLiquidConcrete.hardenRate,"This is an arbitrary rate that determines how quickly concrete dries, the higher this is, the faster it dries.");
			BlockLiquidConcrete.hardenRate = concreteDryRate.getInt();
			//////////////Volcano Stuff/////////////////////////////////////////////////
			
			Property coolRate = conf.get("Cooling Rate", "Cooling Rate", coolrate,"this is 1 - the rate of cooling");
			CoolRate = coolRate.getDouble(coolrate);
			
			Property spawnvolcanos = conf.get("volcano", "volcano", volcano,"do volcanos spawn?");
			volcanos = spawnvolcanos.getBoolean(volcano);
			
			Property VolcanoRate = conf.get("volcanoRate", "volcanoRate", volcanoRate,"volcanos occur once every this many chunks");
			VolcRate = VolcanoRate.getInt();
			
			
			// Load Item Ids
			Property itemIdea = conf.getItem("Item", itemRange);
			itemIdea.comment = "The initial Item ID";
			IDItem = itemIdea.getInt();

			Property chunksize = conf.get("Chunk Size", "chunksize", chunkSize,"the size of wrapping chunks, If you set this less than 20 it will disabe the wrapping.  This only applies to the new worldtype added by this mod.  it has no effect on any other world type (like Flat or LargeBiomes)");
			ChunkSize = chunksize.getInt();

		}catch(RuntimeException e){
			ConcreteCore.instance.log.log(Level.INFO, "Config file not found, creating new one");
		}finally{
			conf.save();
		}
	}

}