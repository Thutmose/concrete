package thutconcrete.common.utils;

import thutconcrete.api.utils.Vector3;

public interface ITurretAI 
{
	public boolean powered();
	public Vector3 origin();
	public int rate();
	public int fireCooldown();
	public int notFired();
	public Vector3 source();
	
}
