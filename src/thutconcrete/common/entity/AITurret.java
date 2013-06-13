package thutconcrete.common.entity;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.utils.LinearAlgebra;
import thutconcrete.common.utils.Vector3;

public class AITurret 
{
	public double targetRange = 0;
	
	public EntityTurret turret;
	
	Vector3 g = ConcreteCore.g;
	Vector3 sweepDir = new Vector3();

	Vector3 targetDir = new Vector3();
	Vector3 targetDirSp = new Vector3();
	
	Vector3 targetLoc = new Vector3();
	
	Vector3 turretLook = new Vector3();
	Vector3 aimDir = new Vector3();
	
	double v = 10;
	public double tracking = 0.1;
	public double range = 128;
	public boolean locked = false;
	public static double pi = Math.PI;

	long time = 0;
	
	public AITurret(EntityTurret turret)
	{
		this.turret = turret;
		this.v = turret.v;
	}
	
	public void autoFire()
	{
		if(turret.powered&&time%(turret.target==null?10:1)==0)
		{
			
			double dist = turret.target!=null?turret.origin.distToEntity(turret.target):-1;
			if(dist==-1||dist>range)
			{
				getTarget();
			}

			
			if(turret.target!=null)
			{
				changePointing();
				if(turret.target==null)
					return;
				if(locked&&turret.fireCooldown>=turret.rate)
				{
					turret.notfired = 0;
					turret.fireCooldown = 0;
					locked = false;
					turret.fire();
				}
				else
				{
					turret.fireCooldown++;
					turret.notfired++;
					changePointing();
				}
				if(turret.notfired>100)
				{
					getTarget();
				}
				
			}

		}
		time++;
	}
	
	public void changePointing()
	{
		setVectors();
		if(turret.target==null)
			return;
		if(!Vector3.isEntityVisibleInDirection(turret.target, targetDir, turret.origin.add(targetDir.scalarMult(turret.size)), turret.worldObj))
		{
			nullTarget();
			return;
		}
		
		if(sweepDir.y==0&&sweepDir.z==0) 
		{
			locked = true;
			return; 
		}
		double  dtheta = sweepDir.y!=0?sweepDir.y>0?-tracking:tracking:0, 
				dphi = sweepDir.z!=0?Math.abs(LinearAlgebra.moduloPi(sweepDir.z)+tracking)>Math.abs(sweepDir.z)
				||LinearAlgebra.moduloPi(sweepDir.z)>sweepDir.z? tracking:-tracking:0;
		
		if(Math.abs(sweepDir.y)<=tracking)
		{
			turret.turretDir.y = targetDir.y;
			dtheta = 0;
		}
		if(Math.abs(sweepDir.z)<=tracking)
		{
			turret.turretDir.z = targetDir.z;
			turret.turretDir.x = targetDir.x;
			dphi = 0;
		}	
		
		locked = (dphi==0&&dtheta==0);//||(Math.abs(sweepDir.y)<2*tracking&&Math.abs(sweepDir.z)<2*tracking);
		
		if(!locked)
			turret.turretDir = turret.turretDir.rotateAboutAngles(dtheta, dphi);
		
		turret.rotationAngles();
	}

	public void getTarget()
	{
		
		if(!turret.worldObj.isRemote)
		{
			List<Entity> list = turret.worldObj.getEntitiesWithinAABB(EntityLiving.class, 
					AxisAlignedBB.getBoundingBox(turret.posX-range, turret.posY-range, turret.posZ-range, 
												 turret.posX+range, turret.posY+range, turret.posZ+range));
			if(list.size()==0)
			{
				nullTarget();
				return;
			}
			int i = (int) (Math.random()*(list.size()));
			for(int j=0;j<list.size();j++)
			{
				Entity e = list.get(i);
				
				if(e instanceof EntityTurret||(e instanceof EntityPlayer&&(!((EntityPlayer)e).isSneaking())))
					continue;
				
				targetDir = (new Vector3(e, true)).subtract(turret.origin).normalize();
	
				targetDirSp = targetDir.toSpherical();
				
				if(targetDirSp.y>0.6)
					continue;
				
				if(Vector3.isEntityVisibleInDirection(e, targetDir, turret.origin.add(targetDir.scalarMult(turret.size)), turret.worldObj)&&turret.origin.distToEntity(e)<range)
				{
					turret.target = e;
					turret.getDataWatcher().updateObject(31, Integer.valueOf((int)e.entityId));
					setVectors();
					return;
				}
				
				i = (i+j)%(list.size());
			}
			nullTarget();
		}
		else
		{
			if(getClientTarget())
			{
				setVectors();
			}
		}
	}
	
	public boolean getClientTarget()
	{
		if(turret.worldObj.isRemote)
		{
			int check = turret.getDataWatcher().getWatchableObjectInt(31);
			
			if(check!=-1)
			{
				turret.target = turret.worldObj.getEntityByID(check);
				return true;
			}
			else
			{
				turret.target = null;
				return false;
			}
		}
		return false;
	}
    
	public void setVectors()
	{
		if(turret.target!=null)
		{

			if(!turret.origin.equals(turret.source()))
			{
				turret.origin.set(turret.source());
			}
			
			targetLoc = new Vector3(turret.target, true);
			targetRange = targetLoc.HorizonalDist(turret.origin);
			targetDir = (targetLoc.subtract(turret.origin)).normalize();
			
			targetDirSp = targetDir.toSpherical();
			
			if(targetDirSp.y>0.16)
			{
				nullTarget();
				return;
			}
			
			double y = targetLoc.y - turret.origin.y;
			double x = targetRange;
			double vsq = v*v;
			
			double num = (vsq-Math.sqrt(vsq*vsq - g.y*(2*y*vsq + g.y*x)));
			
			double theta = (Math.atan((num)/(g.y*x)));
			
			

			aimDir = targetDirSp;
			aimDir.y = theta;
			aimDir = aimDir.toCartesian();

			System.out.println(theta+" "+targetDirSp.toString()+" "+(num)+" "+(g.y*x)+" "+Math.atan(num/(g.y*x)));
		//	System.out.println(targetDir.toString()+" "+targetDirSp.toCartesian().toString());
			
			sweepDir = turret.turretDir.anglesTo(aimDir);
			
		}
	}
	
	public void nullTarget()
	{
		turret.target = null;

		if(!turret.worldObj.isRemote)
		{
			turret.getDataWatcher().updateObject(31, Integer.valueOf((int)-1));
		}
	}
	
	
	
}
