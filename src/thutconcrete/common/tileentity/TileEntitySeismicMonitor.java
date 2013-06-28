package thutconcrete.common.tileentity;

import icbm.api.explosion.ExplosionEvent;
import icbm.api.explosion.IExplosive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import thutconcrete.api.utils.Vector3;
import thutconcrete.common.Volcano;
import thutconcrete.common.network.PacketInt;
import thutconcrete.common.network.PacketSeismicMonitor;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import cpw.mods.fml.server.FMLServerHandler;

import dan200.computer.api.IComputerAccess;
import dan200.computer.api.IPeripheral;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.management.PlayerInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class TileEntitySeismicMonitor extends TileEntity implements IPeripheral
{
	
	public static int VALUECOUNT = 160;
	
	public boolean hasSensors = false;
	
	public boolean emitRedstone = false;
	
	public static double speed = 1000;
	
	public static int MAXID = 0;
	public int id = -1;
	
	public double coef = 1;
	public int offset = 0;
	
	public int side = 2;
	
	public static Map<Integer, TileEntitySeismicMonitor> stationMap = new ConcurrentHashMap<Integer, TileEntitySeismicMonitor>();
	
	public Map<EntityTNTPrimed, Double> tnt = new ConcurrentHashMap<EntityTNTPrimed, Double>();

	public static int FUTURE = 100;
	ArrayList<Double> values = new ArrayList<Double>(VALUECOUNT);
	public LinkedList<Double> futureValues = new LinkedList<Double>();
	ArrayList<Double> values_old = new ArrayList<Double>(VALUECOUNT);
	
	public TileEntitySeismicMonitor[] stations = new TileEntitySeismicMonitor[16];
	public int[] stationIDs = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
	
	public int button = 0;
	
	Vector<Volcano> volcanoList = new Vector<Volcano>();
	
	int num = 0;
	int count = 0;
	
	Random r = new Random(1000);
	
	public double scale = 10;
	public int exponent = 1;
	public boolean first = true;
	boolean changed = false;
	
	public boolean paused = false;
	
	int prevPower = 0;
	
	long time = 0;
	public int rate = 1;
	IComputerAccess comp;
	
	@ForgeSubscribe
	public void tntPrimed(EntityJoinWorldEvent evt)
	{
		if(evt.entity instanceof EntityTNTPrimed && evt.world == worldObj)
		{
			tnt.put((EntityTNTPrimed)evt.entity, (double) 80);
		}
	}
	
	@ForgeSubscribe
	public void explosionEvent(ExplosionEvent evt)
	{
		if(evt.explosive!=null)
		{
			double energy = evt.explosive.getEnergy();
			System.out.println(energy+" "+evt.explosive.getExplosiveName());
			Vector3 explosion = new Vector3(evt.x, evt.y, evt.z);
			Vector3 here = new Vector3(this);
			double distanceSq = explosion.distToSq(here);
			int shift = ((int) (Math.sqrt(distanceSq)*VALUECOUNT/speed))%(FUTURE*VALUECOUNT);
			double signal = energy/distanceSq;
			futureValues.set(shift, signal);
			
		}
	}

	@Override
	public void updateEntity()
	{
		if(first)
		{
			first = false;
			MinecraftForge.EVENT_BUS.register(this);
			init();
		}
		
		int power = worldObj.getBlockPowerInput(xCoord, yCoord, zCoord);
		if(power>0||power!=prevPower)
		{
			prevPower = power;
			paused = power!=0;
		}
		
		
		//*/
		if(tnt.size()>0)
		{
			//System.out.println("tnt");
			for(EntityTNTPrimed t:tnt.keySet())
			{
				double d = tnt.get(t) - 1;
				tnt.put(t, d);
				if(d<1)
				{
					double energy = 10E5;
					Vector3 explosion = new Vector3(t);
					Vector3 here = new Vector3(this);
					double distanceSq = explosion.distToSq(here);
					int shift = ((int) (VALUECOUNT*Math.sqrt(distanceSq)/speed))%(FUTURE*VALUECOUNT);
					double signal = energy/distanceSq;
					futureValues.set(shift, signal);
					System.out.println(shift+" "+energy+" "+distanceSq+" "+futureValues.size()+" "+values.size());
					
					tnt.remove(t);
				}
			}
		}
		//*/
		if(!paused&&hasSensors)
		{
			boolean before = emitRedstone;
			for(TileEntitySeismicMonitor s:stations)
			{
				if(s!=null)
				{
					s.scale = scale;
					s.exponent = exponent;
					s.rate = rate;
					s.takeData(time);
					emitRedstone = emitRedstone || s.emitRedstone;
				}
			}
			if(before!=emitRedstone)
			{
				updateBlock();
				notifySurroundings();
			}
			cleanUp();
		}
		
		
		
		if(emitRedstone)
		{
			count++;
		//	System.out.println(emitRedstone);
		}
		
		if(count>5)
		{
			count = 0;
			emitRedstone = false;
			updateBlock();
			notifySurroundings();
		}
		
		if(!changed)
		{
			futureValues.pop();
			futureValues.add(0.0);
		}
		
		if(time%(VALUECOUNT)==0&&comp!=null)
		{
			comp.queueEvent("seismicData", allValues());
		}
		time++;
		changed = false;
		syncTime();
	}
	
	public synchronized void takeData(long time)
	{
		
		if(num!=(Volcano.volcanoMap.size()))
		{
			num = Volcano.volcanoMap.size();
			for(Integer i :Volcano.volcanoMap.keySet())
			{
				if(Volcano.volcanoMap.get(i)!=null)
					for(Integer j :Volcano.volcanoMap.get(i).keySet())
					{
						Volcano v = Volcano.volcanoMap.get(i).get(j);
						if(!volcanoList.contains(v))
						{
							volcanoList.add(v);
						}
					}
			}
		}
		
		double dv = 0;
		for(Volcano v: volcanoList)
		{
			double distanceSq = v.distanceSq(this);
			dv += (v.strength(time-(Math.sqrt(distanceSq)/speed)))/distanceSq;
		}
		dv+=futureValues.size()>0?futureValues.pop():0;
		futureValues.add((double) 0);
		changed = true;
		
	//	System.out.println(dv+" "+scale+" "+exponent+worldObj);
		emitRedstone = emitRedstone||dv>scale;
		values.add(0,dv);
		if(values.size()>VALUECOUNT)
		{
			values_old.add(0,values.get(VALUECOUNT));
			values.remove(VALUECOUNT);
		}
		if(values_old.size()>VALUECOUNT)
		{
			values_old.remove(VALUECOUNT);
		}
	}
	
	public boolean doButtonClick( int side, float hitX, float hitY, float hitZ)
	{
		if(side==1)
		{
			int button = getButtonFromClick(hitX, hitY, hitZ);
			toggleButton(button);
			System.out.println(button+" "+getButton(button)+" "+worldObj);
			return true;
		}
		if(side==this.side)
		{
			int button = getSideButtonFromClick(side, hitX, hitY, hitZ);
		//	getExponentFromButton(button);
			if(button==8)
				setScale(exponent+1);
			if(button==5)
				setScale(exponent-1);
			if(button==4)
				setScale(coef+0.1);
			if(button==1)
				setScale(coef-0.1);
			if(button==6)
				setRate(rate-1);
			if(button==7)
				setRate(rate+1);
			if(button==10)
				setRate(rate-10);
			if(button==11)
				setRate(rate+10);
			if(button==2)
				offset = offset<=0?0:offset-1;
			if(button==9)
				paused = !paused;
			if(button==3)
				offset = offset>=rate-1?rate-1:offset+1;
			if(button==15||button==14)
				clearData();
		//	System.out.println(button+" "+rate+" "+exponent+" "+coef);
			return true;
		}
		if(side==ForgeDirection.getOrientation(this.side).getOpposite().ordinal())
		{
			int button = getSideButtonFromClick(side, hitX, hitY, hitZ);
			if(button==4)
			{
				stations = new TileEntitySeismicMonitor[16];
				stationIDs = new int[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
			}
		}
		return false;
	}
	
	 public int getButtonFromClick(float hitX, float hitY, float hitZ)
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
	        	   ret = 1+(int)(((1-hitX)*4)%4) + 4*(int)(((1-hitZ)*4)%4);
	        	   return ret;
	           }
	           case 3:
	           {	        	   
	        	   ret = 1+(int)(((hitX)*4)%4) + 4*(int)(((hitZ)*4)%4);
	        	   return ret;
	           }
	           case 4:
	           {
	        	   ret =1+4*(int)(((1-hitX)*4)%4) + (int)(((hitZ)*4)%4);
	        	   return ret;
	           }
	           case 5:
	           {
	        	   ret = 1+4*(int)(((hitX)*4)%4) + (int)(((1-hitZ)*4)%4);
	        	   return ret;
	           }
             default:
             {
          	   return 0;
             }
         
         }
		   
	   }
	public int getSideButtonFromClick(int side, float hitX, float hitY, float hitZ)
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
	 
	
	public void syncTime()
	{
		for(TileEntitySeismicMonitor s : stations)
		{
			if(s!=null)
			{
				s.time = time;
			}
		}
	}
	
	public void cleanUp()
	{
		hasSensors = false;
		for(int i = 0; i<16; i++)
		{
			if(stations[i]==null&&stationIDs[i]!=-1)
			{
				stationIDs[i]=-1;
			}
			hasSensors = hasSensors||stationIDs[i]!=-1;
		}
	}
	
	public void init()
	{
		if(!worldObj.isRemote&&id==-1)
		{
			id = MAXID;
			MAXID++;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
		for(int i = 0; i<FUTURE*VALUECOUNT;i++)
		{
			futureValues.add((double) 0);
		}
		
		popMap();
	}
	
	public void addStation(TileEntitySeismicMonitor station, int index)
	{
		boolean toClear = false;
		if(index>=0&&index<16)
		{
			if(stations[index]!=station||stationIDs[index]!=station.id)
			{
				stations[index] = station;
				stationIDs[index]= station.id;
				toClear = true;
				System.out.println("station added");
				hasSensors = true;
			}
		}
		if(toClear)
		{
			clearData();
		}
	}
	
	public void addStation(int i, int index)
	{
		if(index<0||index>15)
			return;
		boolean toClear = false;
		if(stationMap.containsKey(i))
		{
			if(stations[index]!=stationMap.get(i)||i != stationIDs[index])
			{
				stations[index] = stationMap.get(i);
				stationIDs[index]= i;
				System.out.println("station added");
				toClear = true;
				hasSensors = true;
			}
		}
		if(toClear)
		{
			clearData();
		}
		//System.out.println(i+" "+this.id);
	}

	public void clearData()
	{
		for(TileEntitySeismicMonitor s : stations)
		{
			if(s!=null)
			{
				s.values = new ArrayList<Double>(VALUECOUNT);
			}
		}
		offset=0;
		setRate(rate);
		setScale(exponent);
	}
	
	public String[] names = 
		{
			"data",
			"oldData",
			"setScale",
			"setRate",
		};
	
	@Override
	public String getType() 
	{
		return "seismicMonitor";
	}

	@Override
	public String[] getMethodNames()
	{
		return names;
	}

	@Override
	public Object[] callMethod(IComputerAccess computer, int method,
			Object[] arguments) throws Exception 
	{
		if(method==0)
		{
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
				return values(getValues(num));
			}
			return allValues();
		}
		if(method==1)
		{
			return values(getOldValues());
		}
		if(method==2)
		{
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
				setScale(num);
			}
		}
		if(method==3)
		{
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
				setScale(num);
			}
		}
		
		return null;
	}
	
	@Override
	public boolean canAttachToSide(int side) 
	{
		return side==0;
	}

	@Override
	public void attach(IComputerAccess computer) 
	{
		comp = computer;
	}

	@Override
	public void detach(IComputerAccess computer) 
	{
		comp = null;
	}
	
	Object[] values(Double[] vals)
	{
		Object[] ret;
		Map map = new HashMap();
		for(int i = 0; i<vals.length; i++)
		{
			map.put(i+1, vals[i]);
		}
		//System.out.println(map.toString());
		ret = new Map[] {map};
		return ret;
	}
	
	Object[] allValues()
	{
		Object[] ret;
		List<Object> obj = new ArrayList<Object>();
		for(TileEntitySeismicMonitor s: stations)
		{
			Double[] vals = s.getValues();
			Map map = new HashMap();
			for(int i = 0; i<vals.length; i++)
			{
				map.put(i+1, vals[i]);
			}
			//System.out.println(map.toString());
		}
		ret = obj.toArray();
		return ret;
	}
	
    @Override
    public Packet getDescriptionPacket()
    {
        return PacketSeismicMonitor.getPacket(this);
    }
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setScale(int num)
	{
		exponent = num;
		if(exponent < -7)
		{
			exponent = 17;
		}
		if(exponent > 17)
		{
			exponent = -7;
		}
		scale = coef*Math.pow(10, exponent);
		if(!worldObj.isRemote)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public void setScale(double coef)
	{
		this.coef = coef;
		if(this.coef < 1)
		{
			this.coef = 9.9;
			exponent -= 1;
		}
		if(this.coef >= 10)
		{
			this.coef = coef/10;
			exponent += 1;
		}
		setScale(exponent);
	}
	
	public void setRate(int num)
	{
		rate = Math.min(Math.max(num,0),VALUECOUNT);
		if(!worldObj.isRemote)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public Double[] getValues() {
		return values.toArray(new Double[0]);
	}
	public Double[] getValues(int i) {
		if(i<0||i>15) return null;
		if(stationIDs[i]!=-1&&stations[i]!=null)
		return stations[i].getValues();
		return null;
	}
	public Double[] getOldValues() {
		return values_old.toArray(new Double[0]);
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
    	worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord,getBlockId(),1);
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
    
    public void readFromNBT(NBTTagCompound par1)
	   {
	      super.readFromNBT(par1);
	      id = par1.getInteger("thisid");
	      rate = par1.getInteger("rate");
	      exponent = par1.getInteger("exponent");
	      side = par1.getInteger("side");
	      coef = Math.min(par1.getDouble("coef"),1);
	      stationMap.put(id, this);
	      MAXID = Math.max(MAXID, par1.getInteger("maxID"));
	      for(int i = 0; i<16; i++)
	      {
	    	  stationIDs[i] = par1.getInteger("station"+i);
	      }
	      button = par1.getInteger("button");
	   }
    
	public void writeToNBT(NBTTagCompound par1)
	   {
		   super.writeToNBT(par1);
		   par1.setInteger("thisid", id);
		   par1.setInteger("side", side);
		   par1.setInteger("rate", rate);
		   par1.setInteger("exponent", exponent);
		   par1.setInteger("maxID", MAXID);
		   par1.setDouble("coef", coef);
		   for(int i = 0; i<16; i++)
		   {
			   TileEntitySeismicMonitor s = stations[i];
			   par1.setInteger("station"+i, stationIDs[i]);
		   }
		   par1.setInteger("button", button);
	   }
	
	public void popMap()
	{
		stationMap.put(id, this);
		for(int i = 0; i<16; i++)
		{
			if(stationMap.get(stationIDs[i])!=null)
				addStation(stationMap.get(stationIDs[i]), i);
		}
	}
	
    public ForgeDirection getFacing()
    {
    	return ForgeDirection.getOrientation(side);
    }
    
   public void setSide(int side)
   {
	   if(side!=0&&side!=1)
	   {
		   this.side = side;
		   worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	   }
   }
   
   public boolean getButton(int i)
   {
	   i-=1;
	   if(i<0||i>15)
		   return false;
	   return ((button>>i)&1) == 1;
   }
	
   public void setButton(int i)
   {
	   i-=1;
	   if(i<0||i>15)
		   return;
	   button |= 1<<i;
   }
   
   public void toggleButton(int i)
   {
	   i-=1;
	   if(i<0||i>15)
		   return;
	   button ^= 1<<i;
   }
   
   public int getScaleIndex()
   {
	   int ret = 0;
	   ret = exponent+7;
	   return ret;
   }
   
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return INFINITE_EXTENT_AABB;
	}
	
	
	
}
