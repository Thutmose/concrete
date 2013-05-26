package thutconcrete.common.corehandlers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thutconcrete.common.blocks.BlockLava;
import thutconcrete.common.blocks.BlockLiquidConcrete;
import thutconcrete.common.blocks.BlockWater;
import thutconcrete.common.items.ItemBucketConcrete;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.liquids.LiquidDictionary;

public class LiquidHandler {

	@ForgeSubscribe
	public void onBucketFill(FillBucketEvent event) {

		ItemStack result = fillCustomBucket(event.world, event.target);

		if (result == null)
			return;

		event.result = result;
		event.setResult(Result.ALLOW);
	}

	public ItemStack fillCustomBucket(World world, MovingObjectPosition pos) 
	{

		int blockID = world.getBlockId(pos.blockX, pos.blockY, pos.blockZ);
		
		boolean isLava = false;
		for(int i = 0;i<3;i++)
		{
			isLava = isLava||blockID == BlockLava.getInstance(i).blockID;
		}
		
		if ((blockID==BlockLiquidConcrete.instance.blockID)
				&& world.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ) == 0) 
		{

			world.setBlock(pos.blockX, pos.blockY, pos.blockZ, 0);

			return new ItemStack(ItemBucketConcrete.instance);
		} 
		else if ((isLava)
				&& world.getBlockMetadata(pos.blockX, pos.blockY, pos.blockZ) == 0) 
		{

			world.setBlock(pos.blockX, pos.blockY, pos.blockZ, 0);

			return new ItemStack(Item.bucketLava);
		} 
		
		
		else
			return null;
	}
	
	
	@ForgeSubscribe
	@SideOnly(Side.CLIENT)
	public void textureHook(TextureStitchEvent.Post event) {
		LiquidDictionary.getCanonicalLiquid("mafic lava").setRenderingIcon(BlockHandler.blocks[6].getBlockTextureFromSide(1)).setTextureSheet("/terrain.png");
		LiquidDictionary.getCanonicalLiquid("intermediate lava").setRenderingIcon(BlockHandler.blocks[8].getBlockTextureFromSide(1)).setTextureSheet("/terrain.png");
		LiquidDictionary.getCanonicalLiquid("felsic lava").setRenderingIcon(BlockHandler.blocks[10].getBlockTextureFromSide(1)).setTextureSheet("/terrain.png");
		LiquidDictionary.getCanonicalLiquid("concrete").setRenderingIcon(BlockHandler.blocks[8].getIcon(0,0)).setTextureSheet("/terrain.png");

	}

}
