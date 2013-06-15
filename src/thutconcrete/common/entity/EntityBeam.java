package thutconcrete.common.entity;

import java.util.List;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.network.PacketBeam;
import thutconcrete.common.utils.EntityChunkLoader;
import thutconcrete.common.utils.ExplosionCustom;
import thutconcrete.common.utils.IChunkEntity;
import thutconcrete.common.utils.Vector3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeDirection;

public class EntityBeam extends Entity implements IEntityAdditionalSpawnData, IChunkEntity
{
	public double range;
	public double v = 10;
	public Vector3 direction = new Vector3();
	public Vector3 origin = new Vector3();
	public Vector3 target = new Vector3();
	public Vector3 currentLoc = new Vector3();
	
	Vector3 targetLoc = new Vector3();
	Vector3 next = new Vector3();
	
	boolean real = false;
	boolean gravity = false;
	public static Vector3 g = ConcreteCore.g;
	int n = 0;
	public int x,z;
	public boolean tick = false;
	public int count = 0;
	public float mass = 0;
	double y;
	public boolean first = true;
	
	public EntityBeam(World par1World) 
	{
		super(par1World);
		this.setSize(.5f, .5f);
		this.noClip = false;
	}

	public EntityBeam(World worldObj, Vector3 source,Vector3 target)
	{
		this(worldObj);

		this.origin = source;
		this.setPosition(source.x, source.y, source.z);

		this.target = target;
		this.direction = (target.subtract(source)).scalarMult(v);
		this.motionX = direction.x;
		this.motionY = direction.y;
		this.motionZ = direction.z;

		this.rotationPitch= (float) Math.toDegrees((direction.toSpherical()).y);
		this.rotationYaw= (float) (90+Math.toDegrees((direction.toSpherical()).z));
		
		this.real = true;
	}
	
	public EntityBeam(World worldObj, Vector3 source,Vector3 target, boolean gravity)
	{
		this(worldObj);

		this.origin = source;
		this.setPosition(source.x, source.y, source.z);

		this.target = target;
		this.direction = target.scalarMult(v);
		this.motionX = direction.x;
		this.motionY = direction.y;
		this.motionZ = direction.z;

		this.rotationPitch= (float) Math.toDegrees((direction.toSpherical()).y);
		this.rotationYaw= (float) (90+Math.toDegrees((direction.toSpherical()).z));
		
		this.real = true;
		this.gravity = gravity;
	}	
	
	public EntityBeam(World worldObj, Vector3 source,Vector3 target, boolean gravity, float mass)
	{
		this(worldObj);

		this.origin = source;
		this.setPosition(source.x, source.y, source.z);
	//	System.out.println(source.toString()+" "+this);

		this.target = target;
		this.direction = target.scalarMult(v);
		this.motionX = direction.x;
		this.motionY = direction.y;
		this.motionZ = direction.z;

		this.rotationPitch= (float) Math.toDegrees((direction.toSpherical()).y);
		this.rotationYaw= (float) (90+Math.toDegrees((direction.toSpherical()).z));
		
		this.real = true;
		this.mass = mass;
		this.gravity = gravity;
	}
	
	public EntityBeam(World worldObj, Vector3 source,Vector3 target, boolean gravity, float mass, double v)
	{
		this(worldObj,source,target,gravity,mass);
		this.v = v;
	}

	
	@Override
	public void onUpdate()
	{
	//	System.out.println(this);
		tick = false;
		count = 0;
		if(!real||this.ticksExisted>(gravity?30000:3000))
		{
			this.setDead();
		}
		
		this.prevPosX=this.posX;
		this.prevPosY=this.posY;
		this.prevPosZ=this.posZ;

		this.rotationPitch= (float) Math.toDegrees((direction.toSpherical()).y);
		this.rotationYaw= (float) (90+Math.toDegrees((direction.toSpherical()).z));
		//this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.explode", (float)(v/5), 1f);
		if(!this.worldObj.isRemote)
		{
		  move();
		}

		tick = true;
	}
	
