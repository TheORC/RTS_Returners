package com.thirdtake.au.rts_returners.map;

import com.thirdtake.au.rts_returners.enums.TileTypes;

public class Tile {
	TileTypes type;
	
	public Tile(TileTypes _type){
		type = _type;
	}
	
	public TileTypes getType(){
		return type;
	}
	
	public boolean getTraversableOnFoot(){ //Returns true if tile can be walked over
		return type.traversableOnFoot; //Will need to be modified when buildings are added
	}
	public boolean getTraversableSwimming(){ //Returns true is tile is water
		return type.traversableSwimming;
	}
	public boolean getTraversableFlying(){ //Returns true if tile can be flown over
		return type.traversableFlying;
	}
}
