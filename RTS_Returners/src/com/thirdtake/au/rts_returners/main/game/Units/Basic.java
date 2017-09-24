package com.thirdtake.au.rts_returners.main.game.Units;

import com.thirdtake.au.rts_returners.main.Server.NetworkView;
import com.thirdtake.au.rts_returners.main.Utils.Debug;
import com.thirdtake.au.rts_returners.main.Utils.Vector3PlaceHolder;

public class Basic extends NetworkView {

	public Vector3PlaceHolder m_position = Vector3PlaceHolder.Zero();

	public void SetPosition(Vector3PlaceHolder position){
		m_position = position;
	}
	
	public void Say(String message){
		Debug.Log(message);
	}
	
	public void Die(){
		Debug.Log("Entity( " + this.GetViewID() + ") died!");
	}
}
