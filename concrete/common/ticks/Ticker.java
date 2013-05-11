package concrete.common.ticks;


import java.util.List;
import java.util.Vector;

import concrete.common.ticks.*;

public abstract class Ticker{

	private static List<Ticker> instances = new Vector<Ticker>();


	public void setDead(){
		instances.remove(this);
	}

	public Ticker() {
		instances.add(this);
	}

	public abstract void onUpdate();

	public static void updateAll(){

		synchronized(instances){
			for(Ticker ticker : instances.toArray( new Ticker[instances.size()] ))
				ticker.onUpdate();
		}
	}
}
