package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import thutconcrete.api.datasources.DataSources;
import thutconcrete.api.network.IPacketProcessor;
import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.tileentity.TileEntityLiftAccess;
import thutconcrete.common.tileentity.TileEntityLimekiln;
import thutconcrete.common.tileentity.TileEntityRTG;
import thutconcrete.common.tileentity.TileEntitySensors;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

public class PacketInt implements IPacketProcessor
{

	@Override
	public void processPacket(ByteArrayDataInput dat, Player player, World world) {
		int x = dat.readInt();
        int y = dat.readInt();
        int z = dat.readInt();
        int f = dat.readInt();
        TileEntity te = world.getBlockTileEntity(x, y, z);
        System.out.println(te);
		if(te instanceof TileEntityLimekiln)
		{
	        TileEntityLimekiln tel = (TileEntityLimekiln)te;
	        tel.facing = ForgeDirection.getOrientation(f);
		}
		else if(te instanceof TileEntityLiftAccess)
		{
			TileEntityLiftAccess tel = (TileEntityLiftAccess)te;
			tel.side = f&15;
			tel.floor = (f>>4)&15;
			tel.calledFloor = f>>8;
			int id = dat.readInt();
			if(EntityLift.lifts.containsKey(id))
			{
				tel.lift = EntityLift.lifts.get(id);
			}
			
		//	System.out.println("set: "+(f&15)+" "+(f>>4)+" "+f);
		}
		else if(te instanceof TileEntityRTG)
		{
			TileEntityRTG tel = (TileEntityRTG)te;
			tel.id = f;
			DataSources.MAXID = Math.max(dat.readInt(), DataSources.MAXID);
			tel.energyStored = dat.readDouble();
		}
	}
	
	public static Packet250CustomPayload getPacket(TileEntity te)
	{
			int x = te.xCoord;
			int y = te.yCoord;
			int z = te.zCoord;
			int f = 0;
			int v = 0;
			double d = 0;
			boolean hasDouble = false;
			
			if(te instanceof TileEntityLimekiln)
			{
				TileEntityLimekiln tel = (TileEntityLimekiln)te;
				f = tel.facing.ordinal();
			}
			else if(te instanceof TileEntityLiftAccess)
			{
				TileEntityLiftAccess tel = (TileEntityLiftAccess)te;
				f = tel.side+tel.floor*16+tel.calledFloor*256;
				if(tel.lift!=null)
					v = tel.lift.id;
			//	System.out.println("read: "+tel.side+" "+tel.floor+" "+f);
			}
			else if(te instanceof TileEntityRTG)
			{
				TileEntityRTG tel = (TileEntityRTG)te;
				f = tel.id;
				v = DataSources.MAXID;
				d = tel.energyStored;
				hasDouble = true;
			}
		 	ByteArrayOutputStream bos = new ByteArrayOutputStream(24);
		 	DataOutputStream dos = new DataOutputStream(bos);
			
			try
	        {
	        	dos.writeInt(3);
	            dos.writeInt(x);
	            dos.writeInt(y);
	            dos.writeInt(z);
	            dos.writeInt(f);
	            dos.writeInt(v);
	            if(hasDouble)
	            	dos.writeDouble(d);
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
