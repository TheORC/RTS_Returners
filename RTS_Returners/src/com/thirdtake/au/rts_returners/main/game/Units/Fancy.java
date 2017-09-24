package com.thirdtake.au.rts_returners.main.game.Units;

import com.thirdtake.au.rts_returners.main.Server.NetworkView;
import com.thirdtake.au.rts_returners.main.Utils.Debug;
import com.thirdtake.au.rts_returners.main.Utils.Vector3PlaceHolder;

public class Fancy extends NetworkView{
	
	public Vector3PlaceHolder m_Position = Vector3PlaceHolder.Zero();
	
	public void BobTheBuilder(String building){
		Debug.LogError("Bob the Builder just built a " + building + ".");
	}
	
	public void SetPosition(Vector3PlaceHolder position){
		//Debug.Log("Position Changed!");
		m_Position = position;
	}

}
