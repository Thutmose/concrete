package thutconcrete.common.blocks;

import static net.minecraftforge.common.ForgeDirection.DOWN;
import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.UP;
import static net.minecraftforge.common.ForgeDirection.WEST;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.corehandlers.TSaveHandler;
import thutconcrete.common.tileentity.TileEntityBlock16Fluid;
import thutconcrete.common.utils.*;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Icon;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.*;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.liquids.IBlockLiquid;


public class Block16Fluid extends Block
{
   /**
	* 
	* format for the Integer[][] used in the fluid16Blocks map.
	* 
	* 
			data = new Integer[][]{
				{
					ID that this returns when meta hits -1, 
					the viscosity factor,  
					a secondary ID that this can turn into used for hardening,
					The hardening differential that prevents things staying liquid forever.,
					a randomness coefficient, this is multiplied by a random 0-10 then added to the hardening differential and viscosity.,
					The will fall of edges factor, this is 0 or 1,
					0 = not colourable, 1 = colourable.
				}
				{Array of desiccants, format: id+4096*efficiency}
				{Array of combination targets format: IDtarget + (4096*IDturnTo)}
			};
	*		
	*/
	private Random r = new Random();
	private LinearAlgebra vec;
	public ThreadSafeWorldOperations safe = new ThreadSafeWorldOperations();
	public boolean solidifiable = false;
	public double rate = 0.9;
	public boolean wanderer = false;
	public int placeamount = 16;
	public static final Icon[] iconsArray = new Icon[16];
	
	public static double SOLIDIFY_CHANCE = 0.0004;
	
	public static Block16Fluid instance;
	
	public static List<Integer> breaks = new ArrayList<Integer>();
	public static Map<Integer, Integer[][]> fluid16Blocks = new HashMap<Integer, Integer[][]>();

    public Block16Fluid(int par1, Material par2)
    {
    	super(par1, par2);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
		setCreativeTab(ConcreteCore.tabThut);
		this.setTickRandomly(true);
		breaks.add(78);
		breaks.add(38);
		breaks.add(37);
		breaks.add(31);
		breaks.add(Block.crops.blockID);
		breaks.add(Block.potato.blockID);
		breaks.add(Block.carrot.blockID);
		breaks.add(Block.melonStem.blockID);
		breaks.add(Block.reed.blockID);
		breaks.add(Block.leaves.blockID);
		instance = this;
		
    }
    @Override
    public boolean canCreatureSpawn(EnumCreatureType type,World worldObj, int x, int y, int z){
    	return false;
    }


