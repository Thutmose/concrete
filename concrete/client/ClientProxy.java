package concrete.client;

import concrete.common.CommonProxy;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;

public class ClientProxy extends CommonProxy{
	
	final Minecraft mc= Minecraft.getMinecraft();
	
	public static ClientTickHandler TH = new ClientTickHandler();
	
	@Override
	public void initClient()
	{

		TickRegistry.registerTickHandler(TH, Side.CLIENT);

		RenderingRegistry.registerBlockHandler(BlockRenderHandler.ID,new BlockRenderHandler());
		
	}
	
}
