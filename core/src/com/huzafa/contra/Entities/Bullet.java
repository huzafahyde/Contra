package com.huzafa.contra.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.huzafa.contra.GameClass;
import com.huzafa.contra.Data.BodyData;
import com.huzafa.contra.Entities.Player.State;
import com.huzafa.contra.Entities.Enemies.Enemy;

public class Bullet implements Projectile {

	private World world;
	private Player player;
	private Enemy enemy;
	private String type;

	private Body body;

	private Sprite sprite;
	private Sprite explodeSprite;

	private Vector2 direction;

	private float timer = 0f, worldTimer = 0f;
	public boolean destroy;
	public boolean playerBullet;

	private Sprite originSprite;

	public Bullet(World world, Player player, String type) {
		// TODO Auto-generated constructor stub
		this.world = world;
		this.player = player;
		this.type = type;

		sprite = new Sprite();

		playerBullet = true;

		originSprite = new Sprite(player.getSprite());

		generateBullet(this.type);
	}

	public Bullet(World world, Enemy enemy, String type, Vector2 direction) {
		// TODO Auto-generated constructor stub
		this.world = world;
		this.enemy = enemy;
		this.type = type;
		this.direction = direction;

		sprite = new Sprite();

		originSprite = new Sprite(enemy.getSprite());

		playerBullet = false;

		generateBullet(this.type);
	}

	@Override
	public void generateBullet(String type) {
		// TODO Auto-generated method stub

		// Determine sprite

		if (type.equals("Normal_Bullet")) {
			sprite = new Sprite(new Texture("Projectiles/bullet.png"));
			sprite.setSize(2f / GameClass.PPM, 2f / GameClass.PPM);

			explodeSprite = new Sprite(new Texture("Projectiles/bullet_explode.png"));
		}

		// Create a new body that is not affected by gravity

		BodyDef bDef = new BodyDef();
		bDef.bullet = true;
		bDef.type = BodyType.DynamicBody;
		bDef.position.set(new Vector2(originSprite.getX(), originSprite.getY() + originSprite.getHeight() * (3f / 4f)));

		body = world.createBody(bDef);
		body.setGravityScale(0f);

		CircleShape circleShape = new CircleShape();

		circleShape.setRadius(1f / GameClass.PPM);

		FixtureDef fDef = new FixtureDef();
		fDef.density = 0.1f;
		fDef.shape = circleShape;

		if (playerBullet) {
			body.setUserData(new BodyData("Projectile"));

			fDef.filter.categoryBits = GameClass.BULLET_ENTITY;
			fDef.filter.maskBits = GameClass.ENEMY_ENTITY;
		} else {
			body.setUserData(new BodyData("Enemy_Bullet"));

			fDef.filter.categoryBits = GameClass.ENEMY_BULLET_ENTITY;
			fDef.filter.maskBits = GameClass.PLAYER_ENTITY;
		}

		((BodyData) body.getUserData()).setDamage(1f);
		
		Fixture fixture = body.createFixture(fDef);

		// Set the velocity based on the player direction
		if (playerBullet) {
			if (player.getState() == State.UP_SHOOT) {
				body.setLinearVelocity(new Vector2(0, 100f / GameClass.PPM));
			} else {
				if (player.getState() == State.UP_RIGHT_SHOOT) {
					if (!player.isFlipped()) {
						body.setLinearVelocity(new Vector2(100f / GameClass.PPM, 50f / GameClass.PPM));
					} else {
						body.setLinearVelocity(new Vector2(-100f / GameClass.PPM, 50f / GameClass.PPM));
					}
				} else if (player.getState() == State.DOWN_RIGHT_SHOOT) {
					if (!player.isFlipped()) {
						body.setLinearVelocity(new Vector2(100f / GameClass.PPM, -50f / GameClass.PPM));
					} else {
						body.setLinearVelocity(new Vector2(-100f / GameClass.PPM, -50f / GameClass.PPM));
					}
				} else {
					if (!player.isFlipped()) {
						body.setLinearVelocity(new Vector2(100f / GameClass.PPM, 0f));
					} else {
						body.setLinearVelocity(new Vector2(-100f / GameClass.PPM, 0f));
					}
				}
			}
		} else {
			body.setLinearVelocity(direction);
		}

		sprite.setPosition(body.getPosition().x, body.getPosition().y);

		circleShape.dispose();

	}

	private float explodeTimer = 0f;

	@Override
	public void act(float delta) {
		sprite.setPosition(body.getWorldCenter().x - 0.5f / GameClass.PPM,
				body.getWorldCenter().y - 0.5f / GameClass.PPM);

		timer += delta;
		if (((BodyData) body.getUserData()).isExplode()) {
			explodeTimer += delta;
			sprite = new Sprite(explodeSprite);
			sprite.setSize(5f / GameClass.PPM, 5f / GameClass.PPM);
			sprite.setPosition(body.getWorldCenter().x - 1f / GameClass.PPM,
					body.getWorldCenter().y - 1f / GameClass.PPM);
			if (explodeTimer > 0.15f) {
				((BodyData) body.getUserData()).setFlaggedForDelete(true);
			}
		}

		worldTimer += delta;
	}

	@Override
	public Sprite getSprite() {
		// TODO Auto-generated method stub
		return sprite;
	}

	@Override
	public Vector2 getPosition() {
		// TODO Auto-generated method stub
		return body.getPosition();
	}

	public float getTimer() {
		return timer;
	}

	public void setTimer(float timer) {
		this.timer = timer;
	}

	@Override
	public Body getBody() {
		return body;
	}

	public float getWorldTimer() {
		return worldTimer;
	}

	public void setWorldTimer(float worldTimer) {
		this.worldTimer = worldTimer;
	}

	@Override
	public void destroy() {
		world.destroyBody(body);
	}

}
