package com.thirdtake.au.rts_returners.main.game.Buildings;

import com.Tylabobaid.Centaur.Main.Vector;
import com.thirdtake.au.rts_returners.main.Manager;
import com.thirdtake.au.rts_returners.main.Utils.Images;
import com.thirdtake.au.rts_returners.main.game.Units.WorkerUnit;

public class Base extends Building{
	
	public Base(){
		super("HQ", 1000,  120, 500, Images.baseSprite);
	}
	
	@Override
	public void tick(){
		
	}
	
	@Override
	public void action1(){
		Manager.inGameManager.localPlayerUnits.add(new WorkerUnit(new Vector(position.getX(), position.getY()), new Vector((float) (position.getX()+(100*Math.random())-50), (float) (position.getY()+(100*Math.random())-50))));
	}
}
