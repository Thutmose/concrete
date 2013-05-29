package thutconcrete.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import static net.minecraftforge.common.BiomeDictionary.Type;

import thutconcrete.common.blocks.*;
import thutconcrete.common.corehandlers.*;

import thutconcrete.common.finiteWorld.WorldTypeCustom;
import thutconcrete.common.ticks.TickHandler;
import thutconcrete.common.tileentity.*;

import thutconcrete.common.worldgen.*;


import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.*;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

import thutconcrete.common.network.*;

@Mod( modid = "ThutConcrete", name="Thut's Concrete", version="0.1.0")
@NetworkMod(clientSideRequired = true, serverSideRequired = true, 
channels={"Thut's Concrete"},
packetHandler = PacketHandler.class
)

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
	
	public static WorldType customWorldType;
	
    private static final String[] colourNames = { "White",
        "Orange", "Magenta", "Light Blue",
        "Yellow", "Light Green", "Pink",
        "Dark Grey", "Light Grey", "Cyan",
        "Purple", "Blue", "Brown", "Green",
        "Red", "Black" };
	
	
	public TSaveHandler saveList;
	
	public ChunkloadFinite loader;
	
	public LiquidHandler liquidHndlr;
	public BlockHandler blockList;
	public ItemHandler itemList;
	public RecipeHandler recipes;

	// Configuration Handler that handles the config file
	public ConfigHandler config;

	@PreInit
	public void preInit(FMLPreInitializationEvent e){
		config = new ConfigHandler(e.getSuggestedConfigurationFile());
		
		customWorldType = new WorldTypeCustom(config.worldID, "FINITE");
		
		saveList = new TSaveHandler();
		MinecraftForge.EVENT_BUS.register(saveList);
		
		if(config.ChunkSize>=20)
		{
			loader = new ChunkloadFinite(config.ChunkSize);
			MinecraftForge.EVENT_BUS.register(loader);
		}
	}
	
	
	
	@Init
	public void load(FMLInitializationEvent evt){
		new PacketHandler();
		commproxy.initClient();
		
		liquidHndlr = new LiquidHandler();
		MinecraftForge.EVENT_BUS.register(liquidHndlr);
		
		TickRegistry.registerTickHandler(tickHandler, Side.SERVER);
		
		LanguageRegistry.instance().addStringLocalization("generator.FINITE", "en_US", "Finite World");
		
		
		GameRegistry.registerWorldGenerator(new TrassWorldGen());
		GameRegistry.registerWorldGenerator(new VolcanoWorldGen());
		GameRegistry.registerWorldGenerator(new LimestoneWorldGen());
		
		
		GameRegistry.registerTileEntity(TileEntityBlock16Fluid.class, "Fluid16BlockTE");
		GameRegistry.registerTileEntity(TileEntityVolcano.class, "VolcanoTE");
		
		
		populateMap();
		
		blockList = new BlockHandler(config);
		itemList = new ItemHandler(config);
		recipes = new RecipeHandler(config);
		items = itemList.items;
		blocks = blockList.blocks;
	//*	
		BiomeGenBase theBiome = new BiomeGenChalk(252);
		GameRegistry.addBiome(theBiome);
		BiomeDictionary.registerBiomeType(theBiome, Type.PLAINS);

		liquidHndlr.registerLiquids();
		initOreMap();
	//*/	
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent e)
	{
		initHardens();
		recipes.registerRecipes();
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
	public static Map<String, Integer> oreMap0 = new HashMap<String, Integer>();
	public static Map<String, Integer> oreMap1 = new HashMap<String, Integer>();
	public static Map<String, Integer> oreMap2 = new HashMap<String, Integer>();
	public static Map<String, Byte> volcanoMap = new HashMap<String, Byte>();
	public static List<String> ores = new ArrayList<String>();
	
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
	
	public void initOreMap()
	{
		oreMap0.put("copper", 500);
		oreMap0.put("tin", 500);
		oreMap0.put("thorium", 120);
		oreMap0.put("uranium", 40);
		oreMap0.put("tungsten", 40);
		oreMap0.put("iron", 1000);
		oreMap0.put("chromium", 500);
		oreMap0.put("osmium", 10);
		oreMap0.put("iridium", 10);
		oreMap0.put("silver", 20);
		oreMap0.put("aluminium", 150);
		oreMap0.put("platinum", 20);
		oreMap0.put("diamond", 10);
		oreMap0.put("emerald", 30);
		oreMap0.put("ruby", 30);
		oreMap0.put("sapphire", 30);
		oreMap0.put("quartz", 100);
		oreMap0.put("lead", 100);
		oreMap0.put("zinc", 500);
		oreMap0.put("redstone", 20);
		
		oreMap1.put("copper", 500);
		oreMap1.put("tin", 250);
		oreMap1.put("thorium", 120);
		oreMap1.put("uranium", 40);
		oreMap1.put("tungsten", 40);
		oreMap1.put("iron", 1000);
		oreMap1.put("chromium", 500);
		oreMap1.put("osmium", 10);
		oreMap1.put("iridium", 10);
		oreMap1.put("silver", 20);
		oreMap1.put("aluminium", 150);
		oreMap1.put("platinum", 20);
		oreMap1.put("diamond", 10);
		oreMap1.put("emerald", 30);
		oreMap1.put("ruby", 30);
		oreMap1.put("sapphire", 30);
		oreMap1.put("quartz", 100);
		oreMap1.put("lead", 1000);
		oreMap1.put("zinc", 500);
		oreMap1.put("redstone", 20);
		
		oreMap2.put("copper", 200);
		oreMap2.put("tin", 200);
		oreMap2.put("thorium", 120);
		oreMap2.put("uranium", 40);
		oreMap2.put("tungsten", 40);
		oreMap2.put("iron", 500);
		oreMap2.put("chromium", 200);
		oreMap2.put("osmium", 10);
		oreMap2.put("iridium", 10);
		oreMap2.put("silver", 20);
		oreMap2.put("aluminium", 1000);
		oreMap2.put("platinum", 20);
		oreMap2.put("diamond", 10);
		oreMap2.put("emerald", 30);
		oreMap2.put("ruby", 30);
		oreMap2.put("sapphire", 30);
		oreMap2.put("quartz", 1000);
		oreMap2.put("lead", 100);
		oreMap2.put("zinc", 500);
		oreMap2.put("redstone", 20);
		
	}

	public void initHardens()
	{
		String name;
		for(Block b:Block.blocksList)
		{
			for(int meta = 0; meta<16; meta++)
			{
				if(b!=null&&oreDictName(b.blockID,meta)!="Unknown")
				{
					name = oreDictName(b.blockID,meta);
					for(String s:oreMap0.keySet())
					{
						if(!ores.contains(s))
						if(!BlockSolidLava.getInstance(0).turnto.contains(b.blockID + 4096*ConcreteCore.oreMap0.get(s) + 4096*1024*meta))
						if(name.toLowerCase().contains(s)&&name.toLowerCase().contains("ore"))
						{
							BlockSolidLava.getInstance(0).totalProb += ConcreteCore.oreMap0.get(s);
							BlockSolidLava.getInstance(0).turnto.add(b.blockID + 4096*ConcreteCore.oreMap0.get(s) + 4096*1024*meta);
							
							BlockSolidLava.getInstance(1).totalProb += ConcreteCore.oreMap1.get(s);
							BlockSolidLava.getInstance(1).turnto.add(b.blockID + 4096*ConcreteCore.oreMap1.get(s) + 4096*1024*meta);
							
							BlockSolidLava.getInstance(2).totalProb += ConcreteCore.oreMap2.get(s);
							BlockSolidLava.getInstance(2).turnto.add(b.blockID + 4096*ConcreteCore.oreMap2.get(s) + 4096*1024*meta);
							
							ores.add(s);
						}
					}
				}
			}
			
			
		}
		
		double num = 1-config.CoolRate;
		
		for(int i = 0; i<3; i++)
		{
			BlockSolidLava.getInstance(i).totalProb /= num;
		}
	}

	public static String oreDictName(int id, int meta)
	{
		return OreDictionary.getOreName(OreDictionary.getOreID(new ItemStack(id,1,meta)));
	}
	
}
