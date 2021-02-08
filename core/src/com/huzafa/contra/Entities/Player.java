package com.huzafa.contra.Entities;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.huzafa.contra.GameClass;
import com.huzafa.contra.Data.BodyData;

public class Player {
	public static float NORMAL_GUN_MAX_TIMER = 0.2f;

	private World world;

	private Vector2 position;

	public Body body;

	public Animation<AtlasRegion> currentAnimation;

	private Sprite sprite;

	private TextureAtlas atlas;

	private State state;
	private Direction direction;

	private boolean flipped, shooting, canMove = true, dying;
	public boolean canShoot = true;

	private float shotTimer = 0f, shootTimer = 0f;

	private String bulletType;

	private int lives = 5;

	private ArrayList<Projectile> projectiles;

	public Player(World world, Vector2 position, ArrayList<Projectile> projectiles) {
		// TODO Auto-generated constructor stub
		this.world = world;
		this.position = position;
		this.projectiles = projectiles;

		createPlayer();

		atlas = new TextureAtlas(Gdx.files.internal("test.txt"));
		System.out.println(atlas.getRegions());

		// Set the sprite to the idle sprite found in the TextureAtlas
		TextureRegion region = atlas.findRegion("Emerging");
		sprite = new Sprite(region);
		sprite.setPosition(body.getPosition().x - body.getFixtureList().get(0).getShape().getRadius(),
				body.getPosition().y - body.getFixtureList().get(0).getShape().getRadius());
		sprite.setSize(16f / GameClass.PPM, 32f / GameClass.PPM);

		// Initialize states
		state = State.STATIONARY;
		direction = Direction.STRAIGHT;

		// Initialize bullets
		bulletType = new String("Normal_Bullet");

	}

	public enum Direction {
		UP, DOWN, STRAIGHT
	}

	public enum State {
		STATIONARY, RUNNING, JUMPING, PRONE, DEAD, UP_SHOOT, UP_RIGHT_SHOOT, DOWN_RIGHT_SHOOT
	}

	private float dieTimer = 0f;

	public void updateAnimation() {

		// If state is prone do this
		// Otherwise check other states and set the animation as well as the size of the
		// sprite

		if (state != State.DEAD) {
			canShoot = true;
		}

		if (state == State.DEAD) {
			currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 3f, atlas.findRegions("Death"));
			sprite.setSize(22f / GameClass.PPM, 12f / GameClass.PPM);
			dying = true;
			canShoot = false;

		} else if (state == State.PRONE) {
			currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 3f, atlas.findRegions("Shoot_Prone"));
			sprite.setSize(22f / GameClass.PPM, 12f / GameClass.PPM);
			sprite.setX(sprite.getX() - 10f / GameClass.PPM);

			Filter filter = new Filter();
			filter.categoryBits = GameClass.NULL_ENTITY;
			filter.maskBits = 0;

