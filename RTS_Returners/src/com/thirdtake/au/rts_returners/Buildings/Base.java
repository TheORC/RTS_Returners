package com.thirdtake.au.rts_returners.Buildings;

import com.Tylabobaid.Centaur.Main.Vector;
import com.thirdtake.au.rts_returners.main.Images;
import com.thirdtake.au.rts_returners.main.Manager;

public class Base extends Building{
	
	public Base(String _name, int _maxHealth, Vector _position, int _buildTime, int _buildCost){
		super(_name, _maxHealth, _position, _buildTime, _buildCost, Images.baseSprite);
	}
	
	@Override
	public void tick(){
		
	}
	
	@Override
	public void action1(){
		System.out.println(getName());
		Manager.inGameManager.setMouseBuilding(new Base(name, maxHealth, position, 1000, 20));
	}
}
