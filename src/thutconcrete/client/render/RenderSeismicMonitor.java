package thutconcrete.client.render;

import java.util.Arrays;

import org.lwjgl.opengl.GL11;

import thutconcrete.common.blocks.BlockLift;
import thutconcrete.common.tileentity.TileEntityLiftAccess;
import thutconcrete.common.tileentity.TileEntitySeismicMonitor;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

public class RenderSeismicMonitor extends TileEntitySpecialRenderer// implements ISimpleBlockRenderingHandler
{

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double x, double y,
			double z, float f) 
	{
		
		TileEntitySeismicMonitor monitor = (TileEntitySeismicMonitor)tileentity;

		GL11.glPushMatrix();
		

		GL11.glTranslatef((float)x, (float)y, (float)z);
		
		
		
       //Draw scale and rate and the control buttons on the front
       if(monitor.hasSensors)
       {
	        //Draw the Graphs
	       for(int j = 0; j<16; j++) 
	       {
	    	   drawGraph(monitor, j);
	       }
	       
	       drawGrid(monitor, 14);
	       
	       //Draw the overlay for selected Buttons
	       for(int i = 0; i<16; i++)
	       {
	    	   drawSelectionOverLay(monitor, i, 0);
	       }
       	
           drawFrontButtons(monitor);
           drawScaleCoef(monitor);
    	   drawScaleExponant(monitor);
    	   drawRate(monitor);
       }
       GL11.glPopMatrix();
		
	}
	
	public void drawScaleExponant(TileEntitySeismicMonitor monitor)
	{
		RenderEngine renderengine = Minecraft.getMinecraft().renderEngine;
		
		GL11.glPushMatrix();
		
		if(renderengine != null)
		{
			renderengine.bindTexture("/mods/thutconcrete/textures/models/scale.png");
		}
		
		if(monitor.getFacing() == ForgeDirection.EAST)
		{
			GL11.glTranslated(1, 0, 0);
			GL11.glRotatef(270, 0, 1, 0);
		}
		else if(monitor.getFacing() == ForgeDirection.SOUTH)
		{
			GL11.glTranslated(1, 0, 1);
			GL11.glRotatef(180, 0, 1, 0);
		}
		else if(monitor.getFacing() == ForgeDirection.WEST)
		{
			GL11.glTranslated(0, 0, 1);
			GL11.glRotatef(90, 0, 1, 0);
		}
		GL11.glTranslated(0, 0.2, 1);

		GL11.glPushAttrib(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		RenderHelper.disableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

		GL11.glTranslated(0.75, 0.0, -0.001);
		Tessellator t = Tessellator.instance;
		t.startDrawing(GL11.GL_QUADS);
		
		double n = (double)monitor.getScaleIndex();
		
		GL11.glColor4f(0.5F, 1.0F, 0.0F, 0.95F);
		
	    t.addVertexWithUV(0.85, 1.0, 0, n/25, 0);
	    t.addVertexWithUV(0.85, 0.8, 0, n/25, 1);
	      
	    t.addVertexWithUV(0.65, 0.8, 0, (n+1)/25, 1);
	    t.addVertexWithUV(0.65, 1.0, 0, (n+1)/25, 0);
	    
		t.draw();
		
		if(renderengine != null)
		{
			renderengine.bindTexture("/mods/thutconcrete/textures/models/font.png");
		}

		t.startDrawing(GL11.GL_QUADS);

		double [] UVs = locationFromIndex(37);
		
	    t.addVertexWithUV(1, 1.0, 0, UVs[0], UVs[2]);
	    t.addVertexWithUV(1, 0.8, 0, UVs[0], UVs[3]);
	      
	    t.addVertexWithUV(0.8, 0.8, 0, UVs[1], UVs[3]);
	    t.addVertexWithUV(0.8, 1.0, 0, UVs[1], UVs[2]);
		
		t.draw();
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		
		GL11.glPopAttrib();
		GL11.glPopAttrib();

		GL11.glPopMatrix();
		
		
	}
	
	public void drawScaleCoef(TileEntitySeismicMonitor monitor)
	{
		RenderEngine renderengine = Minecraft.getMinecraft().renderEngine;
		
		GL11.glPushMatrix();
		
		if(renderengine != null)
		{
			renderengine.bindTexture("/mods/thutconcrete/textures/models/font.png");
		}
		if(monitor.getFacing() == ForgeDirection.EAST)
		{
			GL11.glTranslated(1, 0, 0);
			GL11.glRotatef(270, 0, 1, 0);
		}
		else if(monitor.getFacing() == ForgeDirection.SOUTH)
		{
			GL11.glTranslated(1, 0, 1);
			GL11.glRotatef(180, 0, 1, 0);
		}
		else if(monitor.getFacing() == ForgeDirection.WEST)
		{
			GL11.glTranslated(0, 0, 1);
			GL11.glRotatef(90, 0, 1, 0);
		}
		GL11.glTranslated(0, 0.2, 1);
		GL11.glPushAttrib(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		RenderHelper.disableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

		GL11.glTranslated(1.1, 0.0, -0.002);
		Tessellator t = Tessellator.instance;
		t.startDrawing(GL11.GL_QUADS);

		int ones = (int) (monitor.coef%10);
		int tenths = (int) ((10*monitor.coef)%10);
		
		double [] UVs = locationFromNumber(ones);

		GL11.glColor4f(0.5F, 1.0F, 0.0F, 0.95F);
		
	    t.addVertexWithUV(0.95, 1.0, 0, UVs[0], UVs[2]);
	    t.addVertexWithUV(0.95, 0.8, 0, UVs[0], UVs[3]);
	      
	    t.addVertexWithUV(0.75, 0.8, 0, UVs[1], UVs[3]);
	    t.addVertexWithUV(0.75, 1.0, 0, UVs[1], UVs[2]);
		
	    UVs = locationFromIndex(14);
		
	    t.addVertexWithUV(0.875, 1.0, 0, UVs[0], UVs[2]);
	    t.addVertexWithUV(0.875, 0.8, 0, UVs[0], UVs[3]);
	      
	    t.addVertexWithUV(0.675, 0.8, 0, UVs[1], UVs[3]);
	    t.addVertexWithUV(0.675, 1.0, 0, UVs[1], UVs[2]);
		
	    UVs = locationFromNumber(tenths);
		
	    t.addVertexWithUV(0.8, 1.0, 0, UVs[0], UVs[2]);
	    t.addVertexWithUV(0.8, 0.8, 0, UVs[0], UVs[3]);
	      
	    t.addVertexWithUV(0.6, 0.8, 0, UVs[1], UVs[3]);
	    t.addVertexWithUV(0.6, 1.0, 0, UVs[1], UVs[2]);
		
		t.draw();
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		
		GL11.glPopAttrib();
		GL11.glPopAttrib();

		GL11.glPopMatrix();
	}
	
	public void drawRate(TileEntitySeismicMonitor monitor)
	{
		RenderEngine renderengine = Minecraft.getMinecraft().renderEngine;
		
		GL11.glPushMatrix();
		
		if(renderengine != null)
		{
			renderengine.bindTexture("/mods/thutconcrete/textures/models/font.png");
		}
		
		if(monitor.getFacing() == ForgeDirection.EAST)
		{
			GL11.glTranslated(1, 0, 0);
			GL11.glRotatef(270, 0, 1, 0);
		}
		else if(monitor.getFacing() == ForgeDirection.SOUTH)
		{
			GL11.glTranslated(1, 0, 1);
			GL11.glRotatef(180, 0, 1, 0);
		}
		else if(monitor.getFacing() == ForgeDirection.WEST)
		{
			GL11.glTranslated(0, 0, 1);
			GL11.glRotatef(90, 0, 1, 0);
		}
		GL11.glTranslated(0, 0, 1);

		GL11.glPushAttrib(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		RenderHelper.disableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

		GL11.glTranslated(-1.6, 0.2, -0.002);
		Tessellator t = Tessellator.instance;
		t.startDrawing(GL11.GL_QUADS);
		
		int num = monitor.VALUECOUNT-monitor.rate;
		
		int hundreds = num/100;
		int tens = (num/10)%10;
		int ones = num%10;
		
		double [] UVs = locationFromNumber(hundreds);
		if(hundreds!=0)
		{
			GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.95F);
			
		    t.addVertexWithUV(1.10, 1.0, 0, UVs[0], UVs[2]);
		    t.addVertexWithUV(1.10, 0.8, 0, UVs[0], UVs[3]);
		      
		    t.addVertexWithUV(0.90, 0.8, 0, UVs[1], UVs[3]);
		    t.addVertexWithUV(0.90, 1.0, 0, UVs[1], UVs[2]);
		}

	    UVs = locationFromNumber(tens);
		if(tens!=0||hundreds!=0)
		{
			GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.95F);
			
		    t.addVertexWithUV(0.95, 1.0, 0, UVs[0], UVs[2]);
		    t.addVertexWithUV(0.95, 0.8, 0, UVs[0], UVs[3]);
		      
		    t.addVertexWithUV(0.75, 0.8, 0, UVs[1], UVs[3]);
		    t.addVertexWithUV(0.75, 1.0, 0, UVs[1], UVs[2]);
		}
	    UVs = locationFromNumber(ones);
		
		GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.95F);
		
	    t.addVertexWithUV(0.80, 1.0, 0, UVs[0], UVs[2]);
	    t.addVertexWithUV(0.80, 0.8, 0, UVs[0], UVs[3]);
	      
	    t.addVertexWithUV(0.60, 0.8, 0, UVs[1], UVs[3]);
	    t.addVertexWithUV(0.60, 1.0, 0, UVs[1], UVs[2]);
		
		t.draw();
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		
		GL11.glPopAttrib();
		GL11.glPopAttrib();

		GL11.glPopMatrix();
	}
	
	public double[] locationFromNumber(int number)
	{
		double[] ret = new double[4];
		
		if(number>9||number<0)
			return ret;
		int index = 16+number;
		
		ret[0] = (double)(index%10)/10;
		ret[2] = (double)(index/10)/10;
		
		ret[1] = (double)(1+(index)%10)/10;
		ret[3] = (double)(1+(index)/10)/10;
		
		
		return ret;
	}
	
	public double[] locationFromIndex(int index)
	{
		double[] ret = new double[4];

		ret[0] = (double)(index%10)/10;
		ret[2] = (double)(index/10)/10;
		
		ret[1] = (double)(1+(index)%10)/10;
		ret[3] = (double)(1+(index)/10)/10;
		
		
		return ret;
	}
	
	public double[] locationFromLetterCapital(int letterIndex)
	{
		double[] ret = new double[4];
		
		if(letterIndex>26||letterIndex<0)
			return ret;
		int index = 33+letterIndex;
		
		ret[0] = (double)(index%10)/10;
		ret[2] = (double)(index/10)/10;
		
		ret[1] = (double)(1+(index)%10)/10;
		ret[3] = (double)(1+(index)/10)/10;
		
		
		return ret;
	}
	
	public void drawFrontButtons(TileEntitySeismicMonitor monitor)
	{
		RenderEngine renderengine = Minecraft.getMinecraft().renderEngine;
	   	
      GL11.glPushMatrix();
      
      if(monitor.getFacing() == ForgeDirection.EAST)
	   	{
	   		GL11.glTranslated(1, 0, 0);
	   		GL11.glRotatef(270, 0, 1, 0);
	   	}
	   	else if(monitor.getFacing() == ForgeDirection.SOUTH)
	   	{
	   		GL11.glTranslated(1, 0, 1);
	   		GL11.glRotatef(180, 0, 1, 0);
	   	}
	   	else if(monitor.getFacing() == ForgeDirection.WEST)
	   	{
	   		GL11.glTranslated(0, 0, 1);
	   		GL11.glRotatef(90, 0, 1, 0);
	   	}
      
		if(renderengine != null)
		{
			renderengine.bindTexture("/mods/thutconcrete/textures/models/arrow.png");
		}
		
		GL11.glPushAttrib(GL11.GL_BLEND);
		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		RenderHelper.disableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GL11.glTranslated(0.0, 0.0, -0.001);
		Tessellator t = Tessellator.instance;
		t.startDrawing(GL11.GL_QUADS);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
		
		////////////////Big Buttons////////////////////
	    t.addVertexWithUV(1, 0.75, 0, 0, 1);
	    t.addVertexWithUV(1, 0.5, 0, 0, 0);
	      
	    t.addVertexWithUV(0.75, 0.5, 0, 1, 0);
	    t.addVertexWithUV(0.75, 0.75, 0, 1, 1);
	    /////////////////////////////////////////
	    t.addVertexWithUV(0.25, 0.75, 0, 0, 0);
	    t.addVertexWithUV(0.25, 0.5, 0, 0, 1);
	      
	    t.addVertexWithUV(0, 0.5, 0, 1, 1);
	    t.addVertexWithUV(0, 0.75, 0, 1, 0);
	    
		////////////////Small Buttons////////////////////
	    t.addVertexWithUV(0.95, 0.95, 0, 0, 1);
	    t.addVertexWithUV(0.95, 0.8, 0, 0, 0);
	      
	    t.addVertexWithUV(0.8, 0.8, 0, 1, 0);
	    t.addVertexWithUV(0.8, 0.95, 0, 1, 1);
	    /////////////////////////////////////////
	    t.addVertexWithUV(0.20, 0.95, 0, 0, 0);
	    t.addVertexWithUV(0.20, 0.8, 0, 0, 1);
	      
	    t.addVertexWithUV(0.05, 0.8, 0, 1, 1);
	    t.addVertexWithUV(0.05, 0.95, 0, 1, 0);

		t.draw();
		
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopAttrib();
		GL11.glPopAttrib();
	      
		if(renderengine != null)
		{
			renderengine.bindTexture("/mods/thutconcrete/textures/models/arrow1.png");
		}
		
		GL11.glPushAttrib(GL11.GL_BLEND);
		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		RenderHelper.disableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GL11.glTranslated(0.0, 0.0, -0.001);
		t = Tessellator.instance;
		t.startDrawing(GL11.GL_QUADS);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
		
		//////////////////Big buttons/////////////////
	    t.addVertexWithUV(0.75, 0.5, 0, 1, 0);
	    t.addVertexWithUV(0.75, 0.25, 0, 1, 1);
	      
	    t.addVertexWithUV(0.50, 0.25, 0, 0, 1);
	    t.addVertexWithUV(0.50, 0.5, 0, 0, 0);
	    /////////////////////////////////////////
	    t.addVertexWithUV(0.50, 0.5, 0, 0, 0);
	    t.addVertexWithUV(0.50, 0.25, 0, 0, 1);
	      
	    t.addVertexWithUV(0.25, 0.25, 0, 1, 1);
	    t.addVertexWithUV(0.25, 0.5, 0, 1, 0);
	    
		//////////////////small buttons/////////////////
	    t.addVertexWithUV(0.70, 0.7, 0, 1, 0);
	    t.addVertexWithUV(0.70, 0.55, 0, 1, 1);
	      
	    t.addVertexWithUV(0.55, 0.55, 0, 0, 1);
	    t.addVertexWithUV(0.55, 0.7, 0, 0, 0);
	    /////////////////////////////////////////
	    t.addVertexWithUV(0.45, 0.7, 0, 0, 0);
	    t.addVertexWithUV(0.45, 0.55, 0, 0, 1);
	      
	    t.addVertexWithUV(0.3, 0.55, 0, 1, 1);
	    t.addVertexWithUV(0.3, 0.7, 0, 1, 0);
	    
		t.draw();

		t.startDrawing(GL11.GL_QUADS);
	    GL11.glColor4f(0.6F, 1.0F, 0.0F, 0.5F);
	    
		//////////////////small buttons/////////////////
	    t.addVertexWithUV(0.70, 0.95, 0, 1, 0);
	    t.addVertexWithUV(0.70, 0.8, 0, 1, 1);
	      
	    t.addVertexWithUV(0.55, 0.8, 0, 0, 1);
	    t.addVertexWithUV(0.55, 0.95, 0, 0, 0);
	    /////////////////////////////////////////
	    t.addVertexWithUV(0.45, 0.95, 0, 0, 0);
	    t.addVertexWithUV(0.45, 0.8, 0, 0, 1);
	      
	    t.addVertexWithUV(0.3, 0.8, 0, 1, 1);
	    t.addVertexWithUV(0.3, 0.95, 0, 1, 0);

		t.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopAttrib();
		GL11.glPopAttrib();
	      
		GL11.glPushAttrib(GL11.GL_BLEND);
		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GL11.glTranslated(0.0, 0.0, -0.001);
		t = Tessellator.instance;
		t.startDrawing(GL11.GL_QUADS);
		GL11.glColor4f(1.0F, 0.0F, 0.0F, 1F);
		
		//////////////////Reset Button/////////////////
	    t.addVertexWithUV(0.75, 0.25, 0, 1, 0);
	    t.addVertexWithUV(0.75, 0.00, 0, 1, 1);
	      
	    t.addVertexWithUV(0.25, 0.00, 0, 0, 1);
	    t.addVertexWithUV(0.25, 0.25, 0, 0, 0);
	    
		t.draw();
		
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopAttrib();
		GL11.glPopAttrib();
	      
		if(renderengine != null)
		{
			renderengine.bindTexture("/mods/thutconcrete/textures/models/font.png");
		}
		
		GL11.glPushAttrib(GL11.GL_BLEND);
		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		RenderHelper.disableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GL11.glTranslated(0.0, 0.0, -0.001);
		t = Tessellator.instance;
		t.startDrawing(GL11.GL_QUADS);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
		
	    double[] UVs = locationFromIndex(!monitor.paused?29:30);
		
	    t.addVertexWithUV(1, 0.5, 0, UVs[0], UVs[2]);
	    t.addVertexWithUV(1, 0.25, 0, UVs[0], UVs[3]);
	      
	    t.addVertexWithUV(0.75, 0.25, 0, UVs[1], UVs[3]);
	    t.addVertexWithUV(0.75, 0.5, 0, UVs[1], UVs[2]);
	    
		t.draw();
		
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopAttrib();
		GL11.glPopAttrib();
		GL11.glPopMatrix();
			
	}
	
	public void setColour(int index, double alpha)
	{
		double red = 0;
		double green = 0;
		double blue = 0;
		if(index == 0)
		{
			red = 0; blue = 0; green = 1;
		}
		if(index == 1)
		{
			red = 1; blue = 0; green = 0;
		}
		if(index == 2)
		{
			red = 0; blue = 1; green = 0;
		}
		if(index == 3)
		{
			red = 1; blue = 0; green = 1;
		}
		if(index == 4)
		{
			red = 0; blue = 1; green = 1;
		}
		if(index == 5)
		{
			red = 1; blue = 1; green = 0;
		}
		if(index == 6)
		{
			red = 0.5; blue = 0.5; green = 1;
		}
		if(index == 7)
		{
			red = 0.5; blue = 1; green = 0.5;
		}
		if(index == 8)
		{
			red = 1; blue = 0.5; green = 0.5;
		}
		if(index == 9)
		{
			red = 1; blue = 0.5; green = 1;
		}
		if(index == 10)
		{
			red = 0.5; blue = 1; green = 1;
		}
		if(index == 11)
		{
			red = 1; blue = 1; green = 0.5;
		}
		if(index == 12)
		{
			red = 0.5; blue = 0.2; green = 1;
		}
		if(index == 13)
		{
			red = 0.2; blue = 0.5; green = 1;
		}
		if(index == 14)
		{
			red = 1; blue = 1; green = 1;
		}
		if(index == 15)
		{
			red = 0; blue = 0; green = 0;
		}
		
		
		
		
		
		GL11.glColor4d(red, green, blue, alpha);
	}
			
	public void drawGraph(TileEntitySeismicMonitor monitor, int index)
	{
 	   if(!monitor.getButton(index+1))
		   return;
	   if(monitor.stationIDs[index]==-1)
		   return;
	   if(monitor.stations[index]==null)
		   return;
	   
		Double[] values = monitor.stations[index].getValues();
		if(values == null)
			return;
		
		GL11.glPushMatrix();

		GL11.glPushAttrib(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		RenderHelper.disableStandardItemLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glTranslated(0, 2, -0.001*index);
		Tessellator t = Tessellator.instance;
		if(values.length>0)
		{
			t.startDrawing(GL11.GL_LINES);
			GL11.glLineWidth(2.0F);
			setColour(index, 1);
	
			double yMin = Integer.MAX_VALUE;
			double yMax = Integer.MIN_VALUE;
	
			for(int i = 0; i < values.length; i++)
			{
				Double v = values[i];
				if(v == null)
				{
					continue;
				}
				if(v > yMax)
				{
					yMax = v;
				}
				if(v < yMin)
				{
					yMin = v;
				}
			}
	
			yMax = monitor.scale;
			yMin = -monitor.scale;
	
			Double lastValue = null;
			int lastX = 0;
			
			double num = values.length-monitor.rate;
			double xOffset = -(3/num)*((double)monitor.offset);
			//System.out.println(xOffset+" "+values.length+" "+monitor.rate+" "+num);
			for(int i = 1+monitor.offset; i < monitor.offset + num; i++)
			{
				if(i>=values.length||values[i] == null)
				{
					continue;
				}
				if(lastValue == null)
				{
					lastValue = values[i];
					lastX = i;
				}
				else
				{
					double x1 = xOffset + 3*(0.999)/(num) * lastX+0.001;
					double x2 = xOffset + 3*(0.999)/(num) * (i)+0.001;
					double y1 = (values[i - 1]) / (yMax - yMin);
					double y2 = (values[i]) / (yMax - yMin);
					
					t.addVertex(x1-1, y1>0?Math.min(y1, 1):Math.max(y1,-1), 0);
					t.addVertex(x2-1, y2>0?Math.min(y2, 1):Math.max(y2,-1), 0);
					
					lastValue = values[i];
					lastX = i;
				}
			}
			GL11.glRotatef(180, 0, 1, 0);
			GL11.glTranslated(-1, 0, -1);
			
			if(monitor.getFacing() == ForgeDirection.EAST)
			{
				GL11.glTranslated(1, 0, 0);
				GL11.glRotatef(270, 0, 1, 0);
			}
			else if(monitor.getFacing() == ForgeDirection.SOUTH)
			{
				GL11.glTranslated(1, 0, 1);
				GL11.glRotatef(180, 0, 1, 0);
			}
			else if(monitor.getFacing() == ForgeDirection.WEST)
			{
				GL11.glTranslated(0, 0, 1);
				GL11.glRotatef(90, 0, 1, 0);
			}
			t.draw();
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glPopAttrib();
		GL11.glPopAttrib();

		GL11.glPopMatrix();
	}
	
	public void drawGrid(TileEntitySeismicMonitor monitor, int index)
	{
			GL11.glPushMatrix();
			
			if(monitor.getFacing() == ForgeDirection.EAST)
			{
				GL11.glTranslated(1, 0, 0);
				GL11.glRotatef(270, 0, 1, 0);
			}
			else if(monitor.getFacing() == ForgeDirection.SOUTH)
			{
				GL11.glTranslated(1, 0, 1);
				GL11.glRotatef(180, 0, 1, 0);
			}
			else if(monitor.getFacing() == ForgeDirection.WEST)
			{
				GL11.glTranslated(0, 0, 1);
				GL11.glRotatef(90, 0, 1, 0);
			}

			GL11.glRotatef(180, 0, 1, 0);
			GL11.glTranslated(-1, 0, -1);
			
			GL11.glPushAttrib(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
			RenderHelper.disableStandardItemLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glTranslated(0, 2, 0);
			Tessellator t = Tessellator.instance;
			
			t.startDrawing(GL11.GL_LINES);
			GL11.glLineWidth(1.5F);
			setColour(index, 0.25);
				
			for(double i = -10; i<=10; i++)
			{
				t.addVertex(-1, i/10, 0);
				t.addVertex(2, i/10, 0);
			}
			for(double i = -10; i<=20; i++)
			{
				t.addVertex(i/10, 1, 0);
				t.addVertex(i/10, -1, 0);
			}
			
			t.draw();
			
			t.startDrawing(GL11.GL_LINES);
			GL11.glLineWidth(2.2F);
			setColour(index, 0.5);
			GL11.glTranslated(0, 0, 0.001);
			t.addVertex(-1, 0, 0);
			t.addVertex(2, 0, 0);
			
			t.draw();

			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			GL11.glPopAttrib();
			GL11.glPopAttrib();

			GL11.glPopMatrix();
	}
	
	public void drawSelectionOverLay(TileEntitySeismicMonitor monitor, int index, int colour)
	{
		if(monitor.getButton(index+1))
 	   {
				GL11.glPushMatrix();
				if(monitor.getFacing() == ForgeDirection.EAST)
				{
					GL11.glTranslated(1, 0, 0);
					GL11.glRotatef(270, 0, 1, 0);
				}
				else if(monitor.getFacing() == ForgeDirection.SOUTH)
				{
					GL11.glTranslated(1, 0, 1);
					GL11.glRotatef(180, 0, 1, 0);
				}
				else if(monitor.getFacing() == ForgeDirection.WEST)
				{
					GL11.glTranslated(0, 0, 1);
					GL11.glRotatef(90, 0, 1, 0);
				}
				RenderEngine renderengine = Minecraft.getMinecraft().renderEngine;
				if(renderengine != null)
				{
					renderengine.bindTexture("/mods/thutconcrete/textures/blocks/greenOverlay.png");
				}
				
				GL11.glPushAttrib(GL11.GL_BLEND);
		        GL11.glEnable(GL11.GL_BLEND);
		        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
				RenderHelper.disableStandardItemLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
	
				double xs = ((double)(3-index&3))/(double)4,ys= ((double)3-(index>>2))/(double)4;
				
				GL11.glTranslated(xs, 1.001, ys);
				GL11.glColor4d(1, 1, 1, 1);
				Tessellator t = Tessellator.instance;
				t.startDrawing(GL11.GL_QUADS);
				
		        t.addVertexWithUV(0.25, 0, 0.25, 0, 0);
		        t.addVertexWithUV(0.25, 0, 0, 0, 1);
		        
		        t.addVertexWithUV(0, 0, 0, 1, 1);
		        t.addVertexWithUV(0, 0, 0.25, 1, 0);
		        
				t.draw();
				
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glPopAttrib();
				GL11.glPopAttrib();
				GL11.glPopMatrix();
 	   }
			
		
	}
	
}