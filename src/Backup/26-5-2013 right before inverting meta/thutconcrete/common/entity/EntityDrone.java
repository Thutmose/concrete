package thutconcrete.common.entity;

import java.util.HashMap;
import java.util.Map;

import thutconcrete.common.corehandlers.TSaveHandler;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityDrone extends EntityLiving implements IEntityAdditionalSpawnData
{
	
	public int MAXHP = 20;
	
	public EntityPlayer owner;
	
	private Map<String, int[]> locations = new HashMap<String, int[]>();
	
	

	public EntityDrone(World par1World) 
	{
		super(par1World);
	}
	
	public EntityDrone(EntityPlayer player, double x, double y, double z)
	{
		super(player.worldObj);
		owner = player;
		this.setPosition(x+.5, y, z+.5);
		this.motionX = 0.0d;
		this.motionY = 0.0d;
		this.motionZ = 0.0d;
	}

	
	public void onUpdate()
	{
		
		
	}
	
	
	
	@Override
	public int getMaxHealth() 
	{
		return MAXHP;
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) 
	{
		if(this.owner!=null)
			data.writeInt(owner.entityId);

	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) 
	{
		try
		{
			this.owner= (EntityPlayer) this.worldObj.getEntityByID(data.readInt());
		}
		catch(Exception e){}
	}
	
	
	public void readEntityFromNBT(NBTTagCompound var1) 
	{
		super.readEntityFromNBT(var1);
		this.owner= worldObj.getPlayerEntityByName(var1.getString("owner"));
		locations = TSaveHandler.readSIAHashMap(var1);

	}
	public void writeEntityToNBT(NBTTagCompound cmpnd) 
	{
		super.writeEntityToNBT(cmpnd);
		cmpnd.setString("owner", owner.username);
		TSaveHandler.saveSIAHashMap(cmpnd, locations);
	}

}
