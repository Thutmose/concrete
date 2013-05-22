package thutconcrete.common;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.registry.GameRegistry;

import thutconcrete.common.network.PacketHandler;
import thutconcrete.common.network.PacketTPMount;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet13PlayerLookMove;
import net.minecraft.network.packet.Packet30Entity;
import net.minecraft.network.packet.Packet39AttachEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.ChunkWatchEvent.Watch;
import net.minecraftforge.event.world.WorldEvent;

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
	public void onChunk(Watch watch)
	{
		
		EntityPlayerMP player = watch.player;
		World world = player.worldObj;

		double x = player.lastTickPosX;
		double y = player.lastTickPosY;
		double z = player.lastTickPosZ;
		
		double x2 = player.posX;
		double y2 = player.posY;
		double z2 = player.posZ;
		
		double y1 = y;

		int chunkX = player.chunkCoordX;
		int chunkZ = player.chunkCoordZ;
		
		if(!world.provider.terrainType.getWorldTypeName().contentEquals("FINITE")) return;
		if(!world.isRemote)//&&(chunkX>chunkSize||chunkX<-chunkSize||chunkZ>chunkSize||chunkZ<-chunkSize))
		{
			Entity mount;

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
			
			int chunkWarningX = (int)(x/16)-1;
			int chunkWarningZ = (int)(z/16)-1;
			
			
			if(chunkX>(chunkSize-1)||chunkX<-(chunkSize))
			{
				x = x<0?Math.max(-blockSize, x):Math.min(blockSize, x);
				
				if(player.isRiding())
				{
					mount = player.ridingEntity;
					if(mount!=null)
					{
						mount.setPosition(-x,y1,z);
						PacketTPMount.sendToClient(PacketTPMount.getPacketMount(player,mount,-x,y1,z),player);
					}
				}
				else
				{
					player.setPositionAndUpdate(-x,y1,z);
					if(!player.getCanSpawnHere())
						TPhere(player);
				}
				
			}
			if(chunkZ>chunkSize||chunkZ<-chunkSize)
			{
				
				
				z = z<0?Math.max(-chunkSize*16, z):Math.min(chunkSize*16, z);
				
				if(player.isRiding())
				{
					player.ridingEntity.setPosition(x,y1,-z);
				}
				else
				{
					watch.player.setPositionAndUpdate(x,y1,-z);
					if(!player.getCanSpawnHere())
						TPhere(player);
				}
				
			}
		}
	}
	
	public void TPhere(EntityPlayerMP player)
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
	
	
	public boolean blockBelowSolid(EntityPlayerMP player)
	{
		return player.worldObj.isBlockSolidOnSide((int)player.posX, (int)player.posY-1, (int)player.posZ, ForgeDirection.UP, false);
	}

}
