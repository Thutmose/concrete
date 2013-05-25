package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thutconcrete.common.tileEntities.TileEntityBlock16Fluid;

public class PacketTEB16F implements IPacketProcessor
{


	 public static Packet250CustomPayload getPacket(TileEntity te)
	 {
		 
	   int x = te.xCoord;
       int y = te.yCoord;
       int z = te.zCoord;
       TileEntityBlock16Fluid teb16f = (TileEntityBlock16Fluid) te;
       int[] items = teb16f.metaArray;
       
       if(!isSame(items,x,y,z))
	        {
       	
		 	ByteArrayOutputStream bos = new ByteArrayOutputStream(16+(4*items.length));
		 	DataOutputStream dos = new DataOutputStream(bos);
	        
	        try
	        {
	        	dos.writeInt(0);
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
	        pkt.channel = "Thut's Concrete";
	        pkt.data = bos.toByteArray();
	        pkt.length = bos.size();
	        pkt.isChunkDataPacket = true;
	        return pkt;
       }
       return null;
   }
	 
	 public static boolean isSame(int[] array, int x, int y, int z)
	 {
		 {
			 return ( 
					  array[0]==8
					&&array[1]==8
					&&array[2]==8
					&&array[3]==8
					&&array[4]==8
					&&array[5]==8
					);
		 }
	 }

	@Override
	public void processPacket(ByteArrayDataInput dat, Player player, World world) {
		int x = dat.readInt();
        int y = dat.readInt();
        int z = dat.readInt();
        
        int[] intArray = new int[6];
        
        for (int i = 0; i < 6; i++)
        {
            intArray[i] = dat.readInt();
        }
		
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof TileEntityBlock16Fluid)
        {
        	TileEntityBlock16Fluid teb16f = (TileEntityBlock16Fluid) te;
        	teb16f.metaArray=intArray;
        }
	}
	 
	
}
