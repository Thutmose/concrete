package thutconcrete.client.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.opengl.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;

public class RenderCuboid {

	// drawArray 8*3*3=72 vs drawElements 8*3 + 3*2*6=60
	// but drawArray requires 72 additions while drawElements 24

	private static final FloatBuffer cubeVerts 
	= ByteBuffer.allocateDirect(0xff).order(ByteOrder.nativeOrder()).asFloatBuffer();
	
	private static final ByteBuffer cubeIndexOrderBufr
	= ByteBuffer.allocateDirect(3*2*6).order(ByteOrder.nativeOrder());//each triangle of the cube
	
	static{
		
		cubeIndexOrderBufr.put(new byte[]{
				//front
				//2 3
				//0 1
				//back
				//6 7
				//4 5
				0,3,1, 0,2,3,//front
				5,7,4, 6,4,7,//back
				3,2,7, 6,7,2,//top
				0,1,4, 5,4,1,//bottom
				2,0,4, 4,6,2,//left
				1,3,7, 7,5,1,//right
		});
		
		cubeVerts.put(new float[]{
		0,0,0, 1,0,0, 0,1,0, 1,1,0,
		0,0,1, 1,0,1, 0,1,1, 1,1,1});		
	}

	private static FloatBuffer emissive
	= ByteBuffer.allocateDirect(4 *4).order(ByteOrder.nativeOrder()).asFloatBuffer();

	static int callCube;

	public RenderCuboid(){
		callCube= GL11.glGenLists(1);
		GL11.glNewList(callCube, GL11.GL_COMPILE);
		drawCube();
		GL11.glEndList();
	}

	/**@param c xyz centers of the cubes
	 * @param color RBG color for each cube*/
	protected static void drawCube(){

		cubeVerts.position(0);
		cubeIndexOrderBufr.position(0);

		GL11.glVertexPointer(3, 0, cubeVerts);
	
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);

