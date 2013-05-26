package thutconcrete.client;

import thutconcrete.common.blocks.Block16Fluid;
import thutconcrete.common.utils.IMultiPaintableBlock;
import thutconcrete.common.utils.IMultiPaintableTE;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ForgeDirection;

public class RenderIMultiTextureBlock 
{
	public void Render(IBlockAccess world, int x, int y, int z, Block parblock)
	{
      
		if(parblock instanceof IMultiPaintableBlock)
		{
			
		    Tessellator tess = Tessellator.instance;
	        boolean animated = false;
	       
	        tess.setBrightness(parblock.getMixedBrightnessForBlock(world, (int)x, (int)y, (int)z));
	        tess.setColorRGBA(255, 255, 255, 255);
	        
			IMultiPaintableBlock block = (IMultiPaintableBlock) parblock;
			IMultiPaintableTE te = (IMultiPaintableTE)world.getBlockTileEntity(x, y, z);
			
			Icon[][] iconsAA = te.iconArrayArray;
			int[][] intAA = te.metaArrayArray;
			
			for(ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
			{
				tessAddFace(tess, intAA[side.ordinal()], x,y,z,side);
			}
		}
	}

	private void tessAddFace(Tessellator tessellator, int[] icons, double x, double y, double z, ForgeDirection side)
	{
		int pixels = 16;
		for(int i = 0; i<256; i++)
		{
			System.out.println(icons.length);
			double u = ((double)(i&15))/pixels;
			double v = ((double)(i>>4))/pixels;
			
			double u1 = ((double)(i&15) + 1)/pixels;
			double v1 = ((double)(i>>4) + 1)/pixels;
		
			Icon icon = Block16Fluid.iconsArray[icons[i]&15];
			System.out.println(icons[i]+" "+icon);
			
	        double d0 = (double)icon.getMinU();
	        double d1 = (double)d0+icon.getMinU();
	        double d3 = (double)icon.getMinV();
	        double d4 = (double)d3+icon.getMinV();

	        double  xMin= (side==ForgeDirection.WEST? 
	        				0:side==ForgeDirection.EAST?
	        						1:u)+x,
	        		zMin= (side==ForgeDirection.NORTH? 
	        				0:side==ForgeDirection.SOUTH?
	        						1:v)+z,
	        		yMin= (side==ForgeDirection.DOWN? 
	        				0:side==ForgeDirection.UP?
	        						1:0)+y, 
	        		xMax= (side==ForgeDirection.WEST? 
	        				0:side==ForgeDirection.EAST?
	        						1:u1)+x,
	        		zMax= (side==ForgeDirection.NORTH?
	        				0:side==ForgeDirection.SOUTH?
	        						1:v1)+z,
	        		yMax= (side==ForgeDirection.DOWN? 
	        				0:side==ForgeDirection.UP?
	        						1:1)+y;
	        
			switch (side)
	        {
	                case UP:
	                {
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
	                case DOWN:
	                {
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
	                }
	                case NORTH:
	                {
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
	                }
	                case SOUTH:
	                {
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
	                }
	                case EAST:
	                {
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
	                }
	                case WEST:
	                {
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
	                }
	                default:
	                {
	                       
	                }
	        }
		}
        
	}
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
