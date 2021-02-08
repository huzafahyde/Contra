package com.huzafa.contra.Entities.Enemies;

import java.util.ArrayList;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.huzafa.contra.GameClass;
import com.huzafa.contra.Data.BodyData;
import com.huzafa.contra.Entities.Bullet;
import com.huzafa.contra.Entities.Projectile;

public class BossCannon extends Enemy {
	
	private float shootTimer = 0f;

	public BossCannon(World world, String type, RectangleMapObject object, ArrayList<Projectile> projectiles) {
		super(world, type, object, projectiles);
		setHealth(5f);
		generateEnemy(object);
		
		if (type.equals("Boss_Cannon"))
		sprite.setPosition(body.getPosition().x, body.getPosition().y);
		
	}
	
	private void generateEnemy(RectangleMapObject object) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.gravityScale = 0f;
		bodyDef.position.set(new Vector2(object.getRectangle().x / GameClass.PPM, object.getRectangle().y / GameClass.PPM));
		
		body = world.createBody(bodyDef);
		
		PolygonShape shape = getRectangle(object, true);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.filter.categoryBits = GameClass.ENEMY_ENTITY;
		fixtureDef.filter.maskBits = GameClass.PLAYER_ENTITY | GameClass.BULLET_ENTITY;
		
		Fixture fixture = body.createFixture(fixtureDef);
		
		body.setUserData(new BodyData("Enemy"));
		body.setFixedRotation(true);
	}
	
	public void update(float delta) {
		if (isActive) {
			shootTimer += delta;

			if (shootTimer > 3f && body.isActive()) {
				shoot();
				shootTimer = 0f;
			}

		}
	}
	
	private void shoot() {
		Bullet bullet = new Bullet(world, this, "Normal_Bullet", new Vector2(-100f / GameClass.PPM, 0));
		projectiles.add(bullet);
	}
	

}
