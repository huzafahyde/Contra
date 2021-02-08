package com.huzafa.contra.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.huzafa.contra.GameClass;

public class DieScreen implements Screen,InputProcessor {
	private GameClass game;
	
	private OrthographicCamera camera;
	private Viewport viewport;
	
	private Texture texture;
	
	
	public DieScreen(GameClass game) {
		// TODO Auto-generated constructor stub
		this.game = game;
	}


	@Override
	public void show() {
		// TODO Auto-generated method stub

		camera = new OrthographicCamera();
		camera.setToOrtho(false, GameClass.V_WIDTH, GameClass.V_HEIGHT);
		viewport = new FitViewport(GameClass.V_WIDTH, GameClass.V_HEIGHT, camera);
		
		Music music = Gdx.audio.newMusic(Gdx.files.internal("Music/changes.mp3"));
		music.setLooping(true);
		music.play();
		
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.getBatch().setProjectionMatrix(camera.combined);
		game.getBatch().begin();
		
		game.getFont128().draw(game.getBatch(), "YOU LOSE", GameClass.V_WIDTH / 2f - 50, GameClass.V_HEIGHT / 2f);
		
		game.getBatch().end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		camera.update();
		viewport.update(width, height);
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
		Gdx.app.exit();
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
