package com.mygdx.game.Instance;
import com.mygdx.game.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.General.Animations;
import com.mygdx.game.General.Debounce;

import Events.Events;

import java.util.ArrayList;
import java.util.HashMap;

public class Entity extends Instance {
	
	private static final float GRAVITY_ACCEL	= 450;
	private static final float JUMP_DEBOUNCE	= 1f;
	private static final float DASH_DEBOUNCE	= .5f;
	private static final float PARRY_DEBOUNCE	= .25f;
	private static final float DASH_TIME		= .5f;
	private static final float PARRY_TIMING		= .35f;
	private static final float PARRY_STUN		= 1f;
	
	public float ATTACK_DEBOUNCE	= 0.25f;
	public float RUN_VELOCITY		= 150;
	
	//Max health is the entity's max health
	private float maxHealth			= 100;
	private float entityHealth		= 100;
	//This is the Entity's hit box (How far/big their attack range is)
	private Rectangle attackBox		= new Rectangle();
		//The offset is how far the hit box should be from the entity's bounding box
		private Vector2 attackBoxOffset	= new Vector2();

	//IFrames are frames where the Entity cannot be attacked at all
	private boolean IFramed			= false;
	private boolean DashIFrame		= false;
	
	//Cached dash time is used to store the initial time when the dash started so that the time difference can be calculated
	private long cachedDashTime		= 0;
	//Last block time is used so that parrying can be allowed (If the enemy attacks the player when they just blocked (under 0.25 seconds) the enemy gets parried)
	private long lastBlockTime		= 0;
	//Used to cycle through attack animations
	private int comboIndex			= 1;
	private int maxComboIndex		= 0;
	//Stores animations by state name 
	private HashMap<String, Animations> animations = new HashMap<String, Animations>();
	//These are for debugging
	private Texture outlineImage	= new Texture(Gdx.files.internal("outlineBox.png"));
	private Texture hitboxOutline	= new Texture(Gdx.files.internal("outlineBox.png"));
	private SpriteBatch batch;
	private Sprite mainSprite		= new Sprite();
	//State class handles the entity state
	public State stateClass;
	//Handles entity stun as it is time based
	private Stun stunObject			= new Stun();
	//Used to store entities that have already been hit so that they aren't hit more than once per attack
	private ArrayList<Entity> entitiesAlreadyHit	= new ArrayList<Entity>();
	//Velocities append entity by the unit per frame
	private float velocityX			= 0;
	private float velocityY			= 0;
	//Is entity flipped
	private boolean flippedX		= false;
	
	//Used to emulate event based systems
	private Events onHitboxDetection;
	
	//Debounces are used to delay functions
	private Debounce jumpDebounce	= new Debounce();
	private Debounce attackDebounce	= new Debounce();
	private Debounce dashDebounce	= new Debounce();
	private Debounce parryDebounce	= new Debounce();

	private float attackDamage		= 10;
	
	public boolean grounded			= true;
	//Used for debugging
	public boolean showOutline		= false;
	
	//Flips entity sprite if the velocity is negative else keep normal
	private void flipWithVelocity(float velocityDirection) {
		if (flippedX) {
			if (velocityDirection > 0) {
				flippedX	= false;
			}
		} else if (velocityDirection < 0) {
			flippedX	= true;
		}
	}
	
	//Handles entity dash
	private void handleDashProcesses() {
		if ((System.currentTimeMillis() - cachedDashTime)/1000f >= DASH_TIME) {
			velocityX	= 0;
			stateClass.switchState("Idle");
			dashDebounce.setDebounce(DASH_DEBOUNCE);
		}
	}
	
	//Handles entity stun
	private void handleStunProcesses() {
		if (!stunObject.isStunned()) {
			stateClass.switchStunFinished((velocityX == 0) ? "Idle" : "Run");
		}
	}
	
	//Grounds player after they land
	private void groundPlayer() {
		if (!grounded) {
			jumpDebounce.setDebounce(JUMP_DEBOUNCE);
		}
		grounded	= true;
		velocityY	= 0;
		stateClass.switchState((velocityX == 0) ? "Idle" : "Run");
	}
	
	//Moves entity so it isn't colliding with a part
	private void moveEntityFromCollision(Instance collidablePart) {
		//Entity variables
		
		//Check colliding Y
		Vector2 collideSize	= collidablePart.size;
		Vector2 collidePos	= collidablePart.position;
		float sideY	= (position.y + size.y);
		float collideSideY		= (collidePos.y + collideSize.y);
		if (collideSideY >= position.y && position.y >= collidePos.y) {
			groundPlayer();
			setPosition(position.x, collideSideY);
		} else if (sideY >= collidePos.y && sideY <= collideSideY) {
			groundPlayer();
			setPosition(position.x, collidePos.y - size.y);
		}
	}
	
	//Handles moving the entity
	private void modifyPositionWithVelocity() {
		float delta	= Gdx.graphics.getDeltaTime();
		float deltaX	= velocityX * delta;
		float deltaY	= velocityY * delta;
		setPosition(position.x + deltaX, position.y + deltaY);
	}
	