	public void doCollision()
	{
		Vector3 beam = new Vector3(this);
		
		targetLoc = Vector3.firstEntityLocationExcluding(direction.mag(), direction, beam, worldObj, false, this);
		
		if(targetLoc!=null)
		{
			List<Entity> targets = targetLoc.livingEntityAtPointExcludingEntity(worldObj, this);
			if(targets!=null)
			{
				for(Entity e: targets)
				{
					if(e!=this)
					{
						e.setFire(5);
						e.attackEntityFrom(DamageSource.onFire, (int)energy());
						if(energy()<5)
						{
							this.setDead();
						}
					}
				}
			}
		}
	}
	
	public void move()
	{
		doCollision();
		currentLoc = new Vector3(this);
		next = Vector3.getNextSurfacePoint(worldObj, currentLoc, direction, direction.mag());
		EntityChunkLoader.updateLoadedChunks();
		if(next==null)
		{
			if(gravity)
			{
				direction = direction.add(g);
			}

		//	System.out.println("before: "+this+direction.toString()+" "+currentLoc.toString());
			currentLoc = currentLoc.add(direction);
			this.setPosition(currentLoc.x,currentLoc.y,currentLoc.z);
		//	System.out.println("after: "+this+direction.toString());

			n = 0;
		}
		else if (mass <= 0.08)
		{
			this.setPosition(next.x,next.y,next.z);
			if(n>5)
			{
				Block block = next.getBlock(worldObj);
				if(block!=null&&
						block.isFlammable(worldObj, next.intX(), next.intY(), next.intZ(), 
								worldObj.getBlockMetadata(next.intX(), next.intY(), next.intZ()), ForgeDirection.UP))
				{
					worldObj.setBlock(next.intX(), next.intY(), next.intZ(), Block.fire.blockID);
				}
				this.setDead();
			}
			n++;
		}
		else
		{
			if(n>5&&!worldObj.isRemote)
			{
				ExplosionCustom boom = new ExplosionCustom();
				boom.kineticImpactor(worldObj, direction, next, g, mass, energy());
				this.setDead();
			}
			
		n++;
		}
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) 
	{
		real = nbttagcompound.getBoolean("real");
		gravity = nbttagcompound.getBoolean("gravity");
		range = nbttagcompound.getDouble("range");
		v = nbttagcompound.getDouble("v");
		mass = nbttagcompound.getFloat("mass");
		target = target.readFromNBT(nbttagcompound, "target");
		direction = direction.readFromNBT(nbttagcompound, "direction");
		origin = origin.readFromNBT(nbttagcompound, "origin");
		currentLoc = currentLoc.readFromNBT(nbttagcompound, "location");
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) 
	{
		direction.writeToNBT(nbttagcompound, "direction");
		target.writeToNBT(nbttagcompound, "target");
		origin.writeToNBT(nbttagcompound, "origin");
		currentLoc.writeToNBT(nbttagcompound, "location");
		nbttagcompound.setBoolean("real", real);
		nbttagcompound.setBoolean("gravity", gravity);
		nbttagcompound.setDouble("range", range);
		nbttagcompound.setDouble("v", v);
		nbttagcompound.setFloat("mass", mass);
		
	}

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {
		direction.writeToOutputStream(data);
		target.writeToOutputStream(data);
		origin.writeToOutputStream(data);
		data.writeDouble(range);
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) {
		direction = Vector3.readFromInputSteam(data);
		target = Vector3.readFromInputSteam(data);
		origin = Vector3.readFromInputSteam(data);
		range = data.readDouble();
		real = true;
	}

	@Override
	protected void entityInit() 
	{
	//	EntityChunkLoader.registerChunkLoaderEntity(this);
	}
	
    @Override
    public void setDead()
    {
    //	EntityChunkLoader.unRegisterChunkLoaderEntity(this);
    	super.setDead();
    }
	
	public float energy()
	{
		return (float)direction.magSq()*mass;
	}
	
	
	//*/
	
    public boolean shouldRenderInPass(int pass)
    {
        return pass == 0;
    }
//*/
}