			body.getFixtureList().get(1).setFilterData(filter);

		} else {
			Filter filter = new Filter();

			filter.categoryBits = GameClass.PLAYER_ENTITY;
			filter.maskBits = GameClass.ENEMY_BULLET_ENTITY | GameClass.ENEMY_ENTITY;

			body.getFixtureList().get(1).setFilterData(filter);

			switch (direction) {
			case STRAIGHT:
				switch (state) {
				case STATIONARY:
					currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 3f,
							atlas.findRegions("Shoot_Straight"));
					sprite.setSize(16f / GameClass.PPM, 28f / GameClass.PPM);
					break;
				case RUNNING:
					if (shooting) {
						currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 5f,
								atlas.findRegions("ShootStraight_Running"));
						sprite.setSize(16f / GameClass.PPM, 28f / GameClass.PPM);
					} else {
						currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 5f,
								atlas.findRegions("Running"));
						sprite.setSize(12f / GameClass.PPM, 28f / GameClass.PPM);

					}
					break;
				case JUMPING:
					currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 5f, atlas.findRegions("Jumping"));
					sprite.setSize(12f / GameClass.PPM, 12f / GameClass.PPM);
					break;
				case UP_SHOOT:
					currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 5f, atlas.findRegions("Shoot_Up"));
					sprite.setSize(10f / GameClass.PPM, 30f / GameClass.PPM);
					break;
				case UP_RIGHT_SHOOT:
					currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 5f,
							atlas.findRegions("ShootUp_Running"));
					sprite.setSize(12f / GameClass.PPM, 30f / GameClass.PPM);
					break;
				case DOWN_RIGHT_SHOOT:
					currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 5f,
							atlas.findRegions("ShootDown_Running"));
					sprite.setSize(12f / GameClass.PPM, 30f / GameClass.PPM);
					break;
				default:
					break;
				}

				break;

			default:
				break;
			}
		}
	}

	public void shoot() {
		if (getShootTimer() > Player.NORMAL_GUN_MAX_TIMER && canShoot) {

			Bullet bullet = new Bullet(world, this, getBulletType());
			projectiles.add(bullet);

			setShootTimer(0f);
		}
	}

	public void updateTexture(float stateTime) {
		// Updates the animation
		// Updates the texture based on the current frame of the animation

		updateAnimation();

		TextureRegion region;
		if (!dying) {
			region = currentAnimation.getKeyFrame(stateTime, true);
		} else {
			region = currentAnimation.getKeyFrame(stateTime, false);
		}
		region.flip(flipped, false);
		sprite.setRegion(region);
	}

	public TextureRegion getCurrentKeyframe(float stateTime) {
		return currentAnimation.getKeyFrame(stateTime, true);
	}

	public void update(float delta) {

		if (dying) {
			dieTimer += delta;

			Filter filter = new Filter();
			filter.categoryBits = GameClass.NULL_ENTITY;
			filter.maskBits = 0;

			body.getFixtureList().get(1).setFilterData(filter);

		} else {
			Filter filter = new Filter();
			filter.categoryBits = GameClass.PLAYER_ENTITY;
			filter.maskBits = GameClass.ENEMY_BULLET_ENTITY | GameClass.ENEMY_ENTITY;

			body.getFixtureList().get(1).setFilterData(filter);
		}

		if (dieTimer > 2f) {
			state = State.STATIONARY;
			dieTimer = 0f;
			dying = false;
		}

		// Set the sprite position and the size

		sprite.setPosition(body.getPosition().x - body.getFixtureList().get(0).getShape().getRadius(),
				body.getPosition().y - body.getFixtureList().get(0).getShape().getRadius());

		shotTimer += delta;
		shootTimer += delta;

		// Detect if shooting
		// If player has shot for the past 3 seconds, keep using shooting animation
		// Otherwise go back to normal running
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			shooting = true;
		}
		if (shotTimer > 3f) {
			shooting = false;
			shotTimer = 0f;
		}

	}

	private void createPlayer() {
		// Creates new bodyDef for the player
		BodyDef playerBodyDef = new BodyDef();
		playerBodyDef.type = BodyType.DynamicBody;
		playerBodyDef.position.set(position);

		// Adds body to the world
		body = world.createBody(playerBodyDef);

		// New CircleShape that defines the body
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(5f / GameClass.PPM);

		// Creates fixtureDef which defines the physics
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circleShape;
		fixtureDef.restitution = 0f;
		fixtureDef.density = 0.6f;
		fixtureDef.friction = 1f;
		fixtureDef.filter.categoryBits = GameClass.GROUNDCONTACT_ENTITY;
		fixtureDef.filter.maskBits = GameClass.WORLD_ENTITY;

		// Adds fixtureDef to a Fixture and adds that to the body
		Fixture fixture = body.createFixture(fixtureDef);

		// Creates the bullet collider (see above)
		PolygonShape box = new PolygonShape();
		box.setAsBox(5f / GameClass.PPM, 15f / GameClass.PPM, new Vector2(0, 10f / GameClass.PPM), 0);

		FixtureDef fixtureDef2 = new FixtureDef();
		fixtureDef2.shape = box;
		fixtureDef2.filter.categoryBits = GameClass.PLAYER_ENTITY;
		fixtureDef2.filter.maskBits = GameClass.ENEMY_ENTITY | GameClass.ENEMY_BULLET_ENTITY;

		Fixture fixture2 = body.createFixture(fixtureDef2);

		// Make the body always awake
		// Make the body always the same rotation
		body.setSleepingAllowed(false);
		body.setFixedRotation(true);
		body.setUserData(new BodyData("Player"));

		// Gets rid of circle
		circleShape.dispose();
		box.dispose();

		body.setAngularDamping(1);

	}

	public Vector2 getPosition() {
		return body.getPosition();
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public boolean isFlipped() {
		return flipped;
	}

	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public boolean isCanMove() {
		return canMove;
	}

	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}

	public String getBulletType() {
		return bulletType;
	}

	public void setBulletType(String bulletType) {
		this.bulletType = bulletType;
	}

	public float getShootTimer() {
		return shootTimer;
	}

	public boolean isDying() {
		return dying;
	}

	public void setDying(boolean dying) {
		this.dying = dying;
	}

	public void setShootTimer(float shootTimer) {
		this.shootTimer = shootTimer;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

}
