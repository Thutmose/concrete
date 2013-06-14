package thutconcrete.common.items;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.BlockLift;
import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.network.PacketLift;
import thutconcrete.common.tileentity.TileEntityLiftAccess;

public class ItemLiftController extends Item
{
	public static Item instance;
	
	public ItemLiftController(int par1) 
	{
		super(par1);
        this.setHasSubtypes(true);
		this.setUnlocalizedName("liftController");
		this.setCreativeTab(ConcreteCore.tabThut);
		instance = this;
	}
	
    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack itemstack, World worldObj, EntityPlayer player)
    {
    	if(worldObj.isRemote||itemstack.stackTagCompound == null)
    	{
    		return itemstack;
    	}
       	boolean flag = player.isSneaking();
       	int liftId = itemstack.stackTagCompound.getInteger("lift");
       	Entity e = worldObj.getEntityByID(liftId);
       	if(e!=null&&e instanceof EntityLift)
       	{
       		EntityLift lift = (EntityLift)e;
       		player.addChatMessage("lift setting changed from "+lift.move+" "+lift.up);
       	//	if(lift.move)

       		if(player.isSneaking())
       		{
       			lift.move = !lift.move;
       		}
       		else
       		{
	       		lift.up = !lift.up;
       		}
       		
       		
       		PacketDispatcher.sendPacketToPlayer(PacketLift.getPacket(lift, lift.move?1:0, lift.up?1:0), (Player) player);
       		player.addChatMessage(" to "+lift.move+" "+lift.up);
       	}
        return itemstack;
    }
	
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World worldObj, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
       	if(itemstack.stackTagCompound == null)
    	{
    		return false;
    	}
       	
       	int id = worldObj.getBlockId(x, y, z);
       	int meta = worldObj.getBlockMetadata(x, y, z);
       	
       	int liftId = itemstack.stackTagCompound.getInteger("lift");
       	Entity e = worldObj.getEntityByID(liftId);
       	
		if(player.isSneaking()&&e!=null&&e instanceof EntityLift&&id==BlockLift.instance.blockID&&meta==1)
		{
			TileEntityLiftAccess te = (TileEntityLiftAccess)worldObj.getBlockTileEntity(x, y, z);
			te.setLift((EntityLift)e);
			te.setFloor(te.getButtonFromClick(side, hitX, hitY, hitZ));
			player.addChatMessage("Set this Floor to "+te.floor);
		}
    	return false;
    }
    
    public void setLift(EntityLift lift, ItemStack stack)
    {
       	if(stack.stackTagCompound == null)
    	{
    		stack.setTagCompound(new NBTTagCompound() );
    	}
       	stack.stackTagCompound.setInteger("lift", lift.entityId);
    }
    
    /**
     * If this function returns true (or the item is damageable), the ItemStack's NBT tag will be sent to the client.
     */
    public boolean getShareTag()
    {
        return true;
    }
    
	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("thutconcrete:liftController");
    }
}
