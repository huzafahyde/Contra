package com.huzafa.contra.Data;

public class BodyData {
	private String name;
	private boolean flaggedForDelete, explode;
	private boolean hit;
	private float damage;
	public boolean destroyable;

	// When creating userData for any body it takes in only one object
	// By making a new class you can input multiple data types into one userData
	
	public BodyData(String name) {
		// TODO Auto-generated constructor stub
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isFlaggedForDelete() {
		return flaggedForDelete;
	}

	public void setFlaggedForDelete(boolean flaggedForDelete) {
		this.flaggedForDelete = flaggedForDelete;
	}

	public boolean isExplode() {
		return explode;
	}

	public void setExplode(boolean explode) {
		this.explode = explode;
	}

	public boolean isHit() {
		return hit;
	}

	public void setHit(boolean hit) {
		this.hit = hit;
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

}
