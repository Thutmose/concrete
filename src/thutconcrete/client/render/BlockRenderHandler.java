package thutconcrete.client.render;


import org.lwjgl.opengl.GL11;

import thutconcrete.common.blocks.*;
import thutconcrete.common.tileentity.TileEntityLiftAccess;
import thutconcrete.common.utils.*;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class BlockRenderHandler implements ISimpleBlockRenderingHandler{

	public static final int ID = RenderingRegistry.getNextAvailableRenderId();

	private RenderIRebar rebarRender = new RenderIRebar();
	private RenderTurret turret = new RenderTurret();
//	private

	
	@Override
	public void renderInventoryBlock(Block parblock, int meta, int modelID,RenderBlocks renderer) 
	{
		
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
			
			if(parblock instanceof BlockLiquidREConcrete)
			{
	        	BlockLiquidREConcrete block = (BlockLiquidREConcrete)parblock;
	        	icon = block.getBlockTexture(world, x, y, z, 0);
	        	icon1 = block.theIcon;
	        	
	        	for(int i = 0;i<6;i++)
	        	{
	        		icons[i]=block.getBlockTexture(world, x, y, z, i);
	        	}
	        	
	        	concrete = true;
	        	rebar = world.getBlockMetadata(x, y, z)!=0;
	        }
			else if(parblock instanceof BlockREConcrete)
	        {
	        	BlockREConcrete block = (BlockREConcrete)parblock;
	        	icon = block.getBlockTexture(world, x, y, z, 0);
	        	icon1 = block.theIcon;
	        	
	        	for(int i = 0;i<6;i++)
	        	{
	        		icons[i]=block.getBlockTexture(world, x, y, z, i);
	        	}
	        	
	        	concrete = true;
	        	rebar = world.getBlockMetadata(x, y, z)!=0;
	        }
	        else if(parblock instanceof BlockLiftRail)
	        {
	        	BlockLiftRail block = (BlockLiftRail)parblock;
	        	icon = block.getIcon(0, 0);
	        	icon1 = block.getIcon(0, 0);
	        	rebar = true;
	        }
	        else if(parblock instanceof BlockRebar)
	        {
	        	BlockRebar block = (BlockRebar)parblock;
	        	icon = block.getIcon(0, 0);
	        	icon1 = block.getIcon(0, 0);
	        	rebar = true;
	        }
			
			{
				rebarRender.renderREConcrete(world,parblock, x, y, z, meta, sides,icon, icon1,rebar,concrete, icons);
			}
		}
		if(parblock instanceof BlockSeismicMonitor)
		{
			
		}
		
		
		
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
