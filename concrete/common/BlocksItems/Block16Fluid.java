package concrete.common.BlocksItems;

import java.util.*;

import concrete.common.concreteCore;
import concrete.common.utils.*;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.*;


public class Block16Fluid extends Block
{
   /**
	* 
	* format for the Integer[][] used in the fluid16Blocks map.
	* 
	* 
			data = new Integer[][]{
				{IDs that the block can flow to, and make the original block, air is included for most liquids, as is the block itself},
				{IDs that when the block flows to will end up making a second type of block},
				{The ID of aforementioned second type of block , ID that this returns when meta hits -1, the viscosity factor,  a secondary ID that this can turn into used for hardening}
				{IDs that it can harden next to}
			};
	*		
	*/
	private Random r = new Random();
	private LinearAlgebra vec;
	private ThreadSafeWorldOperations safe = new ThreadSafeWorldOperations();
	
	public List<Integer> breaks = new ArrayList<Integer>();
	public static Map<Integer, Integer[][]> fluid16Blocks = new HashMap<Integer, Integer[][]>();

    public Block16Fluid(int par1, Material par2)
    {
    	super(par1, par2);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
		this.setTickRandomly(true);
		setCreativeTab(concreteCore.tabThut);
		breaks.add(78);
		breaks.add(38);
		breaks.add(37);
		breaks.add(31);
    }
    @Override
    public boolean canCreatureSpawn(EnumCreatureType type,World worldObj, int x, int y, int z){
    	return false;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
    	int meta = par1World.getBlockMetadata(par2, par3, par4);
    	
        int l = par1World.getBlockMetadata(par2, par3, par4) & 15;
        float f = 0.0625F;
        if(!(safe.isLiquid(par1World, par2,par3-1,par4)||
        		par1World.isAirBlock(par2, par3-1, par4))){
        return AxisAlignedBB.getAABBPool().getAABB((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ, (double)par2 + this.maxX, (double)((float)par3 + (float)l * f), (double)par4 + this.maxZ);
        }
        else{
        	return AxisAlignedBB.getAABBPool().getAABB(0, 0, 0, 0, 0, 0);
        }
    }
    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }
 
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
 
    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBoundsByMeta(15);
    }
 
    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
    	Material material = par1IBlockAccess.getBlockMaterial(par2, par3 - 1, par4);
    	if((material != Material.air))this.setBoundsByMeta(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
        else this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }
 
    protected void setBoundsByMeta(int par1)
    {
        int j = par1 & 15;
        float f = (float)((1 + j)) / 16.0F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
    }
    
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World worldObj, int x, int y, int z, int par5)
    {
    	tickSides(worldObj,x,y,z);
    }
    
    public void breakBlock(World worldObj, int x, int y, int z, int par5, int par6) {}
  
    
    public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
    {
        super.harvestBlock(par1World, par2EntityPlayer, par3, par4, par5, par6);
    }
    public void onEntityCollidedWithBlock(World worldObj,int x,int y, int z, Entity entity){
    	if(!(entity instanceof EntityLiving)){return;}
    	worldObj.scheduleBlockUpdate(x,y,z,this.blockID,2);
    	int duration = getRadiation(worldObj,x,y,z)*100+500;
    	int intensity = getRadiation(worldObj,x,y,z);
    	if(intensity==0){return;}
    	EntityLiving e = (EntityLiving) entity;
    	if(e!=null&&duration!=0){
    		e.addPotionEffect(new PotionEffect(Potion.poison.id, duration, intensity*intensity/64, true));
    	}
    }
    
    
    public void onBlockPlacedBy(World worldObj,int x,int y,int z,EntityLiving entity, ItemStack item){
    	
    	this.merge(worldObj, x, y, z, x, y-1, z);
    	
    	tickSides(worldObj,x,y,z);
    }
    
    //TODO add item drops
    
    
    
    ///////////////////////////////////////Fluid On Update Stuff////////////////////////////////////////////////////////////
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random){
    	boolean fall = true,spread = true;
    	
    	while(fall||spread){
    		fall = this.tryFall(worldObj, x, y, z);
			spread = this.trySpread(worldObj, x, y, z);
			tickSides(worldObj,x,y,z);
    	}
    	
		 int num = canHarden(worldObj, x, y, z);
		 for(int i=0;i<num;i++)
		 if(Math.random()>0.999){
			 worldObj.setBlock(x, y, z, getTurnToID(worldObj.getBlockId(x, y, z)), worldObj.getBlockMetadata(x, y, z), 3);
		 }
    	
    	
    }
    
    public int tickRate(World par1World)
    {
        return 5;
    }
    
    ////////////////////////////////////////Fluid Block Logic Below Here////////////////////////////////////////////////////
    
    /**
     * Checks if the block should spread to the side.  This is a very loose powder, and is likely do to so.
     * @param par1World
     * @param x
     * @param y
     * @param z
     */
    public boolean trySpread(World worldObj, int x, int y, int z){
        boolean moved = false;
        int[][]sides = {{1,0},{-1,0},{0,1},{0,-1}};
       
        int i = r.nextInt(4);
       
        for(int j = 0; j<4; j++){
                moved = moved || equalize(worldObj, x,y,z,x+sides[i][0], y, z+sides[i][1]);
                i = (i+1)%4;
        }
        return moved;
    }
 
    public boolean tryFall(World worldObj, int x, int y, int z){
    	
    	safe.safeLookUp(worldObj,x, y, z);
    	int id = safe.ID;
    	safe.safeLookUp(worldObj,x, y-1, z);
        boolean fell = false;
        if(merge(worldObj,x, y, z, x, y-1, z))return true;
        boolean DFWater = false;
        if(viscosity(id)==0) DFWater = true;
        
    	double[] below = vec.findNextSolidBlock(worldObj, new double[] {x, y-1, z}, new double[] {0,-1,0}, y);
	
        if(( (int)below[1]>=y)){return false;}
        
            fell = merge(worldObj,x, y, z, x, (int)below[1]+1, z);
        
        if(DFWater){
            int[][]sides = {{1,0},{-1,0},{0,1},{0,-1}};
           
            int i = r.nextInt(4);
           
            for(int j = 0; j<4; j++){
                    safe.safeLookUp(worldObj,x+sides[i][0], y, z+sides[i][1]);
                    int idSide = safe.ID;
                    safe.safeLookUp(worldObj,x+sides[i][0], y-1, z+sides[i][1]);
                    int idSideDown = safe.ID;
                    int metaSideDown = safe.meta;
                    if(metaSideDown!=15&&idSide == 0&&(willCover(id, idSideDown)||willMergeTo(id,idSideDown)))
                    fell = merge(worldObj, x,y,z,x+sides[i][0], y, z+sides[i][1]);
                    i = (i+1)%4;
            }
        }
        
        
        return fell;
    }
   
    public boolean merge(World worldObj, int x, int y, int z, int x1, int y1, int z1){
        if(x==x1&&y==y1&&z==z1){return false;}
        safe.safeLookUp(worldObj,x, y, z);
        int id = safe.ID;
        int meta = safe.meta;
        int rad = this.getRadiation(worldObj, x, y, z);
        
        safe.safeLookUp(worldObj,x1, y1, z1);
        int id1 = safe.ID;
        int meta1 = safe.meta;
        Block block1 = safe.block;
        boolean flowTo = willMergeTo(id,id1);
        boolean oneOfUs = (block1 instanceof Block16Fluid);
        boolean cover = willCover(id,id1);
        boolean canBreak = willBreak(id1);
        int toID = getMergeToID(id);
        int returnToID = getReturnToID(id);
        boolean changed = false;
        boolean harden = willTurnTo(id,id1);

    //*    
        if(harden){
        //	System.out.println("M Case0");
        	while(meta>=0&&meta1<15){
        		meta--;
        		meta1++;
        		safe.safeSet(worldObj, x1, y1, z1, id1, meta1);
        		if(meta>=0){
        			safe.safeSet(worldObj, x, y, z, id, meta);
        		}else{
        			safe.safeSet(worldObj, x, y, z, returnToID, 0);
        		}
        		changed = true;
        	}
        	return changed;
        }
      //*/  
        if(flowTo&&oneOfUs){
       // 	System.out.println("M Case1");
        	while(meta>=0&&meta1<15){
        		meta--;
        		meta1++;
        		safe.safeSet(worldObj, x1, y1, z1, id, meta1);
        		if(meta>=0){
        			safe.safeSet(worldObj, x, y, z, id, meta);
        		}else{
        			safe.safeSet(worldObj, x, y, z, returnToID, 0);
        		}
        		changed = true;
        	}
        	return changed;
        }
        if(cover&&oneOfUs){
       // 	System.out.println("M Case2");
        	while(meta>=0&&meta1<15){
        		meta--;
        		meta1++;
        		safe.safeSet(worldObj, x1, y1, z1, toID, meta1);
        		if(meta>=0){
        			safe.safeSet(worldObj, x, y, z, id, meta);
        		}else{
        			safe.safeSet(worldObj, x, y, z, returnToID, 0);
        		}
        		changed = true;
        	}
        	return changed;
        }
        
        
        if(flowTo){
       // 	System.out.println("M Case3");
        	meta1 = -1;
        	while(meta>=0&&meta1<=15){
        		meta--;
        		meta1++;
        		safe.safeSet(worldObj, x1, y1, z1, id, meta1);
        		if(meta>=0){
        			safe.safeSet(worldObj, x, y, z, id, meta);
        		}else{
        			safe.safeSet(worldObj, x, y, z, returnToID, 0);
        		}
        		changed = true;
        	}
        	return changed;
        }
        if(cover){
        	meta1 = -1;
       // 	System.out.println("M Case4");
        	while(meta>=0&&meta1<=15){
        		meta--;
        		meta1++;
        		safe.safeSet(worldObj, x1, y1, z1, toID, meta1);
        		if(meta>=0){
        			safe.safeSet(worldObj, x, y, z, id, meta);
        		}else{
        			safe.safeSet(worldObj, x, y, z, returnToID, 0);
        		}
        		changed = true;
        	}
        	return changed;
        }
        
        return false;
    }

    public boolean equalize(World worldObj, int x, int y, int z, int x1, int y1, int z1){
    	if(x==x1&&y==y1&&z==z1){return false;}
        safe.safeLookUp(worldObj,x, y, z);
        int id = safe.ID;
        int meta = safe.meta;
        if(meta==0||id!=this.blockID){return false;}
        safe.safeLookUp(worldObj,x1, y1, z1);
        int id1 = safe.ID;
        int meta1 = safe.meta;
        Block block1 = safe.block;
        int spread = viscosity(id);
        
        boolean changed = false;
        boolean flowTo = willMergeTo(id,id1);
        boolean oneOfUs = (block1 instanceof Block16Fluid);
        boolean cover = willCover(id,id1);
        boolean canBreak = willBreak(id1);
        int toID = getMergeToID(id);
        boolean harden = willTurnTo(id,id1);
        int returnToID = getReturnToID(id);

        //*
        if(harden){
        //	System.out.println("E Case0");
        	while(meta>(meta1+spread)&&meta1<=15){
        		meta--;
        		meta1++;
        		safe.safeSet(worldObj, x1, y1, z1, id1, meta1);
        		if(meta>=0){
        			safe.safeSet(worldObj, x, y, z, id, meta);
        		}else{
        			safe.safeSet(worldObj, x, y, z, returnToID, 0);
        		}
        		changed = true;
        	}
        	return changed;
        }
        //*/
        if(flowTo&&oneOfUs){
        //	System.out.println("E Case1");
        	while(meta>(meta1+spread)&&meta1<=15){
        		meta--;
        		meta1++;
        		safe.safeSet(worldObj, x1, y1, z1, id, meta1);
        		safe.safeSet(worldObj, x, y, z, id, meta);
        		changed = true;
        	}
        	return changed;
        }
        if(cover&&oneOfUs){
        //	System.out.println("E Case2");
        	while(meta>meta1+spread&&meta1<=15){
        		safe.safeSet(worldObj, x1, y1, z1, toID, meta1+1);
        		safe.safeSet(worldObj, x, y, z, id, meta-1);
        		changed = true;
        		meta--;
        		meta1++;
        	}
        	return changed;
        }
        
        
        if(flowTo){
        //	System.out.println("E Case3");
        	meta1 = -1;
        	while(meta>meta1+spread&&meta1<=15){
        		safe.safeSet(worldObj, x1, y1, z1, id, meta1+1);
        		safe.safeSet(worldObj, x, y, z, id, meta-1);
        		changed = true;
        		meta--;
        		meta1++;
        	}
        	return changed;
        }
        if(cover){
        	meta1 = -1;
       // 	System.out.println("E Case4");
        	while(meta>meta1+spread&&meta1<=15){
        		safe.safeSet(worldObj, x1, y1, z1, toID, meta1+1);
        		safe.safeSet(worldObj, x, y, z, id, meta-1);
        		changed = true;
        		meta--;
        		meta1++;
        	}
        	return changed;
        }
        
        return false;
    }
    
    
    private boolean willBreak(int id){
    	return breaks.contains(id);
    }
    
    private boolean willMergeTo(int idFrom, int idTo){
    	if(fluid16Blocks.get(idFrom)==null)return false;
    	Integer[][] blockData = fluid16Blocks.get(idFrom);
    	for(Integer i : blockData[0]){
    		if(idTo == i){
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean willCover(int idFrom, int idTo){
    	if(fluid16Blocks.get(idFrom)==null)return false;
    	Integer[][] blockData = fluid16Blocks.get(idFrom);
    	for(Integer i : blockData[1]){
    		if(idTo == i){
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean canHardenNextTo(int idFrom, int idTo){
    	if(fluid16Blocks.get(idFrom)==null)return false;
    	Integer[][] blockData = fluid16Blocks.get(idFrom);
    	for(Integer i : blockData[3]){
    		if(idTo == i){
    			return true;
    		}
    	}
    	return false;
    }
    
    private boolean willTurnTo(int idFrom, int idTo){
    	return idTo == getTurnToID(idFrom);
    }
    
    private int getMergeToID(int id){
    	if(fluid16Blocks.get(id)==null) return 0;
    	if(fluid16Blocks.get(id)[2][0]==null) return 0;
    	return fluid16Blocks.get(id)[2][0];
    }
   
    private int getTurnToID(int id){
    	if(fluid16Blocks.get(id)==null) return 0;
    	if(fluid16Blocks.get(id)[2][3]==null) return 0;
    	return fluid16Blocks.get(id)[2][3];
    }
    
    private int getReturnToID(int id){
    	if(fluid16Blocks.get(id)==null) return 0;
    	if(fluid16Blocks.get(id)[2][1]==null) return 0;
    	return fluid16Blocks.get(id)[2][1];
    }
    
    private int viscosity(int id){
    	if(fluid16Blocks.get(id)==null) return 0;
    	if(fluid16Blocks.get(id)[2][2]==null) return 0;
    	return fluid16Blocks.get(id)[2][2];
    }
    
    
    ////////////////////////////////////////Fluid Block Logic Above Here, Radiation Below///////////////////////////////////////////
    
  //*  
    public int getRadiation(World worldObj, int x, int y, int z) {
		return 0;//effectTracker.getTrackedEffect(Integer.toString(x)+Integer.toString(y)+Integer.toString(z));
	}
    
    public void setRadiation(World worldObj, int x, int y, int z, int radiation) {
  //  	effectTracker.setTrackedEffect(Integer.toString(x)+Integer.toString(y)+Integer.toString(z), radiation);
    }
 
    
    public void tickSides(World worldObj, int x, int y, int z){
    	 int[][]sides = {{0,0,0}, {1,0,0},{-1,0,0},{0,0,1},{0,0,-1},{0,1,0},{0,-1,0}};
         for(int i=0;i<7;i++){
             worldObj.scheduleBlockUpdate(x+sides[i][0], y+sides[i][1], z+sides[i][2], this.blockID,2);// this.tickRate(worldObj));
         }
    }
    
    public int countSides(World worldObj, int x, int y, int z,int id){
    	int num = 0;
    	int[][]sides = {{1,0,0},{-1,0,0},{0,0,1},{0,0,-1},{0,1,0},{0,-1,0}};
        for(int i=0;i<6;i++){
            if(worldObj.getBlockId(x+sides[i][0], y+sides[i][1], z+sides[i][2])==id)
            	num++;
        }
        return num;
   }
    
    public byte canHarden(World worldObj, int x, int y, int z){
    	byte num = 0;
    	int id = worldObj.getBlockId(x, y, z);
    	if(fluid16Blocks.get(id)==null)return 0;
    	if(fluid16Blocks.get(id)[3]==null)return 0;
    	for(Integer i:fluid16Blocks.get(id)[3]){
    		num += countSides(worldObj, x, y, z, i);
    	}
    	
    	return num;
    }
    
    ///////////////////////////////////////////////////////////////////Block Ticking Stuff Above Here///////////////////////////////////////
    @SideOnly(Side.CLIENT)

    /**
     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
     * coordinates.  Args: blockAccess, x, y, z, side
     */
    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int par2, int par3, int par4, int par5)
    {
        return par5 == 1 ? true : super.shouldSideBeRendered(par1IBlockAccess, par2, par3, par4, par5);
    }
    
    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("concrete:" + this.getUnlocalizedName2());
    }
    
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
        return (meta & 15) + 1;
    }
    
}