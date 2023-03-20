package com.mygdx.game;
import java.util.ArrayList;
import com.mygdx.game.Instance.Entity;
import com.mygdx.game.Instance.Instance;
import com.mygdx.game.Instance.Mob;
import com.mygdx.game.Instance.Platform;
import com.mygdx.game.Instance.Player;

public final class Data {
	
	//This is where collideable instances will be stored
	public static final ArrayList<Instance> collidableInstances = new ArrayList<Instance>();
	
	public static final ArrayList<Entity> mobs			= new ArrayList<Entity>();
	
	public static final ArrayList<Entity> players		= new ArrayList<Entity>();
	
	//Parents passed class into appropriate array list
	public static void parentChild(Platform child) {
		collidableInstances.add(child);
	}
	//Removes child from its array list
	public static void disposeChild(Platform child) {
		collidableInstances.remove(child);
	}
	
	public static void parentChild(Player child) {
		players.add(child);
	}

	public static void disposeChild(Player child) {
		players.remove(child);
	}
	
	public static void parentChild(Mob child) {
		mobs.add(child);
	}
	
	public static void disposeChild(Mob child) {
		mobs.remove(child);
	}
	
}
