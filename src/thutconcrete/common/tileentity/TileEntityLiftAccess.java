package thutconcrete.common.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.print.attribute.standard.SheetCollate;

import cpw.mods.fml.common.network.PacketDispatcher;

import thutconcrete.common.blocks.BlockLift;
import thutconcrete.common.blocks.BlockLiftRail;
import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.network.PacketInt;
import thutconcrete.common.network.PacketLift;
import thutconcrete.common.network.PacketStampable;
import thutconcrete.common.utils.IStampableTE;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityLiftAccess extends TileEntity
{
	
	public int power = 0;
	public int prevPower = 1;
	public EntityLift lift;
	
	boolean listNull = false;
	List<Entity> list = new ArrayList<Entity>();
	
	public long time = 0;
	public int metaData = 0;
	public int blockID = 0;
	
	boolean loaded = false;
	
	public boolean called = false;
	public int floor;
	int liftID = -1;
	public int side = 0;
	
	public boolean first = true;
	public boolean read = false;
	public boolean redstone = true;
	public boolean powered = false;
	
	public void updateEntity()
	{
		if(first)
		{
			blockID = worldObj.getBlockId(xCoord, yCoord, zCoord);
			first = false;
		}
		//System.out.println(called);
		if(!worldObj.isRemote && lift == null && liftID!=-1&&blockID==BlockLift.instance.blockID)
		{
			if(EntityLift.lifts.containsKey(liftID))
			{
				lift = EntityLift.lifts.get(liftID);
				PacketDispatcher.sendPacketToAllAround(xCoord, yCoord, zCoord, 50, worldObj.provider.dimensionId, PacketLift.getPacket(this, 2, liftID));
			}
			else
			{
			//System.out.println("lost lift");
				liftID = -1;
			}
		}
		
		if(side==0)
		{
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		
		if(blockID == BlockLiftRail.staticBlock.blockID&&time%10==0)
		{
			
			if(!loaded||listNull||time%1000==0)
			{
				list = worldObj.getEntitiesWithinAABB(EntityLift.class, AxisAlignedBB.getBoundingBox(xCoord+0.5-2, 0, zCoord+0.5-2, xCoord+0.5+2, 255, zCoord+0.5+2));
				loaded = true;
			}
			boolean check = false;
		//	boolean toUpdate = false;
			for(Entity e:list)
			{
			//	System.out.println("rail check");
				if(e!=null)
				{
					check  = check || ((int)e.posY) == yCoord;
				//	toUpdate = toUpdate || e.motionY!=0;
				}
				else
				{
					listNull = true;
				}
			}
		//	if(toUpdate)
				setCalled(check);
		}
		time++;
	}
	
	public boolean checkSides()
	{
		List<Entity> check = worldObj.getEntitiesWithinAABB(EntityLift.class, AxisAlignedBB.getBoundingBox(xCoord+0.5-1, yCoord, zCoord+0.5-1, xCoord+0.5+1, yCoord+1, zCoord+0.5+1));
		if(check!=null&&check.size()>0)
		{
			lift = (EntityLift)check.get(0);
			liftID = lift.id;
		}
		return !(check == null || check.isEmpty());
	}
	
	public void setFloor(int floor)
	{
		assert(floor <=16 && floor > 0);
		if(lift!=null)
		{
			lift.setFoor(this, floor);
			this.floor = floor;
		}
	}
	
	public void setLift(EntityLift lift)
	{
		this.lift = lift;
	}
	
	public void writeToNBT(NBTTagCompound par1)
	   {
		   super.writeToNBT(par1);
		   par1.setInteger("meta", metaData);
		   par1.setInteger("block id", blockID);
		   par1.setInteger("side", side);
		   par1.setInteger("floor", floor);
		   if(lift!=null)
		   {
			   liftID = lift.id;
		   }
		   par1.setInteger("lift", liftID);
	   }
	
	   public void readFromNBT(NBTTagCompound par1)
	   {
	      super.readFromNBT(par1);
	      metaData = par1.getInteger("meta");
	      blockID = par1.getInteger("block id");
	      side = par1.getInteger("side");
	      floor = par1.getInteger("floor");
	      liftID = par1.getInteger("lift");
	      if(EntityLift.lifts.containsKey(liftID))
	      {
	    	  lift = EntityLift.lifts.get(liftID);
	      }
	   }

	   public void doButtonClick( int side, float hitX, float hitY, float hitZ)
	   {
		   if(!worldObj.isRemote)
		   {
			   if(side == this.side)
			   {
				//   System.out.println("click" +" "+side+" "+this.side+" "+lift);
				   int button = getButtonFromClick(side, hitX, hitY, hitZ);
				//   System.out.println(button+" "+hitX+" "+hitY+" "+hitZ+" "+side);
				   if(button!=0&&lift!=null&&lift.floors[button-1]!=null)
				   {
					   if(button==floor)
						   this.called = true;
					   lift.call(button);
				//	   System.out.println("floor called");
				   }
			   }
		   }
	   }
	   
	   public void setCalled(boolean called)
	   {
		   this.called = called;
		   updateBlock();
		   notifySurroundings();
	   }
	   
	   public void setSide(int side)
	   {
		//   System.out.println("side set to "+side);
		   this.side = side;
	   }
	   
	   public int getButtonFromClick(int side, float hitX, float hitY, float hitZ)
	   {
		   int ret = 0;
		   
           switch (side)
           {
	           case 0:
	           {
	        	   return 0;
	           }
	           case 1:
	           {
	        	   return 0;
	           }
	           case 2:
	           {
	        	   ret = 1+(int)(((1-hitX)*4)%4) + 4*(int)(((1-hitY)*4)%4);
	        	   return ret;
	           }
	           case 3:
	           {	        	   
	        	   ret = 1+(int)(((hitX)*4)%4) + 4*(int)(((1-hitY)*4)%4);
	        	   return ret;
	           }
	           case 4:
	           {
	        	   ret =1+4*(int)(((1-hitY)*4)%4) + (int)(((hitZ)*4)%4);
	        	   return ret;
	           }
	           case 5:
	           {
	        	   ret = 1+4*(int)(((1-hitY)*4)%4) + (int)(((1-hitZ)*4)%4);
	        	   return ret;
	           }
               default:
               {
            	   return 0;
               }
           
           }
		   
	   }
	   
	    @Override
	    public Packet getDescriptionPacket()
	    {
	    	return PacketInt.getPacket(this);
	    }

	    public Block thisBlock()
	    {
	    	if(worldObj!=null&&blockType==null)
	    	{
	    		blockType = Block.blocksList[worldObj.getBlockId(xCoord, yCoord, zCoord)];
	    	}
	    	return blockType;
	    }
	    public int getBlockId()
	    {
	    	if(worldObj!=null)
	    	return worldObj.getBlockId(xCoord, yCoord, zCoord);
	    	else
	    		return 0;
	    }

	    public int getBlockId(ForgeDirection side)
	    {
	    	return worldObj.getBlockId(xCoord+side.offsetX, yCoord+side.offsetY, zCoord+side.offsetZ);
	    }
	    public int getBlockMetadata(ForgeDirection side)
	    {
	    	return worldObj.getBlockMetadata(xCoord+side.offsetX, yCoord+side.offsetY, zCoord+side.offsetZ);
	    }
	    public void updateBlock(ForgeDirection side)
	    {
	    	worldObj.notifyBlocksOfNeighborChange(xCoord+side.offsetX, yCoord+side.offsetY, zCoord+side.offsetZ,getBlockId());
	    }
	    public void notifySurroundings()
	    {
	    	worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord,getBlockId(),0);
	    }
	    
	    public void updateBlock()
	    {
	    	worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, getBlockId(),5);
	    }
	    public TileEntity getBlockTE(ForgeDirection side)
	    {
	    	return worldObj.getBlockTileEntity(xCoord+side.offsetX, yCoord+side.offsetY, zCoord+side.offsetZ);
	    }
	    public void setBlock(ForgeDirection side, int id, int meta)
	    {
	    	worldObj.setBlock(xCoord+side.offsetX, yCoord+side.offsetY, zCoord+side.offsetZ, id, meta, 3);
	    }
}

