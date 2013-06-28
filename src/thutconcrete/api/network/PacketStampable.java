package thutconcrete.api.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import thutconcrete.api.utils.*;

public class PacketStampable implements IPacketProcessor
{


	 public static Packet250CustomPayload getPacket(TileEntity te, String channel)
	 {
		 
	   int x = te.xCoord;
       int y = te.yCoord;
       int z = te.zCoord;
       int[] items = new int[6];
       int[] ids = new int[6];
       int[] sides = new int[6];
       int id = te.worldObj.getBlockId(x, y, z);
       if(te instanceof IStampableTE)
       {
    	   IStampableTE tes = (IStampableTE) te;
	       items = tes.getMetaArray();
	       ids = tes.getIdArray();
	       sides = tes.getSideArray();
       }
       
       if(!(isMetaSame(items,x,y,z)&&isIDSame(ids, x, y, z, id)))
	        {
       	
		 	ByteArrayOutputStream bos = new ByteArrayOutputStream(16+(3*4*6));
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
	                dos.writeInt(ids[i]);
	                dos.writeInt(sides[i]);
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
       return null;
	 }
	 
	 public static boolean isMetaSame(int[] array, int x, int y, int z)
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
	 
	 public static boolean isIDSame(int[] array, int x, int y, int z, int id)
	 {
		 {
			 return ( 
					  (array[0]==0||array[0]==id)
					&&(array[1]==0||array[1]==id)
					&&(array[2]==0||array[2]==id)
					&&(array[3]==0||array[3]==id)
					&&(array[4]==0||array[4]==id)
					&&(array[5]==0||array[5]==id)
					);
		 }
	 }

	@Override
	public void processPacket(ByteArrayDataInput dat, Player player, World world) {
		int x = dat.readInt();
        int y = dat.readInt();
        int z = dat.readInt();
        
        int[] metaArray = new int[6];
        int[] idArray = new int[6];
        int[] sideArray = new int[6];
        
        for (int i = 0; i < 6; i++)
        {
            metaArray[i] = dat.readInt();
            idArray[i] = dat.readInt();
            sideArray[i] = dat.readInt();
        }
        TileEntity te = world.getBlockTileEntity(x, y, z);

        if(te instanceof IStampableTE)
        {
     	   IStampableTE tes = (IStampableTE) te;
     	   tes.setArrays(metaArray, idArray, sideArray);
     	   tes.setIconArray();
        }
	}
	 
	
}
