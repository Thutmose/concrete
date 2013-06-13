package thutconcrete.common.tileentity;

import thutconcrete.common.blocks.BlockLimekiln;
import thutconcrete.common.blocks.BlockLimekilnDummy;
import thutconcrete.common.items.Items;
import thutconcrete.common.network.PacketInt;
import thutconcrete.common.network.PacketStampable;
import thutconcrete.common.utils.Vector3;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityLimekiln extends TileEntity implements ISidedInventory
{
	private static final int[] sidedSlotSides = new int[] { 2 };
	private static final int[] sidedSlotBottom = new int[] { 1 };
	private static final int[] sidedSlotTop = new int[] { 0 };
	
	public ForgeDirection facing = ForgeDirection.UP;
	public boolean cooking = false;
	
	public int[] validBlocks = {Block.brick.blockID};
	
	private ItemStack[] furnaceItems = new ItemStack[3];
	public int furnaceBurnTime = 0;
	public int currentItemBurnTime = 0;
	public int furnaceCookTime = 0;
	
	private boolean isValidMultiblock = false;
	 
	public boolean getIsValid()
	{
	    return isValidMultiblock;
	}
	 
	public void invalidateMultiblock()
	{
	    isValidMultiblock = false;
	    
	    furnaceBurnTime = 0;
	    currentItemBurnTime = 0;
	    furnaceCookTime = 0;
	     
	    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 2);
	    
	    revertDummies();
	}
	
	public boolean checkIfProperlyFormed()
	{
		int n=0;
		boolean axis = (facing.equals(ForgeDirection.EAST)||facing.equals(ForgeDirection.WEST));
		int dir = (facing.equals(ForgeDirection.WEST)||facing.equals(ForgeDirection.NORTH))?-1:1;

		for(int k = -1; k<5;k++)
			for(int i=0;i<7;i++)
				for(int j=-3;j<4;j++)
				{
					int r = Vector3.Int((Math.sqrt(j*j+(i-3)*(i-3)+(k+1)*(k+1))));

					if(r!=3&&!(k==-1&&r<=3))
						continue;
					if(j==0&&k==0&&(i==0||i==1))
						continue;
					
					int x = xCoord + (axis?i*dir:j);
					int z = zCoord + (axis?j:i*dir);
					int y = yCoord + k;
					
					if(x == xCoord&&y==yCoord&&z==zCoord)
						continue;
					int id = worldObj.getBlockId(x,y,z);
					//System.out.println(id+" "+x+" "+y+" "+z);
					if(!(id==Block.brick.blockID||id==BlockLimekilnDummy.instance.blockID))
						return false;
					n++;
				}
	//	System.out.println(n);
		return true;
	}
	
	
	public void convertDummies()
	{
		int n=0;
		boolean axis = (facing.equals(ForgeDirection.EAST)||facing.equals(ForgeDirection.WEST));
		int dir = (facing.equals(ForgeDirection.WEST)||facing.equals(ForgeDirection.NORTH))?-1:1;

		for(int k = -1; k<5;k++)
			for(int i=0;i<7;i++)
				for(int j=-3;j<4;j++)
				{
					int r = Vector3.Int((Math.sqrt(j*j+(i-3)*(i-3)+(k+1)*(k+1))));
					//System.out.println(r);
					if(r!=3)
						continue;
					if(j==0&&k==0&&(i==0||i==1))
						continue;
					
					int x = xCoord + (axis?i*dir:j);
					int z = zCoord + (axis?j:i*dir);
					int y = yCoord + k;
					
					if(x == xCoord&&y==yCoord&&z==zCoord)
						continue;
					
					worldObj.setBlock(x, y, z, BlockLimekilnDummy.instance.blockID);
					worldObj.markBlockForUpdate(x, y, z);
					TileEntityLimekilnDummy dummyTE = (TileEntityLimekilnDummy)worldObj.getBlockTileEntity(x, y, z);
					dummyTE.setCore(this);
				}

		isValidMultiblock = true;
	}
	
	private void revertDummies()
	{

		int n=0;
		boolean axis = (facing.equals(ForgeDirection.EAST)||facing.equals(ForgeDirection.WEST));
		int dir = (facing.equals(ForgeDirection.WEST)||facing.equals(ForgeDirection.NORTH))?-1:1;

		for(int k = -1; k<5;k++)
			for(int i=0;i<7;i++)
				for(int j=-3;j<4;j++)
				{
					int r = Vector3.Int((Math.sqrt(j*j+(i-3)*(i-3)+(k+1)*(k+1))));
					//System.out.println(r);
					if(r!=3)
						continue;
					if(j==0&&k==0&&(i==0||i==1))
						continue;
					
					int x = xCoord + (axis?i*dir:j);
					int z = zCoord + (axis?j:i*dir);
					int y = yCoord + k;
					
					if(x == xCoord&&y==yCoord&&z==zCoord)
						continue;
					
					int id = worldObj.getBlockId(x,y,z);
					if(id != BlockLimekilnDummy.instance.blockID)
						continue;
					
					worldObj.setBlock(x, y, z, Block.brick.blockID);
					worldObj.markBlockForUpdate(x, y, z);
				}

		isValidMultiblock = false;
	}
	
	@Override
	public void updateEntity()
	{
		if(!isValidMultiblock)
			return;
		
		boolean flag = furnaceBurnTime > 0;
		boolean flag1 = false;
		
		int metadata = getBlockMetadata();
		int isActive = (metadata >> 3);
		
		if(furnaceBurnTime > 0)
			furnaceBurnTime--;
		if(furnaceBurnTime==0)
		{
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3);
		}
		
		if(!this.worldObj.isRemote)
		{
			if(furnaceBurnTime == 0 && canSmelt())
			{
				currentItemBurnTime = furnaceBurnTime = TileEntityFurnace.getItemBurnTime(furnaceItems[1]);

				if(furnaceBurnTime > 0)
				{
					flag1 = true;
					
					if(furnaceItems[1] != null)
					{
						furnaceItems[1].stackSize--;
						
						if(furnaceItems[1].stackSize == 0)
							furnaceItems[1] = furnaceItems[1].getItem().getContainerItemStack(furnaceItems[1]);
					}
				}
			}
			
			if(isBurning() && canSmelt())
			{
				furnaceCookTime++;
				
				if(furnaceCookTime == 75)
				{
					furnaceCookTime = 0;
					smeltItem();
					flag1 = true;
				}
			}
			else
			{
				furnaceCookTime = 0;
			}
			
			
			if(isActive == 0 && furnaceBurnTime > 0)
			{
				flag1 = true;
				metadata = getBlockMetadata();
				isActive = 1;
				metadata = 1;
				
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, metadata, 3);
			}
		}
		
		if(flag1)
			onInventoryChanged();
	}
	
	@Override
	public int getSizeInventory()
	{
		return furnaceItems.length;
	}
	
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return furnaceItems[slot];
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int count)
	{		
		if(this.furnaceItems[slot] != null)
		{
			ItemStack itemStack;
			
			itemStack = furnaceItems[slot].splitStack(count);
				
			if(furnaceItems[slot].stackSize <= 0)
				furnaceItems[slot] = null;
				
			return itemStack;
		}
		
		return null;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		if(furnaceItems[slot] != null)
		{
			ItemStack stack = furnaceItems[slot];
			furnaceItems[slot] = null;
			return stack;
		}
		
		return null;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack)
	{
		furnaceItems[slot] = itemStack;
		
		if(itemStack != null && itemStack.stackSize > getInventoryStackLimit())
			itemStack.stackSize = getInventoryStackLimit();
	}
	
	@Override
	public String getInvName()
	{
		return "thutconcrete.container.limekiln";
	}

	@Override
	public boolean isInvNameLocalized()
	{
		return false;
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityPlayer)
	{
		return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) != this ? false : entityPlayer.getDistanceSq((double)xCoord + 0.5, (double)yCoord + 0.5, (double)zCoord + 0.5) <= 64.0;
	}

	@Override
	public void openChest() { }

	@Override
	public void closeChest() { }

	@Override
	public boolean isStackValidForSlot(int slot, ItemStack itemStack)
	{
		return slot == 2 ? false : (slot == 1 ? TileEntityFurnace.isItemFuel(itemStack) : true);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tagCompound)
	{
		super.readFromNBT(tagCompound);

		facing = ForgeDirection.getOrientation(tagCompound.getInteger("direction"));
		
		isValidMultiblock = tagCompound.getBoolean("isValidMultiblock");
		
		NBTTagList itemsTag = tagCompound.getTagList("Items");
		furnaceItems = new ItemStack[getSizeInventory()];
		
		for(int i = 0; i < itemsTag.tagCount(); i++)
		{
			NBTTagCompound slotTag = (NBTTagCompound)itemsTag.tagAt(i);
			byte slot = slotTag.getByte("Slot");
			
			if(slot >= 0 && slot < furnaceItems.length)
				furnaceItems[slot] = ItemStack.loadItemStackFromNBT(slotTag);
		}
		
		furnaceBurnTime = tagCompound.getShort("BurnTime");
		furnaceCookTime = tagCompound.getShort("CookTime");
		currentItemBurnTime = TileEntityFurnace.getItemBurnTime(furnaceItems[1]);
		cooking = tagCompound.getBoolean("active");
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tagCompound)
	{
		super.writeToNBT(tagCompound);
		
		tagCompound.setBoolean("isValidMultiblock", isValidMultiblock);
		
		tagCompound.setShort("BurnTime", (short)furnaceBurnTime);
		tagCompound.setShort("CookTime", (short)furnaceCookTime);
		tagCompound.setBoolean("active", cooking);
		tagCompound.setInteger("direction", facing.ordinal());
		NBTTagList itemsList = new NBTTagList();
		
		for(int i = 0; i < furnaceItems.length; i++)
		{
			if(furnaceItems[i] != null)
			{
				NBTTagCompound slotTag = new NBTTagCompound();
				slotTag.setByte("Slot", (byte)i);
				furnaceItems[i].writeToNBT(slotTag);
				itemsList.appendTag(slotTag);
			}
			
			tagCompound.setTag("Items", itemsList);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public int getCookProgressScaled(int scaleVal)
	{
		return furnaceCookTime * scaleVal / 100;
	}
	
	@SideOnly(Side.CLIENT)
	public int getBurnTimeRemainingScaled(int scaleVal)
	{
		if(currentItemBurnTime == 0)
			currentItemBurnTime = 100;
		
		return furnaceBurnTime * scaleVal / currentItemBurnTime;
	}
	
	public boolean isBurning()
	{
		return furnaceBurnTime > 0;
	}
	
	public boolean cookable(ItemStack stack)
	{
		if(stack==null)
		{
			return false;
		}
		
		for(ItemStack cook:Items.cookable)
		{
			if(cook.itemID==stack.itemID&&cook.getItemDamage()==stack.getItemDamage())
			{
			//	System.out.println(stack.itemID+"+"+cook.itemID);
				return true;
			}
		}
		
		return false;
	}
	
	private boolean canSmelt()
	{
		if(furnaceItems[0] == null||!cookable(furnaceItems[0]))
			return false;
		else
		{
			ItemStack itemStack = Items.limeStack;//FurnaceRecipes.smelting().getSmeltingResult(furnaceItems[0]);
			if(itemStack == null)
				return false;
			if(furnaceItems[2] == null)
				return true;
			if(!furnaceItems[2].isItemEqual(itemStack))
				return false;
			
			int resultingStackSize = furnaceItems[2].stackSize + itemStack.stackSize;
			return (resultingStackSize <= getInventoryStackLimit() && resultingStackSize <= itemStack.getMaxStackSize());
		}
	}
	
	public void smeltItem()
	{
		if(canSmelt())
		{
			ItemStack itemStack = Items.limeStack;
			
			if(furnaceItems[2] == null)
				furnaceItems[2] = itemStack.copy();
			else if(furnaceItems[2].isItemEqual(itemStack))
				furnaceItems[2].stackSize += itemStack.stackSize;
			
			furnaceItems[0].stackSize--;
			if(furnaceItems[0].stackSize <= 0)
				furnaceItems[0] = null;
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		return var1 == 0 ? sidedSlotBottom : (var1 == 1 ? sidedSlotTop : sidedSlotSides);
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return this.isStackValidForSlot(i, itemstack);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return i != 0 || i != 1 || itemstack.itemID == Item.bucketEmpty.itemID;
	}
	
    @Override
    public Packet getDescriptionPacket()
    {
        return PacketInt.getPacket(this);
    }

	
	
	
	
	
	
	
	
	
	
}
