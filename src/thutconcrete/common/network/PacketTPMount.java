package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import thutconcrete.api.network.IPacketProcessor;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.server.FMLServerHandler;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet39AttachEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class PacketTPMount implements IPacketProcessor
{

	 
	 public static Packet250CustomPayload getPacketMount(EntityPlayerMP player, Entity mount, double x, double y, double z)
	 {
		
	 	ByteArrayOutputStream bos = new ByteArrayOutputStream(8+8+8+8+4);
	 	DataOutputStream dos = new DataOutputStream(bos);
       
       try
       {
       		dos.writeInt(1);
            dos.writeDouble(x);
            dos.writeDouble(y);
            dos.writeDouble(z);
            dos.writeInt(player.entityId);
            dos.writeInt(mount.entityId);
       }
       catch (IOException e)
       {
           // UNPOSSIBLE?
       }
       
       Packet250CustomPayload pkt = new Packet250CustomPayload();
       pkt.channel = "Thut's Concrete";
       pkt.data = bos.toByteArray();
       pkt.length = bos.size();
       pkt.isChunkDataPacket = false;
       return pkt;
  
  }
	
	public static void sendToClient(Packet packet, EntityPlayerMP player)
	{
		packet.processPacket(player.playerNetServerHandler);
	}

	@Override
	public void processPacket(ByteArrayDataInput dat, Player player, World world) {
		
		double x = dat.readDouble();
		double y = dat.readDouble();
		double z = dat.readDouble();
		int playerID = dat.readInt();
		int mountID = dat.readInt();
		
		EntityPlayer player1 = (EntityPlayer) world.getEntityByID(playerID);

        MinecraftServer minecraftserver = MinecraftServer.getServer();
        WorldServer worldserver = minecraftserver.worldServerForDimension(player1.dimension);
        
		Entity mount  = world.getEntityByID(mountID);
		
		if(mount!=null)
		{
			mount.setPosition(x, y, z);
			worldserver.resetUpdateEntityTick();
		}
	}
	
}
