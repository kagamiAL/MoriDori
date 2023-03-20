package com.mygdx.game.Instance;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Instance {
	
	public Vector2 position 	= new Vector2();
	public Vector2 size			= new Vector2();
	public boolean canCollide	= true;
	//The 2D dimensions of the instance
	private Rectangle boundingBox	= new Rectangle();

	public void setSize(float width, float height) {
		boundingBox.setSize(width, height);
		this.size	= boundingBox.getSize(size);
	}
	
	public void setPosition(float x, float y) {
		boundingBox.setPosition(x, y);
		this.position	= boundingBox.getPosition(position);
	}
	
	public Rectangle getBoundingBox() {
		return boundingBox;
	}
	
	//Returns true if the instance is colliding with the passed Rectangle
	public boolean collidesWith(Rectangle hitbox) {
		return boundingBox.overlaps(hitbox);
	}
	
}
