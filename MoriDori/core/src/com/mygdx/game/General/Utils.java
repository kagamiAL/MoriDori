package com.mygdx.game.General;

import com.mygdx.game.Instance.Entity;

public class Utils {

	//Returns true if the entity can be attacked, also checks for parrying
	public static boolean entityIsAttackable(Entity attacker, Entity entityAttacked) {
		if (entityAttacked.stateClass.equals("Block")) {
			entityAttacked.parriedEnemy(attacker);
			return false;
		}
		return true;
	}
	
}
