package com.huzafa.contra;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.huzafa.contra.Screens.DieScreen;
import com.huzafa.contra.Screens.GameScreen;
import com.huzafa.contra.Screens.StartScreen;
import com.huzafa.contra.Screens.TransitScreen;

public class GameClass extends Game {
	public static final float PPM = 100f;
	public static final int V_WIDTH = 480;
	public static final int V_HEIGHT = 250;
	
	private SpriteBatch batch;
	private BitmapFont font128;
	private World world;
	private Preferences preferences;
	
	public static final short PLAYER_ENTITY = 0x0001;
	public static final short GROUNDCONTACT_ENTITY = 0x0002;
	public static final short WORLD_ENTITY = 0x0004;
	public static final short ENEMY_ENTITY = 0x0008;
	public static final short BULLET_ENTITY = 0x0016;
	public static final short ENEMY_BULLET_ENTITY = 0x0032;
	public static final short NULL_ENTITY = 0x0064;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		generateFonts();
		
		world = new World(new Vector2(0, -98f / GameClass.PPM), true);
		
		preferences = Gdx.app.getPreferences("High Scores");
		
		//setScreen(new TransitScreen(this, 8000, 250));
		//setScreen(new GameScreen(this));
		setScreen(new StartScreen(this));
		//setScreen(new DieScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		font128.dispose();
		world.dispose();
	}
	
	private void generateFonts() {
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Anke.otf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 16;
		parameter.color = Color.WHITE;
		
		font128 = generator.generateFont(parameter);
		generator.dispose();
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public void setBatch(SpriteBatch batch) {
		this.batch = batch;
	}

	public BitmapFont getFont128() {
		return font128;
	}

	public void setFont128(BitmapFont font128) {
		this.font128 = font128;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}
}
