package thutconcrete.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;

public class Volcano {
	
	
	public static Map<String, Volcano> volcanoMap = new HashMap<String, Volcano>();
	
	public static Volcano getVolcano(int x, int z)
	{
		if(!(volcanoMap.containsKey(Integer.toString(x)+Integer.toString(z))))
		{
			addVolcano(x,z);
		}
		return volcanoMap.get(Integer.toString(x)+Integer.toString(z));
	}
	
	public static boolean isOverAnyVolcano(int x, int z)
	{
		for(String s :volcanoMap.keySet())
		{
			Volcano v = volcanoMap.get(s);
			if(v!=null)
			{
				if(v.isOverThisVolcano(x, z))
					return true;
			}
		}
		return false;
	}
	

	public boolean isOverThisVolcano(int x, int z)
	{
		if(z<=this.z+1&&z>=this.z-1&&x<=this.x+1&&x>=this.x-1)
			return true;
		return false;
	}
	
	public static void addVolcano(int x, int z)
	{
		Random rX = new Random(x);
		Random rZ = new Random(z);
		Byte height = (byte) (rX.nextInt(30)+rZ.nextInt(30));
		int type = height>40?2:height>20?1:0;
		volcanoMap.put(Integer.toString(x)+Integer.toString(z), new Volcano(x,z,type, height));
	}

	public int x=0,y=5,z=0,type=10,h=0;
	public boolean active = true;
	public Volcano(int x, int z, int type, int h)
	{
		this.x = x;
		this.z=z;
		this.type=type;
		this.h=h;
	}
	
	public Volcano(){}
	
	public void writeToNBT(NBTTagCompound cmpnd, String tag)
	{
		cmpnd.setInteger(tag+"x",this.x);
		cmpnd.setInteger(tag+"y",this.y);
		cmpnd.setInteger(tag+"z",this.z);
		cmpnd.setInteger(tag+"type",this.type);
		cmpnd.setInteger(tag+"h",this.h);
	}

	public static Volcano readFromNBT(NBTTagCompound cmpnd, String tag)
	{
		Volcano ret = new Volcano();
		ret.x = cmpnd.getInteger(tag+"x");
		ret.y = cmpnd.getInteger(tag+"y");
		ret.z = cmpnd.getInteger(tag+"z");
		ret.type = cmpnd.getInteger(tag+"type");
		ret.h = cmpnd.getInteger(tag+"h");
		return ret;
	}
	
}
