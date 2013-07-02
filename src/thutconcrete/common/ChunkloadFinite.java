package thutconcrete.common;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.network.Player;

import thutconcrete.common.corehandlers.ConfigHandler;
import thutconcrete.common.network.PacketSeedMap;
import thutconcrete.common.network.PacketTPMount;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet13PlayerLookMove;
import net.minecraft.network.packet.Packet30Entity;
import net.minecraft.network.packet.Packet39AttachEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.ChunkWatchEvent.Watch;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;

public class ChunkloadFinite {

	public static int chunkSize = 20;
	public static int blockSize = 320;
	public static final Map<Integer, Long> players = new HashMap<Integer, Long>();
	int n = 0;
	
	public ChunkloadFinite(int size)
	{
		chunkSize = size;
		blockSize = chunkSize*16;
	}
	
	
	@ForgeSubscribe
	public void onJoin(EntityJoinWorldEvent evt)
	{
		if(evt.entity instanceof Player)
		{
			if(!evt.world.isRemote)
			{
				Volcano.init(evt.world);
				PacketSeedMap.sendPacket((Player)evt.entity);
			}
		}
	}
	
	@ForgeSubscribe
	public void onLivingUpdate(LivingEvent evt)
	{
		
		if(!(evt.entityLiving instanceof EntityPlayerMP)) return;
		
		//EntityPlayerMP player = (EntityPlayerMP) evt.entityLiving;
		EntityLiving player = evt.entityLiving;
		World world = player.worldObj;
		if(!ConfigHandler.defaultTypeFinite&&!(world.provider.terrainType.getWorldTypeName().contentEquals("FINITE"))) return;

		double x = player.posX;
		double y = player.posY;
		double z = player.posZ;
		
		if(!world.isRemote&&(abs(floor(x))>blockSize||abs(floor(z))>blockSize))
		{
			System.out.println("wrapping "+x+" "+z+" "+blockSize+" "+player);
			
			Entity mount = null;
			
			if(player.isRiding())
			{
				mount = player.ridingEntity;
				x = mount.posX;
				y = mount.posY;
				z = mount.posZ;
			}
			
			if(players.containsKey(player.entityId)&&players.get(player.entityId)<=world.getTotalWorldTime())
			{
				if(world.getTotalWorldTime()-players.get(player.entityId)>5)
				{
					players.remove(player.entityId);
				}
				
				return;
			}
			
			if(!players.containsKey(player.entityId))
			{
				players.put(player.entityId, world.getTotalWorldTime());
			}
			else if(players.get(player.entityId)>world.getTotalWorldTime()-5)
			{
				players.put(player.entityId, world.getTotalWorldTime());
			}
			
			if(abs(floor(x))>blockSize)
			{
				x = x<0?Math.max(-blockSize, x):Math.min(blockSize, x);
				
				if(player.isRiding())
				{
					if(mount!=null)
					{
						mount.setPosition(-x,y,z);
						//PacketTPMount.sendToClient(PacketTPMount.getPacketMount(player,mount,-x2,y2,z2),player);
					}
				}
				else
				{
					player.setPositionAndUpdate(-x,y,z);
				//	if(!player.getCanSpawnHere())
				//		TPhere(player);
				}
				
			}
			if(abs(floor(z))>blockSize)
			{
				
				
				z = z<0?Math.max(-blockSize, z):Math.min(blockSize, z);
				
				if(player.isRiding())
				{
					player.ridingEntity.setPosition(x,y,-z);
				}
				else
				{
					player.setPositionAndUpdate(x,y,-z);
				//	if(!player.getCanSpawnHere())
				//		TPhere(player);
				}
				
			}
			System.out.println("wrapped "+x+" "+z+" "+blockSize+" "+player);
		}
	}
	
	public void TPhere(EntityLiving player)
	{
		if(player.getCanSpawnHere()&&blockBelowSolid(player))
		{
			return;
		}
		else if(player.getCanSpawnHere()&&!blockBelowSolid(player))
		{
			double y = player.posY;
			while(y>0)
			{
				y--;
				player.setPositionAndUpdate(player.posX,y++,player.posZ);
				if(player.getCanSpawnHere()&&blockBelowSolid(player))
				{
					return;
				}
			}
		}
		else
		{
			double y = player.posY;
			while(!player.getCanSpawnHere())
			{
				player.setPositionAndUpdate(player.posX,y++,player.posZ);
			}
			return;
		}
		return;
	}
	
	
	public boolean blockBelowSolid(EntityLiving player)
	{
		return player.worldObj.isBlockSolidOnSide((int)player.posX, (int)player.posY-1, (int)player.posZ, ForgeDirection.UP, false);
	}

}
