package thutconcrete.common.blocks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.*;
import thutconcrete.common.utils.ISaveable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BlockWorldGen extends Block
{

    @SideOnly(Side.CLIENT)
	public Icon[] iconArray;
    
    public static final String[] names = {
    										"Chalk",
									    	"lava",
									    	"Trass",
									    	"Limestone",
									    };

	public int typeid;
    public static BlockWorldGen instance;
    int n=0;
    public static Map<Integer, Integer> replaceable = new HashMap<Integer, Integer>();
    public static Map<Integer, Integer> lava = new HashMap<Integer, Integer>();
    public static final int MAX_META = names.length;
    
	public BlockWorldGen(int par1) {
		super(par1, Material.rock);
		setUnlocalizedName("worldBlock");
		this.setTickRandomly(true);
		this.instance = this;
		this.setCreativeTab(ConcreteCore.tabThut);
		
		if(replaceable.size()==0){
			replaceable.put(0, 0);
			replaceable.put(Block.stone.blockID, 0);
			replaceable.put(Block.gravel.blockID, 0);
			replaceable.put(Block.grass.blockID, 0);
			replaceable.put(Block.waterMoving.blockID, 0);
			replaceable.put(Block.waterStill.blockID, 0);
			replaceable.put(Block.lavaMoving.blockID, 0);
			replaceable.put(Block.lavaStill.blockID, 0);
			replaceable.put(this.blockID, 0);

			this.setTickRandomly(true);
		}
	}
	

    @Override
    public void onBlockAdded(World worldObj, int x, int y, int z) {
		this.setLightValue(worldObj.getBlockMetadata(x,y,z)==1?1:0);
		worldObj.scheduleBlockUpdate(x, y+1, z, BlockLava.getInstance(typeid).blockID, 5);
		worldObj.scheduleBlockUpdate(x, y, z, this.blockID, 5);
    }
    public void onBlockPlacedBy(World worldObj,int x,int y,int z,EntityLiving entity, ItemStack item){
    	
    	worldObj.setBlockMetadataWithNotify(x,y,z,item.getItemDamage(),3);

		this.setLightValue(worldObj.getBlockMetadata(x,y,z)==1?1:0);
    	
		worldObj.scheduleBlockUpdate(x, y+1, z, BlockLava.getInstance(typeid).blockID, 5);
		worldObj.scheduleBlockUpdate(x, y, z, this.blockID, 5);
    }
    
	public void onBlockClicked(World worldObj, int x, int y, int z, EntityPlayer player){
	//	System.out.println(worldObj.getBlockMetadata(x, y, z));
	}
	
	@Override
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random){
		
		if(worldObj.getBlockMetadata(x,y,z)==1&&worldObj.doChunksNearChunkExist(x, y, z, 10)){
		//	System.out.println(x+" "+y+" "+z);
			int id = worldObj.getBlockId(x, y+1, z);
			if(!lava.containsKey(BlockLava.getInstance(0).blockID)){

				for(Block block:Block.blocksList){
					if(block!=null){
					String name = block.getUnlocalizedName();
					if(block.getUnlocalizedName().toLowerCase().contains("ore")
							||block.getUnlocalizedName().toLowerCase().contains("dirt")	
							||block.getUnlocalizedName().toLowerCase().contains("sand")	
							){
			//			System.out.println("Adding "+block.getUnlocalizedName());
						replaceable.put(block.blockID, 0);
					}}
				}
				for(int i=0;i<3;i++){
					replaceable.put(BlockSolidLava.getInstance(i).blockID,0);
					lava.put(BlockLava.getInstance(i).blockID,0);
				}
	
			}
			

			int typeid = 0;
			Chunk chunk = worldObj.getChunkFromBlockCoords(x, z);
			
			int maxHeight = 64+ConcreteCore.getVolcano(x, z)-y;
			typeid = (ConcreteCore.getVolcano(x, z)<30?0:ConcreteCore.getVolcano(x, z)<60?1:2);
			
			int[][] sides = {{-1,0},{1,0},{0,-1},{0,1}};
			Random r = new Random();
			
			int meta = worldObj.getBlockId(x, y+1, z);
				
			for(int j = 1;j<maxHeight;j++){
				id = worldObj.getBlockId(x, y+j, z);
				meta = worldObj.getBlockMetadata(x, y+j, z);
				if(!(lava.containsKey(id)||replaceable.containsKey(id)))break;
				if((lava.containsKey(id)&&meta!=15)||replaceable.containsKey(id)){
					worldObj.setBlock(x, y+j, z, BlockLava.getInstance(typeid).blockID, 15, 2);
					worldObj.scheduleBlockUpdate(x, y+j, z, BlockLava.getInstance(typeid).blockID, 5);
					worldObj.scheduleBlockUpdate(x, y, z, this.blockID, 5);
					break;
				}
			}
		}
	}
	

    public int tickRate(World par1World)
    {
        return 5;
    }
    
    //*
   
  //*/ 
   /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    @SideOnly(Side.CLIENT)

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int j = 0; j < MAX_META; j++)
        {
            par3List.add(new ItemStack(par1, 1, j));
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.iconArray = new Icon[MAX_META];

        for (int i = 0; i < this.iconArray.length; i++)
        {
            this.iconArray[i] = par1IconRegister.registerIcon("thutconcrete:" + names[i]);
        }
    }
    
    protected ItemStack createStackedBlock(int par1)
    {
        return new ItemStack(this.blockID, 1, par1);
    }
   
    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int par1)
    {
        return par1;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 1;
    }
    
    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return this.blockID;
    }
    

    @SideOnly(Side.CLIENT)
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(int par1, int par2)
    {
        return this.iconArray[par2];
    }

    /**
     * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
     * block and l is the block's subtype/damage.
     */
    public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
    {
        par2EntityPlayer.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        par2EntityPlayer.addExhaustion(0.025F);

        ItemStack itemstack = this.createStackedBlock(par6);

        if (itemstack != null)
        {
            this.dropBlockAsItem_do(par1World, par3, par4, par5, itemstack);
        }
    }
    
    public String getUnlocalizedName(int par1){
    	return names[par1];
    }
    

    /**
     * Determines if the current block is replaceable by Ore veins during world generation.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param target The generic target block the gen is looking for, Standards define stone
     *      for overworld generation, and neatherack for the nether.
     * @return True to allow this block to be replaced by a ore
     */
    @Override
    public boolean isGenMineableReplaceable(World world, int x, int y, int z, int target)
    {
        return true;
    }
    
}
