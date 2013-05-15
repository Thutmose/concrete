package thutconcrete.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import static net.minecraftforge.common.BiomeDictionary.Type;

import thutconcrete.common.blocks.*;
import thutconcrete.common.corehandlers.BlockHandler;
import thutconcrete.common.corehandlers.ConfigHandler;
import thutconcrete.common.corehandlers.ItemHandler;
import thutconcrete.common.corehandlers.PacketHandler;
import thutconcrete.common.corehandlers.TSaveHandler;
import thutconcrete.common.ticks.TickHandler;
import thutconcrete.common.worldgen.ChalkWorldGen;
import thutconcrete.common.worldgen.LimestoneWorldGen;
import thutconcrete.common.worldgen.TrassWorldGen;
import thutconcrete.common.worldgen.VolcanoWorldGen;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.*;
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
	
	public TSaveHandler saveList;
	
	public BlockHandler blockList;
	public ItemHandler itemList;

	// Configuration Handler that handles the config file
	public ConfigHandler config;

	@PreInit
	public void preInit(FMLPreInitializationEvent e){
		config = new ConfigHandler(e.getSuggestedConfigurationFile());
		
		saveList = new TSaveHandler();
		MinecraftForge.EVENT_BUS.register(saveList);
	}
	
	
	
	@Init
	public void load(FMLInitializationEvent evt){
		new PacketHandler();
		commproxy.initClient();
		TickRegistry.registerTickHandler(tickHandler, Side.SERVER);

		GameRegistry.registerWorldGenerator(new TrassWorldGen());
		GameRegistry.registerWorldGenerator(new VolcanoWorldGen());
		GameRegistry.registerWorldGenerator(new LimestoneWorldGen());
		
		populateMap();

		blockList = new BlockHandler(config);
		itemList = new ItemHandler(config);
		items = itemList.items;
		blocks = blockList.blocks;
	//*	
		BiomeGenBase theBiome = new ChalkWorldGen(252);
		GameRegistry.addBiome(theBiome);
		BiomeDictionary.registerBiomeType(theBiome, Type.PLAINS);
	//*/	
	}
	
	static int entityID=0;
	public static void registerEntity(Class<? extends Entity> clas, String name, int freq){
		EntityRegistry.registerGlobalEntityID(clas, name, EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(clas, name, entityID++, ConcreteCore.instance, 80, 1, true);
	}
	public static void registerEntity(Class<? extends Entity> clas, String name){
		registerEntity(clas, name, 1);
	}
	
	
	public static Map<Short, Byte> colourMap = new HashMap<Short, Byte>();
	
	public static Map<String, Byte> volcanoMap = new HashMap<String, Byte>();
	
	void populateMap(){
		
		//White gives
		colourMap.put((short)(0 + 14 * 16),(byte) 6);
		colourMap.put((short)(0 + 15 * 16),(byte) 7);
		colourMap.put((short)(0 + 13 * 16),(byte) 5);
		colourMap.put((short)(0 + 11 * 16),(byte) 3);
		colourMap.put((short)(0 + 7 * 16),(byte) 8);
		
		// Pink Gives:
		colourMap.put((short)(6 + 10 * 16),(byte) 2);
		
		//Yellow gives
		colourMap.put((short)(4 + 14 * 16),(byte) 1);
		colourMap.put((short)(4 + 11 * 16),(byte) 13);
		
		//Light Blue gives
		colourMap.put((short)(3 + 4 * 16),(byte) 5);

		//Dark Blue gives
		colourMap.put((short)(11 + 14 * 16),(byte) 10);
		colourMap.put((short)(11 + 13 * 16),(byte) 9);
		
		//Dark green gives
		colourMap.put((short)(13 + 14 * 16), (byte)12);
		
		
	}
	
	public static int getVolcano(int x, int z){
		if(!(volcanoMap.containsKey(Integer.toString(x)+Integer.toString(z)))){
			addVolcano(x,z);
		}
		return volcanoMap.get(Integer.toString(x)+Integer.toString(z));
	}
	
	public static void addVolcano(int x, int z){
		Random rX = new Random(x);
		Random rZ = new Random(z);
		Byte Height = (byte) (rX.nextInt(45)+rZ.nextInt(45));
		volcanoMap.put(Integer.toString(x)+Integer.toString(z), Height);
	}
	
}
