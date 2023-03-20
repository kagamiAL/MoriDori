package com.mygdx.game;

import java.util.ArrayList;

import com.mygdx.game.Instance.Mob;
import com.mygdx.game.Instance.Mob.*;
import com.mygdx.game.Instance.Player;

public class GameLogic {
	
	//Will be floored currentWave * 0.5
	private static final double AMT_SKELETONS	= 0.5;
	
	private static final double AMT_GOBLINS		= 1;
	
	private static int currentWave	= 1;
	
	public static Player mainPlayer;
	
	public static ArrayList<Mob> mobArray = new ArrayList<Mob>();
	
	//Spawns in mobs based on wave number
	public static void setUp() {
		mainPlayer	= MyGdxGame.mainPlayer;
		for (int x = 0; x < Math.floor(AMT_SKELETONS*currentWave); x++) {
			float separationDist	= (75* (x + 1));
			(new Skeleton(MyGdxGame.batch)).setPosition((x % 2 == 0 ? -separationDist : 700 + separationDist), 0);
		}
		for (int x = 0; x < AMT_GOBLINS*currentWave; x++) {
			float separationDist	= (75* (x + 1));
			(new Goblin(MyGdxGame.batch)).setPosition((x % 2 == 0 ? -separationDist : 700 + separationDist), 0);
		}
	}
	
	//Iterates through mobs and calls their render function
	public static void onRender() {
		for (int x = 0; x < mobArray.size(); x++) {
			mobArray.get(x).handleRendering();
		}
		if (mobArray.size() == 0) {
			currentWave += 1;
			mainPlayer.setMaxHealth(mainPlayer.getMaxHealth()*1.1f);
			setUp();
		}
	}
	
}
