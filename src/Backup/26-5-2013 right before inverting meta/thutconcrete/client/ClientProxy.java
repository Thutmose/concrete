package thutconcrete.client;

import thutconcrete.common.CommonProxy;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy{
	
	public static ClientTickHandler TH = new ClientTickHandler();
	
	@Override
	public void initClient()
	{

		TickRegistry.registerTickHandler(TH, Side.CLIENT);

		RenderingRegistry.registerBlockHandler(BlockRenderHandler.ID,new BlockRenderHandler());
		
	}
	
	
    @Override
    public World getClientWorld()
    {
        return FMLClientHandler.instance().getClient().theWorld;
    }
	
	
	
}
