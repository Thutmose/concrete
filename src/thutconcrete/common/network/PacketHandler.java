package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.TileEntityBlock16Fluid;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public class PacketHandler implements IPacketHandler {

	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {

		ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
		
	//	if(packet.data.length>3)
		{
			int x = dat.readInt();
	        int y = dat.readInt();
	        int z = dat.readInt();
	        
	        int[] intArray = new int[6];
	        
	        for (int i = 0; i < 6; i++)
	        {
	            intArray[i] = dat.readInt();
	        }
			
	        World world = ConcreteCore.commproxy.getClientWorld();
	        TileEntity te = world.getBlockTileEntity(x, y, z);
	        if (te instanceof TileEntityBlock16Fluid)
	        {
	        	TileEntityBlock16Fluid teb16f = (TileEntityBlock16Fluid) te;
	        	teb16f.metaArray=intArray;
	        }
		}
        
	}

	 public static Packet getPacket(TileEntity te)
	 {

	 	
     	int x = te.xCoord;
        int y = te.yCoord;
        int z = te.zCoord;
        TileEntityBlock16Fluid teb16f = (TileEntityBlock16Fluid) te;
        int[] items = teb16f.metaArray;
        
        if(!isSame(items))
	        {
        	
		 	ByteArrayOutputStream bos = new ByteArrayOutputStream(12+(4*items.length));
		 	DataOutputStream dos = new DataOutputStream(bos);
	        
	        try
	        {
	            dos.writeInt(x);
	            dos.writeInt(y);
	            dos.writeInt(z);
	            for (int i = 0; i < 6; i++)
	            {
	                dos.writeInt(items[i]);
	            }
	        }
	        catch (IOException e)
	        {
	            // UNPOSSIBLE?
	        }
	        
	        Packet250CustomPayload pkt = new Packet250CustomPayload();
	        pkt.channel = "TC";
	        pkt.data = bos.toByteArray();
	        pkt.length = bos.size();
	        pkt.isChunkDataPacket = true;
	        return pkt;
        }
        return null;
    }
	 
	 public static boolean isSame(int[] array)
	 {
		 
		 return (array[0]==8&&array[1]==8&&array[2]==8&&array[3]==8&&array[4]==8&&array[5]==8);
		 
	 }
	
}
