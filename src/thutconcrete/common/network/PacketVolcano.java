package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;
import thutconcrete.api.network.IPacketProcessor;
import thutconcrete.common.Volcano;
import thutconcrete.common.tileentity.TileEntityVolcano;
import thutconcrete.common.tileentity.TileEntityVolcano.Vect;

public class PacketVolcano implements IPacketProcessor
{

	@Override
	public void processPacket(ByteArrayDataInput dat, Player player, World world) {
		int x = dat.readInt();
        int y = dat.readInt();
        int z = dat.readInt();
        long age = dat.readLong();
        int active = dat.readInt();
        int growth = dat.readInt();
        long seed = dat.readLong();
        byte b = dat.readByte();
		//System.out.println("read: "+active+" "+growth);
        Volcano.setSeed(world.provider.dimensionId, seed);
        if(world.getBlockTileEntity(x, y, z) instanceof TileEntityVolcano)
        {
        	TileEntityVolcano v = (TileEntityVolcano)world.getBlockTileEntity(x, y, z);
        	v.byteToBools(b);
        	v.age = age;
        	v.activeCount = active;
        	v.growthTimes = growth;
        	v.mainVent = Vect.readFromData(dat);
        	v.z = z;
        	int n = dat.readInt();
        	for(int i = 0; i<n; i++)
        	{
        		v.sideVents.add(Vect.readFromData(dat));
        	}
        }
	}

	
	public static Packet250CustomPayload getPacket(TileEntity te)
	{
		TileEntityVolcano v = (TileEntityVolcano)te;
		int x = te.xCoord;
		int y = te.yCoord;
		int z = v.z;
		
		long age = v.age;
		int active = v.activeCount;
		int growth = v.growthTimes;
		long seed = v.worldObj.getSeed();
		byte bools = v.booleansToByte();
		//System.out.println("wrote: "+active+" "+growth);
	 	ByteArrayOutputStream bos = new ByteArrayOutputStream(128);
	 	DataOutputStream dos = new DataOutputStream(bos);
		int n = v.sideVents.size();
		try
        {
        	dos.writeInt(6);
            dos.writeInt(x);
            dos.writeInt(y);
            dos.writeInt(z);
            dos.writeLong(age);
            dos.writeInt(active);
            dos.writeInt(growth);
            dos.writeLong(seed);
            dos.writeByte(bools);
            v.mainVent.writeToData(dos);
            dos.writeInt(n);
            {
            	for(int i = 0; i<n; i++)
            	{
            		v.sideVents.get(i).writeToData(dos);
            	}
            }
            
             
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.channel = "Thut's Concrete";
        pkt.data = bos.toByteArray();
        pkt.length = bos.size();
        pkt.isChunkDataPacket = true;
        return pkt;
	}
}
