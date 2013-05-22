package thutconcrete.common.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.utils.ExplosionCustom;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BlockBoom extends Block implements ITileEntityProvider
{
	
	public static BlockBoom instance;
	
    
	public BlockBoom(int par1) {
		super(par1, Material.ground);
		setUnlocalizedName("boom");
		setHardness(100000.0f);
		setResistance(10000000.0f);
		this.instance = this;
		this.setCreativeTab(ConcreteCore.tabThut);
	}

	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityVolcano();
	}
 
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:lava");
    }
	
	
}