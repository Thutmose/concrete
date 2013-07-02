package thutconcrete.api.datasources;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * You will probably want to import these to your IDataSource as static imports via 
 * import static thutconcrete.api.datasources.DataSources.*;
 * 
 * 
 * @author Thutmose
 */

public class DataSources 
{
	/**
	 *  when you read from NBT do this: DataSources.MAXID = Math.max(DataSources.MAXID, par1.getInteger("maxID"));
	 *  when you write to NBT do this: 	par1.setInteger("maxID", DataSources.MAXID);
	 *  
	 *  Your IDataSource should set its ID to this, then increment this value when it is first made.
	 *  
	 *  Your IDataSource should also save/load own ID and keep this synchronized between client and server.
	 */
	
	public static int MAXID = 0;
	
	/**
	 * This is a map of id to IDataSource.  whenever your IDataSource sets ID it should add itself to this map.
	 */
	public static Map<Integer, IDataSource> sourceMap = new ConcurrentHashMap<Integer, IDataSource>();
	
}
