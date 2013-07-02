package thutconcrete.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import thutconcrete.api.datasources.*;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.BlockLift;
import thutconcrete.common.blocks.BlockMachine;
import thutconcrete.common.entity.EntityLift;
import thutconcrete.common.network.PacketLift;
import thutconcrete.common.tileentity.TileEntityLiftAccess;
import thutconcrete.common.tileentity.TileEntitySensors;
import universalelectricity.core.block.IConductor;
import universalelectricity.core.block.IConnectionProvider;
import universalelectricity.core.block.INetworkProvider;
import universalelectricity.core.electricity.IElectricityNetwork;
import universalelectricity.core.path.Pathfinder;
import universalelectricity.core.path.PathfinderChecker;
import universalelectricity.core.vector.Vector3;


import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDataLinker  extends Item
{
	public static Item instance;
	
	public ItemDataLinker(int par1) 
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
	       	if(!(te instanceof IDataSource))
	       	{
	       		return false;
	       	}
	       	itemstack.stackTagCompound.setInteger("id", ((IDataSource)te).getID());
    		
    		
			return true;
    	}
       	else
       	{
	       	int id = worldObj.getBlockId(x, y, z);
	       	int meta = worldObj.getBlockMetadata(x, y, z);
	       	
	       	TileEntity te = worldObj.getBlockTileEntity(x, y, z);
	       	if((te instanceof IDataSource))
	       	{
		       	int savedId = itemstack.stackTagCompound.getInteger("id");
		       	
				if(player.isSneaking())
				{
					itemstack.stackTagCompound.setInteger("id", ((IDataSource)te).getID());
					player.addChatMessage("id: "+Integer.toString(itemstack.stackTagCompound.getInteger("id")));
					return true;
				} 
				if(side==1)
				{
					TileEntitySensors tes = (TileEntitySensors)te;
					if(tes.hasDisplay)
					{
						int button = tes.getButtonFromClick(hitX, hitY, hitZ);
						tes.addStation(savedId, button-1);
						player.addChatMessage("Added Sensor: "+Integer.toString(itemstack.stackTagCompound.getInteger("id"))+" as source "+Integer.toString(button));
				    	return true;
					}
				}
		    	return false;
	       	}
	       	if(te instanceof INetworkProvider)
	       	{
	       		IElectricityNetwork net = ((INetworkProvider)te).getNetwork();
	       		if(net!=null)
	       		{
	       			double resistance = 0;
	       			double ret = 0;
	       			List<TileEntity> sources = net.getProviders();
	       			for(TileEntity t: sources)
	       			{
						Pathfinder finder = new PathfinderChecker((te).worldObj, (IConnectionProvider) t, (IConnectionProvider)null);
						finder.init(new Vector3(te));

						if (finder.results!=null&&finder.results.size() > 0)
						{
							for(Vector3 v:finder.results)
							{
								if(v.getTileEntity(worldObj)instanceof IConductor)
								{
									resistance=((IConductor)v.getTileEntity(worldObj)).getResistance();
									ret += resistance!=0?1/resistance:0;
								}
							}
						}
	       				ret = ret!=0?1/ret:0;
	       				player.addChatMessage(Double.toString(ret));
	       			}
	       			
	       			
	       			
	       		}
	       		
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
        this.itemIcon = par1IconRegister.registerIcon("thutconcrete:seismicLinker");
    }
}
