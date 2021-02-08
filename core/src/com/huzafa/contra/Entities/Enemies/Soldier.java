package com.huzafa.contra.Entities.Enemies;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
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
import com.huzafa.contra.Entities.Bullet;
import com.huzafa.contra.Entities.Projectile;

public class Soldier extends Enemy {

	private State state;

	private float shootTimer = 0f;

	public Soldier(World world, String type, RectangleMapObject object, ArrayList<Projectile> projectiles) {
		// TODO Auto-generated constructor stub
		super(world, type, object, projectiles);

		generateEnemy(type, object);

		state = State.RUNNING;
	}

	public enum State {
		RUNNING, JUMPING, SHOOTING, PRONE, RECOVERY, PRONE_RECOVERY, DEFAULT
	}

	public void update(float delta) {

		if (isActive) {

			stateTime += delta;
			tick += delta;

			if (state == State.RECOVERY || state == State.PRONE_RECOVERY)
				shootTimer += delta;

			updateTexture(stateTime);
			randomizeState();

			switch (state) {
			case JUMPING:
				Filter filter = new Filter();
				filter.categoryBits = GameClass.NULL_ENTITY;
				filter.maskBits = 0;

				body.getFixtureList().get(0).setFilterData(filter);

				body.applyLinearImpulse((new Vector2(-.05f / GameClass.PPM, .5f / GameClass.PPM)),
						new Vector2(body.getPosition()), true);
				state = State.DEFAULT;
				break;
			case RUNNING:
				body.setLinearVelocity(-25f / GameClass.PPM, body.getLinearVelocity().y);
				break;
			case SHOOTING:
				shoot();
				state = State.RECOVERY;
				break;
			case RECOVERY:
				body.setLinearVelocity(-5f / GameClass.PPM, body.getLinearVelocity().y);
				if (shootTimer > 5f) {
					state = State.RUNNING;
					shootTimer = 0f;
				}
				break;
			case PRONE:
				filter = new Filter();
				filter.categoryBits = GameClass.NULL_ENTITY;
				filter.maskBits = 0;

				body.getFixtureList().get(0).setFilterData(filter);

				shoot();
				state = State.PRONE_RECOVERY;

				break;
			case PRONE_RECOVERY:
				if (shootTimer > 5f) {
					state = State.RUNNING;
					shootTimer = 0f;
				}
				break;
			default:
				break;
			}

			if (state != State.PRONE) {
				Filter filter = new Filter();

				filter.categoryBits = GameClass.ENEMY_ENTITY;
				filter.maskBits = GameClass.BULLET_ENTITY | GameClass.PLAYER_ENTITY;

				body.getFixtureList().get(0).setFilterData(filter);
			}
		}
	}

	private void shoot() {
		if (body.isActive()) {
			Bullet bullet = new Bullet(world, this, "Normal_Bullet", new Vector2(-100f / GameClass.PPM, 0f));
			projectiles.add(bullet);
		}
	}

	private void updateTexture(float stateTime) {
		sprite.setPosition(body.getPosition().x - 5f / 2f / GameClass.PPM,
				body.getPosition().y - 15f / 2f / GameClass.PPM);
		TextureRegion region = currentAnimation.getKeyFrame(stateTime, true);
		region.flip(flipped, false);
		sprite.setRegion(region);

		switch (state) {
		case DEFAULT:
			currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 6f, atlas.findRegions("Running"));
			sprite.setSize(10f / GameClass.PPM, 30f / GameClass.PPM);
			break;
		case RUNNING:
			currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 6f, atlas.findRegions("Running"));
			sprite.setSize(10f / GameClass.PPM, 30f / GameClass.PPM);
			break;
		case SHOOTING:
			currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 6f, atlas.findRegions("Shooting"));
			sprite.setSize(15f / GameClass.PPM, 30f / GameClass.PPM);
			break;
		case PRONE:
			currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 6f, atlas.findRegions("Prone"));
			sprite.setSize(30f / GameClass.PPM, 10f / GameClass.PPM);
			break;
		case RECOVERY:
			currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 6f, atlas.findRegions("Shooting"));
			sprite.setSize(15f / GameClass.PPM, 30f / GameClass.PPM);
			break;
		case PRONE_RECOVERY:
			currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 6f, atlas.findRegions("Prone"));
			sprite.setSize(30f / GameClass.PPM, 10f / GameClass.PPM);
			break;
		default:
			break;
		}
	}

	private void randomizeState() {
		if (tick > 3f) {
			double random = Math.random();

			if (random > 0.6f) {
				state = State.RUNNING;
			} else if (random > 0.4f) {
				state = State.SHOOTING;
			} else if (random > 0.1f) {
				state = State.JUMPING;
			} else {
				state = State.PRONE;
			}
			tick = 0f;
		}
	}

	protected void generateEnemy(String type, RectangleMapObject object) {
		if (type.equals("Soldier") || type.equals("Rifleman")) {
			PolygonShape shape = getRectangle(object, true);

			BodyDef bodyDef = new BodyDef();
			bodyDef.type = BodyType.DynamicBody;
			bodyDef.position
					.set(new Vector2(object.getRectangle().x / GameClass.PPM, object.getRectangle().y / GameClass.PPM));

			body = world.createBody(bodyDef);

			FixtureDef hitbox = new FixtureDef();
			hitbox.shape = shape;
			hitbox.filter.categoryBits = GameClass.ENEMY_ENTITY;
			hitbox.filter.maskBits = GameClass.BULLET_ENTITY | GameClass.PLAYER_ENTITY;

			Fixture fixture = body.createFixture(hitbox);

			CircleShape circleShape = new CircleShape();
			circleShape.setRadius(object.getRectangle().width / 2f / GameClass.PPM);

			FixtureDef contactCircle = new FixtureDef();
			contactCircle.shape = circleShape;
			contactCircle.friction = 1f;
			contactCircle.restitution = 0f;
			contactCircle.density = 0.6f;
			contactCircle.filter.categoryBits = GameClass.GROUNDCONTACT_ENTITY;
			contactCircle.filter.maskBits = GameClass.WORLD_ENTITY;

			Fixture fixture2 = body.createFixture(contactCircle);

			body.setUserData(new BodyData("Enemy"));
			body.setFixedRotation(true);
		}

	}

}
