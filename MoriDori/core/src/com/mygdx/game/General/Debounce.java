package com.mygdx.game.General;

//General debounce class
public class Debounce {

	private long cachedTime		= 0;
	private float timeDebounce	= 0;
	
	//Returns if the debounce class is on debounce
	public boolean isOnDebounce() {
		return ((System.currentTimeMillis() - cachedTime)/1000f < timeDebounce);
	}
	
	//Sets the debounce with the specified time
	public void setDebounce(float timeDebounce) {
		this.timeDebounce	= timeDebounce;
		this.cachedTime		= System.currentTimeMillis();
	}
	
}
