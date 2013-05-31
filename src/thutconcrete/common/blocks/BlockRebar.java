package thutconcrete.common.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import thutconcrete.client.BlockRenderHandler;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.utils.IRebar;
import thutconcrete.common.utils.ThreadSafeWorldOperations;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockRebar extends Block implements IRebar
{
	public static Block instance;
	@SideOnly(Side.CLIENT)
	public Icon theIcon;
	@SideOnly(Side.CLIENT)
	public Icon itemIcon;
	boolean[] side = new boolean[6];
	

	public ThreadSafeWorldOperations safe = new ThreadSafeWorldOperations();
	public static int MAX_PLACEMENT_RANGE = 64;
	
	public BlockRebar(int par1)
	{
		super(par1,Material.iron);
		setHardness((float) 10.0);
		setUnlocalizedName("rebar");
		setCreativeTab(ConcreteCore.tabThut);
		this.setBlockBounds(0, 0, 0, 0, 0, 0);
		setResistance(10.0f);
		this.instance=this;
		setLightOpacity(0);
	}
	
	
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9)
    {
    	boolean placed = false;
    	ItemStack item = player.getHeldItem();
    	if(item!=null)
    	{
	    	int itemID = item.itemID;
	    	if(itemID<4095)
	    	{
		    	if(Block.blocksList[itemID] instanceof IRebar)
		    	{
			    	if(placeBlock(world, x, y, z, itemID, item.getItemDamage(), ForgeDirection.getOrientation(side)))
			    	{
			    		placed = true;
		    				if(!player.capabilities.isCreativeMode)
		    					item.splitStack(1);
			    	}
		    	}
		    	if(Block.blocksList[itemID] instanceof BlockLiquidConcrete)
		    	{
		    		placed = true;
			    	world.setBlock(x, y, z, BlockLiquidREConcrete.instance.blockID,0,3);
			    	world.scheduleBlockUpdate(x, y, z, BlockLiquidREConcrete.instance.blockID, 5);
					if(!player.capabilities.isCreativeMode)
						item.splitStack(1);
			    	
		    	}
	    	}
	    	
    	}
        return placed;
    }
	

    public boolean placeBlock(World worldObj, int x, int y, int z, int rebarID, int rebarMeta, ForgeDirection side)
    {
    	int dx = side.offsetX, dy = side.offsetY, dz = side.offsetZ;
    	while(Math.abs(dx)<MAX_PLACEMENT_RANGE&&Math.abs(dy)<MAX_PLACEMENT_RANGE&&Math.abs(dz)<MAX_PLACEMENT_RANGE)
    	{
    		if(dy+y>worldObj.getActualHeight()) return false;
        	int id = safe.safeGetID(worldObj,x+dx, y+dy, z+dz);
        	Block block = safe.safeGetBlock(worldObj, x+dx, y+dy, z+dz);
        	if(id==0||(safe.isLiquid(worldObj,x+dx, y+dy, z+dz)))
    		{
    			safe.safeSet(worldObj, x+dx, y+dy, z+dz, rebarID, rebarMeta);
    			return true;
    		}
        	else if (id!=rebarID)
        	{
        		return false;
        	}
        	
			dy+=side.offsetY;
			dx+=side.offsetX;
			dz+=side.offsetZ;
		
    	}
    	return false;
    }

	
    /**
     * Adds all intersecting collision boxes to a list. (Be sure to only add boxes to the list if they intersect the
     * mask.) Parameters: World, X, Y, Z, mask, list, colliding entity
     */
	@Override
    public void addCollisionBoxesToList(World worldObj, int x, int y, int z, AxisAlignedBB aaBB, List list, Entity par7Entity)
    {
		side = sides(worldObj,x,y,z);
		
		if(!(side[0]||side[1]||side[2]||side[3]||side[4]||side[5]))
			side = new boolean[] {true, true, true, true, false, false};
		
    	AxisAlignedBB aabb;
    	int n = 5;
    	
        for (ForgeDirection fside : ForgeDirection.VALID_DIRECTIONS)
        {
                AxisAlignedBB coll = getBoundingBoxForSide(fside).offset(x, y, z);
                if (aaBB.intersectsWith(coll)&&this.side[n])
                        list.add(coll);
                n--;
        }
    }

	
	
    /**
     * Checks if a player or entity can use this block to 'climb' like a ladder.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y position
     * @param z Z position
     * @return True if the block should act like a ladder
     */
    public boolean isLadder(World world, int x, int y, int z)
    {
    	
    	side = sides(world,x,y,z);
		
		if(!(side[0]||side[1]||side[2]||side[3]||side[4]||side[5]))
			side = new boolean[] {true, true, true, true, false, false};
		
        return side[0]||side[1]||side[2]||side[3];
    }
	
	
	
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int x, int y, int z)
    {
		
		side = sides(par1IBlockAccess,x,y,z);
		
		if(!(side[0]||side[1]||side[2]||side[3]||side[4]||side[5]))
			side = new boolean[] {true, true, true, true, false, false};
		setBlockBounds(0.35F, 0.35F, 0.35F, 0.65F, 0.65F, 0.65F);
    }

    public AxisAlignedBB getBoundingBoxForSide(ForgeDirection fside)
    {
            switch (fside)
            {
                    case UP:
                    {
                            return AxisAlignedBB.getBoundingBox(0.35F, 0.4F, 0.35F, 0.65F, 1F, 0.65F);
                    }
                    case DOWN:
                    {
                            return AxisAlignedBB.getBoundingBox(0.35F, 0.0F, 0.35F, 0.65F, 0.6F, 0.65F);
                    }
                    case NORTH:
                    {
                            return AxisAlignedBB.getBoundingBox(0.35F, 0.35F, 0.0F, 0.65F, 0.65F, 0.6F);
                    }
                    case SOUTH:
                    {
                            return AxisAlignedBB.getBoundingBox(0.35F, 0.35F, 0.4F, 0.65F, 0.65F, 1F);
                    }
                    case EAST:
                    {
                            return AxisAlignedBB.getBoundingBox(0.4F, 0.35F, 0.35F, 1F, 0.65F, 0.65F);
                    }
                    case WEST:
                    {
                            return AxisAlignedBB.getBoundingBox(0.0F, 0.35F, 0.35F, 0.60F, 0.65F, 0.65F);
                    }
                    default:
                    {
                            return AxisAlignedBB.getBoundingBox(0f, 0f, 0f, 1f, 1f, 1f);
                    }
            }
    }
    
    private void setBlockBoundsForSide(int x, int y, int z, ForgeDirection side)
    {
            switch (side)
	        {
		            case UP:
		            {
		                    setBlockBounds(0.35F, 0.4F, 0.35F, 0.65F, 1F, 0.65F);
		                    break;
		            }
		            case DOWN:
		            {
		                    setBlockBounds(0.35F, 0.0F, 0.35F, 0.65F, 0.6F, 0.65F);
		                    break;
		            }
		            case NORTH:
		            {
		                    setBlockBounds(0.35F, 0.35F, 0.0F, 0.65F, 0.65F, 0.6F);
		                    break;
		            }
		            case SOUTH:
		            {
		                    setBlockBounds(0.35F, 0.35F, 0.4F, 0.65F, 0.65F, 1F);
		                    break;
		            }
		            case EAST:
		            {
		                    setBlockBounds(0.4F, 0.35F, 0.35F, 1F, 0.65F, 0.65F);
		                    break;
		            }
		            case WEST:
		            {
		                    setBlockBounds(0.0F, 0.35F, 0.35F, 0.60F, 0.65F, 0.65F);
		                    break;
		            }
		            default:
		            {
		                    setBlockBounds(0f, 0f, 0f, 1f, 1f, 1f);
		                    break;
		            }
            }
    }
    
    
 
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
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:" + this.getUnlocalizedName2());
        this.theIcon = par1IconRegister.registerIcon("thutconcrete:" + this.getUnlocalizedName2());
    }

	public boolean[] sides(IBlockAccess worldObj, int x, int y, int z) 
	{
		boolean[] side = new boolean[]{false, false, false, false, false, false};
    	int[][]sides = {{1,0,0},{-1,0,0},{0,0,1},{0,0,-1},{0,1,0},{0,-1,0}};
		for(int i = 0; i<6; i++)
		{
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
    public boolean renderAsNormalBlock()
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

	@Override 
	public String getItemIconName(){
		return "thutconcrete:" + "rebar";
	}
	
	@Override
	public Icon getIcon(Block block) {
		return this.blockIcon;
	}
		
	
}
