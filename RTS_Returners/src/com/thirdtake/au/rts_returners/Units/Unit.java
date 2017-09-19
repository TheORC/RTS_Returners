package com.thirdtake.au.rts_returners.Units;

import com.thirdtake.au.rts_returners.enums.UnitTypes;

public class Unit {
	private UnitTypes type;
	private int movementSpeed = 2;
	private int damage = 1;
	private int attackCooldown = 5;
	private int attackRange = 10;
	private int health = 5;
	private String name = "Unit";
	
	public Unit(){
		
	}
}
