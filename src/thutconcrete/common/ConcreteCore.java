package thutconcrete.common;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import thutconcrete.common.blocks.*;
import thutconcrete.common.corehandlers.BlockHandler;
import thutconcrete.common.corehandlers.ConfigHandler;
import thutconcrete.common.corehandlers.ItemHandler;
import thutconcrete.common.corehandlers.PacketHandler;
import thutconcrete.common.ticks.TickHandler;
import thutconcrete.common.worldgen.TrassWorldGen;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;


@Mod( modid = "ThutConcrete", name="Thut's Concrete", version="0.0.2")
@NetworkMod(clientSideRequired = false, serverSideRequired = false, 
channels={"ThutConcrete"},
packetHandler = PacketHandler.class)

public class ConcreteCore {

	@SidedProxy(clientSide = "thutconcrete.client.ClientProxy", serverSide = "thutconcrete.common.CommonProxy")
	public static CommonProxy commproxy;
	public static TickHandler tickHandler = new TickHandler();
	
	public Logger log = FMLLog.getLogger();
		
	@Instance("ThutConcrete")
	public static ConcreteCore instance;
	
	public static String modid = "ThutConcrete";
    
    public static CreativeTabConcrete tabThut = new CreativeTabConcrete();
    
	public static Block[] blocks;
	public static Item[] items;
	
	public BlockHandler blockList;
	public ItemHandler itemList;

	// Configuration Handler that handles the config file
	public ConfigHandler config;

	@PreInit
	public void preInit(FMLPreInitializationEvent e){
		config = new ConfigHandler(e.getSuggestedConfigurationFile());
	}
	
	
	
	@Init
	public void load(FMLInitializationEvent evt){
		new PacketHandler();
		commproxy.initClient();
		TickRegistry.registerTickHandler(tickHandler, Side.SERVER);
		
		GameRegistry.registerWorldGenerator(new TrassWorldGen());

		populateMap();
		
		itemList = new ItemHandler(config);
		blockList = new BlockHandler(config);
		items = itemList.items;
		blocks = blockList.blocks;
		
	}
	
	static int entityID=0;
	public static void registerEntity(Class<? extends Entity> clas, String name, int freq){
		EntityRegistry.registerGlobalEntityID(clas, name, EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(clas, name, entityID++, ConcreteCore.instance, 80, 1, true);
	}
	public static void registerEntity(Class<? extends Entity> clas, String name){
		registerEntity(clas, name, 1);
	}
	
	
	public static Map<Integer, Integer> colourMap = new HashMap<Integer, Integer>();
	
	public static Map<String, Integer> volcanoMap = new HashMap<String, Integer>();
	
	void populateMap(){
		
		//White gives
		colourMap.put(0 + 14 * 16, 6);
		colourMap.put(0 + 15 * 16, 7);
		colourMap.put(0 + 13 * 16, 5);
		colourMap.put(0 + 11 * 16, 3);
		colourMap.put(0 + 7 * 16, 8);
		
		// Pink Gives:
		colourMap.put(6 + 10 * 16, 2);
		
		//Yellow gives
		colourMap.put(4 + 14 * 16, 1);
		colourMap.put(4 + 11 * 16, 13);
		
		//Light Blue gives
		colourMap.put(3 + 4 * 16, 5);

		//Dark Blue gives
		colourMap.put(11 + 14 * 16, 10);
		colourMap.put(11 + 13 * 16, 9);
		
		//Dark green gives
		colourMap.put(13 + 14 * 16, 12);
		
		
	}
	
}
