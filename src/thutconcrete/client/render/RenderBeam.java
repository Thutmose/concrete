package thutconcrete.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import thutconcrete.common.entity.EntityBeam;

public class RenderBeam extends Render {

	public static final int
	bullet=0, cannon=1, laser=2, plasma=3;
	
	Box box;
	public RenderBeam(int id) {
		box = new Box(id);
	}

	public class Box extends ModelBase
	{
		ModelRenderer shape;
		int id;
		int fraction = 50;
		public Box(int id)
		{
			textureWidth = 32;
		    textureHeight = 32;
		    setTextureOffset("shape.shape", 0, 0);
			shape= new ModelRenderer(this,"shape");
		    shape.setRotationPoint(0F, 0F, 0F);
			this.id=id;
			shape.rotateAngleX=0;
			shape.rotateAngleY=0;
			shape.rotateAngleZ=0;

		    shape.addBox(-2.0F, -2.0F, -10F, 4, 4, 20);
		}
		
		public void render(Entity e) 
		{		
			
			EntityBeam beam = (EntityBeam)e;
			//if(beam.tick)
			{
				beam.count++;
				GL11.glTranslated(beam.direction.x*(beam.count/fraction), beam.direction.y*(beam.count/fraction), beam.direction.z*(beam.count/fraction));
			}
			
		    shape.render(0.0625f);
		 }
	}

	@Override
	public void doRender(Entity e, double d1, double d2, double d3,
			float f1, float f2) {
		
		GL11.glPushMatrix();

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0xf0, 0x00);
		loadTexture("/mods/thutconcrete/textures/models/pew.png");
		GL11.glTranslated(d1, d2, d3);
		
		GL11.glRotatef(-e.rotationYaw, 0F, 1F, 0F);
		GL11.glRotatef(-e.rotationPitch, 1F, 0F, 0F);
		
		box.render(e);
		
		GL11.glPopMatrix();
		
		
	}
}
