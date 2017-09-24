package com.thirdtake.au.rts_returners.main.game.Units;

import com.Tylabobaid.Centaur.Main.Vector;
import com.thirdtake.au.rts_returners.main.game.Enums.UnitTypes;

public class WorkerUnit extends Unit{
	
	public WorkerUnit(Vector _position, Vector _destination){
		super(UnitTypes.MELEE, "WORKER", 1, 1, 60, 50, 5);
		this.destination = _destination;
		this.position = _position;
	}
}
