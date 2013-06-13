package thutconcrete.common.entity;

import java.util.List;

import thutconcrete.common.blocks.BlockLiftRail;
import thutconcrete.common.items.ItemLiftController;
import thutconcrete.common.tileentity.TileEntityLiftRail;
import thutconcrete.common.utils.Vector3;
import thutconcrete.common.utils.Vector3.Matrix3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityLift extends EntityLiving implements IEntityAdditionalSpawnData
{

	public double size = 5;
	public double y0=0;
	public double speedUp = 0.2;//0.34;
	public double speedDown = -0.2;
	public boolean up = true;
	public boolean move = false;
	public boolean axis = true;
	int n;
	
	Matrix3 base = new Matrix3();
	Matrix3 wall1 = new Matrix3();
	Matrix3 wall2 = new Matrix3();
	
	public EntityLift(World par1World) 
	{
		super(par1World);
		this.setSize(5f, 1f);
		this.ignoreFrustumCheck = true;
		// TODO Auto-generated constructor stub
	}
	
	public EntityLift(World world, double x, double y, double z)
	{
		this(world);
		this.setPosition(x, y, z);
		y0 = y;
	}
	
	@Override
	public void onUpdate()
	{
		this.prevPosY = posY;
		checkCollision();
		checkBlocks(0);
    	this.motionY = move?up?speedUp:checkBlocks(speedDown)?speedDown:0:0;
		if(move)
		{
			doMotion();
		}
		else
		{
			setPosition(posX, Math.floor(posY), posZ);
		}
		checkCollision();

	}
	
	public void doMotion()
	{
		if(posY>255||posY<0)
		{
			System.out.println(posY);
			this.setDead();
		}
		if(up)
		{
			if(checkBlocks(speedUp))
			{
				setPosition(posX, posY+speedUp, posZ);
			}
			else
			{
				System.out.println("blocked up");
				setPosition(posX, Math.floor(posY), posZ);
				motionY = 0;
				move = false;
			}
		}
		else
		{
			if(checkBlocks(speedDown))
			{
				setPosition(posX, posY+speedDown, posZ);
			}
			else
			{
				System.out.println("blocked down");
				setPosition(posX, Math.floor(posY), posZ);
				move = false;
			}
		}
	}
	
	public boolean checkBlocks(double dir)
	{
		boolean ret = true;
		Vector3 thisloc = new Vector3(this);
		thisloc = thisloc.add(new Vector3(0,dir,0));
		for(int i = -2; i<=2;i++)
			for(int j = -2;j<=-2;j++)
			{
				ret = ret && thisloc.getBlockId(worldObj)==0;
			}
		move = move && checkRails(dir);
		ret = ret && checkRails(dir);
		return ret;
	}
	
	
	boolean checkRails(double dir)
	{
		int[][] sides = {{3,0},{-3,0},{0,3},{0,-3}};
		
		boolean ret1 = worldObj.getBlockId((int)Math.floor(posX)+sides[axis?2:0][0],(int)Math.floor(posY+dir),(int)Math.floor(posZ)+sides[axis?2:0][1])==BlockLiftRail.instance.blockID;
		boolean ret2 = worldObj.getBlockId((int)Math.floor(posX)+sides[axis?3:1][0],(int)Math.floor(posY+dir),(int)Math.floor(posZ)+sides[axis?3:1][1])==BlockLiftRail.instance.blockID;
		
		if(!(ret1&&ret2))
		{
			axis = !axis;
		}
		
		return ret1&&ret2;
	}
	
	
	
	
    private void checkCollision()
    {
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(posX - (size), posY, posZ - (size), posX+(size), posY + size, posZ + (size)));
        double factor = 5;//worldObj.isRemote? 1.95:5;
        base = new Matrix3(new Vector3(-size/2,0,-size/2), new Vector3(size/2,size/factor,size/2));
        wall1 = new Matrix3(new Vector3(-size/10,0,-size/10), new Vector3(size/10,size*0.8,size/10));
        wall2 = new Matrix3(new Vector3(-size/10,0,-size/10), new Vector3(size/10,size*0.8,size/10));
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
    	Vector3 e = new Vector3(entity);
    	//this.motionY = 0;
    	if(base.pushOutOfBox(this, entity, new Vector3()))
    	{

        	if(move&&up&&worldObj.isRemote)
    		{
        		double factor = 5;//worldObj.isRemote? 1.95:5;
        		double secondFactor = 1.5;//worldObj.isRemote? 3:1.5;
        		entity.setPosition(entity.posX, entity.posY<posY+(secondFactor)&&entity.posY>posY?posY+(size/factor)+motionY:entity.posY+speedUp, entity.posZ);
        		entity.motionY = entity.posY<posY+(secondFactor)&&entity.posY>posY&&entity.motionY<speedUp?motionY:entity.motionY;
        	//	System.out.println(posY+" "+worldObj+" "+motionY+" "+entity.motionY+" "+entity.posY);
    		}
        	//System.out.println("push base");
    	}
    	double wallOffset = size/2 + size/10;
    	if(!axis)
    	{
	    	if(wall1.pushOutOfBox(this, entity, new Vector3(wallOffset,0,0)));
	        //	System.out.println("push wall1");
	    	if(wall1.pushOutOfBox(this, entity, new Vector3(-wallOffset,0,0)));
	        // System.out.println("push wall2");
    	}
    	else
    	{
	    	if(wall2.pushOutOfBox(this, entity, new Vector3(0,0,wallOffset)));
	     //   	System.out.println("push wall3");
	    	if(wall2.pushOutOfBox(this, entity, new Vector3(0,0,-wallOffset)));
	     //   	System.out.println("push wall4");
    	}
    }
	
    /**
     * Called when a player interacts with a mob. e.g. gets milk from a cow, gets into the saddle on a pig.
     */
    public boolean interact(EntityPlayer player)
    {
    	ItemStack item = player.getHeldItem();
    	if(player.isSneaking()&&item!=null&&item.getItem() instanceof ItemLiftController)
    	{
           	if(item.stackTagCompound == null)
        	{
        		item.setTagCompound(new NBTTagCompound() );
        	}
           	item.stackTagCompound.setInteger("lift", entityId);
           	player.addChatMessage("lift set");
    	}
    	if(item!=null&&item.getItem() instanceof ItemAxe)
    	{
    		setDead();
    		return true;
    	}
    	
    	return false;
    }
	
	
	

	@Override
	public void writeSpawnData(ByteArrayDataOutput data) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readSpawnData(ByteArrayDataInput data) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		axis = nbt.getBoolean("axis");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setBoolean("axis", axis);
	}

	@Override
	public int getMaxHealth() {
		// TODO Auto-generated method stub
		return 10;
	}


}
