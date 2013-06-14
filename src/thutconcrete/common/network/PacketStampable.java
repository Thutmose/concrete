package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thutconcrete.common.tileentity.TileEntityBlock16Fluid;
import thutconcrete.common.tileentity.TileEntityLiftAccess;

public class PacketStampable implements IPacketProcessor
{


	 public static Packet250CustomPayload getPacket(TileEntity te)
	 {
		 
	   int x = te.xCoord;
       int y = te.yCoord;
       int z = te.zCoord;
       int[] items = new int[6];
       int[] ids = new int[6];
       int[] sides = new int[6];
       if(te instanceof TileEntityBlock16Fluid)
       {
	       TileEntityBlock16Fluid teb16f = (TileEntityBlock16Fluid) te;
	       items = teb16f.metaArray;
	       ids = teb16f.iconIDs;
	       sides = teb16f.sideArray;
       }
       
       if(!(isMetaSame(items,x,y,z)&&isIDSame(ids, x, y, z)))
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
	 
	 public static boolean isIDSame(int[] array, int x, int y, int z)
	 {
		 {
			 return ( 
					  array[0]==0
					&&array[1]==0
					&&array[2]==0
					&&array[3]==0
					&&array[4]==0
					&&array[5]==0
					);
		 }
	 }

	@Override
	public void processPacket(ByteArrayDataInput dat, Player player, World world) {
		int x = dat.readInt();
        int y = dat.readInt();
        int z = dat.readInt();
        
        int[] intArray = new int[6];
        int[] idArray = new int[6];
        int[] sideArray = new int[6];
        
        for (int i = 0; i < 6; i++)
        {
            intArray[i] = dat.readInt();
            idArray[i] = dat.readInt();
            sideArray[i] = dat.readInt();
        }
		
        TileEntity te = world.getBlockTileEntity(x, y, z);
        if (te instanceof TileEntityBlock16Fluid)
        {
        	TileEntityBlock16Fluid teb16f = (TileEntityBlock16Fluid) te;
        	teb16f.metaArray=intArray;
        	teb16f.iconIDs=idArray;
        	teb16f.sideArray =sideArray;
        	teb16f.setIconArray();
        }
	}
	 
	
}
