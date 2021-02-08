package com.huzafa.contra.Screens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.huzafa.contra.GameClass;

public class TransitScreen implements Screen, TextInputListener {
	private GameClass game;
	private float points;
	private float pointTimer;

	private String name;

	private OrthographicCamera camera;
	private Viewport viewport;

	private boolean hasTyped, writtenFlag;

	private TextInputListener listener = this;
	
	private HashMap<String, Float> sorted;

	public TransitScreen(GameClass game, float points, float pointTimer) {
		// TODO Auto-generated constructor stub
		this.game = game;
		this.points = points;
		this.pointTimer = pointTimer;
		
		Music music = Gdx.audio.newMusic(Gdx.files.internal("Music/Completion.mp3"));
		music.play();
		music.setLooping(true);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

		camera = new OrthographicCamera();
		camera.setToOrtho(false, GameClass.V_WIDTH, GameClass.V_HEIGHT);
		viewport = new FitViewport(GameClass.V_WIDTH, GameClass.V_HEIGHT, camera);

		name = new String();

		HashMap<String, Float> tempMap = new HashMap<String, Float>();
		for (Map.Entry entry : game.getPreferences().get().entrySet()) {
			String key = (String) entry.getKey();
			Float value = Float.parseFloat((String) entry.getValue());

			tempMap.put(key, value);
		}
		
		sorted = sort(tempMap);
		
		Gdx.input.getTextInput(listener, "Enter Name", "", "");
	}

	float timer = 0f;

	@Override
	public void render(float delta) {
		// TODO Auto-generated method stub
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		timer += delta;
		if (timer > 2f) {
			GameScreen.LEVEL1_COMPLETED = false;
			// game.setScreen(new GameScreen(game));
		}

		game.getBatch().setProjectionMatrix(camera.combined);
		game.getBatch().begin();

		game.getFont128().draw(game.getBatch(), "Points: " + points, 0, 250);
		game.getFont128().draw(game.getBatch(), "Time: " + pointTimer + "s", 0, 230);

		float timerPoints = (int) ((900f / pointTimer) * 126);
		float score = timerPoints + points;

		game.getFont128().draw(game.getBatch(), "Timer Points: " + timerPoints, 0, 210);
		game.getFont128().draw(game.getBatch(), "Score: " + score, 0, 180);

		game.getFont128().draw(game.getBatch(), "Enter Name: " + name, 0, 160);

		if (hasTyped && score >= game.getPreferences().getFloat(name)) {
			game.getFont128().draw(game.getBatch(), "Points Saved!", 0, 140);
			if (!writtenFlag) {
				game.getPreferences().putFloat(name, score);
				game.getPreferences().flush();
			}
		} else if (score < game.getPreferences().getFloat(name)) {
			game.getFont128().draw(game.getBatch(),
					"You have not beaten your high score of " + game.getPreferences().getFloat(name), 0, 140);
		}

		game.getFont128().draw(game.getBatch(), "HIGH SCORES", 200, 250);
		int yO = 230;

		for (Map.Entry<String, Float> en : sorted.entrySet()) {
			game.getFont128().draw(game.getBatch(), en.getKey() + " : " + en.getValue(), 200, yO);
			yO -= 20;
		}

		game.getBatch().end();
		
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && hasTyped) {
			Gdx.app.exit();
		}

	}

	private HashMap<String, Float> sort(HashMap<String, Float> map) {
		List<Map.Entry<String, Float>> list = new LinkedList<Map.Entry<String, Float>>(map.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {

			@Override
			public int compare(Map.Entry<String, Float> o1, Map.Entry<String, Float> o2) {
				// TODO Auto-generated method stub
				return -(o1.getValue()).compareTo(o2.getValue());
			}

		});
		
		HashMap<String, Float> output = new LinkedHashMap<String, Float>();
		for (Map.Entry<String, Float> en : list) {
			output.put(en.getKey(), en.getValue());
		}
		
		return output;
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
	public void input(String text) {
		// TODO Auto-generated method stub
		name = new String(text);
		hasTyped = true;
	}

	@Override
	public void canceled() {
		// TODO Auto-generated method stub

	}

}
