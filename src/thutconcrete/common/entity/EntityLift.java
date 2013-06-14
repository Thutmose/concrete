package thutconcrete.common.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import thutconcrete.common.blocks.BlockLift;
import thutconcrete.common.blocks.BlockLiftRail;
import thutconcrete.common.items.ItemLiftController;
import thutconcrete.common.network.PacketLift;
import thutconcrete.common.tileentity.TileEntityLiftAccess;
import thutconcrete.common.utils.Vector3;
import thutconcrete.common.utils.Vector3.Matrix3;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityLift extends EntityLiving implements IEntityAdditionalSpawnData
{

	public double size = 5;
	public double y0=0;
	public double speedUp = 0.5;
	public double speedDown = -0.25;
	public double acceleration = 0.05;
	public boolean up = true;
	public boolean move = false;
	public boolean moved = false;
	public boolean axis = true;
	public boolean hasPassenger = false;
	int n = 0;
	NBTTagCompound tag;
	boolean first = true;
	
	public double destinationY = 0;
	
	public boolean called = false;

	
	public TileEntityLiftAccess[][] floors = new TileEntityLiftAccess[16][4];
	
	Matrix3 base = new Matrix3();
	Matrix3 wall1 = new Matrix3();
	
	public EntityLift(World par1World) 
	{
		super(par1World);
		this.setSize(5f, 1f);
		this.ignoreFrustumCheck = true;
		this.hurtResistantTime =0;
		this.hurtTime = 0;
		this.isImmuneToFire = true;
	}
	
	public boolean canRenderOnFire()
	{
		return false;
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
		if(n>10&&first&&tag!=null)
		{
			readList(tag);
			first = false;
		}
		
		
		checkBlocks(0);
		accelerate();
		if(move)
		{
			doMotion();
		}
		else
		{
			setPosition(posX, called&&Math.abs(posY-destinationY)<0.5?destinationY:Math.floor(posY), posZ);
			called = false;
		}
		checkCollision();
		n++;
	}
	
	
	public void call(int floor)
	{

		if(!worldObj.isRemote&&floors[floor-1]!=null)
		{
			int i = -1;
			for(int j = 0; j<4;j++)
			{
				if(floors[floor-1][j]!=null)
				{
					i = j;
					break;
				}
			}
			if(i == -1||floors[floor-1][i]==null)
				return;
			destinationY = floors[floor-1][i].yCoord - 2;
			up = destinationY > posY;
			move = true;
			called = true;
			System.out.println(destinationY+" "+posY+" "+up+" "+move);
			PacketDispatcher.sendPacketToAllAround(posX, posY, posZ, 100, this.dimension, PacketLift.getPacket(this, 3,destinationY));
			
		}
	}
	
	public void callClient(double destinationY)
	{
		this.destinationY = destinationY;
		up = destinationY > posY;
		move = true;
		called = true;
	}
	
	/*/
	public void call(int floor, TileEntityLiftAccess te)
	{
		if(floorMap.containsKey(te)&&floorMap.get(te).intValue()==floor)
		{
			destinationY = te.yCoord - 1;
			up = destinationY > posY;
			move = true;
			called = true;
			System.out.println(destinationY+" "+posY+" "+up+" "+move);
			if(!worldObj.isRemote)
			{
				PacketDispatcher.sendPacketToAllAround(posX, posY, posZ, 100, this.dimension, PacketLift.getPacket(this, 3, floor));
			}
		}
	}
	//*/
	
	public void accelerate()
	{
		if(!move)
			motionY *= 0.5;
		else
		{
			if(up)
				motionY = Math.min(speedUp, motionY + acceleration*speedUp);
			else
				motionY = Math.max(speedDown, motionY + acceleration*speedDown);
		}
	}
	
	public void doMotion()
	{
		if(up)
		{
			if(checkBlocks(motionY*40))
			{
				setPosition(posX, posY+motionY, posZ);
				moved = true;
				return;
			}
			else
			{
				while(motionY>=0&&!checkBlocks((motionY - acceleration*speedUp)*15))
				{
					motionY = motionY - acceleration*speedUp;
				}
				
				if(checkBlocks(motionY))
				{
					setPosition(posX, posY+motionY, posZ);
					moved = true;
					return;
				}
				else
				{
					System.out.println("blocked up");
					setPosition(posX, called&&Math.abs(posY-destinationY)<0.5?destinationY:Math.floor(posY), posZ);
					called = false;
					motionY = 0;
					move = false;
					moved = false;
				}
			}
		}
		else
		{
			if(checkBlocks(motionY*40))
			{
				setPosition(posX, posY+motionY, posZ);
				moved = true;
				return;
			}
			else
			{
				while(motionY<=0&&!checkBlocks((motionY - acceleration*speedDown)*15))
				{
					motionY = motionY - acceleration*speedDown;
				}
				
				if(checkBlocks(motionY))
				{
					setPosition(posX, posY+motionY, posZ);
					moved = true;
					return;
				}
				else
				{
					System.out.println("blocked down");
					setPosition(posX, called&&Math.abs(posY-destinationY)<0.5?destinationY:Math.floor(posY), posZ);
					called = false;
					motionY = 0;
					move = false;
					moved = false;
				}
			}
		}
		move = false;
		moved = false;
	}
	
	public boolean checkBlocks(double dir)
	{
		boolean ret = true;
		Vector3 thisloc = new Vector3(this);
		thisloc = thisloc.add(new Vector3(0,dir,0));
		
		if(called)
		{
			if(dir > 0 && thisloc.y > destinationY)
			{
				return false;
			}
			if(dir < 0 && thisloc.y < destinationY)
			{
				return false;
			}
		}
		
		
		for(int i = -2; i<=2;i++)
			for(int j = -2;j<=2;j++)
			{
				ret = ret && (thisloc.add(new Vector3(i,0,j))).getBlockId(worldObj)==0;
			}
		ret = ret && checkRails(dir);
		return ret;
	}
	
	
	public boolean checkRails(double dir)
	{
		int[][] sides = {{3,0},{-3,0},{0,3},{0,-3}};
		
		boolean ret = true;
		
		for(int i = 0; i<4; i++)
		{
			ret = ret&&worldObj.getBlockId((int)Math.floor(posX)+sides[axis?2:0][0],(int)Math.floor(posY+dir+i),(int)Math.floor(posZ)+sides[axis?2:0][1])==BlockLiftRail.staticBlock.blockID;
			ret = ret&&worldObj.getBlockId((int)Math.floor(posX)+sides[axis?3:1][0],(int)Math.floor(posY+dir+i),(int)Math.floor(posZ)+sides[axis?3:1][1])==BlockLiftRail.staticBlock.blockID;
		}
		
		if(!(ret||dir!=0))
		{
			axis = !axis;
		}
		
		return ret;
	}
	
    private void checkCollision()
    {
        List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(posX - (size), posY, posZ - (size), posX+(size), posY + size, posZ + (size)));
        double factor = 5;//worldObj.isRemote? 1.95:5;
        base = new Matrix3(new Vector3(-size/2,0,-size/2), new Vector3(size/2,size/factor,size/2));
        wall1 = new Matrix3(new Vector3(-size/10,0,-size/10), new Vector3(size/10,size*0.8,size/10));
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
    	hasPassenger = base.pushOutOfBox(this, entity, new Vector3());
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
	    	if(wall1.pushOutOfBox(this, entity, new Vector3(0,0,wallOffset)));
	     //   	System.out.println("push wall3");
	    	if(wall1.pushOutOfBox(this, entity, new Vector3(0,0,-wallOffset)));
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
    		if(!worldObj.isRemote)
    		{
    			player.addChatMessage("killed lift");
    		}
    		setDead();
    		return true;
    	}
    	
    	return false;
    }
	
    /**
     * Will get destroyed next tick.
     */
    public void setDead()
    {
    	if(!worldObj.isRemote&&!this.isDead)
    	{
	    	this.dropItem(Block.blockIron.blockID, 24);
	    	this.dropItem(BlockLift.instance.blockID, 1);
    	}
        super.setDead();
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
	protected void entityInit()
	{
		super.entityInit();
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
	//	super.readEntityFromNBT(nbt);
		axis = nbt.getBoolean("axis");
		tag = (NBTTagCompound) nbt.copy();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
	//	super.writeEntityToNBT(nbt);
		nbt.setBoolean("axis", axis);
		writeList(nbt);
	}

	public void writeList(NBTTagCompound nbt)
	{
		for(int i = 0; i<16; i++)
		{
			for(int j = 0; j<4; j++)
			{
				if(floors[i]!=null&&floors[i][j]!=null)
					nbt.setIntArray("list"+i+" "+j, new int[]{floors[i][j].xCoord,floors[i][j].yCoord,floors[i][j].zCoord});
				else
					nbt.setIntArray("list"+i+" "+j, new int[]{0,-1,0});
			}
		}
	}
	
	public void readList(NBTTagCompound nbt)
	{
		for(int i = 0; i<16; i++)
		{
			for(int j = 0; j<4; j++)
			{
				int[] loc = nbt.getIntArray("list"+i+" "+j);
				if(loc.length > 0 && loc[1]!=-1)
				{
					floors[i][j] = (TileEntityLiftAccess)worldObj.getBlockTileEntity(loc[0], loc[1], loc[2]);
				}
			}
		}
	}
	
	@Override
	public int getMaxHealth() {
		return 10;
	}
	
	@Override
	public String getTranslatedEntityName()
	{
		return "lift";
	}
    /**
     * Gets the username of the entity.
     */
    public String getEntityName()
    {
    	return "lift";
    }

}
