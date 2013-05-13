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

public class BlockLavaSpawner extends Block{

    public static BlockLavaSpawner instance;
    int n=0;
    public static Map<Integer, Integer> replaceable = new HashMap<Integer, Integer>();
    
	public BlockLavaSpawner(int par1) {
		super(par1, Material.rock);
		setUnlocalizedName("lavaSpawner");
		this.setTickRandomly(true);
		this.instance = this;
		setCreativeTab(ConcreteCore.tabThut);
		//*
		if(replaceable.size()==0){
			replaceable.put(0, 0);
			replaceable.put(Block.stone.blockID, 0);
			replaceable.put(Block.sandStone.blockID, 0);
			replaceable.put(Block.sand.blockID, 0);
			replaceable.put(Block.gravel.blockID, 0);
			replaceable.put(Block.dirt.blockID, 0);
			replaceable.put(Block.grass.blockID, 0);
			replaceable.put(Block.waterMoving.blockID, 0);
			replaceable.put(Block.waterStill.blockID, 0);
			replaceable.put(Block.lavaMoving.blockID, 0);
			replaceable.put(Block.lavaStill.blockID, 0);
		//	System.out.println("Put");

			this.setTickRandomly(true);
		}//*/
	}
	
    public void onBlockPlacedBy(World worldObj,int x,int y,int z,EntityLiving entity, ItemStack item){
    //	System.out.println("derp");
		this.setTickRandomly(true);
    	tickSides(worldObj,x,y,z);
    }
	
	@Override
	public void updateTick(World worldObj, int x, int y, int z, Random par5Random){
		
		//System.out.println("derp");
		//*
		if(!replaceable.containsKey(BlockSolidLava.getInstance(0).blockID)){
			replaceable.put(BlockSolidLava.getInstance(0).blockID,0);
			System.out.println("put "+BlockSolidLava.getInstance(0).blockID);
			for(int i=0;i<3;i++){
				replaceable.put(BlockLava.getInstance(i).blockID,0);
				System.out.println("put "+BlockLava.getInstance(i).blockID);
			}
		//	System.out.println("inititialized");
		}
			
		if(worldObj.getBlockId(x, y+1, z)!=this.blockID){
			int[] side = getSide(worldObj, x, y, z);
			int type = 2;
			int maxHeight = 100;
			if(side!=null){
				if(Block.blocksList[worldObj.getBlockId(x+side[0], y+side[1], z+side[2])] instanceof Block16Fluid
						&&side[1]!=1
						&&worldObj.getBlockMetadata(x+side[0], y, z+side[2])!=15){
					worldObj.setBlock(x+side[0], y+side[1], z+side[2], BlockLava.getInstance(type).blockID, 15, 3);
					n=0;
				//	System.out.println("Set");
				}else if(side[1]!=1){
					worldObj.setBlock(x+side[0], y+side[1], z+side[2], BlockLava.getInstance(type).blockID, 15, 3);
					n=0;
				//	System.out.println("Set");
				}else if(checkTop(worldObj, x, y, z)&&y+1<maxHeight&&n%100==0){
					n=0;
					worldObj.setBlock(x, y+1, z, this.blockID, 0, 2);
				}
				tickSides(worldObj, x, y, z);
			}

			n++;
		}
		//*/
	}
	

    public int tickRate(World par1World)
    {
        return 5;
    }
    
    //*
    
    public boolean checkSides(World worldObj, int x, int y, int z){
    	if(Block16Fluid.instance.fluid16Blocks.get(BlockLava.getInstance(0).blockID)==null)return false;
    	Integer[][] blockData = Block16Fluid.instance.fluid16Blocks.get(BlockLava.getInstance(0).blockID);
    	for(Integer i : blockData[2]){
    		int j = i&4095;
    		if(countSides(worldObj,x,y,z,j)!=0){
    			return true;
    		}
    	}
    	
    	return checkTop(worldObj, x, y, z);
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
   
   public int[] getSide(World worldObj, int x, int y, int z){
	   	int num = 0;
	   	Random r = new Random();
	   	int j = r.nextInt(9);

	   	int[][]sides = {{1,0,0},{-1,0,0},{0,0,1},{0,0,-1},{0,-1,0},{1,0,1},{-1,0,1},{1,0,-1},{-1,0,-1},{0,1,0}};
	   	
       for(int l=0;l<9;l++){
    	   int id = worldObj.getBlockId(x+sides[j][0], y+sides[j][1], z+sides[j][2]);
    	   int meta = worldObj.getBlockMetadata(x+sides[j][0], y+sides[j][1], z+sides[j][2]);
    	 //  System.out.println(id);
           if(replaceable.containsKey(id)){
        	//   System.out.println("Side got "+Arrays.toString(sides[j]));
        	   Block block = Block.blocksList[id];
        	   if(!(block instanceof BlockLava && meta == 15))
        	   return sides[j];
           }
           j =(j+1)%9;
       }
    	
    	int id = worldObj.getBlockId(x, y+1, z);
    	if(replaceable.containsKey(id)){

     	//   System.out.println("Side got "+Arrays.toString(sides[5]));
    		return sides[9];
    	}
    	

  	//   System.out.println("Side null");
	    return null;
	  }
   public boolean checkTop(World worldObj, int x, int y, int z){

   	int id = worldObj.getBlockId(x, y+1, z);
   	if(replaceable.containsKey(id))
   		return true;
	   
	   return false;
   }
  //*/ 
   
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon("thutconcrete:lava");
    }
   
   
}
