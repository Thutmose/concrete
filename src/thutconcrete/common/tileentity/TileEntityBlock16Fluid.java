package thutconcrete.common.tileentity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import thutconcrete.common.blocks.*;
import thutconcrete.common.corehandlers.ConfigHandler;
import thutconcrete.common.network.PacketHandler;
import thutconcrete.common.network.PacketStampable;
import thutconcrete.common.utils.IStampableTE;
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

public class TileEntityBlock16Fluid extends TileEntity implements IStampableTE
{

	public int[] metaArray = {8,8,8,8,8,8};
	public int[] sideArray = {0,0,0,0,0,0};
	public Icon[] icons = new Icon[6];
	public int[] iconIDs = {0,0,0,0,0,0}; 
	
	public void updateEntity()
	{
		if(worldObj.getBlockPowerInput(xCoord, yCoord, zCoord)>0)
		{
			System.out.println(worldObj.getBlockPowerInput(xCoord, yCoord, zCoord));
			worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, getBlockId(), 5);
		}
	}

	public boolean canUpdate()
	{
		return false;
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
				   if(block.iconArray!=null)
				   icons[i] = block.iconArray[metaArray[i]];
			   }
			   else if(Block.blocksList[iconIDs[i]]!=null)
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
	        return PacketStampable.getPacket(this);
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
