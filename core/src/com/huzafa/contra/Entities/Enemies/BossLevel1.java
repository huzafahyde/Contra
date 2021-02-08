package com.huzafa.contra.Entities.Enemies;

import java.util.ArrayList;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.physics.box2d.World;
import com.huzafa.contra.Entities.Projectile;
import com.huzafa.contra.Screens.GameScreen;

public class BossLevel1 extends BossCannon {

	public BossLevel1(World world, String type, RectangleMapObject object, ArrayList<Projectile> projectiles) {
		super(world, type, object, projectiles);
		// TODO Auto-generated constructor stub
		setHealth(10);
		
		
	}
	

	public void destroy() {
		world.destroyBody(body);
		GameScreen.LEVEL1_COMPLETED = true;
	}

}
