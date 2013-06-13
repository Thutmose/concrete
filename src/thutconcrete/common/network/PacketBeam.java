package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import thutconcrete.common.entity.EntityBeam;
import thutconcrete.common.entity.EntityTurret;
import thutconcrete.common.tileentity.TileEntityLaser;
import thutconcrete.common.utils.Vector3;

import net.minecraft.client.entity.EntityClientPlayerMP;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.Player;

public class PacketBeam  implements IPacketProcessor
{

	 
	 public static Packet250CustomPayload getPacket(Vector3 source, int id)
	 {
		
	 	ByteArrayOutputStream bos = new ByteArrayOutputStream(4+4+source.dataSize);
	 	DataOutputStream dos = new DataOutputStream(bos);
      
      try
      {
      		dos.writeInt(2);
      		source.writeToOutputStream(dos);
      		dos.writeInt(id);
      		
      }
      catch (IOException e)
      {
    	  e.printStackTrace();
      }
      
      Packet250CustomPayload pkt = new Packet250CustomPayload();
      pkt.channel = "Thut's Concrete";
      pkt.data = bos.toByteArray();
      pkt.length = bos.size();
      pkt.isChunkDataPacket = false;
      return pkt;
 
	 }


	@Override
	public void processPacket(ByteArrayDataInput data, Player player, World world) {
		World worldObj = ((EntityClientPlayerMP)player).worldObj;
		Vector3 source = Vector3.readFromInputSteam(data);
		Entity e = worldObj.getEntityByID(data.readInt());
		
		
		if(e!=null&&e instanceof EntityTurret)
		{
			((EntityTurret)e).turretDir.set(source);
		}
	}
	
}