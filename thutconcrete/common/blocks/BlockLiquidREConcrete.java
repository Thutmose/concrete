package thutconcrete.common.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import thutconcrete.client.BlockRenderHandler;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.utils.IRebar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLiquidREConcrete extends Block16Fluid implements IRebar{
	
	public static Block[] instances = new BlockLiquidREConcrete[16];
	public Block instance;
	public int colourid;
	static Material wetConcrete = (new Material(MapColor.stoneColor));
	Integer[][] data;
	
	public BlockLiquidREConcrete(int par1, int par2) {
		super(par1, wetConcrete);
		colourid = par2;
		setUnlocalizedName("REconcreteLiquid" + colourid);
		this.setResistance((float) 0.0);
		this.instance = this;
		this.instances[colourid] = this;
	}
	
	public static Block getInstance(int colorid)
	{
		return BlockLiquidREConcrete.instances[colorid];
	}
	
	@Override
    public void onBlockPlacedBy(World worldObj,int x,int y,int z,EntityLiving entity, ItemStack item){
		worldObj.setBlockMetadataWithNotify(x, y, z, 15, 3);

		if(data==null){
			setData();
			}
    	this.setTickRandomly(true);
    	super.onBlockPlacedBy(worldObj, x, y, z, entity, item);
    }
	
	@Override
    public void onBlockAdded(World worldObj, int x, int y, int z) {
		if(data==null){
			setData();
			}
    	this.setTickRandomly(true);
    }
	
	@Override
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random){

		if(data==null){
			setData();
			}
		 super.updateTick(worldObj, x, y, z, par5Random);
		 
	}
	
	private void setData(){
		
		List<Integer> combinationList = new ArrayList<Integer>();
		
		//Rebar to make this colour.
		combinationList.add(BlockRebar.instance.blockID+4096*BlockLiquidREConcrete.getInstance(colourid).blockID);
		
		//RE Concrete to make this colour
		combinationList.add(BlockLiquidREConcrete.getInstance(colourid).blockID+4096*BlockLiquidREConcrete.getInstance(colourid).blockID);
		combinationList.add(BlockREConcrete.getInstance(colourid).blockID+4096*BlockLiquidREConcrete.getInstance(colourid).blockID);
		
		for(int i = 0;i<16;i++){
			int key = Math.max(colourid, i)*16+Math.min(colourid, i);
			int colour = i;
			if(ConcreteCore.colourMap.containsKey(key)){
				colour = ConcreteCore.colourMap.get(key);
				//RE Concrete Mix
				combinationList.add(BlockLiquidREConcrete.getInstance(i).blockID+4096*BlockLiquidREConcrete.getInstance(colour).blockID);
				combinationList.add(BlockREConcrete.getInstance(i).blockID+4096*BlockLiquidREConcrete.getInstance(colour).blockID);
			}else{
				//RE Concrete Mix
				combinationList.add(BlockLiquidREConcrete.getInstance(colour).blockID+4096*BlockLiquidREConcrete.getInstance(colour).blockID);
				combinationList.add(BlockREConcrete.getInstance(colour).blockID+4096*BlockLiquidREConcrete.getInstance(colour).blockID);
			}
		}
		
		data = new Integer[][]{
				{BlockRebar.instance.blockID,0,BlockREConcrete.getInstance(colourid).blockID},
				{BlockREConcrete.getInstance(colourid).blockID+4096*4,0+4096,BlockFullSolidConcrete.instance.blockID+4096*100,BlockFullSolidREConcrete.instance.blockID+4096*100},
				combinationList.toArray(new Integer[0]),
			};
			fluid16Blocks.put(BlockLiquidREConcrete.getInstance(colourid).blockID,data);
	}
	
	

	/////////////////////////////////////////Block Bounds Stuff//////////////////////////////////////////////////////////
	
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
    	this.setBoundsByMeta(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }

	@Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
    	int meta = par1World.getBlockMetadata(par2, par3, par4);
    	
        int l = par1World.getBlockMetadata(par2, par3, par4) & 15;
        float f = 0.0625F;
        return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ,
        								(double)par2 + this.maxX, (double)((float)par3 + (float)l * f), (double)par4 + this.maxZ);
    }
	
	
   /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:wetConcrete_"+colourid);
    	this.theIcon = par1IconRegister.registerIcon("thutconcrete:" + "rebar");
    }
	
    @SideOnly(Side.CLIENT)

    /**
     * Returns the texture index of the thin side of the pane.
     */
    public Icon getSideTextureIndex()
    {
        return this.theIcon;
    }

    @SideOnly(Side.CLIENT)
    public Icon theIcon;
	
	 @SideOnly(Side.CLIENT)

	    /**
	     * Retrieves the block texture to use based on the display side. Args: iBlockAccess, x, y, z, side
	     */
	    public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
	    {
	           return this.blockIcon;
	    }
	

	    @Override
	    public int quantityDropped(int meta, int fortune, Random random)
	    {
	        return 0;
	    }
	    
	    /**
	     * The type of render function that is called for this block
	     */
	    @Override
	    public int getRenderType()
	    {
	        return BlockRenderHandler.ID;
	    }

		public boolean[] sides(IBlockAccess worldObj, int x, int y, int z) {
			boolean[] side = new boolean[6];
	    	int[][]sides = {{1,0,0},{-1,0,0},{0,0,1},{0,0,-1},{0,1,0},{0,-1,0}};
			for(int i = 0; i<6; i++){
				int id = worldObj.getBlockId(x+sides[i][0], y+sides[i][1], z+sides[i][2]);
				Block block = Block.blocksList[id];
				side[i] = (block instanceof IRebar);
			}
			return side;
		}

		@Override
		public boolean[] sides(World worldObj, int x, int y, int z) {
			boolean[] side = new boolean[6];
	    	int[][]sides = {{1,0,0},{-1,0,0},{0,0,1},{0,0,-1},{0,1,0},{0,-1,0}};
			for(int i = 0; i<6; i++){
				int id = worldObj.getBlockId(x+sides[i][0], y+sides[i][1], z+sides[i][2]);
				Block block = Block.blocksList[id];
				side[i] = (block instanceof IRebar);
			}
			return side;
		}

		@Override
		public Icon getIcon(Block block) {
			return this.blockIcon;
		}

}