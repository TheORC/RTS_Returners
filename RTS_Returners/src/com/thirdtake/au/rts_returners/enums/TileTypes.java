package com.thirdtake.au.rts_returners.enums
;

public enum TileTypes {
	GROUND("Ground", true, false, true), WATER("Water", false, true, false), ROCKS("Rocks", false, false, true), SAND("Sand", true, false, false);
	
	public boolean traversableOnFoot;
	public boolean traversableSwimming;
	public boolean traversableFlying;
	public String name;
	
	TileTypes(String _name, boolean ft, boolean wt, boolean at){
		traversableOnFoot = ft;
		traversableSwimming = wt;
		traversableFlying = at;
		name = _name;
	}
	public String getName() {
		return name;
	}
}