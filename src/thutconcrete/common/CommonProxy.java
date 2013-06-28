package thutconcrete.common;

import powercrystals.minefactoryreloaded.api.FarmingRegistry;
import thutconcrete.common.entity.*;
import thutconcrete.common.tileentity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.server.FMLServerHandler;

public class CommonProxy  implements IGuiHandler
{

	public void initClient() {}
	
	public void loadConfiguration() {}
	
    public World getClientWorld() 
    {
    	return null;
    }
    
    public World getServerWorld()
    {
    	return null;
    }
    
    public void registerTEs()
    {
		GameRegistry.registerTileEntity(TileEntityBlock16Fluid.class, "Fluid16BlockTE");
		GameRegistry.registerTileEntity(TileEntityVolcano.class, "VolcanoTE");
		GameRegistry.registerTileEntity(TileEntitySeismicMonitor.class, "seismicMonitorTE");
		
		GameRegistry.registerTileEntity(TileEntityLimekiln.class, "tileEntityLimekiln");
		GameRegistry.registerTileEntity(TileEntityLimekilnDummy.class, "tileEntityLimekilnDummy");

		GameRegistry.registerTileEntity(TileEntityLiftAccess.class, "tileEntityLiftAccess");
    }
    
    public void registerEntities()
    {
    	ConcreteCore.registerEntity(EntityBeam.class, "railgunProjectile");
   // 	ConcreteCore.registerEntity(EntityRocket.class, "tehRocket");
    	ConcreteCore.registerEntity(EntityTurret.class, "railgunTurret");
    	ConcreteCore.registerEntity(EntityLift.class, "tehlift");

		try {
			Class<?> registry = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry");
			if(registry != null)
			{
			FarmingRegistry.registerSafariNetBlacklist(EntityLift.class);
			FarmingRegistry.registerSafariNetBlacklist(EntityTurret.class);
			FarmingRegistry.registerSafariNetBlacklist(EntityBeam.class);
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
		//	System.out.println("[ThutConcrete] MFR not found, lift not added to the non-existant safari net blacklist.");
		//	e.printStackTrace();
		}
		
    	
    //	ConcreteCore.registerEntity(EntitySine.class, "sine");
    }
    
	@Override
	public Object getServerGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntityLimekiln tileEntity = (TileEntityLimekiln)world.getBlockTileEntity(x, y, z);
		if(tileEntity != null)
			return new ContainerLimekiln(player.inventory, tileEntity);
		
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int guiID, EntityPlayer player, World world, int x, int y, int z)
	{
		return null;
	}

	public void loadSounds() {}
    
}
