package thutconcrete.common.utils;


public class IntList {
	int size;
	int[] items;
	public IntList(){
		this(4);
	}
	public IntList(int startingSize){
		size = 0;
		items = new int[4];
	}
	public void add(int addToList){
		if (size==items.length){
			int[] newItems = new int[items.length*2];
			for(int i=0;i<items.length;i++){
				newItems[i] = items[i];
			}
			items = newItems;
		}
		items[size] = addToList;
		size++;
	}
	public int get(int pos){
		return items[pos];
	}
	public int size(){
		return size;
	}
	public void replace(int pos, int value) {
		items[pos] = value;
	}
}
