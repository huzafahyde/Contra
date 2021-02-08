package com.huzafa.contra.Screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.huzafa.contra.GameClass;
import com.huzafa.contra.Data.BodyData;
import com.huzafa.contra.Data.Level;
import com.huzafa.contra.Entities.Player;
import com.huzafa.contra.Entities.Player.State;
import com.huzafa.contra.Entities.Projectile;
import com.huzafa.contra.Entities.Enemies.Enemy;

public class GameScreen implements Screen, InputProcessor {

	public static boolean LEVEL1_COMPLETED;

	private GameClass game;

	private Box2DDebugRenderer renderer;

	private OrthographicCamera camera;
	private Viewport viewport;

	private Level level;

	private Player player;

	private boolean left, right, up, down;
	private boolean fall;
	private boolean debugRendering;

	private TiledMapRenderer tRenderer;
	private TiledMap map;

	private ArrayList<Projectile> projectiles;
	private ArrayList<Enemy> enemies;

	private float stateTime = 0f, gameTimer = 0f;;

	private Sprite life;

	private Music music;

	private int points = 0;

	public GameScreen(GameClass game) {
		// TODO Auto-generated constructor stub
		this.setGame(game);

		projectiles = new ArrayList<Projectile>();
		enemies = new ArrayList<Enemy>();

		// Create camera and viewport that have a screen ratio of about 1.77
		// The V_WIDTH and V_HEIGHT are both stored in the main class
		// The PPM stands for pixels per meter
		// This means that for every one hundred pixels on screen, there is one meter
		// This is useful for Box2D movement since Box2D can handle smaller values way
		// better than larger ones
		camera = new OrthographicCamera();
		camera.setToOrtho(false, GameClass.V_WIDTH / GameClass.PPM, GameClass.V_HEIGHT / GameClass.PPM);

		viewport = new FitViewport(GameClass.V_WIDTH / GameClass.PPM, GameClass.V_HEIGHT / GameClass.PPM, camera);

		// Generates player
		player = new Player(game.getWorld(), new Vector2(30f / GameClass.PPM, 250f / GameClass.PPM), projectiles);

		// Load tiledMap + renderer
		map = new TmxMapLoader().load("Tilemaps/Maps/level1.tmx");
		tRenderer = new OrthogonalTiledMapRenderer(map, 1f / GameClass.PPM);

		// Generates level bodies based on map
		level = new Level(game.getWorld(), map, enemies, projectiles, player);

		life = new Sprite(new Texture("Misc/life.png"));
		life.setSize(8f / GameClass.PPM, 15f / GameClass.PPM);

		music = Gdx.audio.newMusic(Gdx.files.internal("Music/Stage1_Stage7.mp3"));
		music.setLooping(true);

		// Sets the contactListener
		game.getWorld().setContactListener(new ContactListener() {

			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				// TODO Auto-generated method stub
				// Checks if one fixture colliding is a projectile and the other is a platform
				// If so, flag the body that the projectile fixture belongs to for deletion

				if (((BodyData) contact.getFixtureB().getBody().getUserData()).getName().equals("Projectile")
						&& ((BodyData) contact.getFixtureA().getBody().getUserData()).getName().equals("Enemy")) {
					((BodyData) contact.getFixtureB().getBody().getUserData()).setExplode(true);
					contact.getFixtureB().setSensor(true);
					contact.getFixtureB().getBody().setLinearVelocity(0, 0);

					((BodyData) contact.getFixtureA().getBody().getUserData()).setHit(true);
					((BodyData) contact.getFixtureA().getBody().getUserData())
							.setDamage(((BodyData) contact.getFixtureB().getBody().getUserData()).getDamage());
				}

				if (((BodyData) contact.getFixtureB().getBody().getUserData()).getName().equals("Enemy_Bullet")
						&& ((BodyData) contact.getFixtureA().getBody().getUserData()).getName().equals("Player")) {

					((BodyData) contact.getFixtureB().getBody().getUserData()).setExplode(true);
					contact.getFixtureB().setSensor(true);
					contact.getFixtureB().getBody().setLinearVelocity(0, 0);

					((BodyData) contact.getFixtureA().getBody().getUserData()).setHit(true);
					((BodyData) contact.getFixtureA().getBody().getUserData()).setDamage(1);

				}
			}

			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
				// TODO Auto-generated method stub

			}

			@Override
			public void endContact(Contact contact) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beginContact(Contact contact) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

		renderer = new Box2DDebugRenderer();

		Gdx.input.setInputProcessor(this);
		music.play();

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// TileMapRenderer
		tRenderer.setView(camera);
		tRenderer.render();

		stateTime += delta;
		gameTimer += delta;

		game.getWorld().step(1 / 60f, 6, 2);

		checkForDelete();

		System.out.println(player.body.getPosition().x);

		// Step world

		game.getBatch().setProjectionMatrix(camera.combined);
		game.getBatch().begin();

		player.getSprite().draw(game.getBatch());

		for (int i = 0; i < projectiles.size(); i++) {
			// Draw and act projectiles
			projectiles.get(i).getSprite().draw(game.getBatch());
			projectiles.get(i).act(delta);
			if (projectiles.get(i).getWorldTimer() > 10f) {
				((BodyData) projectiles.get(i).getBody().getUserData()).setFlaggedForDelete(true);
			}
		}

		update(delta);

		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).draw(game.getBatch());
			enemies.get(i).update(delta);
		}

		float posX = -100f / GameClass.PPM;
		for (int i = 0; i < player.getLives() - 1; i++) {
			game.getBatch().draw(life.getTexture(), posX + player.getBody().getPosition().x - 100f / GameClass.PPM,
					180f / GameClass.PPM, life.getWidth(), life.getHeight());
			posX += 9f / GameClass.PPM;
		}

		game.getBatch().end();

		// Debug renderer for Box2D
		if (debugRendering)
			renderer.render(game.getWorld(), camera.combined);

		movePlayer();

		if (player.body.getLinearVelocity().y > 0.1f || fall) {
			// If player is jumping, disable collision
			player.getBody().getFixtureList().get(0).setSensor(true);
		} else {
			player.getBody().getFixtureList().get(0).setSensor(false);

		}

		for (int i = 0; i < enemies.size(); i++) {
			if (((BodyData) enemies.get(i).getBody().getUserData()).isHit()) {
				enemies.get(i).damage(((BodyData) enemies.get(i).getBody().getUserData()).getDamage());
				((BodyData) enemies.get(i).getBody().getUserData()).setHit(false);
			}

			if (Math.abs(
					enemies.get(i).getBody().getPosition().x - player.getBody().getPosition().x) > GameClass.V_WIDTH
							/ 2f / GameClass.PPM) {
				enemies.get(i).getBody().setActive(false);
				enemies.get(i).setActive(false);
			} else {
				enemies.get(i).getBody().setActive(true);
				enemies.get(i).setActive(true);
			}
		}

		if (((BodyData) player.getBody().getUserData()).isHit()) {
			player.setLives(player.getLives() - 1);
			((BodyData) player.getBody().getUserData()).setHit(false);
			player.setState(State.DEAD);
		}

		// System.out.println(points);

		if (LEVEL1_COMPLETED) {
			level.update();

			if (player.getPosition().x >= 33.1f) {

				Array<Body> bodies = new Array<Body>();
				game.getWorld().getBodies(bodies);
				for (int i = 0; i < bodies.size; i++) {
					game.getWorld().destroyBody(bodies.get(i));
				}

				game.setScreen(new TransitScreen(game, points, gameTimer));
			}

		}

		if (player.getLives() <= 0) {
			game.setScreen(new DieScreen(game));
		}

		if (player.getBody().getPosition().y <= 0) {
			player.setLives(0);
		}

	}

	private void movePlayer() {
		// Move player
		if (right) {
			if (player.body.getLinearVelocity().x < 0.5f)
				player.body.applyForceToCenter(2f / GameClass.PPM, 0, true);
		} else if (left) {
			if (player.body.getLinearVelocity().x > -0.5f)
				player.body.applyForceToCenter(-2f / GameClass.PPM, 0, true);
		} else {
			player.body.setLinearVelocity(0, player.body.getLinearVelocity().y);
		}
	}

	private void updatePlayer() {
		if (!Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
			fall = false;
		}

		// Set booleans based on whether the player is:
		// Falling
		// Flipped
		// Running
		// Jumping
		if (!player.isDying()) {
			player.setCanMove(true);

			if (down && player.body.getLinearVelocity().x != 0) {

				player.setState(State.DOWN_RIGHT_SHOOT);

				if (player.body.getLinearVelocity().x > 0) {
					player.setFlipped(false);
				} else {
					player.setFlipped(true);
				}
			} else if (down && !fall) {
				player.setState(State.PRONE);
				if (!fall) {
					player.setCanMove(false);
				}
				if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
					fall = true;
				} else {
					fall = false;
				}
			} else if (up) {
				if (player.body.getLinearVelocity().x != 0) {
					player.setState(State.UP_RIGHT_SHOOT);
				} else {
					player.setCanMove(true);
					player.setState(State.UP_SHOOT);
				}

				if (player.body.getLinearVelocity().x > 0) {
					player.setFlipped(false);
				} else {
					player.setFlipped(true);
				}

			} else if (player.body.getLinearVelocity().y > 0.1f || player.body.getLinearVelocity().y < -0.1f) {
				player.setCanMove(true);
				player.setState(State.JUMPING);
				if (player.body.getLinearVelocity().x < 0f) {
					player.setFlipped(true);
				} else {
					player.setFlipped(false);
				}

				System.out.println(player.getBody().getLinearVelocity().x);
			} else if (player.body.getLinearVelocity().x != 0) {
				player.setCanMove(true);
				player.setState(State.RUNNING);

				if (right) {
					player.setFlipped(false);
				} else if (left) {
					player.setFlipped(true);
				}
			} else {
				player.setCanMove(true);
				player.setState(State.STATIONARY);
			}
		} else {
			player.setCanMove(false);
		}

		if (!player.isCanMove()) {
			player.getBody().setLinearVelocity(0f, 0f);
		}
	}

	private void checkForDelete() {
		// If the world step is complete then check for every projectile if it has been
		// flagged
		// Destroy if flagged
		for (int i = 0; i < projectiles.size(); i++) {
			if (((BodyData) projectiles.get(i).getBody().getUserData()).isFlaggedForDelete()) {
				if (!game.getWorld().isLocked()) {
					projectiles.get(i).destroy();
					projectiles.remove(i);
				}
			}
		}

		for (int i = 0; i < enemies.size(); i++) {
			if (enemies.get(i).getSprite().getY() < -100f / GameClass.PPM) {
				((BodyData) enemies.get(i).getBody().getUserData()).setFlaggedForDelete(true);
			}

			if (((BodyData) enemies.get(i).getBody().getUserData()).isFlaggedForDelete()) {
				if (!game.getWorld().isLocked()) {

					if (enemies.get(i).getType().equals("Soldier")) {
						points += 50;
					} else if (enemies.get(i).getType().equals("Rifleman")) {
						points += 150;
					} else if (enemies.get(i).getType().equals("Turret")) {
						points += 300;
					} else if (enemies.get(i).getType().equals("Cannon")) {
						points += 500;
					} else if (enemies.get(i).getType().equals("Boss")) {
						points += 2000;
					} else if (enemies.get(i).getType().equals("Boss_Cannon")) {
						points += 500;
					}

					enemies.get(i).destroy();
					enemies.remove(i);

				}
			}
		}

	}

	private void update(float delta) {

		// Update
		moveCamera();
		camera.update();

		player.update(delta);

		if (((BodyData) player.getBody().getUserData()).isHit()) {
			stateTime = 0f;
		}
		player.updateTexture(stateTime);
		updatePlayer();
	}

	private void moveCamera() {
		camera.position.set(player.getPosition().x, 1f, 0);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
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
		music.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub

		if (player.isCanMove()) {
			// Move left or right
			if (keycode == Input.Keys.W) {
				up = true;
			}

			if (keycode == Input.Keys.A) {
				left = true;
			} else if (keycode == Input.Keys.D) {
				right = true;
			}

			if (keycode == Input.Keys.SPACE && player.body.getLinearVelocity().y < 0.1f
					&& player.body.getLinearVelocity().y > -0.1f) {
				player.body.applyLinearImpulse(0, 0.5f / GameClass.PPM, player.getPosition().x, player.getPosition().y,
						true);
			}
		}

		if (keycode == Input.Keys.S && player.getBody().getLinearVelocity().y < 0.1f
				&& player.getBody().getLinearVelocity().y > -0.1f) {
			// Move down faster
			down = true;
			player.body.applyForceToCenter(0, -0.15f / GameClass.PPM, true);
		}

		if (keycode == Input.Keys.ENTER) {
			// Shoot
			player.shoot();

		}

		if (keycode == Input.Keys.T) {
			if (debugRendering) {
				debugRendering = false;
			} else {
				debugRendering = true;
			}
		}

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		if (keycode == Input.Keys.A) {
			left = false;
		}
		if (keycode == Input.Keys.D) {
			right = false;
		}

		if (keycode == Input.Keys.W) {
			up = false;
		}
		if (keycode == Input.Keys.S) {
			down = false;
		}
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
		// Find points on screen
		System.out.println(viewport.unproject(new Vector2(screenX, screenY)));

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

	public GameClass getGame() {
		return game;
	}

	public void setGame(GameClass game) {
		this.game = game;
	}

}
