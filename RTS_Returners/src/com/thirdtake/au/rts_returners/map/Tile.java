package com.thirdtake.au.rts_returners.map;

import com.thirdtake.au.rts_returners.Buildings.Building;
import com.thirdtake.au.rts_returners.enums.TileTypes;

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
		}else{
			currentlyBuilding = false;
			if(building != null){
				containsBuilding = true;
				building.tick();
			}
		}
	}
	
	public boolean build(Building _building){
		if(canBuild){
			building = _building;
			canBuild = false;
			currentlyBuilding = true;
			maxBuildTime = building.getBuildTime();
			buildTime = 0;
			
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
}
