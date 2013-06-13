package thutconcrete.common.tileentity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.server.FMLServerHandler;

import thutconcrete.common.entity.EntityBeam;
import thutconcrete.common.network.PacketBeam;
import thutconcrete.common.network.PacketStampable;
import thutconcrete.common.utils.LinearAlgebra;
import thutconcrete.common.utils.Vector3;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.management.PlayerInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Icon;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityLaser extends TileEntity
{
	long time = 0;
	
	public Entity target;
	public Vector3 origin = new Vector3();
	public Vector3 thisLaser = new Vector3();
	public Vector3 targetDir = new Vector3();
	public Vector3 turretDir = new Vector3(1,0,0);
	public Vector3 sweepDir = new Vector3();
	
	public float rotationPitch = 0;
	public float rotationYaw = 0;
	
	public static double pi = Math.PI;
	
	public boolean type = false;

	public boolean powered = false;

	public double tracking = 0.2;
	public double range = 50;


	public void updateEntity()
	{

	} 

	public void changePointing()
	{
		setVectors();
		if(!Vector3.isEntityVisibleInDirection(target, targetDir, origin, worldObj)) //was cTarget
		{
			target=null;
			return;
		}
		
		if(sweepDir.y==0&&sweepDir.z==0) return; 

		double dphi = sweepDir.y>0?-tracking:tracking, dtheta = Math.abs(LinearAlgebra.moduloPi(sweepDir.z)+tracking)>Math.abs(sweepDir.z)||LinearAlgebra.moduloPi(sweepDir.z)>sweepDir.z? tracking:-tracking;
		
		if(Math.abs(sweepDir.y)<tracking&&Math.abs(sweepDir.z)<tracking)
		{
		 turretDir.set(targetDir);
		}
		else
		{
			turretDir = Vector3.vectorRotateAboutAngles(turretDir, dphi, dtheta);
		}
		
	}

	public void getTarget()
	{
		List<Entity> list = worldObj.getEntitiesWithinAABB(EntityLiving.class, 
				AxisAlignedBB.getBoundingBox(xCoord-range, yCoord-range, zCoord-range, xCoord+range, yCoord+range, zCoord+range));
		
		for(Entity e:list)
		{
			targetDir = (new Vector3(e, true)).subtract(origin).normalize();
			
			if(Vector3.isEntityVisibleInDirection(e, targetDir, origin, worldObj)&&origin.distToEntity(e)<range)
			{
				target = e;
				updateClient();
				setVectors();
				return;
			}
		}
	}
    
    public void updateClient()
    {
    	if(!worldObj.isRemote)
    	{
			PlayerInstance players = ((WorldServer)worldObj).getPlayerManager().getOrCreateChunkWatcher(xCoord >> 4, zCoord >> 4, false);
			if(players != null)
			{
				players.sendToAllPlayersWatchingChunk(PacketBeam.getPacket(origin, target.entityId));
			}
    	}
    }

	private void setVectors()
	{
		if(target!=null)
		{
			sweepDir = turretDir.anglesTo(targetDir);
		}
	}
	
	public Vector3 source()
	{
		thisLaser = new Vector3(this);
		return thisLaser.add(new Vector3(0.5,0.5,0.5));
	}

	public boolean canUpdate()
	{
		return true;
	}
	
	public void rotationAngles()
	{
		this.rotationPitch= (float) Math.toDegrees((turretDir.toSpherical()).y);
		this.rotationYaw= (float) (Math.toDegrees((turretDir.toSpherical()).z));
	}
	public void setAngles(float pitch, float yaw)
	{
		this.rotationPitch = pitch;
		this.rotationYaw = yaw;
		
	}
	
	public String toString()
	{
		return (rotationYaw+" "+rotationPitch+" "+xCoord+" "+yCoord+" "+zCoord+" "+worldObj);
	}
}
