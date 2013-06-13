package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import thutconcrete.common.entity.EntityTurret;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.*;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

public class PacketMountedCommand implements IPacketProcessor
{

	@Override
	public void processPacket(ByteArrayDataInput dat, Player player, World world) 
	{
		int id = dat.readInt();
		int command = dat.readInt();
	//	System.out.println(player);
		Entity e = ((EntityPlayerMP)player).worldObj.getEntityByID(id);
		
		if(e instanceof EntityTurret)
		{
			if(command == 0)
			{
				((EntityTurret)e).toDismount = true;
			}
			if(command == 1)
			{
				System.out.println("fire");
				((EntityTurret)e).fire = true;
			}
		}
		
	}
	
	public static Packet250CustomPayload getPacket(Entity e, int command)
	 {
		int id = e.entityId;
		
	 	ByteArrayOutputStream bos = new ByteArrayOutputStream(20);
	 	DataOutputStream dos = new DataOutputStream(bos);
		
		try
        {
        	dos.writeInt(4);
            dos.writeInt(id);
            dos.writeInt(command);
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
