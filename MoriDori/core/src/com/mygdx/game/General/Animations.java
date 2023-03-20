package com.mygdx.game.General;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Instance.Entity;

public class Animations {
	
	private Animation<TextureRegion> animationTrack;
	private SpriteBatch batch;
	private Sprite playerSprite;
	private Texture animationTexture;
	//Records the animation time
	private float animationStateTime	= 0f;
	//Offsets the animation image
	private Vector2 offsetSize	= new Vector2();
	private Vector2 offsetPos	= new Vector2();
	//Parent instance
	private Entity mainInstance;
	
	//Loads animation by separating sprite sheet into sections and stores them
	public void loadAnimation(Entity animationInstance, Texture animationSheet, int FRAME_COLS, int FRAME_ROWS){
		TextureRegion[][]tmp = TextureRegion.split(animationSheet, 
				animationSheet.getWidth() / FRAME_COLS, 
				animationSheet.getHeight() / FRAME_ROWS);
		TextureRegion animationFrames[]	= new TextureRegion[FRAME_COLS*FRAME_ROWS];
		mainInstance = animationInstance;
		int index = 0;
		for (int i = 0; i < FRAME_ROWS; i++) {
			for (int j = 0; j < FRAME_COLS; j++) {
				animationFrames[index++] = tmp[i][j];
			}
		}
		animationTexture	= animationSheet;
		animationTrack = new Animation<TextureRegion>(0.025f, animationFrames);
	}
	
	//Plays animation by drawing each frame after elapsed time
	public void play() {
		TextureRegion currentFrame = animationTrack.getKeyFrame(animationStateTime, true);
		animationStateTime += Gdx.graphics.getDeltaTime();
		playerSprite.setRegion(currentFrame);
		if (offsetSize.x > 0 || offsetSize.y > 0) {
			playerSprite.setSize(offsetSize.x, offsetSize.y);
		}
		playerSprite.setPosition((mainInstance.position.x + offsetPos.x), mainInstance.position.y + offsetPos.y);
		if (mainInstance.isFlipped()) {
			playerSprite.flip(true, false);
		} else if (playerSprite.isFlipX()) {
			playerSprite.flip(true, false);
		}
		playerSprite.draw(batch);
		playerSprite.setPosition(mainInstance.position.x, mainInstance.position.y);
	}
	
	//Returns if the animation is finished
	public boolean isAnimationFinished() {
		if (animationTrack.isAnimationFinished(animationStateTime)) {
			animationStateTime	= 0;
			return true;
		}
		return false;
	}
	
	//Offsets image size by x and y
	public void setOffsetSize(float x, float y) {
		this.offsetSize.set(x, y);
	}
	
	//Sets image offset position
	public void setOffsetPosition(float x, float y) {
		this.offsetPos.set(x, y);
	}
	
	public void dispose() {
		animationTexture.dispose();
	}
	
	public Animations(SpriteBatch batch, Sprite playerSprite) {
		this.batch	= batch;
		this.playerSprite = playerSprite;
	};
}
