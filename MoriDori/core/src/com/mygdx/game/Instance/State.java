package com.mygdx.game.Instance;


public class State {
	
	private static final String[] PRIORITY_STATES	= {
			"Dead",
			"Stun",
			"Block",
			"null",
	};
	
	//Parent entity class
	private Entity entityParent;
	private String state		= "Idle";
	private String lastState	= "Idle";
	
	
	public String getState() {
		return state;
	}
	
	public String getLastState() {
		return lastState;
	}
	
	//Returns true if the state is in PRIORITY_STATES
	private boolean isPriority(String newState) {
		for (int x = 0; x < PRIORITY_STATES.length; x++) {
			if (PRIORITY_STATES[x].equals(newState)) {
				return true;
			}
		}
		return false;
	}
	
	//Checks if entity Dash IFrame = true and Entity is dashing
	public void removeIFrameFromDash() {
		if (!state.equals("Dash")) {
			entityParent.setDashIFrame(false);
		}
	}
	
	//Changes state
	public void switchState(String newString) {
		if (!isPriority(newString)) {
			if (state.equals("Attack") || state.equals("Stun") || state.equals("Block")) {return;}
			if (newString.equals("Idle") || newString.equals("Run")) {
				if (state.equals("Jump")) {
					return;
				}
				if (state.equals("Fall") && entityParent.getVelocityY() < 0) {
					return;
				}
			}
		}
		lastState	= state;
		state	= newString;
	}
	
	//Switches state from certain states
	public void switchAttackFinished(String newString) {
		if (state.equals("Attack")) {
			lastState	= state;
			state	= newString;
		}
	}
	
	public void switchStunFinished(String newString) {
		if (state.equals("Stun")) {
			lastState	= state;
			state	= newString;
		}
	}
	
	public void switchBlockFinished(String newString) {
		if (state.equals("Block")) {
			lastState	= state;
			state	= newString;
		}
	}
	
	//Returns if the state equals the passed string
	public boolean equals(String mainString) {
		return state.equals(mainString);
	}
	
	public State(Entity mainEntity) {
		entityParent	= mainEntity;
	}
	
}
