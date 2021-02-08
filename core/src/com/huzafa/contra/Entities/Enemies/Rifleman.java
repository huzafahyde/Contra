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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.huzafa.contra.GameClass;
import com.huzafa.contra.Data.BodyData;
import com.huzafa.contra.Entities.Bullet;
import com.huzafa.contra.Entities.Player;
import com.huzafa.contra.Entities.Projectile;

public class Rifleman extends Enemy {

	private Player player;

	private float angle = 0f, stateTime = 0f, shootTimer = 0f;

	private Vector2 direction;

	private State state;

	public Rifleman(World world, String type, RectangleMapObject object, ArrayList<Projectile> projectiles,
			Player player) {
		super(world, type, object, projectiles);
		generateEnemy(type, object);

		this.player = player;

		state = State.STRAIGHT;

		direction = new Vector2();
		setHealth(5f);
		// TODO Auto-generated constructor stub
	}

	public void update(float delta) {
		if (isActive) {
			sprite.setPosition(body.getPosition().x - 5f / 2 / GameClass.PPM,
					body.getPosition().y - 15f / 2 / GameClass.PPM);

			stateTime += delta;
			shootTimer += delta;

			if (shootTimer > 5f && body.isActive()) {
				shoot();
				shootTimer = 0f;
			}

			findAngle();
			updateTexture(stateTime);
		}

	}

	private void shoot() {

		Bullet bullet = new Bullet(world, this, "Normal_Bullet", direction);
		projectiles.add(bullet);
	}

	public enum State {
		UP, STRAIGHT, DOWN
	}

	private void updateState() {

		if (angle == 0) {
			state = State.STRAIGHT;
			if (body.getPosition().x - player.getPosition().x < 0f) {
				direction.set(100f / GameClass.PPM, 0);
				flipped = true;
			} else {
				direction.set(-100f / GameClass.PPM, 0);
				flipped = false;
			}
		} else {
			if (body.getPosition().y - player.getPosition().y > 0f) {
				direction.set(direction.x, -50f / GameClass.PPM);
				state = State.DOWN;
			} else {
				direction.set(direction.x, 50f / GameClass.PPM);
				state = State.UP;
			}

			if (body.getPosition().x - player.getPosition().x < 0f) {

				direction.set(100f / GameClass.PPM, direction.y);
				flipped = true;
			} else {
				direction.set(-100f / GameClass.PPM, direction.y);
				flipped = false;
			}
		}

	}

	private void updateTexture(float stateTime) {

		updateState();

		switch (state) {
		case STRAIGHT:
			currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 4f, atlas.findRegions("Straight"));
			break;
		case UP:
			currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 4f, atlas.findRegions("Up"));
			break;
		case DOWN:
			currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 4f, atlas.findRegions("Down"));
			break;
		default:
			break;
		}
		TextureRegion region = currentAnimation.getKeyFrame(stateTime, true);
		region.flip(flipped, false);
		sprite.setRegion(region);

		sprite.setRegion(currentAnimation.getKeyFrame(stateTime, true));
	}

	private void findAngle() {
		if (Math.abs(player.getPosition().x - body.getPosition().x) < 200f / GameClass.PPM) {
			float dx = body.getPosition().x - player.getPosition().x;
			float dy = body.getPosition().y - player.getPosition().y;

			angle = Math.round(Math.toDegrees(Math.atan(dy / dx)) / 45) * 45;
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
