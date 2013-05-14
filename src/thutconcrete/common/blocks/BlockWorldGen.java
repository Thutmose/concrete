package thutconcrete.common.blocks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BlockWorldGen extends Block{


	public int typeid;
    public static Block instance;
    int n=0;
    public static Map<Integer, Integer> replaceable = new HashMap<Integer, Integer>();
    public static Map<Integer, Integer> lava = new HashMap<Integer, Integer>();
    
	public BlockWorldGen(int par1) {
		super(par1, Material.rock);
		setUnlocalizedName("lavaSpawner"+typeid);
		this.setTickRandomly(true);
		this.instance = this;

		this.setLightValue(1);
		setCreativeTab(ConcreteCore.tabThut);
		//*
		if(replaceable.size()==0){
			replaceable.put(0, 0);
			replaceable.put(Block.stone.blockID, 0);
			replaceable.put(Block.gravel.blockID, 0);
			replaceable.put(Block.grass.blockID, 0);
			replaceable.put(Block.waterMoving.blockID, 0);
			replaceable.put(Block.waterStill.blockID, 0);
			replaceable.put(Block.lavaMoving.blockID, 0);
			replaceable.put(Block.lavaStill.blockID, 0);
		//	System.out.println("Put");

			this.setTickRandomly(true);
		}//*/
	}
	

    @Override
    public void onBlockAdded(World worldObj, int x, int y, int z) {
		this.setTickRandomly(true);
		worldObj.scheduleBlockUpdate(x, y+1, z, BlockLava.getInstance(typeid).blockID, 5);
		worldObj.scheduleBlockUpdate(x, y, z, this.blockID, 5);
    }
    public void onBlockPlacedBy(World worldObj,int x,int y,int z,EntityLiving entity, ItemStack item){
    //	System.out.println("derp");
		worldObj.scheduleBlockUpdate(x, y+1, z, BlockLava.getInstance(typeid).blockID, 5);
		worldObj.scheduleBlockUpdate(x, y, z, this.blockID, 5);
		this.setTickRandomly(true);
    }
	
	@Override
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random){
		
		if(worldObj.getBlockMetadata(x,y,z)==0&&worldObj.doChunksNearChunkExist(x, y, z, 10)){
			
			int id = worldObj.getBlockId(x, y+1, z);
			
			if(!lava.containsKey(BlockLava.getInstance(0).blockID)){

				for(Block block:Block.blocksList){
					if(block!=null){
					String name = block.getUnlocalizedName();
					if(block.getUnlocalizedName().toLowerCase().contains("ore")
							||block.getUnlocalizedName().toLowerCase().contains("dirt")	
							||block.getUnlocalizedName().toLowerCase().contains("sand")	
							){
						System.out.println("Adding "+block.getUnlocalizedName());
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
			//
			//chunk.
			int[][] sides = {{-1,0},{1,0},{0,-1},{0,1}};
			Random r = new Random();
			
			int meta = worldObj.getBlockId(x, y+1, z);
				
				
			System.out.println(x+" "+y+" "+z);

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
   
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:lava");
    }
   
   
}
