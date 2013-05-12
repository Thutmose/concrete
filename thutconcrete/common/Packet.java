package thutconcrete.common;

import java.io.DataInputStream;

import net.minecraft.entity.player.EntityPlayer;

public abstract class Packet {
	public abstract void handle(DataInputStream iStream, EntityPlayer player);
}
