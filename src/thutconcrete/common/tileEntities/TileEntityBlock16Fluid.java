package thutconcrete.common.tileEntities;

import java.util.Arrays;
import java.util.Random;

import thutconcrete.common.blocks.*;
import thutconcrete.common.corehandlers.ConfigHandler;
import thutconcrete.common.network.PacketHandler;
import thutconcrete.common.network.PacketTEB16F;
import thutconcrete.common.utils.IMultiPaintableTE;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityBlock16Fluid extends TileEntity// implements IMultiPaintableTE
{

	public int[] metaArray = {8,8,8,8,8,8};
	int tryFalls = 0;
	int trySpreads = 0;
	Random r = new Random();

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

