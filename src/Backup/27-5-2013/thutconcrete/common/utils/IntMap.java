package thutconcrete.common.utils;


public class IntMap {
	private int filledSlots;
	private IntList[] keys;
	private IntList[] values; 
	private float loadFactor;
	public IntMap(){
		filledSlots = 0;
		keys = new IntList[1024];
		values = new IntList[1024];
		loadFactor = 0.5f;
	}
	public IntMap(float loadFactor){
		this();
		this.loadFactor = loadFactor;
	}
	
	public IntMap(float loadFactor, int size){
		this();
		this.loadFactor = loadFactor;
		keys = new IntList[size];
		values = new IntList[size];
	}
	
	
	
	public void put(int key, int value){
		if (filledSlots>keys.length*loadFactor){
			IntList[] newKeys = new IntList[keys.length*2], newValues = new IntList[values.length*2];
			for(int i=0;i<values.length;i++){
				if (values[i]!=null){
					IntList currKeys = keys[i];
					IntList currValues = values[i];
					for(int j=0;j<currKeys.size();j++){
						actualPut(currKeys.get(j), currValues.get(j), newKeys, newValues);
					}	
				}
			}
			keys = newKeys;
			values = newValues;
		}
		filledSlots += actualPut(key, value, keys, values);
	}
	public int get(int key){
		int hash = hash(key);
		IntList currKeys = keys[hash];
		IntList currVals = values[hash];
		for(int i=0;i<currVals.size();i++){
			if (currKeys.get(i)==key){
				return currVals.get(i);
			}
		}
		throw new IllegalArgumentException("Tried to get value that wasn't there");
	}
	public boolean contains(int key){
		boolean ret = false;
		int hash = hash(key);
		IntList currKeys = keys[hash];
		IntList currVals = values[hash];
		if(currVals!=null)
		for(int i=0;i<currVals.size();i++){
			ret = ret || currKeys.get(i)==key;
		}
		return ret;
	}
	private int actualPut(int key, int value, IntList[] keyArr, IntList[] valArr){
		int hash = hash(key), pos = 0;
		IntList currKeys = keyArr[hash];
		IntList currVals = valArr[hash];
		if (currKeys==null){
			currKeys = new IntList();
			currVals = new IntList();
			keyArr[hash] = currKeys;
			valArr[hash] = currVals;
		}
		
		currKeys.add(key);
		currVals.add(value);
		return 1;
	}
	public int size(){
		return filledSlots;
	}
	private int hash(int h){
		h ^= (h >>> 20) ^ (h >>> 12);
	    return (h ^ (h >>> 7) ^ (h >>> 4))%keys.length;
	}
}
