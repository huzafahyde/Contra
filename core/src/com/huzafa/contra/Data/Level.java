package com.huzafa.contra.Data;

import java.util.ArrayList;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.huzafa.contra.GameClass;
import com.huzafa.contra.Entities.Player;
import com.huzafa.contra.Entities.Projectile;
import com.huzafa.contra.Entities.Enemies.BossCannon;
import com.huzafa.contra.Entities.Enemies.BossLevel1;
import com.huzafa.contra.Entities.Enemies.Enemy;
import com.huzafa.contra.Entities.Enemies.Rifleman;
import com.huzafa.contra.Entities.Enemies.Soldier;
import com.huzafa.contra.Entities.Enemies.Turret;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class Level {

	private ArrayList<Body> bodies;

	private World world;

	private TiledMap map;

	private ArrayList<Enemy> enemies;
	private ArrayList<Projectile> projectiles;

	private Player player;

	public Level(World world, TiledMap map, ArrayList<Enemy> enemies, ArrayList<Projectile> projectiles,
			Player player) {
		this.world = world;
		this.map = map;
		this.enemies = enemies;
		this.projectiles = projectiles;
		this.player = player;

		bodies = new ArrayList<Body>();

		generateLevel();
		generateEnemies();
	}

	public void update() {
		for (int i = 0; i < bodies.size(); i++) {
			if (((BodyData) bodies.get(i).getUserData()).destroyable) {
				world.destroyBody(bodies.get(i));
				bodies.remove(i);
			}
		}
	}

	public void generateLevel() {
		MapObjects mapObjects = map.getLayers().get("Platform").getObjects();

		// For every rectangle in the TiledMap:
		// Create a new static body for each one
		// Add it to the array of bodies
		for (RectangleMapObject object : mapObjects.getByType(RectangleMapObject.class)) {

			Shape shape = getRectangle(object, false);

			BodyDef bDef = new BodyDef();
			bDef.type = BodyType.StaticBody;

			Body body = world.createBody(bDef);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.density = 1f;
			fixtureDef.restitution = 0f;
			fixtureDef.filter.categoryBits = GameClass.WORLD_ENTITY;
			fixtureDef.filter.maskBits = GameClass.GROUNDCONTACT_ENTITY;

			Fixture fixture = body.createFixture(fixtureDef);

			body.setUserData(new BodyData("Platform"));
			bodies.add(body);

			shape.dispose();
		}

		mapObjects = map.getLayers().get("Static").getObjects();

		// For every rectangle in the TiledMap:
		// Create a new static body for each one
		// Add it to the array of bodies
		for (RectangleMapObject object : mapObjects.getByType(RectangleMapObject.class)) {

			Shape shape = getRectangle(object, false);

			BodyDef bDef = new BodyDef();
			bDef.type = BodyType.StaticBody;

			Body body = world.createBody(bDef);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = shape;
			fixtureDef.density = 1f;
			fixtureDef.restitution = 0f;
			fixtureDef.filter.categoryBits = GameClass.WORLD_ENTITY;
			fixtureDef.filter.maskBits = GameClass.GROUNDCONTACT_ENTITY;

			Fixture fixture = body.createFixture(fixtureDef);

			body.setUserData(new BodyData("Static"));

			if (object.getName() != null) {
				if (object.getName().equals("Destroyable")) {
					((BodyData) body.getUserData()).destroyable = true;
				}
			}

			bodies.add(body);

			shape.dispose();
		}
	}

	private PolygonShape getRectangle(RectangleMapObject rectangleObject, boolean localCenter) {
		// Converts RectangleMapObject to PolygonShape
		Rectangle rectangle = rectangleObject.getRectangle();
		PolygonShape polygon = new PolygonShape();
		Vector2 center;

		if (!localCenter) {
			center = new Vector2((rectangle.x + rectangle.width * 0.5f) / GameClass.PPM,
					(rectangle.y + rectangle.height * 0.5f) / GameClass.PPM);
		} else {
			center = new Vector2(0, rectangle.height / 3f / GameClass.PPM);
		}
		polygon.setAsBox(rectangle.width * 0.5f / GameClass.PPM, rectangle.height * 0.5f / GameClass.PPM, center, 0.0f);
		return polygon;
	}

	private void generateEnemies() {
		MapObjects mapObjects = map.getLayers().get("Enemies").getObjects();

		for (RectangleMapObject object : mapObjects.getByType(RectangleMapObject.class)) {
			if (object.getProperties().get("Type").equals("Soldier")) {

				Soldier soldier = new Soldier(world, "Soldier", object, projectiles);
				enemies.add(soldier);
			} else if (object.getProperties().get("Type").equals("Rifleman")) {
				Rifleman rifleman = new Rifleman(world, "Rifleman", object, projectiles, player);
				enemies.add(rifleman);
			} else if (object.getProperties().get("Type").equals("Turret")) {
				Turret turret = new Turret(world, "Turret", object, projectiles, player);
				enemies.add(turret);
			} else if (object.getProperties().get("Type").equals("Boss_Cannon")) {
				BossCannon bossCannon = new BossCannon(world, "Boss_Cannon", object, projectiles);
				enemies.add(bossCannon);
			} else if (object.getProperties().get("Type").equals("Boss")) {
				BossLevel1 boss = new BossLevel1(world, "Boss", object, projectiles);
				enemies.add(boss);
			}
		}
	}
}
