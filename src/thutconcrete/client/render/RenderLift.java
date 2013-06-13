package thutconcrete.client.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.entity.EntityTurret;
import thutconcrete.common.tileentity.TileEntityLaser;
import thutconcrete.common.utils.Vector3;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class RenderLift  extends Render 
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
   //     GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glScalef(scale, scale, scale);


		GL11.glRotatef(lift.axis?90:0, 0F, 1F, 0F);

		FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/railAttatchment.png");
		modelTurret.renderPart("railAttatchment2");
		
	//	GL11.glTranslated(0, 2.0, 0);
	//	GL11.glRotatef(-pitch, 0f, 0F, 1f);
	//	GL11.glTranslated(0, -(2.0), 0);
		
		/*/
		if(laser.riddenByEntity==null)
			FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/sphere.png");
		else
			FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/sphereClear.png");
		//*/	

		FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/railAttatchment.png");
    	modelTurret.renderPart("railAttatchment1");
         
   //     GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();

	}
	
	private void renderBase(Entity te, float scale, double x,double y,double z)
	{
        GL11.glPushMatrix();
        
        GL11.glTranslated(x, y, z);
        
        GL11.glScalef(scale, scale, scale);
    	
    	
        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/turretBase.png");
    	modelTurret.renderPart("base");
    	
        GL11.glPopMatrix();
	}

}
