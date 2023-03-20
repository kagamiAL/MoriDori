package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.Instance.Platform;
import com.mygdx.game.Instance.Player;

public class MyGdxGame extends ApplicationAdapter {
	public static SpriteBatch batch;
	public static Player mainPlayer;
	Texture backgroundImage;
	Platform[] mainPlatform;
	private OrthographicCamera camera;
	//Used to load in assets
	AssetManager manager = new AssetManager();

	
	//This queues the assets that will be loaded into the game
	private void queueAssetsToLoad() {
		manager.load("_Attack.png", Texture.class);
		manager.load("_Attack2.png", Texture.class);
		manager.load("_Crouch.png", Texture.class);
		manager.load("_Dash.png", Texture.class);
		manager.load("_Death.png", Texture.class);
		manager.load("_Fall.png", Texture.class);
		manager.load("_Hit.png", Texture.class);
		manager.load("_Idle.png", Texture.class);
		manager.load("_Jump.png", Texture.class);
		manager.load("_Roll.png", Texture.class);
		manager.load("_Run.png", Texture.class);
		manager.load("backgroundForest.jpg", Texture.class);
		manager.load("outlineBox.png", Texture.class);
		manager.load("PLATFORM.jpg", Texture.class);
		manager.load("Skeleton/Attack.png", Texture.class);
		manager.load("Skeleton/Attack2.png", Texture.class);
		manager.load("Skeleton/Death.png", Texture.class);
		manager.load("Skeleton/Idle.png", Texture.class);
		manager.load("Skeleton/Shield.png", Texture.class);
		manager.load("Skeleton/Take Hit.png", Texture.class);
		manager.load("Skeleton/Walk.png", Texture.class);
		manager.load("Goblin/Attack.png", Texture.class);
		manager.load("Goblin/Attack2.png", Texture.class);
		manager.load("Goblin/Death.png", Texture.class);
		manager.load("Goblin/Idle.png", Texture.class);
		manager.load("Goblin/Take Hit.png", Texture.class);
		manager.load("Goblin/Run.png", Texture.class);
	}
	
	//Creates the platforms that will be in the game
	private void createPlatforms() {
		Platform platformOne	= new Platform("PLATFORM.jpg");
		platformOne.setSize(800, 100);
		platformOne.setPosition(0, 0);
		Platform platformTwo	= new Platform("PLATFORM.jpg");
		platformTwo.setSize(200, 50);
		platformTwo.setPosition(300, 250);
		Platform[] platformArray	= {platformOne, platformTwo};
		mainPlatform	= platformArray;

	}
	
	//Called on application open
	@Override
	public void create () {
		//Creating camera
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		//Batch draws images to screen
		batch = new SpriteBatch();
		//The user class
		mainPlayer		= new Player(batch);
		backgroundImage = new Texture("backgroundForest.jpg");
		//Platforms
		createPlatforms();
		queueAssetsToLoad();
		GameLogic.setUp();
	}

	//Called on each frame
	@Override
	public void render () {
		if (manager.update()) {
			ScreenUtils.clear(1, 0, 0, 1);
			camera.update();
			batch.begin();
			batch.draw(backgroundImage, 0, 0, 800, 480);
			for (int x = 0; x < mainPlatform.length; x++) {
				mainPlatform[x].render(batch);
			}
			GameLogic.onRender();
			mainPlayer.handleRendering();
			batch.end();
		}
	}
	
	//Called on application close
	@Override
	public void dispose () {
		batch.dispose();
		mainPlayer.dispose();
		backgroundImage.dispose();
		for (int x = 0; x < mainPlatform.length; x++) {
			mainPlatform[x].dispose();
		}
	}
}
