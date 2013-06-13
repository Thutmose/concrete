package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
        TileEntityLimekiln tel = (TileEntityLimekiln)world.getBlockTileEntity(x, y, z);
        tel.facing = ForgeDirection.getOrientation(f);
	}
	
	public static Packet250CustomPayload getPacket(TileEntity te)
	 {
			int x = te.xCoord;
			int y = te.yCoord;
			int z = te.zCoord;
			TileEntityLimekiln tel = (TileEntityLimekiln)te;
			int f = tel.facing.ordinal();
			
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
