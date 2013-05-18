package thutconcrete.common.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

import thutconcrete.common.blocks.TileEntityBlock16Fluid;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;

public class PacketTEB16F extends Packet
{

	public static PacketTEB16F instance;
	public int xPosition;
    public int yPosition;
    public int zPosition;
    public int[] metaArray;
    
    public PacketTEB16F()
    {
        this.isChunkDataPacket = true;
    }

    public PacketTEB16F(int par1, int par2, int par3, int[] par4ArrayOfint)
    {
        this.isChunkDataPacket = true;
        this.xPosition = par1;
        this.yPosition = par2;
        this.zPosition = par3;
        this.metaArray = new int[] {par4ArrayOfint[0], par4ArrayOfint[1], par4ArrayOfint[2], par4ArrayOfint[3]};
    }
	
	

	@Override
	public void readPacketData(DataInputStream par1DataInputStream)
			throws IOException {
			System.out.println("Reading");
		  	this.xPosition = par1DataInputStream.readInt();
	        this.yPosition = par1DataInputStream.readShort();
	        this.zPosition = par1DataInputStream.readInt();
	        this.metaArray = new int[4];

	        for (int i = 0; i < 4; ++i)
	        {
	            this.metaArray[i] = par1DataInputStream.readInt();
	        }
		
	}
	
	@Override
	public void writePacketData(DataOutputStream par1DataOutputStream)
			throws IOException {
		System.out.println("writing");
			par1DataOutputStream.writeInt(this.xPosition);
	        par1DataOutputStream.writeShort(this.yPosition);
	        par1DataOutputStream.writeInt(this.zPosition);

	        for (int i = 0; i < 4; ++i)
	        {
	        	par1DataOutputStream.writeInt(metaArray[i]);
	        }
	}

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.unexpectedPacket(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
    	return 42;
    }

}
