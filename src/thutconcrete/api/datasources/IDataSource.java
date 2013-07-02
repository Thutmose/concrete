package thutconcrete.api.datasources;

public interface IDataSource 
{
	
	/**
	 * this is called to change whatever data your source produces. The time is usually the age of the data recorder in ticks.
	 * This can be left blank if your data source gathers data by itself.
	 * @param time
	 */
	public void takeData(long time);
	
	/**
	 * Returns your data as a Double[].
	 * @return
	 */
	public Double[] getValues();
	
	/**
	 * This can be used to set your data via some external means if needed.  This can be left blank
	 * @param data
	 */
	
	public void setData(Double[] data);
	
	/**
	 * This should reset whatever you use for storing the data.
	 */
	public void clearData();
	
	/**
	 *  If your device does anything special based on these, this is used to synchronize the display and the sensors
	 *  This can just be left blank for a sensor that does nothing besides give data.
	 * @param yCoef
	 * @param yExponent
	 * @param rate
	 * @param time
	 */
	public void setScales(double yCoef, int yExponent, int rate, long time);
	
	/**
	 * If your sensor keeps track of its own time, this can be used to synchronize it with the display, this is not
	 * always needed, and can be left blank
	 * @param time
	 */
	public void syncTime(long time);
	
	
	/**
	 * The display will emit a redstone signal out the bottom if this returns true.
	 * @return
	 */
	public boolean isDataOutOfBounds();
	
	/**
	 * 
	 * @return the maximum number of values, used for max value on graph.
	 */
	public int maxValues();
	
	
	/**
	 * The id here is a unique ID used to identify the source.  It is also the key to find the source in the sourceMap in DataSources.
	 * @param id
	 */
	public void setID(int id);
	public int getID();
	
}
