package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import thutconcrete.api.network.IPacketProcessor;
import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.entity.EntityTurret;
import thutconcrete.common.tileentity.TileEntityLiftAccess;
import net.minecraft.entity.Entity;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

public class PacketLift implements IPacketProcessor
{

	@Override
	public void processPacket(ByteArrayDataInput dat, Player player, World world) {
		int id = dat.readInt();
		int command = dat.readInt();
		int command2 = -1;
		if(command==1||command==0)
			 command2 = dat.readInt();
	//	System.out.println(player);
		Entity e = ((EntityClientPlayerMP)player).worldObj.getEntityByID(id);
			
		if(e instanceof EntityLift)
		{
			if(command == 1 || command == 0)
				((EntityLift)e).toMoveY = command!=0;
			if((command == 1 || command == 0)&&(command2 == 1 || command2 == 0))
				((EntityLift)e).up = command2!=0;
			if(command == 3)
			{
				((EntityLift)e).callClient(dat.readDouble());
				((EntityLift)e).destinationFloor = dat.readInt();
			}
			if(command == 4)
			{
				((EntityLift)e).size = dat.readDouble();
			}
			
		}
		
		if(command == 2)
		{
			int x = dat.readInt();
			int y = dat.readInt();
			int z = dat.readInt();
			TileEntity te = ((EntityClientPlayerMP)player).worldObj.getBlockTileEntity(x, y, z);
			if(te instanceof TileEntityLiftAccess)
			{
				((TileEntityLiftAccess) te).lift = EntityLift.lifts.get(id);
				System.out.println(((TileEntityLiftAccess) te)+" "+((TileEntityLiftAccess) te).lift);
			}
		}

	}
	
	
	
	public static Packet250CustomPayload getPacket(Entity e, int command, int command2)
	 {
		int id = e.entityId;
		
	 	ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
	 	DataOutputStream dos = new DataOutputStream(bos);
		
		try
       {
		dos.writeInt(5);
           dos.writeInt(id);
           dos.writeInt(command);
           dos.writeInt(command2);
       }
       catch (IOException ex)
       {
       	ex.printStackTrace();
       }
       
       Packet250CustomPayload pkt = new Packet250CustomPayload();
       pkt.channel = "Thut's Concrete";
       pkt.data = bos.toByteArray();
       pkt.length = bos.size();
       pkt.isChunkDataPacket = true;
       return pkt;
	 }
	
	public static Packet250CustomPayload getPacket(Entity e, int command, double value, int secondValue)
	 {
		int id = e.entityId;
		
	 	ByteArrayOutputStream bos = new ByteArrayOutputStream(24);
	 	DataOutputStream dos = new DataOutputStream(bos);
		
		try
      {
		dos.writeInt(5);
          dos.writeInt(id);
          dos.writeInt(command);
          dos.writeDouble(value);
          dos.writeInt(secondValue);
      }
      catch (IOException ex)
      {
      	ex.printStackTrace();
      }
      
      Packet250CustomPayload pkt = new Packet250CustomPayload();
      pkt.channel = "Thut's Concrete";
      pkt.data = bos.toByteArray();
      pkt.length = bos.size();
      pkt.isChunkDataPacket = true;
      return pkt;
	 }

	public static Packet250CustomPayload getPacket(TileEntity e, int command, int id)
	 {
		int x = e.xCoord;
		int y = e.yCoord;
		int z = e.zCoord;
		
	 	ByteArrayOutputStream bos = new ByteArrayOutputStream(28);
	 	DataOutputStream dos = new DataOutputStream(bos);
		
		try
      {
			dos.writeInt(5);
	        dos.writeInt(id);
	        dos.writeInt(command);
	        dos.writeInt(x);
	        dos.writeInt(y);
	        dos.writeInt(z);
      }
      catch (IOException ex)
      {
      	ex.printStackTrace();
      }
      
      Packet250CustomPayload pkt = new Packet250CustomPayload();
      pkt.channel = "Thut's Concrete";
      pkt.data = bos.toByteArray();
      pkt.length = bos.size();
      pkt.isChunkDataPacket = true;
      return pkt;
	 }
	
}
