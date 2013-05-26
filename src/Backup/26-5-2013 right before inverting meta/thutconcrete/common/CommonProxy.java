package thutconcrete.common;

import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.server.FMLServerHandler;

public class CommonProxy {

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
    
}
