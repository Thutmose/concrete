package thutconcrete.common.entity;

import java.util.ArrayList;
import java.util.List;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityRocket extends EntityLiving implements IEntityAdditionalSpawnData
{
	
	public List<EntityRocket> rocketParts = new ArrayList<EntityRocket>();
	public int boosters = 0;
	public EntityRocket main;
	
	public boolean subPart = false;

	public EntityRocket(World par1World) {
		super(par1World);
		this.ignoreFrustumCheck = true;
		worldObj.MAX_ENTITY_RADIUS = 25;
		this.setSize(3f, 25f);
		this.noClip = false;
		main = this;
	}
	public EntityRocket(World par1World, double x, double y, double z) {
		this(par1World);
		this.setSize(.5f, 5f);
		this.setPosition(x, y, z);
	}
	
	public EntityRocket(World worldObj, double x, double y, double z, int boosters)
	{
		this(worldObj);
		this.boosters = boosters;
	}
	@Override
	public int getMaxHealth() {
		return 10;
	}
	
	
	@Override
	public void writeSpawnData(ByteArrayDataOutput data) {}
	@Override
	public void readSpawnData(ByteArrayDataInput data) {}

    
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
        return false;
    }
	
    public boolean shouldRenderInPass(int pass)
    {
        return pass == 1;
    }
    
}
