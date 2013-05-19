package thutconcrete.common.blocks;

import java.util.Arrays;
import java.util.Random;

import thutconcrete.common.network.PacketHandler;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;

public class TileEntityBlock16Fluid extends TileEntity{

	public int[] metaArray = {8,8,8,8,8,8};
	public boolean shouldUpdate = true;
	int tryFalls = 0;
	int trySpreads = 0;
	Random r = new Random();
	@Override
	public void updateEntity()
	{
		if(shouldUpdate)
		{
			Block blocki = Block.blocksList[worldObj.getBlockId(xCoord, yCoord, zCoord)];
			
			if(blocki instanceof Block16Fluid)
			{
				Block16Fluid blockf = (Block16Fluid) blocki;
		    	
				if(blockf!=null&&blockf.rate<Math.random())
				{
		    		if(!Block16Fluid.instance.tryFall(worldObj, xCoord, yCoord, zCoord))
		    		{
		    			tryFalls++;
		    		}
		    		
		    		if(!Block16Fluid.instance.trySpread(worldObj, xCoord, yCoord, zCoord))
		    		{
		    			trySpreads++;
		    		}
		    		
					
					int num = Block16Fluid.instance.canHarden(worldObj, xCoord, yCoord, zCoord);
						
					if(num>0)
					{
					      tryFalls = 0;
					      trySpreads = 0;
					}
					
					 if(Math.random()>(1-(Block16Fluid.instance.SOLIDIFY_CHANCE*num)))
					 {
						 int metai = Block16Fluid.instance.getMetaData(worldObj,xCoord, yCoord, zCoord);
						 worldObj.setBlock(xCoord, yCoord, zCoord, Block16Fluid.instance.getTurnToID(worldObj.getBlockId(xCoord, yCoord, zCoord)), worldObj.getBlockMetadata(xCoord, yCoord, zCoord), 3);
						 Block16Fluid.instance.setColourMetaData(worldObj,xCoord, yCoord, zCoord, (byte) metai);
					 }
				}
			}

			if(tryFalls>100&&trySpreads>100)
			{
				tryFalls = 0;
			    trySpreads = 0;
				shouldUpdate = false;
			}
		}
		
    	
	}
	
	   public void writeToNBT(NBTTagCompound par1)
	   {
		   super.writeToNBT(par1);
		   par1.setIntArray("metaArray", metaArray);
	   }

	   public void readFromNBT(NBTTagCompound par1)
	   {
	      super.readFromNBT(par1);
	      metaArray = par1.getIntArray("metaArray");
	      
	      tryFalls = 0;
	      trySpreads = 0;
	   }
	   
	   public void sendUpdate()
	   {
		   worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	   }
	   
	   
	    @Override
	    public Packet getDescriptionPacket()
	    {
	        return PacketHandler.getPacket(this);
	    }
	   
	   
	}