	//Returns if entity is out of bounds
	private boolean outOfBounds() {
		if (position.x >= 800 || (position.x + size.x) <= 0) {
			return true;
		}
		return false;
	}
	
	//Returns true if entity is indeed on top of a platform
	private boolean entityIsOnPlatform() {
		if (outOfBounds()) {
			return true;
		}
		for (int x = 0; x < Data.collidableInstances.size(); x++) {
			Instance collideable	= Data.collidableInstances.get(x);
			float sideX	= position.x + size.x;
			if (position.x <= (collideable.position.x + collideable.size.x) && sideX >= collideable.position.x) {
				if (position.y == collideable.position.y + collideable.size.y) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	//Handles gravity
	private void simulateGravity() {
		if (stateClass.equals("Dead")) {return;}
		if (grounded) {
			if (entityIsOnPlatform()) {
				return;
			}
			grounded	= false;
		}
		float delta	= Gdx.graphics.getDeltaTime();
		velocityY -= (delta*GRAVITY_ACCEL);
		if (velocityY <= 0 && !stateClass.equals("Fall")) {
			stateClass.switchState("Fall");
		}
	}
	
	//Handles attack finish
	private void attackFinish() {
		entitiesAlreadyHit.clear();
		if (stateClass.getLastState().equals("Run")) {
			velocityX = (flippedX ? -1 : 1)*RUN_VELOCITY;
			stateClass.switchAttackFinished("Run");
			return;
		}
		stateClass.switchAttackFinished("Idle");
	}
	
	//Handles hit box detection
	private void detectEnemiesWithinHitbox() {
		attackBox.setPosition(position.x + (flippedX ? -1 : 1) * attackBoxOffset.x, position.y + attackBoxOffset.y);
		onHitboxDetection.call();
		if (showOutline) {
			batch.draw(hitboxOutline, attackBox.x, attackBox.y, attackBox.width, attackBox.height);
		}
	}
	
	//Handles hit detection + state 
	private void processAttack() {
		Animations animationPlay	= animations.get("Attack" + comboIndex);
		animationPlay.play();
		detectEnemiesWithinHitbox();
		if (animationPlay.isAnimationFinished()) {
			attackFinish();
			comboIndex++;
			if (comboIndex > maxComboIndex) {
				attackDebounce.setDebounce(ATTACK_DEBOUNCE);
				comboIndex = 1;
			}
		}
	}

	//Handles state logic after animation player
	private void stateLogicAnimationPlay() {
		stateClass.removeIFrameFromDash();
		if (stateClass.equals("Stun")) {
			handleStunProcesses();
			return;
		}
		if (stateClass.equals("Dash")) {
			handleDashProcesses();
			return;
		}
	}
	
	//Damages entity
	public void takeDamage(float dmgAmount, float stunTime) {
		if (isIFramed()) {return;}
		velocityX = 0;
		entityHealth -= dmgAmount;
		if (entityHealth <= 0) {
			stateClass.switchState("Dead");
			return;
		}
		stunObject.stunEntity(stunTime);
		stateClass.switchState("Stun");
	}
	
	//Returns velocity Y
	public float getVelocityY() {
		return velocityY;
	}
	
	//Handles rendering player character
	public void handleRendering() {
		String entityState	= stateClass.getState();
		modifyPositionWithVelocity();
		simulateGravity();
		handleCollisions();
		if (entityState.equals("Attack")) {
			processAttack();
		} else {
			Animations animationPlay	= animations.get(stateClass.getState());
			if (animationPlay != null) {
				animationPlay.play();	
			}
			stateLogicAnimationPlay();
		}
		if (showOutline) {
			batch.draw(outlineImage, position.x, position.y, size.x, size.y);
		}
		return; 
	}
	
	//Makes the entity attack
	public void handleAttacking() {
		if (stateClass.equals("Attack") || stateClass.equals("Stun") || attackDebounce.isOnDebounce()) {return;}
		entitiesAlreadyHit.clear();
		if (grounded) {
			velocityX = 0;
		}
		stateClass.switchState("Attack");
	}
	
	//Makes entity jump
	public void jump(float velocityY) {
		if (!grounded || stateClass.equals("Stun") || jumpDebounce.isOnDebounce()) {return;}
		this.velocityY	= velocityY;
		grounded		= false;
		stateClass.switchState("Jump");
	}
	
	//Handles walking
	public void handleEntityRunning(float velocityDirection) {
		if (stateClass.equals("Stun") || stateClass.equals("Dash")) {return;}
		stateClass.switchState("Run");
		flipWithVelocity(velocityDirection);
		velocityX	= velocityDirection * RUN_VELOCITY;
	}
	
	//Stops walking
	public void stopEntityRunning() {
		if (stateClass.equals("Dash")) {return;}
		stateClass.switchState("Idle");
		velocityX	= 0;
	}
	
	//Returns the entity's state
	public String getEntityState() {
		return stateClass.getState();
	}
	
	public boolean isGrounded() {
		return grounded;
	}
	
	public boolean isFlipped() {
		return flippedX;
	}
	
	public void dispose() {
		for (Animations value : animations.values()) {
			value.dispose();
		}
		outlineImage.dispose();
		hitboxOutline.dispose();
	}
	
	//Sets the size of the entity's attack box
	public void setHitboxSize(float width, float height) {
		attackBox.setSize(width, height);
	}
	
	//Sets the offset of the entity's attack box
	public void setHitboxOffset(float x, float y) {
		attackBoxOffset.set(x, y);
	}
	
	public void setSize(float width, float height) {
		super.setSize(width, height);
		mainSprite.setSize(width, height);
	}
	
	public void setPosition(float x, float y) {
		super.setPosition(x, y);
		mainSprite.setPosition(x, y);
	}
	
	public void setAttackDamage(float newDamage) {
		this.attackDamage	= newDamage;
	}
	
	public float getAttackDamage() {
		return attackDamage;
	}
	
	public Rectangle getHitbox() {
		return attackBox;
	}
	
	//Detects if entity is colliding with anything, if so reposition so it isn't colliding
	public void handleCollisions() {
		ArrayList<Instance> collidableParts	= Data.collidableInstances;
		for (int x = 0; x < collidableParts.size(); x++) {
			Instance partCollide	= collidableParts.get(x);
			if (partCollide != null && partCollide.collidesWith(getBoundingBox())) {
				moveEntityFromCollision(partCollide);
			}
		}
	}
	
	//Connects an event to the Hit box function
	public void connectToHitboxDetection(Events hitboxEvent) {
		this.onHitboxDetection	= hitboxEvent;
	}
	
	//Loads an animation from a sprite sheet assigns a state name to the animation
	public Animations loadAnimation(String animationStateName, String fileName, int columns, int rows) {
		Animations idleAnimation	= new Animations(batch, mainSprite);
		idleAnimation.loadAnimation(this, new Texture(Gdx.files.internal(fileName)), columns, rows);
		animations.put(animationStateName, idleAnimation);
		return idleAnimation;
	}
	
	//Loads an attack animation (combo chain)
	public Animations loadAttackAnimation(int chainNumber, String fileName, int columns, int rows) {
		Animations idleAnimation	= new Animations(batch, mainSprite);
		idleAnimation.loadAnimation(this, new Texture(Gdx.files.internal(fileName)), columns, rows);
		animations.put("Attack" + chainNumber, idleAnimation);
		maxComboIndex++;
		return idleAnimation;
	}
	
	//Returns animation track
	public Animations getAnimationTrack(String stateName) {
		return animations.get(stateName);
	}
	
	//Checks if an entity is already hit
	public boolean entityIsAlreadyHit(Entity enemyChar) {
		return entitiesAlreadyHit.contains(enemyChar);
	}
	
	//Adds entity to the entities already hit list
	public void addEntityAlreadyHit(Entity enemyChar) {
		entitiesAlreadyHit.add(enemyChar);
	}
	
	//Stops entity from moving and makes them into idle state
	public void changeToIdle() {
		velocityX	= 0;
		stateClass.switchState("Idle");
	}
	
	//Stuns entity for a specified period of time
	public void stunEntityWithTime(float stunTime) {
		stunObject.stunEntity(stunTime);
		stateClass.switchState("Stun");
	}
	
	public float getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(float maxHealth) {
		this.maxHealth = maxHealth;
		this.entityHealth	= maxHealth;
	}

	//Makes entity Dash
	public void handleDashing(float dashVelocity) {
		if (dashDebounce.isOnDebounce() || stateClass.equals("Stun")) {return;}
		setDashIFrame(true);
		stateClass.switchState("Dash");
		cachedDashTime	= System.currentTimeMillis();
		flipWithVelocity(dashVelocity);
		velocityX	= dashVelocity;
	}
	
	//Makes entity block
	public void handleBlocking(boolean isBlocking) {
		if (stateClass.equals("Block")) {
			if (!isBlocking) {
				parryDebounce.setDebounce(PARRY_DEBOUNCE);
				stateClass.switchBlockFinished("Idle");
			}
			velocityX	= 0;
			return;
		}
		if (isBlocking && !parryDebounce.isOnDebounce()) {
			stateClass.switchState("Block");
			velocityX	= 0;
			lastBlockTime	= System.currentTimeMillis();
			return;
		}
	}
	
	public boolean isIFramed() {
		return (IFramed || DashIFrame);
	}

	public void setDashIFrame(boolean isIframed) {
		DashIFrame = isIframed;
	}
	
	public void setIFramed(boolean isIframed) {
		IFramed = isIframed;
	}

	//Returns if the entity has parried another entity
	public void parriedEnemy(Entity enemyEntity) {
		if ((System.currentTimeMillis() - lastBlockTime)/1000f <= PARRY_TIMING) {
			enemyEntity.stunEntityWithTime(PARRY_STUN);
			return;
		}
		return;
	}
	
	public Entity(SpriteBatch batch) {	
		this.stateClass	= new State(this);
		this.batch	= batch;
	}
	
}
