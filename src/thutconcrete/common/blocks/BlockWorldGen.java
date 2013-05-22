package thutconcrete.common.blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import thutconcrete.common.ConcreteCore;
import thutconcrete.common.blocks.*;
import thutconcrete.common.utils.ExplosionCustom;
import thutconcrete.common.utils.ISaveable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BlockWorldGen extends Block
{

    @SideOnly(Side.CLIENT)
	public Icon[] iconArray;
    
    public static final String[] names = {
    										"Chalk",
									    	"Trass",
									    	"Limestone",
									    };

    public static BlockWorldGen instance;
    public static final int MAX_META = names.length;
    
	public BlockWorldGen(int par1) {
		super(par1, Material.rock);
		setUnlocalizedName("worldBlock");
		this.instance = this;
		this.setCreativeTab(ConcreteCore.tabThut);
		this.setResistance(10);
		this.setHardness(1);
	}
	
    //*
   
  //*/ 
   /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    
    @SideOnly(Side.CLIENT)

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(int par1, CreativeTabs par2CreativeTabs, List par3List)
    {
        for (int j = 0; j < MAX_META; j++)
        {
            par3List.add(new ItemStack(par1, 1, j));
        }
    }

    @SideOnly(Side.CLIENT)

    /**
     * When this method is called, your block should register all the icons it needs with the given IconRegister. This
     * is the only chance you get to register icons.
     */
    public void registerIcons(IconRegister par1IconRegister)
    {
        this.iconArray = new Icon[MAX_META];

        for (int i = 0; i < this.iconArray.length; i++)
        {
            this.iconArray[i] = par1IconRegister.registerIcon("thutconcrete:" + names[i]);
        }
    }
    
    protected ItemStack createStackedBlock(int par1)
    {
        return new ItemStack(this.blockID, 1, par1);
    }
   
    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int par1)
    {
        return par1;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random par1Random)
    {
        return 1;
    }
    
    /**
     * Returns the ID of the items to drop on destruction.
     */
    public int idDropped(int par1, Random par2Random, int par3)
    {
        return this.blockID;
    }
    

    @SideOnly(Side.CLIENT)
    /**
     * From the specified side and block metadata retrieves the blocks texture. Args: side, metadata
     */
    public Icon getIcon(int par1, int par2)
    {
        return this.iconArray[par2%MAX_META];
    }

    /**
     * Called when the player destroys a block with an item that can harvest it. (i, j, k) are the coordinates of the
     * block and l is the block's subtype/damage.
     */
    public void harvestBlock(World par1World, EntityPlayer par2EntityPlayer, int par3, int par4, int par5, int par6)
    {
        par2EntityPlayer.addStat(StatList.mineBlockStatArray[this.blockID], 1);
        par2EntityPlayer.addExhaustion(0.025F);

        ItemStack itemstack = this.createStackedBlock(par6);

        if (itemstack != null)
        {
            this.dropBlockAsItem_do(par1World, par3, par4, par5, itemstack);
        }
    }
    
    public String getUnlocalizedName(int par1){
    	return names[par1];
    }
    

    /**
     * Determines if the current block is replaceable by Ore veins during world generation.
     *
     * @param world The current world
     * @param x X Position
     * @param y Y Position
     * @param z Z Position
     * @param target The generic target block the gen is looking for, Standards define stone
     *      for overworld generation, and neatherack for the nether.
     * @return True to allow this block to be replaced by a ore
     */
    @Override
    public boolean isGenMineableReplaceable(World world, int x, int y, int z, int target)
    {
        return true;
    }
    
}
