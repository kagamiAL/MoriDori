package com.mygdx.game.Instance;

public class Stun {

	//How long stun is
	private float stunDuration	= 0f;
	//Stored time so that time difference can be calculated
	private long cachedTime		= 0;
	
	//Returns if entity stunned (time difference >= stunDuration)
	public boolean isStunned() {
		return ((System.currentTimeMillis() - cachedTime)/1000f < stunDuration);
	}
	
	//Begins stun, caches time/sets stun duration
	public void stunEntity(float stunDur) {
		stunDuration	= stunDur;
		cachedTime		= System.currentTimeMillis();
	}
	
}
