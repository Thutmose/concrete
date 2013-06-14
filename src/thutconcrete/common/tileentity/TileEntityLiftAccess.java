package thutconcrete.common.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import thutconcrete.common.blocks.BlockLiftRail;
import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.network.PacketInt;
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
	public long time = 0;
	public int metaData = 0;
	
	public boolean called = false;
	public int floor;
	int liftID = -123456;
	public int side = 0;
	
	public boolean first = true;
	
	public boolean redstone = true;
	public boolean powered = false;
	
	public void updateEntity()
	{
		if(first)
		{
			metaData = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
			first = false;
		}
		if(lift == null && liftID!=-123456)
		{
			Entity e = worldObj.getEntityByID(liftID);
			if(e instanceof EntityLift)
			{
				lift = (EntityLift)e;
			}
		}

		if(metaData==0&&time%10==0)
		{
			power = checkSides()?1:0;
			if(power!=prevPower)
			{
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, power, 3);
			}
			prevPower = power;
		}
		time++;
	}
	
	public boolean checkSides()
	{
		List<Entity> check = worldObj.getEntitiesWithinAABB(EntityLift.class, AxisAlignedBB.getBoundingBox(xCoord+0.5-1, yCoord, zCoord+0.5-1, xCoord+0.5+1, yCoord+1, zCoord+0.5+1));
		if(check!=null&&check.size()>0)
		{
			lift = (EntityLift)check.get(0);
		}
		return !(check == null || check.isEmpty());
	}
	
	public void setFloor(int floor)
	{
		assert(floor <=16 && floor > 0);
		if(lift!=null)
		{
			int j = 0;
			for(int i = 0; i<4; i++)
			{
				if(lift.floors[floor-1][i]==null)
				{
					j = i;
					break;
				}
			}
			lift.floors[floor-1][j]=this;
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
		   par1.setInteger("side", side);
		   par1.setInteger("floor", floor);
		      System.out.println("wrote "+side);
		   if(lift!=null)
		   {
			   liftID = lift.entityId;
			   par1.setInteger("lift", liftID);
			   System.out.println("saved lift"+ " "+liftID);
		   }
		   else
			   par1.setInteger("lift", -123456);
		   par1.setBoolean("first", first);
	   }
	
	   public void readFromNBT(NBTTagCompound par1)
	   {
	      super.readFromNBT(par1);
	      metaData = par1.getInteger("meta");
	      side = par1.getInteger("side");
	      floor = par1.getInteger("floor");
	      first = par1.getBoolean("first");
	      liftID = par1.getInteger("lift");
	      
	      System.out.println("read "+side+ " "+liftID);
	      if(worldObj!=null&&liftID!=-123456)
    	  if(worldObj.getEntityByID(liftID)!=null)
    	  {
    		  lift = (EntityLift)worldObj.getEntityByID(liftID);
    		  System.out.println("found lift");
    	  }
	      
	   }

	   public void doButtonClick( int side, float hitX, float hitY, float hitZ)
	   {
		   System.out.println("click" +" "+side+" "+this.side);
		   if(side == this.side)
		   {
			int button = getButtonFromClick(side, hitX, hitY, hitZ);
			   System.out.println(button+" "+hitX+" "+hitY+" "+hitZ+" "+side);
			   if(lift!=null&&lift.floors[button-1]!=null)
			   {
				   this.called = true;
				   lift.call(button);
				   System.out.println("floor called");
			   }
		   }
	   }
	   
	   public void setSide(int side)
	   {
		   System.out.println("side set to "+side);
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
	    public TileEntity getBlockTE(ForgeDirection side)
	    {
	    	return worldObj.getBlockTileEntity(xCoord+side.offsetX, yCoord+side.offsetY, zCoord+side.offsetZ);
	    }
	    public void setBlock(ForgeDirection side, int id, int meta)
	    {
	    	worldObj.setBlock(xCoord+side.offsetX, yCoord+side.offsetY, zCoord+side.offsetZ, id, meta, 3);
	    }
}

