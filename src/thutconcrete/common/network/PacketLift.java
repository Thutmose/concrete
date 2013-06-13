package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.entity.EntityTurret;
import net.minecraft.entity.Entity;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

public class PacketLift implements IPacketProcessor
{

	@Override
	public void processPacket(ByteArrayDataInput dat, Player player, World world) {
		int id = dat.readInt();
		int command = dat.readInt();
		int command2 = dat.readInt();
	//	System.out.println(player);
		Entity e = ((EntityClientPlayerMP)player).worldObj.getEntityByID(id);
		
		if(e instanceof EntityLift)
		{
			((EntityLift)e).move = command!=0;
			((EntityLift)e).up = command2!=0;
		}
		
	}
	
	public static Packet250CustomPayload getPacket(Entity e, int command, int command2)
	 {
		int id = e.entityId;
		
	 	ByteArrayOutputStream bos = new ByteArrayOutputStream(20);
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

}