    public boolean isBlockNormalCube(World world, int x, int y, int z)
    {
        return world.getBlockMetadata(x,y,z)==0;
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
    	int meta = par1World.getBlockMetadata(par2, par3, par4);
        int l = 15-par1World.getBlockMetadata(par2, par3, par4);
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
 
    public void setData(){}
    
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }
    
 
    @Override
    public void onBlockAdded(World worldObj, int x, int y, int z)
    {
		worldObj.scheduleBlockUpdate(x, y, z, worldObj.getBlockId(x, y, z), 5);
    	setTEUpdate(worldObj, x, y, z);
    }
    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBoundsByMeta(0);
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
        int j = 15-par1;
        float f = (float)((1 + j)) / 16.0F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
    }
    
    public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
    {
        super.harvestBlock(par1World, par2EntityPlayer, par3, par4, par5, par6);
    }
    
    public void onBlockPlacedBy(World worldObj,int x,int y,int z,EntityLiving entity, ItemStack item)
    {
    	merge(worldObj, x, y, z, x, y-1, z);
    	setTEUpdate(worldObj, x, y, z);
    	worldObj.scheduleBlockUpdate(x, y, z, worldObj.getBlockId(x, y, z), 5);
    }
    
    public boolean onBlockActivated(World worldObj, int x, int y, int z, EntityPlayer player, int side, float par7, float par8, float par9)
    {
    	ItemStack item = player.getHeldItem();
    	int meta = safe.safeGetMeta(worldObj,x,y,z);
    	setData();
    	
    	
    	if(item!=null)
    	{
    		int itemID = item.itemID;
    		int id = safe.safeGetID(worldObj,x, y, z);
    		Block16Fluid block = (Block16Fluid)Block.blocksList[id];
	    	if(canColour(id)&&item.getItem() instanceof ItemDye)
	    	{
		    	int meta1 = (15-item.getItemDamage());
		    	recolourBlock(worldObj, x, y, z, ForgeDirection.getOrientation(side), meta1);
		    	
	    	}
	    	else if(meta!=0&&block.willCombine(itemID, id))
	    	{
	    		return placedStack(worldObj, item, x, y, z, ForgeDirection.getOrientation(side), block, player);
	    	}
    	}
        return false;
        
    }
    ///////////////////////////////////////Fluid On Update Stuff////////////////////////////////////////////////////////////
	
	
	@Override
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random)
	{ 
		doFluidTick(worldObj, x, y, z);
    }
	
	public int tickRate(World worldObj)
	{
		return 5;
	}
	
    ////////////////////////////////////////Fluid Block Logic Below Here////////////////////////////////////////////////////
    
    /**
     * Checks if the block should spread to the side
     * @param worldObj
     * @param x
     * @param y
     * @param z
     */
    public boolean trySpread(World worldObj, int x, int y, int z){
        boolean moved = false;
        int viscosity = viscosity(safe.safeGetID(worldObj, x, y, z));

        Block block = Block.blocksList[safe.safeGetID(worldObj,x, y, z)];
        
        if(!(block instanceof Block16Fluid)|| viscosity==15)
        {
        	return false;
        }
        	
        int[][]sides = {
        					{1,0},{-1,0},{0,1},{0,-1},
        };
       
        int n = sides.length;
        int i = r.nextInt(n);
        int highestMeta = 0;
        int k = 0;
        for(int j = 0; j<n; j++){
            int id = safe.safeGetID(worldObj,x+sides[i][0], y, z+sides[i][1]);
            int meta = safe.safeGetMeta(worldObj,x+sides[i][0], y, z+sides[i][1]);
        	block = Block.blocksList[id];
        	if(!(block instanceof Block16Fluid))
        		moved = moved || equalize(worldObj, x,y,z,x+sides[i][0], y, z+sides[i][1]);
        	else if (meta > highestMeta){
        		highestMeta = meta;
        		k=i;
        	}
        	i = (i+1)%n;
        }
        
        if(highestMeta!=0)
        	moved = moved || equalize(worldObj, x,y,z,x+sides[k][0], y, z+sides[k][1]);
        
        if(block instanceof Block16Fluid && ((Block16Fluid)block).wanderer)
        {
	        int meta = safe.safeGetMeta(worldObj, x, y, z);
	        if(meta==15&&viscosity==0)
	        {
	        	moved = moved || merge(worldObj, x,y,z,x+sides[i][0], y, z+sides[i][1]);
	        }
	        
        }
        
        return moved;
    }
 
    /**
     * Checks if the block should fall down
     * @param par1World
     * @param x
     * @param y
     * @param z
     */
    public boolean tryFall(World worldObj, int x, int y, int z){
    	int id = safe.safeGetID(worldObj,x, y, z);
    	int id1 = safe.safeGetID(worldObj,x, y-1, z);
    	boolean fell = false;
        fell = merge(worldObj,x, y, z, x, y-1, z);
            
        boolean fallOff = willFallOffEdges(id);
        boolean flowOff = willFlowOffEdges(id);
        if(fallOff||flowOff){
        	int[][]sides = {{1,0},{-1,0},{0,1},{0,-1},{1,1},{-1,1},{1,-1},{-1,-1}};
           
            int i = r.nextInt(sides.length);
           
            int lowestMeta = 0;
            int k = 0;
            
            for(int j = 0; j<sides.length; j++){
                    int idSide = safe.safeGetID(worldObj,x+sides[i][0], y, z+sides[i][1]);
                    int idSideDown = safe.safeGetID(worldObj,x+sides[i][0], y-1, z+sides[i][1]);
                    int metaSideDown = safe.safeGetMeta(worldObj,x+sides[i][0], y-1, z+sides[i][1]);
                    if(metaSideDown!=0&&idSide == 0&&(willCombine(id, idSideDown)))
                    {
	                    if (metaSideDown > lowestMeta)
	                    {
	                		lowestMeta = metaSideDown;
	                		k=i;
	                	}
                    }
                    i = (i+1)%sides.length;
            }
            
            if(lowestMeta!=0)
            {
            	fell = fallOff?merge(worldObj, x,y,z,x+sides[k][0], y-1, z+sides[k][1]):equalize(worldObj, x,y,z,x+sides[k][0], y-1, z+sides[k][1]);
            }
        }
        return fell;
    }
   
    public boolean merge(World worldObj, int x, int y, int z, int x1, int y1, int z1){
        if(x==x1&&y==y1&&z==z1){return false;}
        
        int id = safe.safeGetID(worldObj,x, y, z);
        int meta = safe.safeGetMeta(worldObj,x, y, z);
        
        int id1 = safe.safeGetID(worldObj,x1, y1, z1);
        int meta1 = safe.safeGetMeta(worldObj,x1, y1, z1);
        
        boolean canBreak = willBreak(id1);

        boolean breakException = breakException(id, id1);
        if(canBreak&&!breakException){
        	safe.notAsSafeSet(worldObj, x1, y1, z1, 0, 0);
        	id1 = 0;
        	meta1=0;
        }
        boolean combine = willCombine(id, id1);
        if(combine){

            int newColour = getMetaData(worldObj, x, y, z);
            boolean changed = false;
            Block block1 = safe.safeGetBlock(worldObj,x1, y1, z1);
            boolean oneOfUs = (block1 instanceof Block16Fluid);

            boolean willColour = canColour(id1);
            int idCombine = getCombineID(worldObj,x,y,z, x1, y1, z1);
            int returnToID = getReturnToID(id);
            int idHarden = getTurnToID(safe.safeGetID(worldObj, x, y, z));

        	
        	
        	if(!oneOfUs)meta1 = 16;
           	if(willColour)
        	{
        		newColour = getNewColour(worldObj, x, y, z, x1, y1, z1);
        	}
        	if(id1==0){
        		safe.notAsSafeSet(worldObj, x1, y1, z1, idCombine, meta);
        		safe.notAsSafeSet(worldObj, x, y, z, returnToID, 0);
        	}else
        	
        	while(meta<=15&&meta1>0){
        		meta++;
        		meta1--;
        		if(meta1<0)break;
        		safe.notAsSafeSet(worldObj, x1, y1, z1, idCombine, meta1);
        		if(meta<=15){
        			safe.notAsSafeSet(worldObj, x, y, z, id, meta);
        		}else{
        			safe.notAsSafeSet(worldObj, x, y, z, returnToID, 0);
        		}
        		changed = true;
        	}
        	if(willColour)
        	{
        		setColourMetaData(worldObj, x1, y1, z1, (byte) newColour);
        	}
        	return changed;
        }

        return false;
    }

    public boolean equalize(World worldObj, int x, int y, int z, int x1, int y1, int z1){
    	if(x==x1&&y==y1&&z==z1){return false;}
        int id = safe.safeGetID(worldObj,x, y, z);
        int meta = safe.safeGetMeta(worldObj,x, y, z);
        //System.out.println(id+" "+meta);
        boolean additionalMeta = true;
        int spread = viscosity(id);
        if(meta==15){return false;}
        
        int id1 = safe.safeGetID(worldObj,x1, y1, z1);
        int meta1 = safe.safeGetMeta(worldObj,x1, y1, z1);
        boolean canBreak = willBreak(id1);

        boolean combine = willCombine(id, id1);
        boolean breakException = breakException(id, id1);

        if(canBreak&&!breakException){
        	safe.notAsSafeSet(worldObj, x1, y1, z1, 0, 0);
        	id1 = 0;
        	meta1=0;
        }
        
        if(combine){
            Block block1 = safe.safeGetBlock(worldObj,x1, y1, z1);
            int diff = hardenDifferential(id) + spread;

            boolean willColour = canColour(id1);
            boolean changed = false;
            boolean oneOfUs = (block1 instanceof Block16Fluid);
        	
            int newColour = getMetaData(worldObj, x, y, z);
            int idCombine = getCombineID(worldObj,x,y,z, x1, y1, z1);
            int returnToID = getReturnToID(id);
            int idHarden = getTurnToID(safe.safeGetID(worldObj, x, y, z));
        	
        	if(!oneOfUs)meta1 = 16;
        	
        	if(willColour)
        	{
        		newColour = getNewColour(worldObj, x, y, z, x1, y1, z1);
        	}
        	
        	while(meta<((id1==idHarden)?meta1-diff:meta1-spread)&&meta1>0){
        		meta++;
        		meta1--;
        		safe.notAsSafeSet(worldObj, x1, y1, z1, idCombine, meta1);
        		safe.notAsSafeSet(worldObj, x, y, z, id, meta);
        		changed = true;
        	}
        	
        	if(willColour)
        	{
        		setColourMetaData(worldObj, x1, y1, z1, (byte) newColour);
        	}
        	
        	return changed;
        	
        }
        return false;
    }
    //////////////////////////////////////////////////Item placement related stuff///////////////////////////////////////////////
    
    public boolean placedStack(World worldObj, ItemStack stack, int x, int y, int z, ForgeDirection side, Block16Fluid block, EntityPlayer player)
    {
    	int id = worldObj.getBlockId(x, y, z);
    	int id1 = worldObj.getBlockId(x+side.offsetX,y+side.offsetY, z+side.offsetZ);
    	
    	int itemID = stack.itemID;
    	
    	int meta = worldObj.getBlockMetadata(x, y, z);
    	
    	int meta1 = worldObj.getBlockMetadata(x+side.offsetX,y+side.offsetY, z+side.offsetZ);
    	int placementamount = block.placeamount;
    	
    	int initialamount = 16-meta;
    	
    	int newMeta = 16-(placementamount + initialamount);
    	

    	int remainder = (placementamount - (16-meta));
    	
    	Block block1 = Block.blocksList[id1];
    	
    	if(id1==0||block1.blockMaterial.isReplaceable())
    	{
    		worldObj.setBlock(x,y, z, getCombineID(worldObj, id, itemID), Math.max(newMeta,0), 3);
    		if(newMeta<0)
    		worldObj.setBlock(x+side.offsetX,y+side.offsetY, z+side.offsetZ, itemID, remainder, 3);
    		
    		if(!player.capabilities.isCreativeMode)
		    	{
		    		stack.splitStack(1);
		    	}
    		return true;
    	}
    	
    	return false;
    }
       
    /////////////////////////////////////////////////Checks used in the fluid code////////////////////////////////////////////////////
    
    private boolean willBreak(int id)
    {
    	return breaks.contains(id);
    }
    
    private boolean breakException(int idfrom, int id)
    {
    	if(fluid16Blocks.get(idfrom)==null) return false;
    	if(fluid16Blocks.get(idfrom).length<4) return false;
    	for(int i = 0; i<fluid16Blocks.get(idfrom)[3].length;i++)
    	{
    		if(fluid16Blocks.get(idfrom)[3][i]==id)
    		{
    			return true;
    		}
    	}	
    	return false;
    }
    
    private boolean canHardenNextTo(int idFrom, int idTo)
    {
    	if(fluid16Blocks.get(idFrom)==null)return false;
    	Integer[][] blockData = fluid16Blocks.get(idFrom);
    	for(Integer i : blockData[1]){
    		int j = i&4095;
    		if(idTo == j){		
    			return true;
    		}
    	}
    	return false;
    }
    
    public boolean willCombine(int idFrom, int idTo)
    {
    	if(fluid16Blocks.get(idFrom)==null)return false;
    	Integer[][] blockData = fluid16Blocks.get(idFrom);
    	for(Integer i : blockData[2]){
    		int j = i&4095;
    		if(idTo == j){		
    			return true;
    		}
    	}
    	return false;
    }
    
    private int getReturnToID(int id)
    {
    	if(fluid16Blocks.get(id)==null) return 0;
    	if(fluid16Blocks.get(id)[0][0]==null) return 0;
    	return fluid16Blocks.get(id)[0][0];
    }
    
    private boolean willFallOffEdges(int id){
    	if(fluid16Blocks.get(id)==null) return false;
    	if(fluid16Blocks.get(id)[0][5]==null) return false;
    	return (fluid16Blocks.get(id)[0][5]==1?true:false);
    }
    
    private boolean willFlowOffEdges(int id){
    	if(fluid16Blocks.get(id)==null) return false;
    	if(fluid16Blocks.get(id)[0][5]==null) return false;
    	return (fluid16Blocks.get(id)[0][5]==2?true:false);
    }
    
    public int getTurnToID(int id){
    	if(fluid16Blocks.get(id)==null) return 0;
    	if(fluid16Blocks.get(id)[0][2]==null) return 0;
    	return fluid16Blocks.get(id)[0][2];
    }

    private int viscosity(int id){
    	Random r = new Random();
    	if(fluid16Blocks.get(id)==null) return 0;
    	if(fluid16Blocks.get(id)[0][1]==null) return 0;
    	if(fluid16Blocks.get(id)[0][4]==null) return 0;
    	int v = fluid16Blocks.get(id)[0][1];
    	int dv = (int) (fluid16Blocks.get(id)[0][4]*Math.random());
    	return Math.min(v+dv,15);
    }
    private int hardenDifferential(int id){
    	Random r = new Random();
    	if(fluid16Blocks.get(id)==null) return 0;
    	if(fluid16Blocks.get(id)[0][3]==null) return 0;
    	int dv = (int) (fluid16Blocks.get(id)[0][4]*Math.random());
    	return Math.min(fluid16Blocks.get(id)[0][3]+dv,15);
    }
    
    private int getCombineID(World worldObj, int x, int y, int z, int x1, int y1, int z1){
    	int idFrom = safe.safeGetID(worldObj, x, y, z);
    	if(fluid16Blocks.get(idFrom)==null)return idFrom;
    	int idTo = safe.safeGetID(worldObj, x1, y1, z1);
    	int combineID = idFrom;
    	
    	Integer[][] blockData = fluid16Blocks.get(idFrom);
    	
    	for(Integer i : blockData[2]){
    		int j = i&4095;
    		if(idTo == j){
    			combineID = i>>12;
    		}
    	}
    	return combineID;
    }
    
    public int getCombineID(World worldObj, int idTo, int idFrom){
    	if(fluid16Blocks.get(idFrom)==null)return idFrom;
    	
    	int combineID = idFrom;
    	
    	Integer[][] blockData = fluid16Blocks.get(idFrom);
    	
    	for(Integer i : blockData[2]){
    		int j = i&4095;
    		if(idTo == j){
    			combineID = i>>12;
    		}
    	}
    	return combineID;
    }
    ////////////////////////////////////////Fluid Block Logic Above Here, special data Below///////////////////////////////////////////
    
    private boolean canColour(int id){
    	if(fluid16Blocks.get(id)==null) return false;
    	if(fluid16Blocks.get(id)[0][6]==null) return false;
    	return (fluid16Blocks.get(id)[0][6]>0?true:false);
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
    
    public int canHarden(World worldObj, int x, int y, int z){
    	int num = 0;
    	int id = safe.safeGetID(worldObj, x, y, z);
    	int val;
    	if(fluid16Blocks.get(id)==null)return 0;
    	if(fluid16Blocks.get(id)[1]==null)return 0;
    	for(Integer i:fluid16Blocks.get(id)[1]){
    		int j = i&4095;
    		val = 1 + i>>12;
    		num += val*countSides(worldObj, x, y, z, j);
    	}
    	return num;
    }
    
    public boolean isHardenable(World worldObj, int x, int y, int z)
    {

    	int id = safe.safeGetID(worldObj, x, y, z);

    	if(fluid16Blocks.get(id)==null)return false;
    	if(fluid16Blocks.get(id)[0]==null)return false;
    	if(fluid16Blocks.get(id)[0][2]==null) return false;
    	
    	int idDown = safe.safeGetID(worldObj, x, y-1, z);
    	if(idDown==0||breaks.contains(idDown))
    	{
    		return false;
    	}
    	if(safe.safeGetBlock(worldObj, x, y-1, z) instanceof Block16Fluid&&
    			willCombine(id, idDown) &&
    			safe.safeGetMeta(worldObj, x, y-1, z)!=0)
    	{
    		return false;
    	}

    	return true;
    }
    
    private int getHardenRate(int idFrom, int idTo){
    	if(fluid16Blocks.get(idFrom)==null)return idFrom;
    	Integer[][] blockData = fluid16Blocks.get(idFrom);
    	for(Integer i : blockData[1]){
    		int j = i&4095;
    		if(idTo == j){
    			return i>>12;
    		}
    	}
    	return idFrom;
    }
    
    private int getNewColour(World worldObj, int x, int y, int z, int x1, int y1, int z1){
    	int idFrom = safe.safeGetID(worldObj, x, y, z);
    	int idTo = safe.safeGetID(worldObj,x1,y1,z1);
    	int dimID = worldObj.provider.dimensionId;
    	Block block = Block.blocksList[idFrom];

		 if(fluid16Blocks.get(idFrom)==null||fluid16Blocks.get(idFrom)[0]==null||fluid16Blocks.get(idFrom)[0][6]==null) return 8;
		 if(fluid16Blocks.get(idFrom)[0][6]==0) return 8;
    	if(block instanceof Block16Fluid)
    	{
    		int colourfrom = instance.getMetaData(worldObj,x, y, z);
    		int colourto = instance.getMetaData(worldObj,x1, y1, z1);
    		int i = Math.min(instance.getMetaData(worldObj,x, y, z), ((Block16Fluid)block).instance.getMetaData(worldObj,x1, y1, z1));
    		int j = Math.max(instance.getMetaData(worldObj,x, y, z), ((Block16Fluid)block).instance.getMetaData(worldObj,x1, y1, z1));
    		
    		return (ConcreteCore.colourMap.get(i+16*j)==null?colourfrom:(byte)ConcreteCore.colourMap.get(i+16*j));
    	}
    	
    	return 8;
    }
    
    public void colourChange(World worldObj, int x, int y, int z, int x1, int y1, int z1){
    	int idFrom = safe.safeGetID(worldObj, x, y, z);
    	int idTo = safe.safeGetID(worldObj,x1,y1,z1);
    	int dimID = worldObj.provider.dimensionId;
    	Block block = Block.blocksList[idFrom];
    	
		if(fluid16Blocks.get(idFrom)==null||fluid16Blocks.get(idFrom)[0]==null||fluid16Blocks.get(idFrom)[0][6]==null) return;
		if(fluid16Blocks.get(idFrom)[0][6]==0) return;
    	if(block instanceof Block16Fluid)
    	{
    		int colourfrom = instance.getMetaData(worldObj,x, y, z);
    		int colourto = instance.getMetaData(worldObj,x1, y1, z1);
    		int i = Math.min(instance.getMetaData(worldObj,x, y, z), ((Block16Fluid)block).instance.getMetaData(worldObj,x1, y1, z1));
    		int j = Math.max(instance.getMetaData(worldObj,x, y, z), ((Block16Fluid)block).instance.getMetaData(worldObj,x1, y1, z1));
    		
    		setColourMetaData(worldObj, x1, y1, z1, (byte) (ConcreteCore.colourMap.get(i+16*j)==null?colourfrom:(byte)ConcreteCore.colourMap.get(i+16*j)));
    	}
    }
    ///////////////////////////////////////////////////////////////////Block effects/ticking Stuff Above Here///////////////////////////////////////

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:" + "dryConcrete_"+8);
        for (int i = 0; i < this.iconsArray.length; ++i)
        {
            this.iconsArray[i] = par1IconRegister.registerIcon("thutconcrete:" + "dryConcrete_"+i);
        }
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
        return (15-meta & 15) + 1;
    }

	 ///////////////////////////////////////////////TE Specific stuff//////////////////////////////////////////////
	 
	 public TileEntityBlock16Fluid getTE(World worldObj, int x, int y, int z)
	 {
		 return (TileEntityBlock16Fluid)safe.safeGetTE(worldObj, x, y, z);
	 }
	 
	 public void setTEUpdate(World worldObj, int x, int y, int z)
	 {
		 TileEntity TE = safe.safeGetTE(worldObj, x, y, z);
		 if(TE!=null&&TE instanceof TileEntityBlock16Fluid)
		 {
			 TileEntityBlock16Fluid te = (TileEntityBlock16Fluid)TE;
			 te.sendUpdate();
		 }
	 }
	     
	 public int getMetaData(World worldObj, int x, int y, int z, int side)
	 {
		 TileEntityBlock16Fluid te = (TileEntityBlock16Fluid) safe.safeGetTE(worldObj, x, y, z);
		 if(te!=null)
			 return te.metaArray[side];
		 else return 8;
	 }
	 
	 public int getMetaData(World worldObj, int x, int y, int z)
	 {
		 TileEntityBlock16Fluid te = (TileEntityBlock16Fluid) safe.safeGetTE(worldObj, x, y, z);
		 if(te!=null)
		 {
			 return te.metaArray[1];
		 }
		 return 8;
	 }

	 public void setColourMetaData(World worldObj, int x, int y, int z, byte meta, int side)
	 {
		 TileEntityBlock16Fluid te = (TileEntityBlock16Fluid) safe.safeGetTE(worldObj, x, y, z);
		 if(te!=null&&meta!=te.metaArray[side])
		 {
			 te.metaArray[side] = meta;
			 te.sendUpdate();
		 }
	 }
	 
	 public void setColourMetaData(World worldObj, int x, int y, int z, byte meta)
	 {
		 TileEntityBlock16Fluid te = (TileEntityBlock16Fluid) safe.safeGetTE(worldObj, x, y, z);
		 if(te!=null&&
				 (
						  meta!=te.metaArray[0]
						||meta!=te.metaArray[1]
						||meta!=te.metaArray[2]
						||meta!=te.metaArray[3]
						||meta!=te.metaArray[4]
						||meta!=te.metaArray[5]
				 )
						
			)
		 {
			 te.metaArray = new int[] {meta,meta,meta,meta,meta,meta};
			 te.sendUpdate();
		 }
	 }
	 
	 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 @SideOnly(Side.CLIENT)

	    /**
	     * Returns true if the given side of this block type should be rendered, if the adjacent block is at the given
	     * coordinates.  Args: blockAccess, x, y, z, side
	     */
	    public boolean shouldSideBeRendered(IBlockAccess par1IBlockAccess, int x, int y, int z, int dir)
	    {

	    	ForgeDirection side = ForgeDirection.getOrientation(dir);
	    	
	    	int id1 = par1IBlockAccess.getBlockId(x, y, z);

	    	int meta = par1IBlockAccess.getBlockMetadata(x-side.offsetX, y-side.offsetX, z-side.offsetX);
	    	if(side == ForgeDirection.UP && meta !=15)
	    	{
	    		return true;
	    	}
	    	
	    	
	    	if(Block.opaqueCubeLookup[id1]&&meta==0)
	    	{
	    		return false;
	    	}
	    	
	    	Block block1 = Block.blocksList[id1];
	    	
	    	
	    	if(block1 instanceof Block16Fluid)
	    	{
		    	int meta1 = par1IBlockAccess.getBlockMetadata(x, y, z);
		    	if(meta==0&&meta1==0)
		    	{
		    		return false;
		    	}
	    	}
	        return true;
	        //*/
	    }
	 
	 
	    /**
	     * Checks if the block is a solid face on the given side, used by placement logic.
	     *
	     * @param world The current world
	     * @param x X Position
	     * @param y Y position
	     * @param z Z position
	     * @param side The side to check
	     * @return True if the block is solid on the specified side.
	     */
	    public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
	    {
	        int meta = world.getBlockMetadata(x, y, z);
	        switch (side)
	        {
		            case UP:
		            {
		                return (meta==0);
		            }
		            case DOWN:
		            {
		                return true;
		            }
		            case NORTH:
		            {
		            	return (meta==0);
		            }
		            case SOUTH:
		            {
		            	return (meta==0);
		            }
		            case EAST:
		            {
		            	return (meta==0);
		            }
		            case WEST:
		            {
		            	return (meta==0);
		            }
		            default:
		            {
		            	return (meta==0);
		            }
	        }//*/
	    }
	 ////////////////////////////////////////////////////////////////////////////////////////////////////////


		 public void doFluidTick(World worldObj, int xCoord, int yCoord, int zCoord)
		 {
			 if(!worldObj.isRemote)
			 {
				Block16Fluid blockf = (Block16Fluid)Block.blocksList[worldObj.getBlockId( xCoord, yCoord, zCoord)];

				Block16Fluid.instance.tryFall(worldObj, xCoord, yCoord, zCoord);
				Block16Fluid.instance.trySpread(worldObj, xCoord, yCoord, zCoord);
			
				if(blockf.solidifiable)
				{
					int num = Block16Fluid.instance.canHarden(worldObj, xCoord, yCoord, zCoord);
					 if(Math.random()>(1-(Block16Fluid.instance.SOLIDIFY_CHANCE*num)))
					 {
						 int metai = Block16Fluid.instance.getMetaData(worldObj,xCoord, yCoord, zCoord);
						 if(!worldObj.isRemote)
						 {
							 safe.notAsSafeSet(worldObj, xCoord, yCoord, zCoord, Block16Fluid.instance.getTurnToID(worldObj.getBlockId( xCoord, yCoord, zCoord)), worldObj.getBlockMetadata( xCoord, yCoord, zCoord));
						 }
						 Block16Fluid.instance.setColourMetaData(worldObj,xCoord, yCoord, zCoord, (byte) metai);
					 }

					tickSides(worldObj, xCoord, yCoord, zCoord, 5);
				}
				
			 }
		 }
		 
	    
		    public void tickSides(World worldObj, int x, int y, int z, int rate){
		    	int[][]sides = {{1,0,0},{-1,0,0},{0,0,1},{0,0,-1},{0,1,0},{0,-1,0}};
		        for(int i=0;i<6;i++){
		        	Block blocki = Block.blocksList[worldObj.getBlockId(x+sides[i][0], y+sides[i][1], z+sides[i][2])];
		  
		        	if(blocki instanceof Block16Fluid && ((Block16Fluid)blocki).solidifiable)
		        	{
		        		int id = worldObj.getBlockId( x+sides[i][0], y+sides[i][1], z+sides[i][2]);
		        		worldObj.scheduleBlockUpdate(x+sides[i][0], y+sides[i][1], z+sides[i][2],id,rate);
		        	}
		        }
		   }
		 
		    
		    /**
		     * Common way to recolour a block with an external tool
		     * @param world The world
		     * @param x X
		     * @param y Y
		     * @param z Z
		     * @param side The side hit with the colouring tool
		     * @param colour The colour to change to
		     * @return If the recolouring was successful
		     */
		    public boolean recolourBlock(World worldObj, int x, int y, int z, ForgeDirection side, int colour)
		    {
		    	int meta = getMetaData(worldObj,x,y,z,side.ordinal());
		    	setData();
		    	if(canColour(safe.safeGetID(worldObj,x, y, z)))
		    	{
			    	int meta1 = (colour);
			    	if(meta!=meta1)
			    	{
			    		this.setColourMetaData(worldObj, x, y, z, (byte) (meta1),side.ordinal());
			    		setTEUpdate(worldObj, x, y, z);
			    		return true;
			    	}
		    	}
		        return false;
		    }		 
	 ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 
	 
	 public static class WetConcrete extends Material
	 {

		public WetConcrete(MapColor par1MapColor) {
			super(par1MapColor);
		}
		
		 /**
	     * Returns if blocks of these materials are liquids.
	     */
	    public boolean isLiquid()
	    {
	        return true;
	    }

	    public boolean isSolid()
	    {
	        return false;
	    }
	    
	    public boolean isReplaceable()
	    {
	        return false;
	    }
	    public boolean isOpaque()
	    {
	        return false;
	    }
	    /**
	     * Returns if this material is considered solid or not
	     */
	    public boolean blocksMovement()
	    {
	        return false;
	    }

		 
	 }
	 
}