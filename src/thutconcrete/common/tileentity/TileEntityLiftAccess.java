package thutconcrete.common.tileentity;


import static net.minecraftforge.common.ForgeDirection.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.print.attribute.standard.SheetCollate;

import appeng.api.WorldCoord;
import appeng.api.events.GridTileLoadEvent;
import appeng.api.events.GridTileUnloadEvent;
import appeng.api.me.tiles.IDirectionalMETile;
import appeng.api.me.tiles.IGridMachine;
import appeng.api.me.tiles.IGridTileEntity;
import appeng.api.me.util.IGridInterface;

import cpw.mods.fml.common.network.PacketDispatcher;
import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

import thutconcrete.api.network.PacketStampable;
import thutconcrete.api.utils.IStampableTE;
import thutconcrete.api.utils.Vector3;
import thutconcrete.common.blocks.BlockLift;
import thutconcrete.common.blocks.BlockLiftRail;
import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.network.PacketInt;
import thutconcrete.common.network.PacketLift;
import universalelectricity.core.block.IElectricityStorage;
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
import net.minecraftforge.common.MinecraftForge;

public class TileEntityLiftAccess extends TileEntity implements IPeripheral, IGridMachine, IDirectionalMETile
{
	
	public int power = 0;
	public int prevPower = 1;
	public EntityLift lift;
	
	boolean listNull = false;
	List<Entity> list = new ArrayList<Entity>();
	Vector3 here;
	
	public Vector3 root = new Vector3();
	public IElectricityStorage source;
	public TileEntityLiftAccess rootNode;
	public Vector<TileEntityLiftAccess> connected = new Vector<TileEntityLiftAccess>();
	ForgeDirection sourceSide;
	public double energy;
	
	public long time = 0;
	public int metaData = 0;
	public int blockID = 0;
	
	boolean loaded = false;
	
	public boolean called = false;
	public int floor = 0;
	public int calledYValue = -1;
	public int calledFloor = 0;
	int liftID = -1;
	public int side = 2;
	
	int tries = 0;
	
	public boolean toClear = false;
	
	public boolean first = true;
	public boolean read = false;
	public boolean redstone = true;
	public boolean powered = false;
	
