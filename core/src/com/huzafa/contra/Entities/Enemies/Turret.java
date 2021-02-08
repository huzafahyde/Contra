package com.huzafa.contra.Entities.Enemies;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.huzafa.contra.GameClass;
import com.huzafa.contra.Data.BodyData;
import com.huzafa.contra.Entities.Bullet;
import com.huzafa.contra.Entities.Player;
import com.huzafa.contra.Entities.Projectile;

public class Turret extends Enemy {

	private Player player;

	private int angle, shotCounter = 0;

	private int texturePosition;

	private float stateTime = 0f, shootTimer = 0f;

	private boolean hasShot;

	public Turret(World world, String type, RectangleMapObject object, ArrayList<Projectile> projectiles,
			Player player) {
		super(world, type, object, projectiles);
		// TODO Auto-generated constructor stub
		generateEnemy(object);
		this.player = player;

		setHealth(10);
		System.out.println();
		// System.out.println(1);

		findAngle();
	}

	private void generateEnemy(RectangleMapObject object) {
		PolygonShape shape = getRectangle(object, true);

		BodyDef bDef = new BodyDef();
		bDef.type = BodyType.StaticBody;
		bDef.position
				.set(new Vector2(object.getRectangle().x / GameClass.PPM, object.getRectangle().y / GameClass.PPM));

		// System.out.println(object.getRectangle().x / GameClass.PPM + ", " +
		// object.getRectangle().y / GameClass.PPM);

		body = world.createBody(bDef);
		body.setGravityScale(0f);

		FixtureDef fDef = new FixtureDef();
		fDef.shape = shape;
		fDef.filter.categoryBits = GameClass.ENEMY_ENTITY;
		fDef.filter.maskBits = GameClass.BULLET_ENTITY | GameClass.PLAYER_ENTITY;
		
		Fixture fixture = body.createFixture(fDef);

		body.setUserData(new BodyData("Enemy"));

		sprite.setSize(object.getRectangle().width / GameClass.PPM, object.getRectangle().height / GameClass.PPM);
	}

	private void findAngle() {
		float dx = player.getPosition().x - body.getPosition().x;
		float dy = player.getPosition().y - body.getPosition().y;

		float unroundedAngle = (float) Math.toDegrees(Math.atan2(dy, dx));
		if (unroundedAngle < 0) {
			unroundedAngle += 360;
		}

		angle = Math.round(unroundedAngle / 30) * 30;

		texturePosition = (int) ((angle / 30f) * 3);
		if (texturePosition == 36) {
			texturePosition = 0;
		}
	}

	public void update(float delta) {
		if (isActive) {
			findAngle();

			updateTexture(delta);

			shootTimer += delta;
			if (shootTimer > 9f && !hasShot) {
				hasShot = true;
				shootTimer = 0f;
				shotCounter++;
				shoot();
			}
			if (shootTimer > 0.1f && hasShot) {
				shotCounter++;
				shootTimer = 0f;
				shoot();
			}
			if (shotCounter == 3) {
				hasShot = false;
				shotCounter = 0;
			}
		}
	}

	private Vector2 findDirection() {
		Vector2 direction;
		float x = (float) (100f * Math.cos(Math.toRadians(angle)) / GameClass.PPM);
		float y = (float) (100f * Math.sin(Math.toRadians(angle)) / GameClass.PPM);
		direction = new Vector2(x, y);
		return direction;
	}

	private void shoot() {
		Bullet bullet = new Bullet(world, this, "Normal_Bullet", findDirection());
		projectiles.add(bullet);
	}

	private void updateTexture(float delta) {
		stateTime += delta;

		sprite.setPosition(body.getPosition().x, body.getPosition().y);

		Array<AtlasRegion> animationTextures = new Array<TextureAtlas.AtlasRegion>();

		for (int i = texturePosition; i < texturePosition + 3; i++) {
			animationTextures.add(atlas.findRegion("turret", i));
		}

		currentAnimation = new Animation<TextureAtlas.AtlasRegion>(1 / 3f, animationTextures);

		TextureRegion region = currentAnimation.getKeyFrame(stateTime, true);
		sprite.setRegion(currentAnimation.getKeyFrame(stateTime, true));
		sprite.setFlip(false, true);
	}
}
