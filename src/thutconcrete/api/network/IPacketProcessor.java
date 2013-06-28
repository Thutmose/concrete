package thutconcrete.api.network;

import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;

import cpw.mods.fml.common.network.Player;

public interface IPacketProcessor {

	public abstract void processPacket(ByteArrayDataInput dat,Player player,World world);
	
}
