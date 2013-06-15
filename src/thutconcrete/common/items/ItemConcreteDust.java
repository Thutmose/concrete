package thutconcrete.common.items;

import static net.minecraftforge.common.ForgeDirection.DOWN;
import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.SOUTH;
import static net.minecraftforge.common.ForgeDirection.UP;
import static net.minecraftforge.common.ForgeDirection.WEST;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.BlockDust;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class ItemConcreteDust extends Item {
	
	public static Item instance;
	
	public ItemConcreteDust(int par1) {
		super(par1);
		this.maxStackSize = 64;
		this.setCreativeTab(ConcreteCore.tabThut);
		this.setUnlocalizedName("dustConcrete");
		instance = this;
	}

	@SideOnly(Side.CLIENT)
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.itemIcon = par1IconRegister.registerIcon("thutconcrete:ConcreteDust");
    }
	
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
    	if(!world.isRemote)
    	{
    		int x1 = ForgeDirection.getOrientation(side).offsetX+x,y1 = ForgeDirection.getOrientation(side).offsetY+y, z1 = ForgeDirection.getOrientation(side).offsetZ+z;
    		int id = world.getBlockId(x1, y1, z1);
    		int meta = world.getBlockMetadata(x1, y1, z1);
    		Block block = Block.blocksList[id];
    		
            if (!player.isSneaking()&&ItemDye.applyBonemeal(stack, world, x, y, z, player))
            {
                if (!world.isRemote)
                {
                    world.playAuxSFX(2005, x, y, z, 0);
                }

                return true;
            }
    		
    	//	System.out.println("side "+id);
    		if(block instanceof BlockDust&&meta!=0)
    		{
    			world.setBlockMetadataWithNotify(x1, y1, z1, meta-1, 3);
    			stack.splitStack(1);
    		}
    		else if (id==0||world.getBlockMaterial(x1, y1, z1).isReplaceable())
    		{
    			world.setBlock(x1, y1, z1, BlockDust.instance.blockID, Math.min(stack.stackSize-16, 0), 3);
    			stack.splitStack(Math.min(stack.stackSize, 16));
    		}
    	}
    	return false;
    }
    
}
