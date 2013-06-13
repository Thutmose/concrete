package thutconcrete.client;

import thutconcrete.client.gui.GuiLimekiln;
import thutconcrete.client.render.*;
import thutconcrete.client.render.RenderBeam;
import thutconcrete.client.render.RenderRocket;
import thutconcrete.common.CommonProxy;
import thutconcrete.common.entity.*;
import thutconcrete.common.entity.EntityRocket;
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

public class ClientProxy extends CommonProxy{
	
	public static ClientTickHandler TH = new ClientTickHandler();
	
	@Override
	public void initClient()
	{

		TickRegistry.registerTickHandler(TH, Side.CLIENT);

		RenderingRegistry.registerBlockHandler(BlockRenderHandler.ID,new BlockRenderHandler());
		

		RenderingRegistry.registerEntityRenderingHandler(EntityRocket.class, new RenderRocket());
		RenderingRegistry.registerEntityRenderingHandler(EntityBeam.class, new RenderBeam(RenderBeam.laser));
		RenderingRegistry.registerEntityRenderingHandler(EntityTurret.class, new RenderTurret());
		RenderingRegistry.registerEntityRenderingHandler(EntityLift.class, new RenderLift());
		
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
	
}
