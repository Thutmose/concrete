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
    	if(itemstack.stackTagCompound == null)
    	{
    		return itemstack;
    	}
       	boolean flag = player.isSneaking();
       	int liftID = itemstack.stackTagCompound.getInteger("lift");
       	EntityLift lift = EntityLift.lifts.get(liftID);
       	if(lift!=null)
       	{


       		boolean move = lift.toMoveY;
       		boolean up = lift.up;
       		
       		if(!worldObj.isRemote)
       		{
	       		if(player.isSneaking())
	       		{
	       			lift.toMoveY = !lift.toMoveY;
	       		}
	       		else
	       		{
		       		lift.up = !lift.up;
	       		}
       			PacketDispatcher.sendPacketToPlayer(PacketLift.getPacket(lift, lift.toMoveY?1:0, lift.up?1:0), (Player) player);
       		}
       		else
       		{
	       		if(player.isSneaking())
	       		{
	       			move = !lift.toMoveY;
	       		}
	       		else
	       		{
		       		up = !lift.up;
	       		}
       		}
       		String message = "Lift "+(move?"Moving "+(up?"Up":"Down"):"Stopped");
       		if(worldObj.isRemote)
       			player.addChatMessage(message);
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
       	
       	int liftID = itemstack.stackTagCompound.getInteger("lift");
       	EntityLift lift = EntityLift.lifts.get(liftID);
       	
		if(player.isSneaking()&&lift!=null&&id==BlockLift.instance.blockID&&meta==1)
		{
			TileEntityLiftAccess te = (TileEntityLiftAccess)worldObj.getBlockTileEntity(x, y, z);
			te.setLift(lift);
			te.setFloor(te.getButtonFromClick(side, hitX, hitY, hitZ));
			if(worldObj.isRemote)
			player.addChatMessage("Set this Floor to "+te.floor);
			return true;
		}       	
		if(lift!=null&&id==BlockLift.instance.blockID&&meta==1)
		{
			TileEntityLiftAccess te = (TileEntityLiftAccess)worldObj.getBlockTileEntity(x, y, z);
			if(side!=te.side)
			{
				te.setSide(side);
				return true;
			}
		}
    	return false;
    }
    
    public void setLift(EntityLift lift, ItemStack stack)
    {
       	if(stack.stackTagCompound == null)
    	{
    		stack.setTagCompound(new NBTTagCompound() );
    	}
       	stack.stackTagCompound.setInteger("lift", lift.id);
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
