package thutconcrete.common;

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


@Mod( modid = "ThutConcrete", name="Thut's Concrete", version="0.01")
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
	
}
