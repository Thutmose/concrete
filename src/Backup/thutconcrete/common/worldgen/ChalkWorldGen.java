package thutconcrete.common.worldgen;

import java.util.Random;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.BlockWorldGen;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class ChalkWorldGen extends BiomeGenBase
{

	public ChalkWorldGen(int par1) {
		super(par1);
        this.theBiomeDecorator.treesPerChunk = -999;
        this.theBiomeDecorator.flowersPerChunk = 4;
        this.theBiomeDecorator.grassPerChunk = 10;
        this.fillerBlock = (byte)BlockWorldGen.instance.blockID;
        this.topBlock = (byte)BlockWorldGen.instance.blockID;
        
	}

	
}
