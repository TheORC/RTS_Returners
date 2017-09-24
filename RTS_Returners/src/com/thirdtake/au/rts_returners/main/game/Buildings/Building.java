package com.thirdtake.au.rts_returners.main.game.Buildings;

import java.awt.Image;
import java.awt.image.BufferedImage;

import com.Tylabobaid.Centaur.Main.Vector;

public class Building {
	Vector position;
	int maxHealth;
	int health;
	int buildTime;
	int buildCost;
	String name;
	BufferedImage sprite;
	
	public Building(String _name, int _maxHealth, int _buildTime, int _buildCost, BufferedImage _sprite){
		name = _name;
		maxHealth = _maxHealth;
//		health = maxHealth;
		buildTime = _buildTime;
		buildCost = _buildCost;
		sprite = _sprite;
	}
	
	public void tick(){
		
	}
	
	public int getBuildTime(){
		return this.buildTime;
	}
	
	public int getBuildCost(){
		return this.buildCost;
	}
	
	public String getName(){
		return name;
	}

	public int getMaxHealth() {
		return maxHealth;
	}
	
	public int getHealth() {
		return health;
	}

	public Image getSprite() {
		return sprite;
	}

	public void setHealth(int i) {
		this.health = i;
	}
	
	public void action1(){
		
	}
	public void action2(){
		
	}
	public void action3(){
		
	}
	public void action4(){
		
	}
	public void action5(){
		
	}
	public void action6(){
		
	}
	public void action7(){
		
	}
	public void action8(){
		
	}
	public void action9(){
		
	}

	public void setPosition(Vector _position) {
		position = _position;
	}
	
}
