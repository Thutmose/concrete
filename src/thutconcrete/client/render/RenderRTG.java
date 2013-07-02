package thutconcrete.client.render;

import static org.lwjgl.opengl.GL11.*;

import cpw.mods.fml.client.FMLClientHandler;

import thutconcrete.common.tileentity.TileEntityRTG;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class RenderRTG extends TileEntitySpecialRenderer
{
	private IModelCustom model;
	
	public RenderRTG()
	{
		model = AdvancedModelLoader.loadModel("/mods/thutconcrete/models/RTG.obj");
	}
	

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float f) 
	{
		
		TileEntityRTG rtg = (TileEntityRTG)tileentity;
		if(rtg.source)
		{
			glPushMatrix();
	
			glTranslated(x+0.5, y, z+0.5);
			glScaled(2.6, 2, 2);
			if(rtg.facing==4||rtg.facing==5)
				glRotatef(90, 0, 1, 0);
			
			FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/RTG.png");
			model.renderAll();
			
			glPopMatrix();
		}
		else
		{
			glPushMatrix();
			
			glTranslated(x+0.5, y, z+0.5);
			glScaled(2.6, 2, 2);
			if(rtg.sourceDir==4)
				glRotatef(90, 0, 1, 0);
			
			FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/RTG.png");
			model.renderAll();
			
			glPopMatrix();
		}
	}

}
