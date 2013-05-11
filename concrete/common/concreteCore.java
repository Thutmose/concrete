package concrete.common;

import concrete.common.BlocksItems.*;
import concrete.common.ticks.TickHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
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

public class concreteCore {

	@SidedProxy(clientSide = "concrete.client.ClientProxy", serverSide = "concrete.common.CommonProxy")
	public static CommonProxy commproxy;
	public static TickHandler tickHandler = new TickHandler();
	
	@Instance("ThutConcrete")
	public static concreteCore instance;
	
	public static String modid = "ThutConcrete";
    
    public static CreativeTabConcrete tabThut = new CreativeTabConcrete();
    
    public static int id = 1000;
	public static Block[] blocks = {
		new BlockDust(id++),
		new BlockRebar(id++),
		new BlockConcrete(id++),
		new BlockLiquidConcrete(id++),
		new BlockREConcrete(id++),
		new BlockLiquidREConcrete(id++),
	};
	
	
	@Init
	public void load(FMLInitializationEvent evt){
		new PacketHandler();
		commproxy.initClient();
		TickRegistry.registerTickHandler(tickHandler, Side.SERVER);
		
		for(Block block : blocks){
			GameRegistry.registerBlock(block, block.getLocalizedName().substring(5));
			LanguageRegistry.addName(block, block.getLocalizedName().substring(5));
		}
		
		
	}
	
	static int entityID=0;
	public static void registerEntity(Class<? extends Entity> clas, String name, int freq){
		EntityRegistry.registerGlobalEntityID(clas, name, EntityRegistry.findGlobalUniqueEntityId());
		EntityRegistry.registerModEntity(clas, name, entityID++, concreteCore.instance, 80, 1, true);
	}
	public static void registerEntity(Class<? extends Entity> clas, String name){
		registerEntity(clas, name, 1);
	}
	
}
