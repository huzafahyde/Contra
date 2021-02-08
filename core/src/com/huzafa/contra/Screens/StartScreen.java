package com.huzafa.contra.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.huzafa.contra.GameClass;

public class StartScreen implements Screen, InputProcessor {

	private GameClass game;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	
	private Texture startTexture;
	
	private Music music;
	
	public StartScreen(GameClass game) {
		// TODO Auto-generated constructor stub
		this.game = game;
		
		camera = new OrthographicCamera();
		camera.setToOrtho(false, GameClass.V_WIDTH / GameClass.PPM, GameClass.V_HEIGHT / GameClass.PPM);
		viewport = new FitViewport(GameClass.V_WIDTH / GameClass.PPM, GameClass.V_HEIGHT / GameClass.PPM, camera);
		
		startTexture = new Texture("Misc/startImage.jpg");
		
		Gdx.input.setInputProcessor(this);
		
		music = Gdx.audio.newMusic(Gdx.files.internal("Music/Intro.mp3"));
		music.play();
		music.setLooping(true);
		
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.getBatch().setProjectionMatrix(camera.combined);
		game.getBatch().begin();
		
		game.getBatch().draw(startTexture, (GameClass.V_WIDTH / 2f - 125f) / GameClass.PPM, 0, 250f / GameClass.PPM, 250f / GameClass.PPM);
		
		game.getBatch().end();
		
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		viewport.update(width, height);
		camera.update();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		startTexture.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		music.stop();
		game.setScreen(new GameScreen(game));
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	

}
