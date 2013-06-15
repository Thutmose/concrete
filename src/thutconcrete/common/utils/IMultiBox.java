package thutconcrete.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.entity.Entity;

import thutconcrete.common.utils.Vector3.Matrix3;

public interface IMultiBox 
{
	
	public abstract void setBoxes();
	public abstract void setOffsets();

	public abstract ConcurrentHashMap<String, Matrix3> getBoxes();
	public abstract void addBox(String name, Matrix3 box);
	
	public abstract ConcurrentHashMap<String, Vector3> getOffsets();
	public abstract void addOffset(String name, Vector3 offset);
	
	
	public abstract void applyEntityCollision(Entity e);
	
	public abstract Matrix3 bounds(Vector3 target);
	
	/**

	public ConcurrentHashMap<String, Matrix3> boxes = new ConcurrentHashMap<String, Matrix3>();
	public ConcurrentHashMap<String, Vector3> offsets = new ConcurrentHashMap<String, Vector3>();
	
	@Override
	public ConcurrentHashMap<String, Matrix3> getBoxes() 
	{
		return boxes;
	}

	@Override
	public void addBox(String name, Matrix3 box) 
	{
		boxes.put(name, box);
	}

	@Override
	public ConcurrentHashMap<String, Vector3> getOffsets()
	{
		return offsets;
	}

	@Override
	public void addOffset(String name, Vector3 offset) 
	{
		offsets.put(name, offset);
	}

	 */
	
}
