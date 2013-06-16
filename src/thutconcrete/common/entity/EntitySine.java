package thutconcrete.common.entity;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import thutconcrete.common.utils.IMultiBox;
import thutconcrete.common.utils.Vector3;
import thutconcrete.common.utils.Vector3.Matrix3;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntitySine extends EntityLiving implements IEntityAdditionalSpawnData, IMultiBox
{
	
	public ConcurrentHashMap<String, Matrix3> boxes = new ConcurrentHashMap<String, Matrix3>();
	public ConcurrentHashMap<String, Vector3> offsets = new ConcurrentHashMap<String, Vector3>();
	public ConcurrentHashMap<String, Vector3> speeds = new ConcurrentHashMap<String, Vector3>();
	
	int time = 0;
	int ticks = 0;
	float yaw = 0;
	
	public EntitySine(World par1World) 
	{
		super(par1World);
		this.ignoreFrustumCheck = true;
		// TODO Auto-generated constructor stub
	}
	
	public EntitySine(World worldObj, double x, double y, double z) 
	{
		this(worldObj);
		setPosition(x, y, z);
	}
	
	@Override
	public void onUpdate()
	{
		checkCollision();
		if(ticks%1==0)
		{
			time++;
		//	yaw++;
		}
		ticks++;
	}
	
	private void checkCollision()
    {
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(posX - 10, posY-10, posZ - 10, posX+10, posY + 10, posZ + 10));
        
        setOffsets();
        setBoxes();
        
        if (list != null && !list.isEmpty())
        {
        	if(list.size() == 1 && this.riddenByEntity!=null)
        	{
        		return;
        	}
        	
            for (int i = 0; i < list.size(); ++i)
            {
                Entity entity = (Entity)list.get(i);
                if(entity!=this.riddenByEntity)
                {
                	applyEntityCollision(entity);
                }
            }
        }
    }
	
    /**
     * Applies a velocity to each of the entities pushing them away from each other. Args: entity
     */
    public void applyEntityCollision(Entity entity)
    {
    	
    	for(String key: boxes.keySet())
    	{
    		Matrix3 box = boxes.get(key);
    		Vector3 offset = new Vector3();
    		if(offsets.containsKey(key))
    		{
    			offset = offsets.get(key);
    			this.motionY = speeds.get(key).y;
    			this.motionX = speeds.get(key).x;
    			this.motionZ = speeds.get(key).z;
    		}
    		if(box!=null)
    		{
    			boolean push = box.pushOutOfBox(this, entity, offset);
    			
    		}
    	}
    	
    }
	

	@Override
	public void setBoxes() {
		for(int i = -6; i<=6;i++)
		{
		     boxes.put("base"+Integer.toString(i), new Matrix3(new Vector3(-0.5,0,-0.5), new Vector3(0.5,1,0.5), new Vector3(0,  Math.toRadians(yaw))));
		}
	}

	@Override
	public void setOffsets() 
	{
		for(int i = -6; i<=6;i++)
		{
	//		System.out.println(((new Vector3(i,y(i),0))).rotateAboutAngles(0, Math.toRadians(yaw)).toString()+" "+"base"+Integer.toString(i)+" "+offsets.toString());
			Vector3 speed = new Vector3();
			Vector3 temp = new Vector3();
			if(offsets.containsKey("base"+Integer.toString(i)))
			{
				temp = offsets.get("base"+Integer.toString(i));
			}
			
		     offsets.put(("base"+Integer.toString(i)), ((new Vector3(i,y(i),z(i)))).rotateAboutAngles(0, Math.toRadians(yaw)));
		     
		     if(!(temp.isNaN()||offsets.get("base"+Integer.toString(i)).isNaN()))
		     {
		    	 speed = offsets.get("base"+Integer.toString(i)).subtract(temp);
		     }
		     speeds.put(("base"+Integer.toString(i)), speed);
		}
	}
	
	public double y(int i)
	{
		return Math.sin((i/Math.PI)+time/10);
	}
	public double z(int i)
	{
		return 1.5*Math.sin((i/Math.PI)+time/20); 
	}

	@Override
	public ConcurrentHashMap<String, Matrix3> getBoxes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addBox(String name, Matrix3 box) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ConcurrentHashMap<String, Vector3> getOffsets() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addOffset(String name, Vector3 offset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Matrix3 bounds(Vector3 target) {
		return new Matrix3(new Vector3(-1,0, -1), new Vector3(1, 1, 1));
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
	public int getMaxHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

}
