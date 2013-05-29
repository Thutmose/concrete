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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityBlock16Fluid extends TileEntity// implements IMultiPaintableTE
{

	public int[] metaArray = {8,8,8,8,8,8};
	double[] pointing = {1, 0, 0};
	int tryFalls = 0;
	int trySpreads = 0;
	Random r = new Random();
	long time = 0;
	
	public void updateEntity()
	{
		if(time%5==0)
		{
			List<Entity> list = LinearAlgebra.isEntityOnLine(50, pointing, new double[] {xCoord+0.5, yCoord+3.5, zCoord+0.5}, worldObj);
			if(list!=null&&list.size()>0)
			{
				System.out.println(list.get(0)+" from "+xCoord+" "+yCoord+" "+zCoord);
				list.get(0).setFire(5);
			}
			else
			{
				System.out.println("null");
			}
			System.out.println("before "+Arrays.toString(pointing));
			pointing = LinearAlgebra.vectorRotate(pointing, new double[] {0, 1,0}, 0.1);
			System.out.println("after "+Arrays.toString(pointing));
		}
		
		time++;
	} 

	public boolean canUpdate()
	{
		return false;
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
	   }
	   
	   public void sendUpdate()
	   {
		   worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	   }
	   
	   
	    @Override
	    public Packet getDescriptionPacket()
	    {
	        return PacketTEB16F.getPacket(this);
	    }

	}

