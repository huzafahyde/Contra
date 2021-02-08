package com.huzafa.contra.Entities.Enemies;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.huzafa.contra.GameClass;
import com.huzafa.contra.Data.BodyData;
import com.huzafa.contra.Entities.Projectile;

public class Enemy {
	protected World world;

	private float health;

	protected Animation<AtlasRegion> currentAnimation;

	protected TextureAtlas atlas;

	protected Body body;

	protected Sprite sprite;

	protected float stateTime = 0f;
	protected float tick = 0f;

	protected ArrayList<Projectile> projectiles;

	protected boolean flipped = false, isActive = true;

	protected String type;

	public Enemy(World world, String type, RectangleMapObject object, ArrayList<Projectile> projectiles) {
		// TODO Auto-generated constructor stub
		this.world = world;
		this.projectiles = projectiles;
		this.type = type;

		if (type.equals("Boss_Cannon") | type.equals("Boss")) {
			atlas = new TextureAtlas("Enemies/Rifleman/rifleman.txt");
		} else {
			atlas = new TextureAtlas("Enemies/" + type + "/" + type.toLowerCase() + ".txt");
		}

		if (type.equals("Soldier")) {
			sprite = atlas.createSprite("Miscellaneous");
			sprite.setSize(10f / GameClass.PPM, 30f / GameClass.PPM);
		} else if (type.equals("Rifleman")) {
			sprite = atlas.createSprite("Misc");
			sprite.setSize(10f / GameClass.PPM, 30f / GameClass.PPM);
		} else if (type.equals("Turret")) {
			sprite = atlas.createSprite("turret", 1);
			sprite.setSize(32f / GameClass.PPM, 32f / GameClass.PPM);
		} else if (type.equals("Boss_Cannon") | type.equals("Boss")) {
			sprite = new Sprite(new Texture("badlogic.jpg"));
			sprite.setSize(0f / GameClass.PPM, 0f / GameClass.PPM);
		}

		if (type.equals("Soldier")) {
			currentAnimation = new Animation<AtlasRegion>(1 / 4f, atlas.findRegions("Running"));

			health = 3f;
		}
	}

	public void destroy() {
		world.destroyBody(body);
	}

	public void damage(float damage) {
		health -= damage;

		if (health <= 0f) {
			((BodyData) body.getUserData()).setFlaggedForDelete(true);
		}
	}

	protected PolygonShape getRectangle(RectangleMapObject rectangleObject, boolean localCenter) {
		// Converts RectangleMapObject to PolygonShape
		Rectangle rectangle = rectangleObject.getRectangle();
		PolygonShape polygon = new PolygonShape();
		Vector2 center;

		if (!localCenter) {
			center = new Vector2((rectangle.x + rectangle.width * 0.5f) / GameClass.PPM,
					(rectangle.y + rectangle.height * 0.5f) / GameClass.PPM);
		} else {
			center = new Vector2((rectangle.width * 0.5f) / GameClass.PPM, (rectangle.height * 0.5f) / GameClass.PPM);
		}
		polygon.setAsBox(rectangle.width * 0.5f / GameClass.PPM, rectangle.height * 0.5f / GameClass.PPM, center, 0.0f);

		// System.out.println(center);
		return polygon;
	}

	public void draw(SpriteBatch batch) {
		sprite.draw(batch);
	}

	public void update(float delta) {

	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	public float getHealth() {
		return health;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public boolean isFlipped() {
		return flipped;
	}

	public void setFlipped(boolean flipped) {
		this.flipped = flipped;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
