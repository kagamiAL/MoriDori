package com.mygdx.game.Instance;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.Data;
import com.mygdx.game.General.Animations;
import com.mygdx.game.General.Utils;
import Events.Events;

class playerHitboxEvent implements Events	{
	
	Player Player;
	
	public void call() {
		Rectangle hitBox	= Player.getHitbox();
		for (int x = 0; x < Data.mobs.size(); x++) {
			Entity enemyEntity	= Data.mobs.get(x);
			if (!Player.entityIsAlreadyHit(enemyEntity) && hitBox.overlaps(enemyEntity.getBoundingBox()) && Utils.entityIsAttackable(Player, enemyEntity)) {
				Player.addEntityAlreadyHit(enemyEntity);
				enemyEntity.takeDamage(Player.getAttackDamage(), .25f);
			}
		}
	}
	
	public playerHitboxEvent(Player mainplayer) {
		Player = mainplayer;
	}
	
}

public class Player extends Entity {
	
	//How fast the player jumps
	private static final float JUMP_VELOCITY = 450;
	
	private boolean playerIsDead	= false;
	
	//Last key pressed
	private int lastKey;
	
	private long lastKeyPressedTime	= 0;
			
	//Detects user input for jumping
	private void detectIsUserJump() {
		if (!Gdx.input.isKeyJustPressed(Input.Keys.W)) {return;}
		jump(JUMP_VELOCITY);
	}
	
	private void detectIsUserAttack() {
		if (Gdx.input.isTouched()) {
			this.handleAttacking();
		}
	}
	
	//Detects if player has tapped the A or D key twice in under 0.25 seconds to enable dash
	private void userDashEnable(int keyTouched) {
		if (keyTouched == lastKey && (System.currentTimeMillis() - lastKeyPressedTime)/1000f < .5f) {
			int multiplier	= (keyTouched == Input.Keys.D ? 1 : -1);
			this.handleDashing(multiplier*this.RUN_VELOCITY*2.5f);
			return;
		}
		lastKey	= keyTouched;
		lastKeyPressedTime	= System.currentTimeMillis();
	}
	
	private void detectIsUserRunning() {
		String currentState	= getEntityState();
		if (currentState.equals("Action")) {return;}
		float velocityX	= 0;
		boolean keysDown	= false;
		if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.A)){
			keysDown	= true;
			if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
				velocityX	= 1;
				userDashEnable(Input.Keys.D);
			} else if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
				velocityX	= -1;
				userDashEnable(Input.Keys.A);
			} 
		} 
		if (keysDown) {
			if (velocityX != 0) {
				handleEntityRunning(velocityX);
			}
			return;
		}
		stopEntityRunning();
	}

	private void detectIsUserBlocking() {
		if (Gdx.input.isKeyPressed(Input.Keys.S)) {
			handleBlocking(true);
			return;
		}
		handleBlocking(false);
	}
	
	//Loads animation sprites
	private void loadAnimations() {
		final float OFFSET_SIZE	= 225;
		final float X_OFFSET	= -65;
		Animations idleAnim	= loadAnimation("Idle", "_Idle.png", 10, 1);
		idleAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
		idleAnim.setOffsetPosition(X_OFFSET, 0);
		Animations walkAnim	= loadAnimation("Run", "_Run.png", 10, 1);
		walkAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
		walkAnim.setOffsetPosition(X_OFFSET, 0);
		Animations jumpAnim	= loadAnimation("Jump", "_Jump.png", 3, 1);
		jumpAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
		jumpAnim.setOffsetPosition(X_OFFSET, 0);
		Animations fallAnim	= loadAnimation("Fall", "_Fall.png", 3, 1);
		fallAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
		fallAnim.setOffsetPosition(X_OFFSET, 0);
		Animations stunAnim	= loadAnimation("Stun", "_Hit.png", 1, 1);
		stunAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
		stunAnim.setOffsetPosition(X_OFFSET, 0);
		Animations deathAnim	= loadAnimation("Dead", "_Death.png", 10, 1);
		deathAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
		deathAnim.setOffsetPosition(X_OFFSET, 0);
		Animations dashAnim		= loadAnimation("Dash", "_Dash.png", 2, 1);
		dashAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
		dashAnim.setOffsetPosition(X_OFFSET, 0);
		Animations blockAnim		= loadAnimation("Block", "_Crouch.png", 1, 1);
		blockAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
		blockAnim.setOffsetPosition(X_OFFSET, 0);
		Animations attackAnim = loadAttackAnimation(1, "_Attack.png", 4, 1);
		attackAnim.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
		attackAnim.setOffsetPosition(X_OFFSET, 0);
		Animations attackAnim2 = loadAttackAnimation(2, "_Attack2.png", 6, 1);
		attackAnim2.setOffsetSize(OFFSET_SIZE, OFFSET_SIZE);
		attackAnim2.setOffsetPosition(X_OFFSET, 0);
	}
	
	//This gets called on every frame
	public void handleRendering() {
		if (playerIsDead) {return;}
		if (this.getEntityState().equals("Dead")) {
			if (getAnimationTrack("Dead").isAnimationFinished()) {
				dispose();
				return;
			}
		} else {
			detectIsUserJump();
			detectIsUserRunning();
			detectIsUserBlocking();
			detectIsUserAttack();
		}
		super.handleRendering();
	}
	
	//This gets called when the application is closed
	public void dispose() {
		playerIsDead	= true;
		super.dispose();
		Data.disposeChild(this);
	}
	
	public Player(SpriteBatch batch) {
		super(batch);
		loadAnimations();
		Data.parentChild(this);
		//this.showOutline	= true;
		this.ATTACK_DEBOUNCE	= 0;
		this.RUN_VELOCITY		= 250;
		setAttackDamage(20);
		setHitboxSize(90, 125);
		setHitboxOffset(75, 0);
		setSize(75, 125);
		setPosition(400, 0);
		connectToHitboxDetection(new playerHitboxEvent(this));
	}
	
}
