package thutconcrete.common.entity;

import java.util.List;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.BlockMisc;
import thutconcrete.common.utils.LinearAlgebra;
import thutconcrete.common.utils.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityMinecartEmpty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class CopyOfEntityTurret  extends EntityLiving implements IEntityAdditionalSpawnData
{
	long time = 0;
	int notfired = 0;
	
	public Entity target;
	public Entity owner;
	
	public float yaw = 0; 
	public float pitch = 0;
	
	public Vector3 origin = new Vector3();
	public Vector3 thisLaser = new Vector3();
	

	public Vector3 targetDir = new Vector3();
	public Vector3 targetImage = new Vector3();

	public Vector3 turretLook = new Vector3(1,0,0);
	public Vector3 turretPoint = new Vector3(1,0,0);
	

	public Vector3 sweepDir = new Vector3();
	
	
	Vector3 g = ConcreteCore.g;
	double v = 10;
	
	public double targetRange = 0;
	
	public static double pi = Math.PI;

	public boolean powered = false;

	public double tracking = 0.1;
	public double range = 50;
	public double size = 4;
	
	public boolean locked = false;
	
	public CopyOfEntityTurret(World par1World) {
		super(par1World);
		this.setSize((float)size, (float)size);
		this.entityCollisionReduction = 1;
		this.ignoreFrustumCheck = true;
	}
	
	public CopyOfEntityTurret(World world, double x, double y, double z)
	{
		this(world);
		this.setPosition(x, y, z);
	}
	
	
	@Override
	public void onUpdate()
	{
		init();

		if(worldObj.getBlockPowerInput((int)Math.floor(posX),(int)Math.floor(posY)-1,(int)Math.floor(posZ))>0)
		{
			powered = true;;
		}

		if(this.riddenByEntity==null)
		{
			autoFire();
		}
		else
		{
			turretLook = new Vector3(this.riddenByEntity.getLookVec());
			if(this.riddenByEntity instanceof EntityPlayer)
			{
				EntityPlayer player = (EntityPlayer)this.riddenByEntity;
				fakeRotationAngles();
				if(player.getHeldItem()!=null)
				{
					fire();
				}
			}
		}
		
	}
	
	public void init()
	{
		if((worldObj.getBlockId((int)Math.floor(posX),(int)Math.floor(posY)-1,(int)Math.floor(posZ))!=BlockMisc.instance.blockID))
		{
			this.setDead();
		}
		
		if(!origin.equals(source()))
		{
			origin.set(source());
		}
	}
	
	
	public void autoFire()
	{

		
		if(worldObj.isRemote)
		{
			int check = this.dataWatcher.getWatchableObjectInt(31);
			
			if(check!=-1)
			{
				target = worldObj.getEntityByID(check);
			}
			else
			{
				target = null;
			}
		}

		if(powered&&time%(target==null?10:1)==0)
		{
			
			double dist = target!=null?origin.distToEntity(target):-1;
			if(dist==-1||dist>range)
			{
				getTarget();
			}

			
			if(target!=null)
			{
				if(locked)
				{/*/
					Vector3 targetLoc = Vector3.firstEntityLocation(range, turretLook, origin.add(turretLook.scalarMult(size)), worldObj, false);
	
					List<Entity> list = targetLoc!=null? targetLoc.livingEntityAtPoint(worldObj):null;
		
					if(!worldObj.isRemote&&list!=null&&list.size()>0&&time%15==0)
					{
						for(Entity e: list)
						{
							if(!e.isDead)
							{//*/
								fire();/*/
							}
						}
					}
					else
					{
						notfired++;
						changePointing();
					}//*/
				}
				else
				{
					notfired++;
					changePointing();
				}
				if(notfired>100)
				{
					getTarget();
				}
			}

		}

		powered = false;
		time++;
	}
	
	
	public void fire()
	{			
		if(!worldObj.isRemote)
		{
			EntityBeam beam = new EntityBeam(worldObj, origin.add(turretLook.scalarMult(size)), turretLook, true,0);
			worldObj.spawnEntityInWorld(beam);
			notfired = 0;
		}
	}
	
	public void changePointing()
	{
		setVectors();
		if(!Vector3.isEntityVisibleInDirection(target, targetDir, origin.add(targetDir.scalarMult(size)), worldObj))
		{
			target=null;
			return;
		}
		
		if(sweepDir.y==0&&sweepDir.z==0) return; 

		double dtheta = sweepDir.y!=0?sweepDir.y>0?-tracking:tracking:0, 
				dphi = sweepDir.z!=0?Math.abs(LinearAlgebra.moduloPi(sweepDir.z)+tracking)>Math.abs(sweepDir.z)
				||LinearAlgebra.moduloPi(sweepDir.z)>sweepDir.z? tracking:-tracking:0;
		
		if(Math.abs(sweepDir.y)<=tracking)
		{
			turretLook.y = targetDir.y;
			dtheta = 0;
		}
		if(Math.abs(sweepDir.z)<=tracking)
		{
			turretLook.z = targetDir.z;
			dphi = 0;
		}	
		
		locked = (dphi==0&&dtheta==0);//||(Math.abs(sweepDir.y)<2*tracking&&Math.abs(sweepDir.z)<2*tracking);
		if(!locked)
			turretLook = turretLook.rotateAboutAngles(dtheta, dphi);
		
		rotationAngles();
	}

	public void getTarget()
	{
		List<Entity> list = worldObj.getEntitiesWithinAABB(EntityLiving.class, 
				AxisAlignedBB.getBoundingBox(posX-range, posY-range, posZ-range, posX+range, posY+range, posZ+range));
		if(list.size()==0)
		{
			target=null;
			return;
		}
		int i = (int) (Math.random()*(list.size()));
		for(int j=0;j<list.size();j++)
		{
			Entity e = list.get(i);
			
			if(e instanceof CopyOfEntityTurret||e instanceof EntityPlayer)
				continue;
			
			targetDir = (new Vector3(e, true)).subtract(origin).normalize();
			
			if(Vector3.isEntityVisibleInDirection(e, targetDir, origin.add(targetDir.scalarMult(size)), worldObj)&&origin.distToEntity(e)<range)
			{
				target = e;
				if(!worldObj.isRemote)
				{
					this.dataWatcher.updateObject(31, Integer.valueOf((int)e.entityId));
				}
				setVectors();
				return;
			}
			
			i = (i+j)%(list.size());
		}

		if(!worldObj.isRemote)
		{
			this.dataWatcher.updateObject(31, Integer.valueOf((int)-1));
		}
	}
    
	private void setVectors()
	{
		if(target!=null)
		{
			
			targetRange = (new Vector3(target, true)).HorizonalDist(origin);
			targetDir = ((new Vector3(target, true)).subtract(origin)).normalize();
			
			targetImage.set(targetDir);
			
		
			sweepDir = turretLook.anglesTo(targetDir);
		}
	}
	
	public void rotationAngles()
	{
		this.rotationPitch = pitch = (float) Math.toDegrees((turretLook.toSpherical()).y);
		this.rotationYaw = yaw = (float) (Math.toDegrees((turretLook.toSpherical()).z));
	}
	public void fakeRotationAngles()
	{
		pitch = (float) Math.toDegrees((turretLook.toSpherical()).y);
		yaw = (float) (Math.toDegrees((turretLook.toSpherical()).z));
	}
	public void setAngles(float pitch, float yaw)
	{
		this.rotationPitch = pitch;
		this.rotationYaw = yaw;
		this.pitch = pitch;
		this.yaw = yaw;
	}
	
	public Vector3 source()
	{
		thisLaser = new Vector3(this);
		return thisLaser.add(new Vector3(0.0,0.6*(size),0.0));
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void entityInit() 
	{
		this.dataWatcher.addObject(31, Integer.valueOf((int)0));
	}


	@Override
	public int getMaxHealth() 
	{
		return 20;
	}


	@Override
	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		// TODO Auto-generated method stub
		
	}

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return true;
    }

    /**
     * Returns true if this entity should push and be pushed by other entities when colliding.
     */
    public boolean canBePushed()
    {
        return true;
    }
    
    /**
     * Returns the Y offset from the entity's position for any entity riding this one.
     */
    public double getMountedYOffset()
    {
        return (double)this.height/2;
    }
    
    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer par1EntityPlayer)
    {
        if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != par1EntityPlayer)
        {
            return true;
        }
        else
        {
            if (!this.worldObj.isRemote)
            {
                par1EntityPlayer.mountEntity(this);
            }

            return true;
        }
    }

    public boolean shouldRenderInPass(int pass)
    {
        return pass == 1;
    }
    
}
