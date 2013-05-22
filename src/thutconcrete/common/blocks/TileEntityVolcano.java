package thutconcrete.common.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.utils.ExplosionCustom;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;

public class TileEntityVolcano extends TileEntity
{
	
	public int typeid = 10;
	public int height = 0;
	public int z;
    int n=0;
    public static List<Integer> replaceable =new ArrayList<Integer>();
    public static List<Integer> lava = new ArrayList<Integer>();
    public static List<Integer> solidlava = new ArrayList<Integer>();
    public boolean firstTime = true;
    
	
	@Override
	public void updateEntity()
	{
		if(firstTime)
		{
			init();
		}
			
		if(typeid>2)
		{
			height = ConcreteCore.getVolcano(xCoord, z);
			typeid = height>60?2:height>30?1:0;
		}
		
		if(Math.random()>0.85)
		{
			int id;
			Chunk chunk = worldObj.getChunkFromBlockCoords(xCoord, getZCoord());
			
			int maxHeight = height+64-yCoord;
			
			int[][] sides = {{-1,0},{1,0},{0,-1},{0,1}};
			
			int meta = worldObj.getBlockId(xCoord, yCoord+1, getZCoord());
				
			for(int j = 1;j<maxHeight;j++)
			{
				id = worldObj.getBlockId(xCoord, yCoord+j, getZCoord());
				meta = worldObj.getBlockMetadata(xCoord, yCoord+j, getZCoord());
				
				if(!(lava.contains(id)||replaceable.contains(id)||solidlava.contains(id))) break;
				
				if((lava.contains(id)&&meta!=15)||replaceable.contains(id))
				{
					if(Math.random()<0.9999)
					{
						setLava(j);
					}
					else
					{
						ExplosionCustom boom = new ExplosionCustom();
				    	boom.doExplosion(worldObj, xCoord, yCoord+j, getZCoord(), 40*(typeid+1), true);
					}
					break;
				}
				if(solidlava.contains(id))
				{
					setLava(j);
				}
			}
		}
	}
	
	private void init()
	{
		if(replaceable.size()==0){
			replaceable.add(0);
			replaceable.add(Block.stone.blockID);
			replaceable.add(Block.gravel.blockID);
			replaceable.add(Block.grass.blockID);
			replaceable.add(Block.waterMoving.blockID);
			replaceable.add(Block.waterStill.blockID);
			replaceable.add(Block.lavaMoving.blockID);
			replaceable.add(Block.lavaStill.blockID);
		}
		
		if(!lava.contains(BlockLava.getInstance(0).blockID))
		{

			for(Block block:Block.blocksList){
				if(block!=null){
				String name = block.getUnlocalizedName();
				if(block.getUnlocalizedName().toLowerCase().contains("ore")
						||block.getUnlocalizedName().toLowerCase().contains("dirt")	
						||block.getUnlocalizedName().toLowerCase().contains("sand")	
						){
					replaceable.add(block.blockID);
				}}
			}
			for(int i=0;i<3;i++){
				solidlava.add(BlockSolidLava.getInstance(i).blockID);
				lava.add(BlockLava.getInstance(i).blockID);
			}

		}
	}
	
	private void setLava(int j)
	{
		if(!worldObj.isRemote)
		{
			worldObj.setBlock(xCoord, yCoord+j, getZCoord(), BlockLava.getInstance(typeid).blockID, 15, 3);
			TileEntityBlock16Fluid te = new TileEntityBlock16Fluid();
			if(worldObj.getBlockTileEntity(xCoord, yCoord+j, getZCoord())==null)
			{
				te = new TileEntityBlock16Fluid();
				worldObj.setBlockTileEntity(xCoord, yCoord+j, getZCoord(), te);
				te.shouldUpdate = true;
			}
			else
			{
				te = (TileEntityBlock16Fluid)worldObj.getBlockTileEntity(xCoord, yCoord+j, getZCoord());
				te.shouldUpdate = true;
			}
			
		}
	}
	
	   public void writeToNBT(NBTTagCompound par1)
	   {
		   super.writeToNBT(par1);
		   par1.setInteger("type", typeid);
		   par1.setInteger("h", height);
		   par1.setInteger("z location", z);
	   }

	   public void readFromNBT(NBTTagCompound par1)
	   {
	      super.readFromNBT(par1);
	      typeid = par1.getInteger("type");
	      height = par1.getInteger("h");
	      z = par1.getInteger("z location");
	   }

	   public int getZCoord()
	   {
		   return z;
	   }
	   
}
