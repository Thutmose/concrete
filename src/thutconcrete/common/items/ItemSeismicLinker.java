package thutconcrete.common.items;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.BlockLift;
import thutconcrete.common.blocks.BlockSeismicMonitor;
import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.network.PacketLift;
import thutconcrete.common.tileentity.TileEntityLiftAccess;
import thutconcrete.common.tileentity.TileEntitySeismicMonitor;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSeismicLinker  extends Item
{
	public static Item instance;
	
	public ItemSeismicLinker(int par1) 
	{
		super(par1);
        this.setHasSubtypes(true);
		this.setUnlocalizedName("seismicLinker");
		this.setCreativeTab(ConcreteCore.tabThut);
		instance = this;
	}
	
	
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World worldObj, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
       	if(itemstack.stackTagCompound == null)
    	{
    		itemstack.setTagCompound(new NBTTagCompound());
	       	TileEntity te = worldObj.getBlockTileEntity(x, y, z);
	       	if(!(te instanceof TileEntitySeismicMonitor))
	       	{
	       		return false;
	       	}
	       	itemstack.stackTagCompound.setInteger("id", ((TileEntitySeismicMonitor)te).id);
    		
    		
			return true;
    	}
       	else
       	{
	       	int id = worldObj.getBlockId(x, y, z);
	       	int meta = worldObj.getBlockMetadata(x, y, z);
	       	
	       	if(!(id==BlockSeismicMonitor.instance.blockID))
	       	{
	       		return false;
	       	}
	       	TileEntity te = worldObj.getBlockTileEntity(x, y, z);
	       	if(!(te instanceof TileEntitySeismicMonitor))
	       	{
	       		return false;
	       	}
	       	int savedId = itemstack.stackTagCompound.getInteger("id");
	       	
			if(player.isSneaking())
			{
				itemstack.stackTagCompound.setInteger("id", ((TileEntitySeismicMonitor)te).id);
				player.addChatMessage("id: "+Integer.toString(itemstack.stackTagCompound.getInteger("id")));
				return true;
			} 
			if(side==1)
			{
				TileEntitySeismicMonitor tes = (TileEntitySeismicMonitor)te;
				tes.addStation(savedId, tes.getButtonFromClick(hitX, hitY, hitZ)-1);
			}
			
       	}
    	return false;
    }
    
    /**
     * Gets the localized name of the given item stack.
     */
    public String getLocalizedName(ItemStack par1ItemStack)
    {
        String s = par1ItemStack.stackTagCompound!=null?Integer.toString(par1ItemStack.stackTagCompound.getInteger("id")):"";
        return s == null ? "" : s;
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