	public void updateEntity()
	{
		if(first)
		{
			blockID = worldObj.getBlockId(xCoord, yCoord, zCoord);
			here = new Vector3(this);
			GridTileLoadEvent evt = new GridTileLoadEvent(this, worldObj, getLocation());
			MinecraftForge.EVENT_BUS.post(evt);
			first = false;
		}
		
		if(lift!=null&&blockID==BlockLift.instance.blockID&&getBlockMetadata()==1)
		{
			calledFloor = lift.destinationFloor;
			if(calledFloor!=lift.destinationFloor)
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		
		if(blockID == BlockLiftRail.staticBlock.blockID&&time%10==0)
		{
			if(rootNode==null)
			{
				checkPower();
			}
			if(rootNode==this&&time%100==0)
			{
				TileEntity te = here.offset(sourceSide).getTileEntity(worldObj);
				if(te!=source)
				{
					clearConnections();
				}
			}
			if(!loaded||listNull||time%1000==0)
			{
				list = worldObj.getEntitiesWithinAABB(EntityLift.class, AxisAlignedBB.getBoundingBox(xCoord+0.5-2, 0, zCoord+0.5-2, xCoord+0.5+2, 255, zCoord+0.5+2));
				loaded = true;
			}
			boolean check = false;
			for(Entity e:list)
			{
				if(e!=null)
				{
					((EntityLift)e).source = source;
					boolean flag = ((EntityLift)e).destinationFloor!=0&&((int)((EntityLift)e).prevFloorY)==yCoord;
					check  = check || ((int)(e.posY) == yCoord && !flag);
				}
				else
				{
					listNull = true;
				}
			}
			setCalled(check);
		}
		time++;
	}
	
	public void checkPower()
	{
		TileEntity down = here.offset(DOWN).getTileEntity(worldObj);
		int id = here.offset(DOWN).getBlockId(worldObj);
		if(down==null)
		{
			boolean found = false;
			for(ForgeDirection side: VALID_DIRECTIONS)
			{
				if(side!=UP&&side!=DOWN)
				{
					TileEntity te = here.offset(side).getTileEntity(worldObj);
					if(te instanceof IElectricityStorage)
					{
						found = true;
						source = (IElectricityStorage)te;
						rootNode = this;
						sourceSide = side;
					}
				}
			}
			if(!found)
				clearConnections();
			else
			{
				TileEntity up = here.offset(UP).getTileEntity(worldObj);
				if(up!=null&&up instanceof TileEntityLiftAccess)
				{
					((TileEntityLiftAccess)up).clearConnections();
				}
			}
		}
		else if(down instanceof TileEntityLiftAccess)
		{
			source = ((TileEntityLiftAccess)down).source;
			rootNode = ((TileEntityLiftAccess)down).rootNode;
		}
	}
	
	public void clearConnections()
	{
		if(here!=null)
		{
			source = null;
			rootNode = null;
			TileEntity up = here.offset(UP).getTileEntity(worldObj);
			if(up!=null&&up instanceof TileEntityLiftAccess)
			{
				((TileEntityLiftAccess) up).clearConnections();
			}
		}
	}
	
	public double getEnergy()
	{
		if(source!=null)
			return source.getJoules();
		else
			return 0;
	}
	
	public void setEnergy(double energy)
	{
		if(source!=null)
		{
			source.setJoules(energy);
		}
	}
	
	public String connectionInfo()
	{
		String ret = "";
		if(source!=null)
		{
			ret = "Energy stored: "+getEnergy();
		}
		return ret;
	}
	
    /**
     * invalidates a tile entity
     */
    public void invalidate()
    {
        this.tileEntityInvalid = true;
        clearConnections();
		GridTileUnloadEvent evt = new GridTileUnloadEvent(this, worldObj, getLocation());
		MinecraftForge.EVENT_BUS.post(evt);
    }
    
    /**
     * validates a tile entity
     */
    public void validate()
    {
        this.tileEntityInvalid = false;
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
	
	public synchronized void setFloor(int floor)
	{
		if(lift!=null&&floor <=64 && floor > 0)
		{
			lift.setFoor(this, floor);
			this.floor = floor;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
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
		   root.writeToNBT(par1, "root");
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
	      root = root.readFromNBT(par1, "root");
	      if(EntityLift.lifts.containsKey(liftID))
	      {
	    	  lift = EntityLift.lifts.get(liftID);
	      }
	   }

	   public void doButtonClick( int side, float hitX, float hitY, float hitZ)
	   {
		   if(!worldObj.isRemote&&lift!=null)
		   {
			   if(side == this.side)
			   {
				   int button = getButtonFromClick(side, hitX, hitY, hitZ);
				   buttonPress(button);
				   calledFloor = lift.destinationFloor;
				   worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				   System.out.println(calledFloor+" "+button+" "+lift);
			   }
		   }
	   }
	   
	   public synchronized void callYValue(int yValue)
	   {
		   if(lift!=null)
		   {
			   lift.callYValue(yValue);
		   }
	   }
	   
	   public synchronized void buttonPress(int button)
	   {
		   if(button!=0&&button<=64&&lift!=null&&lift.floors[button-1]!=null)
		   {
			   if(button==floor)
				   this.called = true;
			   lift.call(button);
		   }
	   }
	   
	   public void setCalled(boolean called)
	   {
		   if(called!=this.called)
		   {
			   this.called = called;
			   updateBlock();
			   notifySurroundings();
		   }
	   }
	   
	   public void setSide(int side)
	   {
		   if(side!=0&&side!=1)
		   {
			   this.side = side;
			   worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		   }
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
	    	if(blockID==BlockLift.instance.blockID&&getBlockMetadata()==1)
	    		return PacketInt.getPacket(this);
	    	else
	    		return null;
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
	    public int getBlockMetadata()
	    {
	    	if(worldObj!=null)
	    	return worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
	    	else
	    		return 0;
	    }
	    
	    public ForgeDirection getFacing()
	    {
	    	return ForgeDirection.getOrientation(side);
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

	    //////////////////////////////////////////////////////////ComputerCraft Stuff/////////////////////////////////////////////////////////////////
		@Override
		public String getType() {
			if(blockID==BlockLift.instance.blockID&&getBlockMetadata()==1)
				return "LiftController";
			return null;
		}
		
		public String[] names = 
			{
				"call",
				"goto",
				"setFloor",
			};

		@Override
		public String[] getMethodNames() {
			return names;
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, int method,
				Object[] arguments) throws Exception {
			
			
			if(arguments.length>0)
			{
				int num = 0;
						
				if(arguments[0] instanceof Double)
				{
					num = ((Double)arguments[0]).intValue();
				}
				if(arguments[0] instanceof String)
				{
					num = Integer.parseInt((String)arguments[0]);
				}
				
				if(num!=0)
				{
					if(method==0)
					{
						buttonPress(num);
					}
					if(method==1)
					{
						callYValue(num);
					}
					if(method==2)
					{
						setFloor(num);
					}
				}
			}
			
			
			return null;
		}

		@Override
		public boolean canAttachToSide(int side) {
			if(blockID==BlockLift.instance.blockID&&getBlockMetadata()==1)
				return side!=this.side;
			return false;
		}

		@Override
		public void attach(IComputerAccess computer) {
			// TODO Auto-generated method stub
		}

		@Override
		public void detach(IComputerAccess computer) {
			// TODO Auto-generated method stub
			
		}

	    IGridInterface igi;
	    boolean hasPower;

		@Override
		public WorldCoord getLocation() {
			return new WorldCoord( xCoord, yCoord, zCoord );
		}

		@Override
		public boolean isValid() {
			return true;
		}

		@Override
		public void setPowerStatus(boolean hasPower) {
			this.hasPower = hasPower;
		}

		@Override
		public boolean isPowered() {
			return hasPower;
		}

		@Override
		public IGridInterface getGrid() {
			return igi;
		}

		@Override
		public void setGrid(IGridInterface gi) {
			igi = gi;
		}

		@Override
		public World getWorld() {
			return worldObj;
		}

		@Override
		public boolean canConnect(ForgeDirection dir) {
			if(blockID == BlockLiftRail.staticBlock.blockID)
				return true;
			else
				return dir != ForgeDirection.getOrientation(side);
		}

		@Override
		public float getPowerDrainPerTick() {
			return 0.0625F;
		}

}

