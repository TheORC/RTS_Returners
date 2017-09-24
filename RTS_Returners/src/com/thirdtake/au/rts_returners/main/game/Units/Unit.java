package com.thirdtake.au.rts_returners.main.game.Units;

import com.Tylabobaid.Centaur.Collisions.BoundaryBox;
import com.Tylabobaid.Centaur.Main.Vector;
import com.thirdtake.au.rts_returners.main.game.Enums.UnitTypes;

@SuppressWarnings("serial")
public class Unit extends BoundaryBox {
	
	private UnitTypes type;
	private int movementSpeed = 1;
	private int damage = 1;
	private int attackCooldown = 5;
	private int attackRange = 10;
	private int maxHealth = 5;
	private int health;
	private String name = "Unit";
	
	private boolean attackMoving = false;
	private boolean attacking = false;
	
	Vector position;
	Vector destination;
	private Vector direction;
	
	public Unit(UnitTypes _type, String _name, int _movementSpeed, int _damage, int _attackCooldown, int _attackRange, int _health){
		super();
		setup(0, 0, 10, 10);
		
		type = _type;
		name = _name;
		movementSpeed = _movementSpeed;
		damage = _damage;
		attackCooldown = _attackCooldown;
		attackRange = _attackRange;
		maxHealth = _health;
		health = _health;
	}
	
	public void move(){
		if(position != destination){
			direction = Vector.direction(destination, position);
			direction.normalize();
			
			direction.setVector(new Vector(direction.getX()*movementSpeed, direction.getY()*movementSpeed));
	
			position.setVector(new Vector(position.getX()+direction.getX(), position.getY()+direction.getY()));
			
			rect.setLocation((int) position.getX()-5,(int) position.getY()-5);
		}
	}

	/**
	 * @return  Gets the attack damage of the unit.
	 */
	public int GetAttackDamage(){
		return damage;
	}
	
	/**
	 * @return  Gets the attack cool down of the unit.
	 */
	public int GetAttackCoolDown(){
		return attackCooldown;
	}
	
	/**
	 * @return  Gets the attack range of the unit.
	 */
	public int GetAttackRange(){
		return attackRange;
	}
	
	/**
	 * @return  Tells if the unit can attack while moving.
	 */
	public boolean IsAttaceMove(){
		return attackMoving;
	}
	
	/**
	 * @return  Tells if the unit is currently attacking.
	 */
	public boolean IsAttacking(){
		return attacking;
	}
	
	
	public Vector getPosition() {
		return position;
	}

	public int getHealth() {
		return health;
	}

	public int getMaxHealth() {
		return maxHealth;
	}

	public void setDestination(Vector _destination) {
		destination = _destination;
	}

	public UnitTypes getType() {
		return this.type;
	}

	public String getName() {
		return name;
	}
}
