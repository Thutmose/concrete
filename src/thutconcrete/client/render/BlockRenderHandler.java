package thutconcrete.client.render;


import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.opengl.GL11;

import thutconcrete.common.blocks.*;
import thutconcrete.common.tileentity.TileEntityLiftAccess;
import thutconcrete.common.utils.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class BlockRenderHandler implements ISimpleBlockRenderingHandler{

	public static final int ID = RenderingRegistry.getNextAvailableRenderId();

	private RenderIRebar rebarRender = new RenderIRebar();
	private RenderTurret turret = new RenderTurret();
	private RenderTEB16F rebar = new RenderTEB16F();
//	private

	
	@Override
	public void renderInventoryBlock(Block parblock, int meta, int modelID,RenderBlocks renderer) 
	{
		
		String texture = parblock instanceof BlockLiftRail?"liftRail":"rebar";
		
		glPushMatrix();
		glTranslated(0, 0, 0);
		glScaled(1.5, 1.5, 1.5);
		
		GL11.glPushAttrib(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		RenderHelper.disableStandardItemLighting();
//		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
//		
		Tessellator t = Tessellator.instance;
		
		t.startDrawing(GL11.GL_QUADS);
		
		FMLClientHandler.instance().getClient().renderEngine.bindTexture("/mods/thutconcrete/textures/blocks/"+texture+".png");
		
		rebar.tessAddRebar(t, 0, 0, 0, new boolean[6], true);
		
		t.draw();
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopAttrib();
		GL11.glPopAttrib();
		glPopMatrix();
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z,
			Block parblock, int modelId, RenderBlocks renderer) {

        boolean concrete = false;
        boolean rebar = false;
		int meta = world.getBlockMetadata(x, y, z);
		boolean[] sides = null;
		Icon icon = parblock.getIcon(0, 0);
		Icon icon1 = parblock.getIcon(0, 0);
		Icon[] icons = new Icon[6];
		
		
		if(parblock instanceof IRebar)
		{
			IRebar temp = (IRebar) parblock;
			sides = temp.sides(world, x, y, z);

	        if(parblock instanceof BlockLiftRail)
	        {
	        	BlockLiftRail block = (BlockLiftRail)parblock;
	        	icon = block.getIcon(0, 0);
	        	rebar = true;
	        }
	        else if(parblock instanceof BlockRebar)
	        {
	        	BlockRebar block = (BlockRebar)parblock;
	        	icon = block.getIcon(0, 0);
	        	rebar = true;
	        }
			
			{
				rebarRender.renderREConcrete(parblock, x, y, z,sides,icon, rebar);
			}
		}
		if(parblock instanceof BlockMachine)
		{
			
		}
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory() {
		return true;
	}

	@Override
	public int getRenderId() {
		return ID;
	}

	
}
