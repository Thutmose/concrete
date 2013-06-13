package thutconcrete.client.render;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class RenderRocket extends Render 
{

	ModelRocket model = new ModelRocket();

	@Override
	public void doRender(Entity entity, double x, double y, double z, float f,
		float f1) {
		
		
		model.render(entity, x, y, z);
		
		
		}

}
