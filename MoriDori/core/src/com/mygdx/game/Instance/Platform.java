package com.mygdx.game.Instance;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Data;

public class Platform extends Instance {

	private Texture platformImage;
	
	public void dispose() {
		platformImage.dispose();
		Data.disposeChild(this);
	}
	
	//Called on every frame
	public void render(SpriteBatch batch) {
		batch.draw(platformImage, position.x, position.y, size.x, size.y);
	}
	
	public Platform(String imageName) {
		Data.parentChild(this);
		platformImage = new Texture(imageName);
	}
	
}
