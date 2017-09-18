package com.thirdtake.au.rts_returners.enums
;

public enum TileTypes {
	GROUND(true, false, true), WATER(false, true, false), ROCKS(false, false, true), SAND(true, false, false);
	
	public boolean traversableOnFoot;
	public boolean traversableSwimming;
	public boolean traversableFlying;
	TileTypes(boolean ft, boolean wt, boolean at){
		traversableOnFoot = ft;
		traversableSwimming = wt;
		traversableFlying = at;
	}
}