package com.thirdtake.au.rts_returners.main.entities;

import com.thirdtake.au.rts_returners.main.utils.Debug;

public enum EntityTypes {
	
	//Format (ID, Type)
	BASIC(0, Basic.class), FANCY(1, Fancy.class);
	
	private int ID;
	private Class<?> c;
	
	EntityTypes(int _ID, Class<?> _c){
		ID = _ID;
		c = _c;
	}
	
	public Class<?> GetEntityClass(){
		return c;
	}
	
	public int GetID(){
		return ID;
	}
	
	public static EntityTypes GetFromID(int id){
		for(EntityTypes e : values()){
			if(e.ID == id) return e;
		}
		Debug.LogWarning("Failed to find entity with id: " + id);

		return null;
	}

}