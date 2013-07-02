package thutconcrete.common.tileentity;

import static thutconcrete.api.datasources.DataSources.MAXID;
import static thutconcrete.api.datasources.DataSources.sourceMap;
import static net.minecraftforge.common.ForgeDirection.*;

import java.util.ArrayList;
import java.util.Vector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import thutconcrete.api.datasources.IDataSource;
import thutconcrete.api.utils.Vector3;
import thutconcrete.common.network.PacketInt;
import universalelectricity.core.block.IConductor;
import universalelectricity.core.block.IConnector;
import universalelectricity.core.block.IElectricityStorage;
import universalelectricity.core.block.IVoltage;
import universalelectricity.core.electricity.ElectricityNetworkHelper;
import universalelectricity.core.electricity.IElectricityNetwork;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityRTG extends TileEntity implements IElectricityStorage, IVoltage, IConnector, IDataSource
{
	
	public double energyStored = 0;
	public double MAXENERGY = 1E9;
	
	public static double POWEROUTPUT = 500;
	
	Vector3 here;
	Vector3 output;
	
	public int facing = 2;
	public int sourceDir = 0;
	
	int time = 0;
	
	public boolean source;
	
	public boolean hasSides = false;
	
	public int id = -1;

	@Override
	public void updateEntity()
	{
		if(here==null)
		{
			here = new Vector3(this);
			output = here.offset(ForgeDirection.getOrientation(facing));
			source = worldObj.getBlockMetadata(xCoord, yCoord, zCoord)==2;
			if(!source)
			{
				for(ForgeDirection side: VALID_DIRECTIONS)
				{
					if(side.ordinal()>1&&here.offset(side).getTileEntity(worldObj) instanceof TileEntityRTG)
					{
						sourceDir = side.ordinal();
						break;
					}
				}
			}
			
			
			if(!worldObj.isRemote&&id==-1)
				init();
		}
		
		if(source&&!hasSides)
		{
			checkSides();
		}
		
		if(source&&hasSides)
		{
			energyStored = Math.min(energyStored + (POWEROUTPUT/25 - POWEROUTPUT*energyStored/(25*MAXENERGY)), MAXENERGY);
			energy.add(0, energyStored);
			if(energy.size()>2000)
			{
				energy.remove(2000);
			}
			
			if(!worldObj.isRemote)
			{
				TileEntity tileEntity = output.getTileEntity(worldObj);
	
				if(tileEntity instanceof IConductor)
				{
					ForgeDirection outputDirection = ForgeDirection.getOrientation(facing);
	
					ArrayList<IElectricityNetwork> inputNetworks = new ArrayList<IElectricityNetwork>();
	
					IElectricityNetwork outputNetwork = ElectricityNetworkHelper.getNetworkFromTileEntity(tileEntity, outputDirection);
	
					if(outputNetwork != null && !inputNetworks.contains(outputNetwork))
					{
						double outputWatts = Math.min(outputNetwork.getRequest().getWatts(), Math.min(getJoules(), 10000));
	
						if(getJoules() > 0 && outputWatts > 0 && getJoules()-outputWatts >= 0)
						{
							outputNetwork.startProducing(this, Math.min(outputWatts, getJoules()) / getVoltage(), getVoltage());
							setJoules(energyStored - outputWatts);
						}
						else 
						{
							outputNetwork.stopProducing(this);
						}
					}
				}
			}
		}
	}
	
    /**
     * invalidates a tile entity
     */
    public void invalidate()
    {
    	super.invalidate();
    	if(sourceDir!=0)
    	{
    		TileEntity te = here.offset(getOrientation(sourceDir)).getTileEntity(worldObj);
    		if(te!=null&&te instanceof TileEntityRTG)
    		{
    			((TileEntityRTG)te).checkSides();
    		}
    	}
    }
	
	public void init()
	{
		id = MAXID;
		MAXID++;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	
	public boolean checkSides()
	{
		boolean ret = true;
		ForgeDirection[] sides = {ForgeDirection.getOrientation(facing).getRotation(UP), ForgeDirection.getOrientation(facing).getRotation(DOWN)};
		
		ret = ret && here.offset(sides[0]).getTileEntity(worldObj) instanceof TileEntityRTG && !((TileEntityRTG)here.offset(sides[0]).getTileEntity(worldObj)).source;
		ret = ret && here.offset(sides[1]).getTileEntity(worldObj) instanceof TileEntityRTG && !((TileEntityRTG)here.offset(sides[1]).getTileEntity(worldObj)).source;
		hasSides = ret;
		return ret;
	}
	
	@Override
	public double getJoules() 
	{
		return source?energyStored:0;
	}

	@Override
	public void setJoules(double joules) 
	{
		if(source)
		energyStored = joules;
	}

	@Override
	public double getMaxJoules() {
		return source?MAXENERGY:0;
	}

	@Override
	public double getVoltage() 
	{
		return source?100:0.1;
	}

	@Override
	public boolean canConnect(ForgeDirection direction) {
		return source?direction == ForgeDirection.getOrientation(facing)||direction == ForgeDirection.getOrientation(facing).getOpposite():false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox()
	{
		return AxisAlignedBB.getBoundingBox(-2, 0, -2, 2, 3, 2).offset(xCoord, yCoord, zCoord);
	}

	Vector<Double> energy = new Vector<Double>();
	
	@Override
	public void takeData(long time) {}

	@Override
	public Double[] getValues() 
	{
		return energy.toArray(new Double[energy.size()]);
	}

	@Override
	public void setData(Double[] data) {}

	@Override
	public void clearData() {}

	@Override
	public void setScales(double yCoef, int yExponent, int rate, long time) {}

	@Override
	public void syncTime(long time) {}

	@Override
	public boolean isDataOutOfBounds()
	{
		return false;
	}

	@Override
	public int maxValues() {
		return energy.size();
	}

	@Override
	public void setID(int id) 
	{
		this.id = id;
	}

	@Override
	public int getID() 
	{
		return id;
	}
	
	
   public void readFromNBT(NBTTagCompound par1)
   {
      super.readFromNBT(par1);
      id = par1.getInteger("thisid");
      facing = par1.getInteger("facing");
      sourceDir = par1.getInteger("source direction");
      energyStored = par1.getDouble("energy");
      MAXENERGY = par1.getDouble("MAXENERGY");
      sourceMap.put(id, this);
      MAXID = Math.max(MAXID, par1.getInteger("maxID"));
   }
    
	public void writeToNBT(NBTTagCompound par1)
   {
	   super.writeToNBT(par1);
	   par1.setInteger("thisid", id);
	   par1.setInteger("source direction", sourceDir);
	   par1.setInteger("facing", facing);
	   par1.setDouble("energy", energyStored);
	   par1.setDouble("MAXENERGY", MAXENERGY);
	   par1.setInteger("maxID", MAXID);
   }
	
    @Override
    public Packet getDescriptionPacket()
    {
    	return PacketInt.getPacket(this);
    }


}
