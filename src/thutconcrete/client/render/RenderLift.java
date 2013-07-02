package thutconcrete.client.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import thutconcrete.api.utils.Vector3;
import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.entity.EntityTurret;
import thutconcrete.common.tileentity.TileEntitySensors;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class RenderLift extends Render
{

	float pitch = 0.0f;
	float yaw = 0.0f;
	long time = 0;
	
	@Override
	public void doRender(Entity entity, double d0, double d1, double d2,
			float f, float f1) {
		render(entity, d0, d1, d2);
	}
	
	private IModelCustom modelTurret;
	
	public RenderLift()
	{
		modelTurret = AdvancedModelLoader.loadModel("/mods/thutconcrete/models/lift.obj");
	}
	
	public void render(Entity te, double x,double y,double z)
	{
		if(te instanceof EntityLift)
		{
			EntityLift laser = (EntityLift)te;
			
			float scale = (float) laser.size*0.4f;

			renderBase(te, scale,x,y,z);
			renderAttachments(laser, scale,x,y,z);

		}
		
	}
	
	
	private void renderAttachments(EntityLift lift, float scale, double x,double y,double z)
	{
        GL11.glPushMatrix();

        GL11.glTranslated(x, y, z);
		GL11.glRotatef(lift.axis?90:0, 0F, 1F, 0F);
		
        GL11.glPushMatrix();
      
        GL11.glScalef(2, 2.5F, 2);
		
		GL11.glTranslated((-5+lift.size)*0.25, 0, 0);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/railAttatchment.png");
		modelTurret.renderPart("railAttatchment2");
		
        GL11.glPopMatrix();
        
        GL11.glPushMatrix();

        GL11.glScalef(2, 2.5F, 2);
        

		GL11.glTranslated((5-lift.size)*0.25, 0, 0);
		FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/railAttatchment.png");
    	modelTurret.renderPart("railAttatchment1");
         
        GL11.glPopMatrix();
        
        

        GL11.glPopMatrix();
        
	}
	
	private void renderBase(Entity te, float scale, double x,double y,double z)
	{
        GL11.glPushMatrix();
        
        GL11.glTranslated(x, y, z);
        
        GL11.glScalef(scale, 2, scale);
    	
        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/liftFloor.png");
    	modelTurret.renderPart("base");    
    	
    	GL11.glScalef(1, 0.5F, 1);
    	GL11.glTranslated(0, 4.5, 0);
    	FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/liftRoof.png");
    	modelTurret.renderPart("base");
    	
        GL11.glPopMatrix();
	}

}
