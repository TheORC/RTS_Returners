package com.thirdtake.au.rts_returners.Buildings;

import com.Tylabobaid.Centaur.Main.Vector;

public class Building {
	Vector position;
	int maxHealth;
	int health;
	int buildTime;
	int buildCost;
	String name;
	
	public Building(String _name, int _maxHealth, Vector _position, int _buildTime, int _buildCost){
		name = _name;
		maxHealth = _maxHealth;
		position = _position;
		buildTime = _buildTime;
		buildCost = _buildCost;
	}
	
	public void tick(){
		
	}
	
	public int getBuildTime(){
		return this.buildTime;
	}
	
	public int getBuildCost(){
		return this.buildCost;
	}
}
