package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import thutconcrete.common.tileentity.TileEntityLiftAccess;
import thutconcrete.common.tileentity.TileEntityLimekiln;
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
        
		if(te instanceof TileEntityLimekiln)
		{
	        TileEntityLimekiln tel = (TileEntityLimekiln)te;
	        tel.facing = ForgeDirection.getOrientation(f);
		}
		else if(te instanceof TileEntityLiftAccess)
		{
			TileEntityLiftAccess tel = (TileEntityLiftAccess)te;
			tel.side = f;
		}
	}
	
	public static Packet250CustomPayload getPacket(TileEntity te)
	 {
			int x = te.xCoord;
			int y = te.yCoord;
			int z = te.zCoord;
			int f = 0;
			
			if(te instanceof TileEntityLimekiln)
			{
				TileEntityLimekiln tel = (TileEntityLimekiln)te;
				f = tel.facing.ordinal();
			}
			else if(te instanceof TileEntityLiftAccess)
			{
				TileEntityLiftAccess tel = (TileEntityLiftAccess)te;
				f = tel.side;
			}
		 	ByteArrayOutputStream bos = new ByteArrayOutputStream(20);
		 	DataOutputStream dos = new DataOutputStream(bos);
			
			try
	        {
	        	dos.writeInt(3);
	            dos.writeInt(x);
	            dos.writeInt(y);
	            dos.writeInt(z);
	            dos.writeInt(f);
	        }
	        catch (IOException e)
	        {
	            // UNPOSSIBLE?
	        }
	        
	        Packet250CustomPayload pkt = new Packet250CustomPayload();
	        pkt.channel = "Thut's Concrete";
	        pkt.data = bos.toByteArray();
	        pkt.length = bos.size();
	        pkt.isChunkDataPacket = true;
	        return pkt;
			
	 }

}
