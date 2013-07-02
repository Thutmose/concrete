package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;
import thutconcrete.api.datasources.DataSources;
import thutconcrete.api.datasources.IDataSource;
import thutconcrete.api.network.IPacketProcessor;
import thutconcrete.common.tileentity.TileEntitySensors;

public class PacketDataSource implements IPacketProcessor
{

	@Override
	public void processPacket(ByteArrayDataInput dat, Player player, World world) 
	{
		int x = dat.readInt();
        int y = dat.readInt();
        int z = dat.readInt();
        TileEntity te = world.getBlockTileEntity(x, y, z);
        int id = dat.readInt();
        int maxId = dat.readInt();
        int f = dat.readInt();
        int side = dat.readInt();
        int button = dat.readInt();
        int rate = dat.readInt();
        double coef = dat.readDouble();
        if(te instanceof TileEntitySensors)
		{
        	TileEntitySensors tel = (TileEntitySensors)te;
			tel.setScale(f);
			tel.rate = rate;
			tel.id = id;
			DataSources.MAXID = Math.max(maxId, DataSources.MAXID);
			tel.side = side;
			tel.button = button;
			tel.coef = coef;
			//System.out.println(maxId+" "+id);
			for(int i = 0; i<16; i++)
			{
				int k = dat.readInt();
			//	System.out.println(k+" "+i);
				tel.addStation(k,i);
			}
		}
        
	}

	public static Packet250CustomPayload getPacket(TileEntity te, String channel)
	{
		
		int x = te.xCoord;
		int y = te.yCoord;
		int z = te.zCoord;
		if(!(te instanceof TileEntitySensors))
		{
			return null;
		}
		TileEntitySensors s = (TileEntitySensors)te;
		int id = s.id;
		int maxId = DataSources.MAXID;
		int f = s.exponent;
		
		int side = s.side;
		int button = s.button;
		int rate = s.rate;
		
		double coef = s.coef;
		
	 	ByteArrayOutputStream bos = new ByteArrayOutputStream(32+4*16+12);
	 	DataOutputStream dos = new DataOutputStream(bos);
		
		try
        {
        	dos.writeInt(8); 
            dos.writeInt(x);
            dos.writeInt(y);
            dos.writeInt(z);
            dos.writeInt(id);
            dos.writeInt(maxId);
            dos.writeInt(f);
            dos.writeInt(side);
            dos.writeInt(button);
            dos.writeInt(rate);
            dos.writeDouble(coef);
            for(int i = 0; i<16; i++)
            {
            	dos.writeInt(s.stationIDs[i]);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.channel = channel;
        pkt.data = bos.toByteArray();
        pkt.length = bos.size();
        pkt.isChunkDataPacket = true;
        return pkt;
	}
}
