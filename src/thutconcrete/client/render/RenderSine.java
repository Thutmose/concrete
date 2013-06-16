package thutconcrete.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.entity.EntitySine;
import cpw.mods.fml.client.FMLClientHandler;

public class RenderSine  extends Render 
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
	
	public RenderSine()
	{
		modelTurret = AdvancedModelLoader.loadModel("/mods/thutconcrete/models/lift.obj");
	}
	
	public void render(Entity te, double x,double y,double z)
	{
		if(te instanceof EntitySine)
		{
			EntitySine laser = (EntitySine)te;
			
			
			for(int i = -6; i<=6; i++)
			{
				renderBase(te, 1,x+i,y+laser.y(i),z+laser.z(i));
			}
			
			
		}
		
	}

	private void renderBase(Entity te, float scale, double x,double y,double z)
	{
        GL11.glPushMatrix();
        
        GL11.glTranslated(x, y, z);
        
        GL11.glScalef(0.4f, 2, 0.4f);

        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/liftFloor.png");
        modelTurret.renderPart("base");  
        	
    	
        GL11.glPopMatrix();
	}

}