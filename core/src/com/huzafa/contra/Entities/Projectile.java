package com.huzafa.contra.Entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public interface Projectile {
	public void generateBullet(String type);
	public void act(float delta);
	public Sprite getSprite();
	public Vector2 getPosition();
	public float getTimer();
	public Body getBody();
	public void destroy();
	public float getWorldTimer();
}
