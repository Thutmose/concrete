package thutconcrete.common.entity;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityBeam extends Entity //implements IEntityAdditionalSpawnData
{
	double range;
	double[] direction;
	double[] origin;
	double[] target;

	public EntityBeam(World par1World) {
		super(par1World);
		this.setSize(.5f, .5f);
		this.boundingBox.setBounds(-.5, .5, -.5, .5, -.5, .5);
		this.boundingBox.addCoord(posX, posY, posZ);
	}

	public EntityBeam(World worldObj, double range, double[] direction, double[] source)
	{
		this(worldObj);
		this.range = range;
		this.direction = direction;
		this.posX = source[0];
		this.posY = source[1];
		this.posZ = source[2];
	}
	
	@Override
	protected void entityInit() {}
	
	@Override
	public void onUpdate()
	{
		this.prevPosX=this.posX;
		this.prevPosY=this.posY;
		this.prevPosZ=this.posZ;
		
		
	}
	
	private double distToTarget()
	{
		
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		// TODO Auto-generated method stub
		
	}

}
