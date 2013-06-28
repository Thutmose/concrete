package thutconcrete.client;

import thutconcrete.client.gui.GuiLimekiln;
import thutconcrete.client.render.*;
import thutconcrete.common.CommonProxy;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.entity.*;
import thutconcrete.common.tileentity.*;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

public class ClientProxy extends CommonProxy{
	
	public static ClientTickHandler TH = new ClientTickHandler();
	
	@Override
	public void initClient()
	{

		TickRegistry.registerTickHandler(TH, Side.CLIENT);

		RenderingRegistry.registerBlockHandler(BlockRenderHandler.ID,new BlockRenderHandler());
		
		RenderSeismicMonitor panelRenderer = new RenderSeismicMonitor();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySeismicMonitor.class, panelRenderer);
	//	RenderingRegistry.registerBlockHandler(panelRenderer);

		RenderLiftController liftRenderer = new RenderLiftController();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLiftAccess.class, liftRenderer);

		//RenderingRegistry.registerEntityRenderingHandler(EntityRocket.class, new RenderRocket());
		RenderingRegistry.registerEntityRenderingHandler(EntityBeam.class, new RenderBeam(RenderBeam.laser));
		RenderingRegistry.registerEntityRenderingHandler(EntityTurret.class, new RenderTurret());
		RenderingRegistry.registerEntityRenderingHandler(EntityLift.class, new RenderLift());

	//	RenderingRegistry.registerEntityRenderingHandler(EntitySine.class, new RenderSine());
		
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaser.class, new RenderTurret());
	}
	
	
    @Override
    public World getClientWorld()
    {
        return FMLClientHandler.instance().getClient().theWorld;
    }
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z)
	{
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te==null)
			return null;
		
		if(te instanceof TileEntityLimekiln)
		{
			TileEntityLimekiln tileEntity = (TileEntityLimekiln)te;
			return new GuiLimekiln(player.inventory, tileEntity);
		}
		
		return null;
	}
	
	@Override
	public void loadSounds(){
		try{
			MinecraftForge.EVENT_BUS.register(new ClientProxy.sounds());}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public static class sounds{
		@ForgeSubscribe
		public void onSound(SoundLoadEvent event){
			event.manager.soundPoolSounds.addSound("railgun.ogg", ConcreteCore.class.getResource("/mods/thutconcrete/sounds/railgun.ogg"));
		}
	}
	
}
