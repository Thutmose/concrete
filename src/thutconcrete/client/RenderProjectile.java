package thutconcrete.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

public class RenderProjectile extends Render {

	public static final int
	bullet=0, cannon=1, laser=2, plasma=3;
	
	Box box;
	public RenderProjectile(int id) {
		box = new Box(id);
	}

	public class Box extends ModelBase{
		ModelRenderer shape;
		int id;
		public Box(int id){
			textureWidth = 32;
		    textureHeight = 32;
		    setTextureOffset("shape.shape", 0, 0);
			shape= new ModelRenderer(this,"shape");
		    shape.setRotationPoint(0F, 0F, 0F);
			this.id=id;
			shape.rotateAngleX=0;
			shape.rotateAngleY=0;
			shape.rotateAngleZ=0;
			
		    shape.addBox(-0.5F, -0.5F, -1.5F, 1, 1, 3);
		}
		public void render(Entity e) {			
		    shape.render(0.075f);
		  }
	}

	@Override
	public void doRender(Entity e, double d1, double d2, double d3,
			float f1, float f2) {
		GL11.glPushMatrix();
		loadTexture("/thutconcrete/client/TexturePewpew.png");
		GL11.glTranslated(d1, d2, d3);
		GL11.glRotatef(e.rotationYaw, 0F, 1F, 0F);
		GL11.glRotatef(-e.rotationPitch, 1F, 0F, 0F);
		box.render(e);
		GL11.glPopMatrix();
	}
}
