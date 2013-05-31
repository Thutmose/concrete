package thutconcrete.common.tileentity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import thutconcrete.common.blocks.*;
import thutconcrete.common.corehandlers.ConfigHandler;
import thutconcrete.common.network.PacketHandler;
import thutconcrete.common.network.PacketTEB16F;
import thutconcrete.common.utils.IMultiPaintableTE;
import thutconcrete.common.utils.LinearAlgebra;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityBlock16Fluid extends TileEntity// implements IMultiPaintableTE
{

	public int[] metaArray = {8,8,8,8,8,8};
	public int[] sideArray = {0,0,0,0,0,0};
	double[] pointing = {1, 0, 0};
	Random r = new Random();
	long time = 0;
	public Icon[] icons = new Icon[6];
	public int[] iconIDs = {0,0,0,0,0,0}; 
	public boolean init;
	public boolean updates = false;
	Entity target;
	public double[] source = new double[]{(xCoord+0.5), (yCoord+1.5),(zCoord+0.5)};;
	public double[] dTarget = {1, 0, 0};
	public double[] cTarget = {1, 0, 0};
	public double[] sTarget = {1, 0, 0};
	public double[] sPointing = {1, 0, 0};
	public static double pi = Math.PI;
	
	public boolean powered = false;
	
	public double tracking = 0.05;
	public double range = 10;
	
	public TileEntityBlock16Fluid(boolean updates)
	{
		super();
		this.updates = updates;
	}
	
	public TileEntityBlock16Fluid()
	{
		this(false);
	}
	
	
	public void updateEntity()
	{
		
		for(ForgeDirection side: ForgeDirection.VALID_DIRECTIONS)
		{
			if(worldObj.getIndirectPowerOutput(xCoord, yCoord, zCoord, side.ordinal()))
			{
				System.out.println(worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord));
				powered = true;
			}
		}
		
		
		if(powered&&time%5==0)
		{

			source = new double[]{(xCoord+0.5), (yCoord+1.5),(zCoord+0.5)};
			if(LinearAlgebra.distToEntity(source, target)>range)
			{
				getTarget();
			}
			if(target!=null)
			{
			List<Entity> list = LinearAlgebra.firstEntityOnLine(range, pointing, new double[] {xCoord+0.5, yCoord+1.5, zCoord+0.5}, worldObj, true);
			if(!worldObj.isRemote&&list!=null&&list.size()>0)
				for(Entity e: list)
				{
					System.out.println(e+" from "+xCoord+" "+yCoord+" "+zCoord);
					e.setFire(5);
					e.attackEntityFrom(DamageSource.onFire, 5);
				}
			}
			changePointing();
		}
		powered = false;
	} 
	
	public void changePointing()
	{
		getTarget();
		setVectors();
		if(!LinearAlgebra.isEntityVisiblefirstEntityOnLine(target, cTarget, source, worldObj))
		{
			target=null;
			return;
		}

		if(dTarget[1]==0&&dTarget[2]==0) return; 
		
		double dphi = dTarget[1]>0?-tracking:tracking, dtheta = Math.abs(LinearAlgebra.moduloPi(dTarget[2])+tracking)>Math.abs(dTarget[2])||LinearAlgebra.moduloPi(dTarget[2])>dTarget[2]? tracking:-tracking;

		
		pointing = LinearAlgebra.vectorRotateAboutAngles(pointing, dphi, dtheta);
	}
	
	public void getTarget()
	{
		List<Entity> list = worldObj.getEntitiesWithinAABB(EntityLiving.class, 
				AxisAlignedBB.getBoundingBox(xCoord-range, yCoord-range, zCoord-range, xCoord+range, yCoord+range, zCoord+range));
		for(Entity e:list)
		{
			double [] direction = {e.posX-(xCoord+0.5), e.posY-(yCoord+1.5), e.posZ-(zCoord+0.5)};
			source = new double[]{(xCoord+0.5), (yCoord+1.5),(zCoord+0.5)};
			if(LinearAlgebra.isEntityVisiblefirstEntityOnLine(e, direction, source, worldObj))
			{
				target = e;
				setVectors();
				return;
			}
		}
	}
	
	private void setVectors()
	{
		if(target!=null)
		{
			cTarget = LinearAlgebra.vectorNormalize(LinearAlgebra.getVectorToEntity(source, target));
			sTarget = LinearAlgebra.vectorToSpherical(cTarget, true);
			sPointing = LinearAlgebra.vectorToSpherical(pointing,true);
			dTarget = LinearAlgebra.vectorSubtract(sPointing,sTarget);
		}
	}
	
	public boolean canUpdate()
	{
		return updates;
	}
	
	public void writeToNBT(NBTTagCompound par1)
	   {
		   super.writeToNBT(par1);
		   par1.setIntArray("metaArray", metaArray);
		   par1.setIntArray("iconsArray", iconIDs);
		   par1.setIntArray("sideArray", sideArray);
	   }
	
	   public void readFromNBT(NBTTagCompound par1)
	   {
	      super.readFromNBT(par1);
	      metaArray = par1.getIntArray("metaArray");
	      iconIDs = par1.getIntArray("iconsArray");
	      sideArray = par1.getIntArray("sideArray");
	      setIconArray();
	   }
	   
	   public void sendUpdate()
	   {
		   worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	   }
	   
	   public void setIcon(int side,int meta,int id, Icon icon, int iconSide)
	   {
		   icons[side] = icon;
		   iconIDs[side] = id;
		   metaArray[side] = meta;
		   sideArray[side] = iconSide;
	   }
	   
	   public void setIconArray()
	   {
		   if(worldObj!=null)
		   for(int i = 0; i<6; i++)
		   {
			   if(iconIDs[i]==0||iconIDs[i]==thisBlock().blockID)
			   {
				   Block16Fluid block = (Block16Fluid) thisBlock();
				   iconIDs[i] = block.blockID;
				   icons[i] = block.iconArray[metaArray[i]];
			   }
			   else
			   {
				   icons[i] = Block.blocksList[iconIDs[i]].getIcon(sideArray[i],metaArray[i]);
			   }
		   }
	   }
	   
	   public Icon getIcon(ForgeDirection side)
	   {
		   return icons[side.ordinal()]==null?thisBlock().getIcon(side.ordinal(), metaArray[side.ordinal()]):icons[side.ordinal()];
	   }
   
	    @Override
	    public Packet getDescriptionPacket()
	    {
	        return PacketTEB16F.getPacket(this);
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

	}

