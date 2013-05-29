package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.network.*;
import thutconcrete.common.tileentity.TileEntityBlock16Fluid;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
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

		if(!packet.channel.contentEquals("Thut's Concrete")) return;
		
		World world = ConcreteCore.commproxy.getClientWorld();
		ByteArrayDataInput dat = ByteStreams.newDataInput(packet.data);
		TCPacket handler = new TCPacket();
		
		handler.handlePacket(dat, player, world);
	}
	
}