		GL11.glDrawElements(GL11.GL_TRIANGLES, cubeIndexOrderBufr);
		
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
	}
	
	public RenderCuboid(Tessellator tessellator, Icon[] icons, double xMin, double zMin, double yMin, double xMax, double zMax, double yMax) {
		tessAddCuboid(tessellator, icons, xMin, zMin, yMin, xMax, zMax, yMax);
	}

	private void tessAddCuboid(Tessellator tessellator, Icon[] icons, double xMin, double zMin, double yMin, double xMax, double zMax, double yMax){
		
        double d0 = (double)icons[3].getMinU();
        double d1 = (double)icons[3].getMaxU();
        double d3 = (double)icons[3].getMinV();
        double d4 = (double)icons[3].getMaxV();

        ///////////////side1///////////////
        tessellator.addVertexWithUV(xMin, yMax, zMax, d0, d3);
        tessellator.addVertexWithUV(xMin, yMin, zMax, d0, d4);
        
        tessellator.addVertexWithUV(xMax, yMin, zMax, d1, d4);
        tessellator.addVertexWithUV(xMax, yMax, zMax, d1, d3);
        
        tessellator.addVertexWithUV(xMax, yMax, zMax, d0, d3);
        tessellator.addVertexWithUV(xMax, yMin, zMax, d0, d4);
        
        tessellator.addVertexWithUV(xMin, yMin, zMax, d1, d4);
        tessellator.addVertexWithUV(xMin, yMax, zMax, d1, d3);
		////////////////////////////////////////* /
        
        d0 = (double)icons[5].getMinU();
        d1 = (double)icons[5].getMaxU();
        d3 = (double)icons[5].getMinV();
        d4 = (double)icons[5].getMaxV();
        
        ///////////////side2///////////////
        tessellator.addVertexWithUV(xMax, yMax, zMin, d0, d3);
        tessellator.addVertexWithUV(xMax, yMin, zMin, d0, d4);
        
        tessellator.addVertexWithUV(xMax, yMin, zMax, d1, d4);
        tessellator.addVertexWithUV(xMax, yMax, zMax, d1, d3);
        
        tessellator.addVertexWithUV(xMax, yMax, zMax, d0, d3);
        tessellator.addVertexWithUV(xMax, yMin, zMax, d0, d4);
        
        tessellator.addVertexWithUV(xMax, yMin, zMin, d1, d4);
        tessellator.addVertexWithUV(xMax, yMax, zMin, d1, d3);
		////////////////////////////////////////* /
        
        
        d0 = (double)icons[4].getMinU();
        d1 = (double)icons[4].getMaxU();
        d3 = (double)icons[4].getMinV();
        d4 = (double)icons[4].getMaxV();
        
        
        
        ///////////////side3///////////////
        tessellator.addVertexWithUV(xMin, yMax, zMax, d0, d3);
        tessellator.addVertexWithUV(xMin, yMin, zMax, d0, d4);
        
        tessellator.addVertexWithUV(xMin, yMin, zMin, d1, d4);
        tessellator.addVertexWithUV(xMin, yMax, zMin, d1, d3);
        
        tessellator.addVertexWithUV(xMin, yMax, zMin, d0, d3);
        tessellator.addVertexWithUV(xMin, yMin, zMin, d0, d4);
        
        tessellator.addVertexWithUV(xMin, yMin, zMax, d1, d4);
        tessellator.addVertexWithUV(xMin, yMax, zMax, d1, d3);
		////////////////////////////////////////*/
        
        
        d0 = (double)icons[2].getMinU();
        d1 = (double)icons[2].getMaxU();
        d3 = (double)icons[2].getMinV();
        d4 = (double)icons[2].getMaxV();
        
        
        ///////////////side4///////////////
        tessellator.addVertexWithUV(xMax, yMax, zMin, d0, d3);
        tessellator.addVertexWithUV(xMax, yMin, zMin, d0, d4);
        
        tessellator.addVertexWithUV(xMin, yMin, zMin, d1, d4);
        tessellator.addVertexWithUV(xMin, yMax, zMin, d1, d3);
        
        tessellator.addVertexWithUV(xMin, yMax, zMin, d0, d3);
        tessellator.addVertexWithUV(xMin, yMin, zMin, d0, d4);
        
        tessellator.addVertexWithUV(xMax, yMin, zMin, d1, d4);
        tessellator.addVertexWithUV(xMax, yMax, zMin, d1, d3);
		////////////////////////////////////////*/
        
        
        d0 = (double)icons[0].getMinU();
        d1 = (double)icons[0].getMaxU();
        d3 = (double)icons[0].getMinV();
        d4 = (double)icons[0].getMaxV(); 
        
        
        
        ///////////////side5///////////////

        tessellator.addVertexWithUV(xMax, yMin, zMin, d0, d3);
        tessellator.addVertexWithUV(xMax, yMin, zMax, d0, d4);
        
        tessellator.addVertexWithUV(xMin, yMin, zMax, d1, d4);
        tessellator.addVertexWithUV(xMin, yMin, zMin, d1, d3);
        
        
        tessellator.addVertexWithUV(xMax, yMin, zMax, d0, d3);
        tessellator.addVertexWithUV(xMax, yMin, zMin, d0, d4);
        
        tessellator.addVertexWithUV(xMin, yMin, zMin, d1, d4);
        tessellator.addVertexWithUV(xMin, yMin, zMax, d1, d3);
       //* 
       
		////////////////////////////////////////*/   
        
        d0 = (double)icons[1].getMinU();
        d1 = (double)icons[1].getMaxU();
        d4 = (double)icons[1].getMinV();
        d3 = (double)icons[1].getMaxV();  
        
        
        
        ///////////////side6///////////////
        
        tessellator.addVertexWithUV(xMax, yMax, zMax, d0, d3);
        tessellator.addVertexWithUV(xMax, yMax, zMin, d0, d4);
        
        tessellator.addVertexWithUV(xMin, yMax, zMin, d1, d4);
        tessellator.addVertexWithUV(xMin, yMax, zMax, d1, d3);
       //* 
        
        tessellator.addVertexWithUV(xMin, yMax, zMax, d1, d4);
        tessellator.addVertexWithUV(xMin, yMax, zMin, d1, d3);
        
        tessellator.addVertexWithUV(xMax, yMax, zMin, d0, d3);
        tessellator.addVertexWithUV(xMax, yMax, zMax, d0, d4);
       
		////////////////////////////////////////*/          
	}
	
	private void addCuboid(Tessellator tessellator, Icon[] icons, double xMin, double zMin, double yMin, double xMax, double zMax, double yMax){
		
		GL11.glPushMatrix();
		GL11.glTranslated((xMin+xMax)/2, (yMin+yMax)/2, (zMin+zMax)/2);
		
		//GL11.glPushAttrib(GL11.GL_BLEND);
	//	GL11.glPushAttrib(GL11.GL_EMISSION);
	//	GL11.glPushAttrib(GL11.GL_LIGHTING);

	//	GL11.glEnable(GL11.GL_BLEND);
	//	GL11.glEnable(GL11.GL_LIGHTING);

		emissive.put(0, 0);
		emissive.put(1, 1);
		emissive.put(2, 1);
		emissive.put(3, 1);
		emissive.position(0);
		
	//	GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

	//	GL11.glColor4f(.83f,.83f,.83f,.65f);
	//	GL11.glColor4f(0f,1f,1f,1f);

	//	 GL11.glDisable(GL11.GL_COLOR_MATERIAL);
       
	//	 OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0xf0, 0x00);
		//////////
		 GL11.glPushMatrix();
		 GL11.glTranslatef(0,0,0);
		 GL11.glCallList(callCube);
		 GL11.glPopMatrix();
		//////////
		

	//	 GL11.glPopAttrib();
	//	 GL11.glPopAttrib();
	//	 GL11.glPopAttrib();
		 GL11.glPopMatrix();       
	}

	
	
}
