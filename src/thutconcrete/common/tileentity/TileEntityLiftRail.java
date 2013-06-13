package thutconcrete.common.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import thutconcrete.common.blocks.BlockLiftRail;
import thutconcrete.common.entity.EntityLift;
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

public class TileEntityLiftRail extends TileEntity
{
	
	public int redstonePower = 0;
	public EntityLift lift;
	
	public boolean redstone = true;
	public boolean powered = false;
	
	public void updateEntity()
	{
		if(Math.random()>0.)
		{
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, checkSides()?1:0, 3);
		}
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
	
	
	public void writeToNBT(NBTTagCompound par1)
	   {
		   super.writeToNBT(par1);
	   }

	   public void readFromNBT(NBTTagCompound par1)
	   {
	      super.readFromNBT(par1);
	   }

	   public void sendUpdate()
	   {
		   worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
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

