package thutconcrete.common.network;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import thutconcrete.common.blocks.TileEntityBlock16Fluid;

import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.ITinyPacketHandler;
import cpw.mods.fml.common.network.Player;

public class TCPacket
{
	
	Map<Integer, IPacketProcessor> packetTypes = new HashMap<Integer, IPacketProcessor>();
	
	public TCPacket()
	{
		packetTypes.put(0, new PacketTEB16F());
		packetTypes.put(1, new PacketTPMount());
	}
	
	public void handlePacket(ByteArrayDataInput dat,Player player,World world)
	{
		int id = dat.readInt();
		packetTypes.get(id).processPacket(dat, player, world);
	}
	

}