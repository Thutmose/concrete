package thutconcrete.client;


import org.lwjgl.opengl.GL11;

import thutconcrete.common.blocks.*;
import thutconcrete.common.utils.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class BlockRenderHandler implements ISimpleBlockRenderingHandler{

	public static final int ID = RenderingRegistry.getNextAvailableRenderId();
	
	public void renderREConcrete(Block parblock, double x, double y, double z, int meta, boolean[] sides)
    {
        Tessellator tessellator = Tessellator.instance;
        float f = 1.0F;
        Icon icon = parblock.getIcon(0, 0);
        Icon icon1 = parblock.getIcon(0, 0);
        boolean concrete = false;
        boolean rebar = false;
        if(parblock instanceof BlockLiquidREConcrete){
        	BlockLiquidREConcrete block = (BlockLiquidREConcrete)parblock;
        	icon = block.getIcon(0, 0);
        	icon1 = block.theIcon;
        	concrete = true;
        	rebar = true;
        }
        if(parblock instanceof BlockREConcrete){
        	BlockREConcrete block = (BlockREConcrete)parblock;
        	icon = block.getIcon(0, 0);
        	icon1 = block.theIcon;
        	concrete = true;
        	rebar = true;
        }
        if(parblock instanceof BlockRebar){
        	BlockRebar block = (BlockRebar)parblock;
        	icon = block.getIcon(0, 0);
        	icon1 = block.getIcon(0, 0);
        	rebar = true;
        	
        }
        
        tessellator.setColorOpaque_F(f, f, f);
        
        int j = meta & 15;
        double yCCmax =y+ ((1 + j)) / 16.0F;
        
        double xCCmin = 0+x, zCCmin = 0+z, yCCmin = 0+y, xCCmax = 1+x, zCCmax = 1+z;
        
        if(rebar){
        	tessAddRebar(parblock,tessellator, icon1, x,y,z, sides);
        }
        if(concrete)
        	tessAddCuboid(tessellator, icon, xCCmin, zCCmin, yCCmin, xCCmax, zCCmax, yCCmax);
        
    }
	
	private void tessAddRebar(Block parblock, Tessellator tessellator, Icon icon, double x, double y, double z, boolean[] sides){
		if(parblock instanceof IRebar){
			boolean connected = false;
			if(sides[0]){
				xHorizontalRebar(tessellator, icon, x, y, z, 1, 0.4);
				connected = true;
			}
			if(sides[1]){
				xHorizontalRebar(tessellator, icon, x, y, z, 0, 0.6);
				connected = true;
			}
			if(sides[2]){
				zHorizontalRebar(tessellator, icon, x, y, z, 1, 0.4);
				connected = true;
			}
			if(sides[3]){
				zHorizontalRebar(tessellator, icon, x, y, z, 0, 0.6);
				connected = true;
			}
			if(sides[4]){
				columnRebar(tessellator, icon, x, y, z, 1, 0.4);
				connected = true;
			}
			if(sides[5]){
				columnRebar(tessellator, icon, x, y, z, 0, 0.6);
				connected = true;
			}
			if(!connected){
				crossRebar(tessellator, icon, x, y, z);
			}
		}
	}

	private void crossRebar(Tessellator tessellator, Icon icon, double x, double y, double z){
		xHorizontalRebar(tessellator, icon, x, y, z, 1,0);
		zHorizontalRebar(tessellator, icon, x, y, z, 1,0);
		columnRebar(tessellator, icon, x, y, z,0.6,0.4);
	}
	
	
	private void xHorizontalRebar(Tessellator tessellator, Icon icon, double x, double y, double z, double length, double min){

		double dT = 0.05;
		double dS = 0.1;
		double dl = 0.005D;
		
		double 	yMin = y+(0.5-dS-dT),
				xMin = x+dl+min,
				zMin = z+(0.5-dS-dT),
				yMax = y+(0.5-dS+dT),
				xMax = x+length-dl,
				zMax = z+(0.5-dS+dT);
		tessAddCuboid(tessellator, icon, xMin, zMin, yMin, xMax, zMax, yMax);
		
		yMin = y+(0.5+dS-dT);
		xMin = x+dl+min;
		zMin = z+(0.5+dS-dT);
		yMax = y+(0.5+dS+dT);
		xMax = x+length-dl;
		zMax = z+(0.5+dS+dT);
		tessAddCuboid(tessellator, icon, xMin, zMin, yMin, xMax, zMax, yMax);
		
		yMin = y+(0.5-dS-dT);
		xMin = x+dl+min;
		zMin = z+(0.5+dS-dT);
		yMax = y+(0.5-dS+dT);
		xMax = x+length-dl;
		zMax = z+(0.5+dS+dT);
		tessAddCuboid(tessellator, icon, xMin, zMin, yMin, xMax, zMax, yMax);
		
		yMin = y+(0.5+dS-dT);
		xMin = x+dl+min;
		zMin = z+(0.5-dS-dT);
		yMax = y+(0.5+dS+dT);
		xMax = x+length-dl;
		zMax = z+(0.5-dS+dT);
		tessAddCuboid(tessellator, icon, xMin, zMin, yMin, xMax, zMax, yMax);
	}
	
	private void zHorizontalRebar(Tessellator tessellator, Icon icon, double x, double y, double z, double length, double min){

		double dT = 0.05;
		double dS = 0.1;
		double dl = 0.005D;
		
		double 	yMin = y+(0.5-dS-dT),
				zMin = z+dl+min,
				xMin = x+(0.5-dS-dT),
				yMax = y+(0.5-dS+dT),
				zMax = z+length-dl,
				xMax = x+(0.5-dS+dT);
		tessAddCuboid(tessellator, icon, xMin, zMin, yMin, xMax, zMax, yMax);
		
		yMin = y+(0.5+dS-dT);
		zMin = z+dl+min;
		xMin = x+(0.5+dS-dT);
		yMax = y+(0.5+dS+dT);
		zMax = z+length-dl;
		xMax = x+(0.5+dS+dT);
		tessAddCuboid(tessellator, icon, xMin, zMin, yMin, xMax, zMax, yMax);
		
		yMin = y+(0.5-dS-dT);
		zMin = z+dl+min;
		xMin = x+(0.5+dS-dT);
		yMax = y+(0.5-dS+dT);
		zMax = z+length-dl;
		xMax = x+(0.5+dS+dT);
		tessAddCuboid(tessellator, icon, xMin, zMin, yMin, xMax, zMax, yMax);
		
		yMin = y+(0.5+dS-dT);
		zMin = z+dl+min;
		xMin = x+(0.5-dS-dT);
		yMax = y+(0.5+dS+dT);
		zMax = z+length-dl;
		xMax = x+(0.5-dS+dT);
		tessAddCuboid(tessellator, icon, xMin, zMin, yMin, xMax, zMax, yMax);
	}
	
	
	private void columnRebar(Tessellator tessellator, Icon icon, double x, double y, double z, double length, double min){

		double dT = 0.05;
		double dS = 0.1;
		double dl = 0.005D;
		
		double 	xMin = x+(0.5-dS-dT),
				yMin = y+dl+min,
				zMin = z+(0.5-dS-dT),
				xMax = x+(0.5-dS+dT),
				yMax = y+length-dl,
				zMax = z+(0.5-dS+dT);
		tessAddCuboid(tessellator, icon, xMin, zMin, yMin, xMax, zMax, yMax);
		
		xMin = x+(0.5+dS-dT);
		yMin = y+dl+min;
		zMin = z+(0.5+dS-dT);
		xMax = x+(0.5+dS+dT);
		yMax = y+length-dl;
		zMax = z+(0.5+dS+dT);
		tessAddCuboid(tessellator, icon, xMin, zMin, yMin, xMax, zMax, yMax);
		
		xMin = x+(0.5-dS-dT);
		yMin = y+dl+min;
		zMin = z+(0.5+dS-dT);
		xMax = x+(0.5-dS+dT);
		yMax = y+length-dl;
		zMax = z+(0.5+dS+dT);
		tessAddCuboid(tessellator, icon, xMin, zMin, yMin, xMax, zMax, yMax);
		
		xMin = x+(0.5+dS-dT);
		yMin = y+dl+min;
		zMin = z+(0.5-dS-dT);
		xMax = x+(0.5+dS+dT);
		yMax = y+length-dl;
		zMax = z+(0.5-dS+dT);
		tessAddCuboid(tessellator, icon, xMin, zMin, yMin, xMax, zMax, yMax);
	}
	
	
	private void crossColumnRebar(Tessellator tessellator, Icon icon, int x, int y, int z){

		crossRebar(tessellator, icon, x, y, z);
		columnRebar(tessellator, icon, x, y, z,1,0);
		
	}
	
	private void tessAddCuboid(Tessellator tessellator, Icon icon, double xMin, double zMin, double yMin, double xMax, double zMax, double yMax){
		
        double d0 = (double)icon.getMinU();
        double d1 = (double)icon.getInterpolatedU(16.0D);
        double d2 = (double)icon.getMaxU();
        double d3 = (double)icon.getMinV();
        double d4 = (double)icon.getMaxV();

        double d5 = (double)icon.getInterpolatedU(7.0D);
        double d6 = (double)icon.getInterpolatedU(9.0D);
        double d7 = (double)icon.getMinV();
        double d8 = (double)icon.getInterpolatedV(8.0D);
        double d9 = (double)icon.getMaxV();
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
        ///////////////side5///////////////
        
        tessellator.addVertexWithUV(xMax, yMin, zMax, d0, d3);
        tessellator.addVertexWithUV(xMax, yMin, zMin, d0, d4);
        
        tessellator.addVertexWithUV(xMin, yMin, zMin, d1, d4);
        tessellator.addVertexWithUV(xMin, yMin, zMax, d1, d3);
       //* 
        
        tessellator.addVertexWithUV(xMin, yMin, zMax, d1, d4);
        tessellator.addVertexWithUV(xMin, yMin, zMin, d1, d3);
        
        tessellator.addVertexWithUV(xMax, yMin, zMin, d0, d3);
        tessellator.addVertexWithUV(xMax, yMin, zMax, d0, d4);
       
		////////////////////////////////////////*/       
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
	
	public void renderInventoryREC(Block block, int metadata){
		boolean[] sides = {false, false, false, false, false, false};
		double x=0.5,y=0.5,z=0.5;
		renderREConcrete(block, x, y, z, metadata, sides);
	}
	
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID,RenderBlocks renderer) {
		
		//renderInventoryREC(block, metadata);
		renderer.renderBlockAsItem(block, metadata, 1);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block block, int modelId, RenderBlocks renderer) {
		
		int meta = world.getBlockMetadata(x, y, z);
		boolean[] sides = null;
		if(block instanceof IRebar){
			IRebar temp = (IRebar) block;
			sides = temp.sides(world, x, y, z);
		}
		renderREConcrete(block, x, y, z, meta, sides);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return false;
	}

	@Override
	public int getRenderId() {
		return ID;
	}

	
}
