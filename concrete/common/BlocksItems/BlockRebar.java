package concrete.common.BlocksItems;

import java.util.List;

import concrete.client.BlockRenderHandler;
import concrete.common.concreteCore;
import concrete.common.utils.IRebar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRebar extends Block implements IRebar{
	public static Block instance;
	@SideOnly(Side.CLIENT)
	public Icon theIcon;
	@SideOnly(Side.CLIENT)
	public Icon blockIcon;
	boolean[] side = new boolean[6];
	
	public BlockRebar(int par1){
		super(par1,Material.iron);
		setHardness(0.1f);
		setUnlocalizedName("rebar");
		setCreativeTab(concreteCore.tabThut);
		setResistance(10.0f);
		this.instance=this;
		setLightOpacity(0);
	}
	
	
	/*
    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     * /
	@Override
    public void addCollisionBoxesToList(World par1World, int x, int y, int z, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
    {
    	boolean connected = false;
    	AxisAlignedBB aabb;
    	
    	//*
		if(side[0]){
    		this.setBlockBounds(0.0F, 0.35F, 0.35F, 0.60F, 0.65F, 0.65F);
    		super.addCollisionBoxesToList(par1World, x, y, z, par5AxisAlignedBB, par6List, par7Entity);
			connected = true;
		}
		if(side[1]){
			this.setBlockBounds(0.4F, 0.35F, 0.35F, 1F, 0.65F, 0.65F);
	        super.addCollisionBoxesToList(par1World, x, y, z, par5AxisAlignedBB, par6List, par7Entity);
			connected = true;
		}
		if(side[2]){
			this.setBlockBounds(0.4F, 0.4F, 0.0F, 0.6F, 0.6F, 0.6F);
            super.addCollisionBoxesToList(par1World, x, y, z, par5AxisAlignedBB, par6List, par7Entity);
			connected = true;
		}
		if(side[3]){
			this.setBlockBounds(0.4F, 0.4F, 0.4F, 0.6F, 0.6F, 1F);
            super.addCollisionBoxesToList(par1World, x, y, z, par5AxisAlignedBB, par6List, par7Entity);
			connected = true;
		}
		if(side[4]){
			this.setBlockBounds(0.4F, 0.0F, 0.4F, 0.6F, 0.6F, 0.6F);
            super.addCollisionBoxesToList(par1World, x, y, z, par5AxisAlignedBB, par6List, par7Entity);
			connected = true;
		}
		if(side[5]){
			this.setBlockBounds(0.4F, 0.4F, 0.4F, 0.6F, 1F, 0.6F);
            super.addCollisionBoxesToList(par1World, x, y, z, par5AxisAlignedBB, par6List, par7Entity);
			connected = true;
		}
		if(!connected){
			this.setBlockBounds(0.0F, 0.4F, 0.0F, 1F, 0.6F, 1F);
            super.addCollisionBoxesToList(par1World, x, y, z, par5AxisAlignedBB, par6List, par7Entity);
		}
		//* /
		
    }
	//*/
	
	

	 /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType()
    {
        return BlockRenderHandler.ID;
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

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("concrete:" + this.getUnlocalizedName2());
        this.theIcon = par1IconRegister.registerIcon("concrete:" + this.getUnlocalizedName2());
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
	
	/**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

	@Override
	public boolean[] sides(World worldObj, int x, int y, int z) {

    	int[][]sides = {{1,0,0},{-1,0,0},{0,0,1},{0,0,-1},{0,1,0},{0,-1,0}};
		for(int i = 0; i<6; i++){
			int id = worldObj.getBlockId(x+sides[i][0], y+sides[i][1], z+sides[i][2]);
			Block block = Block.blocksList[id];
			side[i] = (block instanceof IRebar);
		}
		return side;
	}
 
    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

	@Override
	public Icon getIcon(Block block) {
		return this.blockIcon;
	}
    
	
	
}
