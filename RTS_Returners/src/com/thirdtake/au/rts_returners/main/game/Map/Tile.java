package com.thirdtake.au.rts_returners.main.game.Map;

import com.Tylabobaid.Centaur.Main.Vector;
import com.thirdtake.au.rts_returners.main.game.Buildings.Building;
import com.thirdtake.au.rts_returners.main.game.Enums.TileTypes;

public class Tile {
	TileTypes type;
	boolean canBuild = true;
	Building building;
	boolean currentlyBuilding = false;
	boolean containsBuilding = false;
	
	int buildTime = 0;
	int maxBuildTime;
	
	public Tile(TileTypes _type){
		type = _type;
		if(!getTraversableOnFoot()){
			canBuild = false;
		}
	}
	
	public TileTypes getType(){
		return type;
	}
	
	public boolean getTraversableOnFoot(){ //Returns true if tile can be walked over
		if(!canBuild && building == null){
			return false;
		}else{
			return type.traversableOnFoot;
		}
	}
	public boolean getTraversableSwimming(){ //Returns true is tile is water
		return type.traversableSwimming;
	}
	public boolean getTraversableFlying(){ //Returns true if tile can be flown over
		return type.traversableFlying;
	}
	
	public boolean getCanBuild(){
		return canBuild;
	}
	
	public void tickBuilding(){
		if(buildTime < maxBuildTime){
			buildTime ++;
			
			building.setHealth((building.getMaxHealth()*buildTime)/maxBuildTime);
		}else{
			currentlyBuilding = false;
			if(building != null){
				building.tick();
				
				if(building.getHealth() <= 0){
					containsBuilding = false;
					building = null;
					canBuild = true;
				}
			}
		}
	}
	
	public boolean build(Building _building, Vector _position){
		if(canBuild){
			building = _building;
			building.setPosition(_position);
			canBuild = false;
			currentlyBuilding = true;
			maxBuildTime = building.getBuildTime();
			buildTime = 0;
			containsBuilding = true;
			
			return true;
		}
		
		return false;
	}
	
	public int getBuildTime(){
		return buildTime;
	}
	public int getMaxBuildTime(){
		return maxBuildTime;
	}
	public boolean getContainsBuilding(){
		return containsBuilding;
	}
	
	public Building getBuilding(){
		return building;
	}

	public boolean getCurrentlyBuilding() {
		return currentlyBuilding;
	}
}
