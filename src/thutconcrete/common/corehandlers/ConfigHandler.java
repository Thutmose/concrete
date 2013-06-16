package thutconcrete.common.corehandlers;


import java.io.File;
import java.util.logging.Level;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.BlockLava;
import thutconcrete.common.blocks.BlockLiquidConcrete;
import thutconcrete.common.blocks.BlockSolidLava;
import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.tileentity.TileEntityVolcano;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ConfigHandler {

    private int blockRange = 800;
    private int blockWorldRange = 252;
    private int biomeID = 252;
    private int itemRange = 7000;

    private int chunkSize = 500;
    private double coolrate = 3.5;
    private int ashamount = 25000;

    private int volcanoRate = 5000;

	// Blocks
	public static int IDBlock;
	public static int IDWorldBlock;
	public static int IDBiome;
	public static int ChunkSize;
	public static double CoolRate;
	public static boolean volcanos;
	public static int VolcRate;
	public static int worldID;
	public static int ashAmount;
	public static boolean debugPrints;
	public static boolean volcanosActive;

	public static double LiftSpeedUp;
	public static double LiftSpeedDown;
	public static double LiftSpeedDownOccupied;
	
	// Items
	public static int IDItem;
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
			
			////////////////////Concrete Stuff////////////////////////////////////////
			
			Property concreteDryRate = conf.get("Drying Rate", "Drying Rate", BlockLiquidConcrete.hardenRate,"This is an arbitrary rate that determines how quickly concrete dries, the higher this is, the faster it dries.");
			BlockLiquidConcrete.hardenRate = concreteDryRate.getInt();
			
			//////////////Volcano Stuff///////////////////////////////////////////////
			BlockLava.HardenRate = conf.get("Volcano Stuff", "Hardening Rate", 5,"this is an arbitrary rate of the conversion of lava to solid lava, scales inversely with viscosity").getInt();
			BlockSolidLava.oreProb = conf.get("Volcano Stuff", "Ore Drop Rate", 0.25,"the chance that solid lava drops ore rather than itself or dust.").getDouble(0.25);
			volcanos = conf.get("Volcano Stuff", "volcano", true,"do volcanoes spawn?" ).getBoolean(true);
			VolcRate = conf.get("Volcano Stuff", "Volcano occurance Rate", volcanoRate,"volcanos occur once every this many chunks").getInt();
			TileEntityVolcano.tickRate = conf.get("Volcano Stuff", "Volcano tick Rate", 10,"volcanos tick once every this many ticks").getInt();
			ashAmount= conf.get("Volcano Stuff", "Ash Volume", ashamount, "The base amount of ash from large explosions, scales with lava type, set below 1000 to completely disable ash").getInt();
			volcanosActive = conf.get("Volcano Stuff", "volcano grow", true,"do volcanos grow?" ).getBoolean(true);
			CoolRate = conf.get("Volcano Stuff", "Major Explosion Rate", coolrate,"the number of standard deviations needed for a large explosion, this causes ash").getDouble(coolrate);
			TileEntityVolcano.minorExplosionRate = conf.get("Volcano Stuff", "Minor Explosion Rate", 2,"the number of standard deviations needed for a small explosion, no ash").getDouble(2);
			TileEntityVolcano.dormancyRate = conf.get("Volcano Stuff", "Dormancy Rate", 5,"the number of standard deviations needed for the volcano to go dormant").getDouble(5);
			TileEntityVolcano.activityRate = conf.get("Volcano Stuff", "Activity Rate", 4.5,"the number of standard deviations needed for the volcano to go active").getDouble(4.5);
			TileEntityVolcano.eruptionStartRate = conf.get("Volcano Stuff", "Eruption start Rate", 3,"the number of standard deviations needed for the volcano to enter an eruptive period").getDouble(3);
			TileEntityVolcano.eruptionStopRate = conf.get("Volcano Stuff", "Eruption stop Rate", 2,"the number of standard deviations needed for the volcano to exit an eruptive period").getDouble(2);
			debugPrints = conf.get("Volcano Stuff", "debug Prints", false,"Do Printouts of whatever the volcano does happen?").getBoolean(false);
			//debug = conf.get("Volcano Stuff", "debug bool", false,"debug bool" ).getBoolean(false);
			////////////////////////////////Lift Stuff////////////////////////////////////////////////////////////////

			LiftSpeedUp = conf.get("Lift Settings", "Upward speed", 0.5,"The speed in blocks/tick for the lift going upwards").getDouble(0.5);
			LiftSpeedDown = conf.get("Lift Settings", "Downward speed", 0.5,"The speed in blocks/tick for the lift going downwards").getDouble(0.5);
			EntityLift.ACCELERATIONTICKS = conf.get("Lift Settings", "stopping ticks", 20,"This corresponds to how slowly the lift stops, setting this to 0 will result in very jerky lift.").getInt();
			EntityLift.AUGMENTG = conf.get("Lift Settings", "smoothdown", true,"Does the lift smooth your downward motion? if set to true will inhibit jumping while lift is moving down.").getBoolean(true);
			
			///////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			
			// Load Item Ids
			Property item = conf.getItem("Item", itemRange);
			item.comment = "The initial Item ID";
			IDItem = item.getInt();

			Property chunksize = conf.get("Chunk Size", "chunksize", chunkSize,"the size of wrapping chunks, If you set this less than 20 it will disabe the wrapping.  This only applies to the new worldtype added by this mod.  it has no effect on any other world type (like Flat or LargeBiomes)");
			ChunkSize = chunksize.getInt();

		}catch(RuntimeException e){
			ConcreteCore.instance.log.log(Level.INFO, "Config file not found, creating new one");
		}finally{
			conf.save();
		}
	}
	
	
	public static class GUIIDs
	{
		public static int limekiln = 0;
	}

}