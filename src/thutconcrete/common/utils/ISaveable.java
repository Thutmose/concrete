package thutconcrete.common.utils;

import net.minecraft.nbt.NBTTagCompound;

public interface ISaveable {
	abstract void save(NBTTagCompound par1NBTTagCompound);
	abstract void load(NBTTagCompound par1NBTTagCompound);
	abstract String getName();
}
