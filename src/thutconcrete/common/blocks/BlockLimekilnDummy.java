package thutconcrete.common.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.tileentity.TileEntityLimekiln;
import thutconcrete.common.tileentity.TileEntityLimekilnDummy;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockLimekilnDummy extends BlockContainer
{
	
	public static BlockLimekilnDummy instance;
	
	public BlockLimekilnDummy(int blockId)
	{
		super(blockId, Material.rock);
		
		setUnlocalizedName("blockLimekilnDummy");
		setStepSound(Block.soundStoneFootstep);
		setHardness(3.5f);
		instance = this;
	}
	
	@Override
	public int idDropped(int par1, Random par2Random, int par3)
	{
		return Block.brick.blockID;
	}
	
	 @SideOnly(Side.CLIENT)

    /**
     * only called by clickMiddleMouseButton , and passed to inventory.setCurrentItem (along with isCreative)
     */
    public int idPicked(World par1World, int par2, int par3, int par4)
    {
		 return Block.brick.blockID;
    }
	
	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityLimekilnDummy();
	}
	
	@Override
	public void registerIcons(IconRegister iconRegister)
	{
		blockIcon = iconRegister.registerIcon("thutconcrete:brick");
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int par5, int par6)
	{
		TileEntityLimekilnDummy dummy = (TileEntityLimekilnDummy)world.getBlockTileEntity(x, y, z);
		
		if(dummy != null && dummy.getCore() != null)
			dummy.getCore().invalidateMultiblock();
		
		super.breakBlock(world, x, y, z, par5, par6);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9)
	{
		if(player.isSneaking())
			return false;
		
		TileEntityLimekilnDummy dummy = (TileEntityLimekilnDummy)world.getBlockTileEntity(x, y, z);
		
		if(dummy != null && dummy.getCore() != null)
		{
			TileEntityLimekiln core = dummy.getCore();
			return core.getBlockType().onBlockActivated(world, core.xCoord, core.yCoord, core.zCoord, player, par6, par7, par8, par9);
		}
		
		return true;
	}
}