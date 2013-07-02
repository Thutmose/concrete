package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import thutconcrete.api.network.IPacketProcessor;
import thutconcrete.api.network.PacketStampable;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.tileentity.TileEntityBlock16Fluid;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.ITinyPacketHandler;
import cpw.mods.fml.common.network.Player;

public class TCPacket implements IPacketHandler
{
	
	Map<Integer, IPacketProcessor> packetTypes = new HashMap<Integer, IPacketProcessor>();
	static Map<Integer, Integer> packetCounts = new HashMap<Integer, Integer>();
	
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {

		if(!packet.channel.contentEquals("Thut's Concrete")) return;
		
		World world = ConcreteCore.commproxy.getClientWorld();
		ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
		
		ConcreteCore.instance.pkthandler.handlePacket(dat, player, world);
	}
	
	public TCPacket()
	{
		packetTypes.put(0, new PacketStampable());
		packetTypes.put(1, new PacketTPMount());
		packetTypes.put(2, new PacketBeam());
		packetTypes.put(3, new PacketInt());
		packetTypes.put(4, new PacketMountedCommand());
		packetTypes.put(5, new PacketLift());
		packetTypes.put(6, new PacketVolcano());
		packetTypes.put(7, new PacketSeedMap());
		packetTypes.put(8, new PacketDataSource());
	}
	
	public void handlePacket(ByteArrayDataInput dat,Player player,World world)
	{
		int id = dat.readInt();
	//	System.out.println("Packet ID: "+id);
		packetTypes.get(id).processPacket(dat, player, world);
	}
	
}