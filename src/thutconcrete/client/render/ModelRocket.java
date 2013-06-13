package thutconcrete.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public class ModelRocket extends ModelBase
{
    private IModelCustom modelRocketBooster;
    private IModelCustom modelMainRocket;
     
    public ModelRocket()
    {
    	modelRocketBooster = AdvancedModelLoader.loadModel("/mods/thutconcrete/models/rocket.obj");
    	modelMainRocket = AdvancedModelLoader.loadModel("/mods/thutconcrete/models/mainRocket.obj");
    }
    
    private void render()
    {
        //renderSingleBooster();
    }
    
    private void renderSingleBooster(double x, double y, double z, float scale)
    {

        // Push a blank matrix onto the stack
        GL11.glPushMatrix();
     
        // Move the object into the correct position on the block (because the OBJ's origin is the center of the object)
        GL11.glTranslatef((float)x, (float)y, (float)z);
     
        // Scale our object to about half-size in all directions (the OBJ file is a little large)
        GL11.glScalef(scale, scale, scale);
    	
    	
        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/boosterNozzle.png");
    	modelRocketBooster.renderPart("nozzle");        // Bind the texture, so that OpenGL properly textures our block.
        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/boosterCylinder.png");
    	modelRocketBooster.renderPart("Cylinder");        // Bind the texture, so that OpenGL properly textures our block.
        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/boosterNose.png");
    	modelRocketBooster.renderPart("Cone");
        
        // Pop this matrix from the stack.
        GL11.glPopMatrix();
        
    }
    
    private void renderMainRocketStage1(double x, double y, double z, float scale)
    {

        // Push a blank matrix onto the stack
        GL11.glPushMatrix();
     
        // Move the object into the correct position on the block (because the OBJ's origin is the center of the object)
        GL11.glTranslatef((float)x, (float)y, (float)z);
     
        // Scale our object to about half-size in all directions (the OBJ file is a little large)
        GL11.glScalef(scale, scale, scale);
    	
    	
        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/mainRocketStage1Nozzle.png");
        modelMainRocket.renderPart("stage1Nozzle");
        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/mainRocketStage1Cylinder.png");
        modelMainRocket.renderPart("stage1Cylinder");
        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/mainRocketStage1Cone.png");
        modelMainRocket.renderPart("stage1Cone");
        
        // Pop this matrix from the stack.
        GL11.glPopMatrix();
        
    }
    
    private void renderMainRocketSeperator(double x, double y, double z, float scale)
    {

        // Push a blank matrix onto the stack
        GL11.glPushMatrix();
     
        // Move the object into the correct position on the block (because the OBJ's origin is the center of the object)
        GL11.glTranslatef((float)x, (float)y, (float)z);
     
        // Scale our object to about half-size in all directions (the OBJ file is a little large)
        GL11.glScalef(scale, scale, scale);
    	
        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/mainRocketSeperator.png");
        modelMainRocket.renderPart("Seperator");
        
        // Pop this matrix from the stack.
        GL11.glPopMatrix();
        
    }
    
    private void renderMainRocketStage2(double x, double y, double z, float scale)
    {

        // Push a blank matrix onto the stack
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
     
        // Move the object into the correct position on the block (because the OBJ's origin is the center of the object)
        GL11.glTranslatef((float)x, (float)y, (float)z);
     
        // Scale our object to about half-size in all directions (the OBJ file is a little large)
        GL11.glScalef(scale, scale, scale);
    	
    	
        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/mainRocketStage2ConeNozzle.png");
        modelMainRocket.renderPart("stage2Nozzle");
        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/mainRocketStage2Cylinder.png");
        modelMainRocket.renderPart("stage2Cylinder");

        FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/models/mainRocketStage2ConeNozzle.png");
        modelMainRocket.renderPart("stage2Cone");
        
        // Pop this matrix from the stack.
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_CULL_FACE);
        
    }
    
    public void render(Entity box, double x, double y, double z)
    {

      //  GL11.glDisable(GL11.GL_LIGHTING);
    	float scale = 2.5f;
    	double boosterOffset = scale/(1.5*Math.sqrt(2));
    	
    	
        this.renderSingleBooster(x+boosterOffset,y,z+boosterOffset,scale);
        this.renderSingleBooster(x-boosterOffset,y,z+boosterOffset,scale);
        this.renderSingleBooster(x+boosterOffset,y,z-boosterOffset,scale);
        this.renderSingleBooster(x-boosterOffset,y,z-boosterOffset,scale);

        this.renderMainRocketStage1(x, y, z,scale);
        this.renderMainRocketSeperator(x, y, z,scale);
        this.renderMainRocketStage2(x, y, z,scale);

       // GL11.glEnable(GL11.GL_LIGHTING);
    }
}
	
