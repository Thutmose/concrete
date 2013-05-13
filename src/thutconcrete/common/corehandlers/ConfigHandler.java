package thutconcrete.common.corehandlers;


import java.io.File;
import java.util.logging.Level;

import thutconcrete.common.ConcreteCore;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

public class ConfigHandler {
    
    private int blockRange = 800;
    private int itemRange = 7000;

	// Blocks
	public int IDBlock;
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