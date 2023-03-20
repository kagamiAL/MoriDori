package com.mygdx.game.Instance;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Data;
import com.mygdx.game.GameLogic;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.General.Animations;
import com.mygdx.game.General.Utils;

import Events.Events;

class mobHitboxEvent implements Events	{
	
	Mob Mob;
	
	public void call() {
		Rectangle hitBox	= Mob.getHitbox();
		for (int x = 0; x < Data.players.size(); x++) {
			Entity enemyEntity	= Data.players.get(x);
			if (!Mob.entityIsAlreadyHit(enemyEntity) && hitBox.overlaps(enemyEntity.getBoundingBox()) && Utils.entityIsAttackable(Mob, enemyEntity)) {
				Mob.addEntityAlreadyHit(enemyEntity);
				enemyEntity.takeDamage(Mob.getAttackDamage(), .25f);
			}
		}
	}
	
	public mobHitboxEvent(Mob mainplayer) {
		Mob = mainplayer;
	}
	
}

public class Mob extends Entity {
	
	private Vector2 hitboxSize		= new Vector2();
	private	Vector2 hitboxOffset	= new Vector2();

	//Makes it so that the mob walks to the player
	private void walkToPlayer() {
		Player playerWalkTo	= MyGdxGame.mainPlayer;
		float walkDirection	= (playerWalkTo.position.x - position.x);
		if (walkDirection != 0) {
			walkDirection		= walkDirection/Math.abs(walkDirection);
			this.handleEntityRunning(walkDirection);
		}
	}
	
	//Returns the distance between the mob and player
	private double getMagnitudeToPlayer() {
		Player mainPlayer	= MyGdxGame.mainPlayer;
		return Math.sqrt(Math.pow((mainPlayer.position.x - position.x), 2) + Math.pow((mainPlayer.position.y - position.y), 2));
	}
	
	//Controls main flow of mob logic
	private void mainLogic() {
		if (getMagnitudeToPlayer() < hitboxSize.x) {
			handleAttacking();
			if (!stateClass.equals("Attack")) {
				changeToIdle();
			}
			return;
		}
		walkToPlayer();
	}
	
	public void dispose() {
		GameLogic.mobArray.remove(this);
		Data.disposeChild(this);
		super.dispose();
	}
	
	public Mob(SpriteBatch batch) {
		super(batch);
		//this.showOutline	= true;
		GameLogic.mobArray.add(this);
		Data.parentChild(this);
		connectToHitboxDetection(new mobHitboxEvent(this));
		// TODO Auto-generated constructor stub
	}
	
	//Called on every frame
	public void handleRendering() {
		if (getEntityState().equals("Dead")) {
			if (getAnimationTrack("Dead").isAnimationFinished()) {
				dispose();
				return;
			}
		} else {
			mainLogic();
		}
		super.handleRendering();
	}
	
	//Sets attack range + size
	public void setHitboxSize(float x, float y) {
		hitboxSize.set(x, y);
		super.setHitboxSize(x, y);
	}
	
	public void setHitboxOffset(float x, float y) {
		hitboxOffset.set(x, y);
		super.setHitboxOffset(x, y);
	}
	
	public static class Skeleton extends Mob {

		public Skeleton(SpriteBatch batch) {
			super(batch);
			final float X_OFFSET	= -115;
			final float Y_OFFSET	= -99;
			final float OFFSET_SIZE	= 300;
			this.ATTACK_DEBOUNCE = 1f;
			this.RUN_VELOCITY	= 100;
			Animations idleAnim	= loadAnimation("Idle", "Skeleton/Idle.png", 4, 1);
				idleAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
				idleAnim.setOffsetPosition(X_OFFSET, Y_OFFSET);
			Animations walkAnim	= loadAnimation("Run", "Skeleton/Walk.png", 4, 1);
				walkAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
				walkAnim.setOffsetPosition(X_OFFSET, Y_OFFSET);
			Animations attackAnim = loadAttackAnimation(1, "Skeleton/Attack.png", 8, 1);
				attackAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
				attackAnim.setOffsetPosition(X_OFFSET, Y_OFFSET);
			Animations attackAnim2 = loadAttackAnimation(2, "Skeleton/Attack2.png", 8, 1);
				attackAnim2.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
				attackAnim2.setOffsetPosition(X_OFFSET, Y_OFFSET);
			Animations stunAnim	= loadAnimation("Stun", "Skeleton/Take Hit.png", 4, 1);
				stunAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
				stunAnim.setOffsetPosition(X_OFFSET, Y_OFFSET);
			Animations deathAnim	= loadAnimation("Dead", "Skeleton/Death.png", 4, 1);
				deathAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
				deathAnim.setOffsetPosition(X_OFFSET, Y_OFFSET);
			setSize(75, 125);
			setHitboxSize(90, 125);
			setHitboxOffset(75, 0);
			// TODO Auto-generated constructor stub
		}
		
	}
	
	public static class Goblin extends Mob {

		public Goblin(SpriteBatch batch) {
			super(batch);
			final float X_OFFSET	= -115;
			final float Y_OFFSET	= -99;
			final float OFFSET_SIZE	= 300;
			this.ATTACK_DEBOUNCE = 0.5f;
			this.RUN_VELOCITY	= 150;
			this.setAttackDamage(5);
			this.setMaxHealth(this.getMaxHealth()/1.5f);
			Animations idleAnim	= loadAnimation("Idle", "Goblin/Idle.png", 4, 1);
				idleAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
				idleAnim.setOffsetPosition(X_OFFSET, Y_OFFSET);
			Animations walkAnim	= loadAnimation("Run", "Goblin/Run.png", 8, 1);
				walkAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
				walkAnim.setOffsetPosition(X_OFFSET, Y_OFFSET);
			Animations attackAnim = loadAttackAnimation(1, "Goblin/Attack.png", 8, 1);
				attackAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
				attackAnim.setOffsetPosition(X_OFFSET, Y_OFFSET);
			Animations attackAnim2 = loadAttackAnimation(2, "Goblin/Attack2.png", 8, 1);
				attackAnim2.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
				attackAnim2.setOffsetPosition(X_OFFSET, Y_OFFSET);
			Animations stunAnim	= loadAnimation("Stun", "Goblin/Take Hit.png", 4, 1);
				stunAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
				stunAnim.setOffsetPosition(X_OFFSET, Y_OFFSET);
			Animations deathAnim	= loadAnimation("Dead", "Goblin/Death.png", 4, 1);
				deathAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
				deathAnim.setOffsetPosition(X_OFFSET, Y_OFFSET);
			setSize(75, 125);
			setHitboxSize(90, 125);
			setHitboxOffset(75, 0);
			// TODO Auto-generated constructor stub
		}
		
	}
	
}