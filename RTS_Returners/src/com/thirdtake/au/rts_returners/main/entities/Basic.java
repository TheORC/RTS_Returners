package com.thirdtake.au.rts_returners.main.entities;

import com.thirdtake.au.rts_returners.main.Server.NetworkView;
import com.thirdtake.au.rts_returners.main.utils.Debug;
import com.thirdtake.au.rts_returners.main.utils.Vector3PlaceHolder;

public class Basic extends NetworkView {

	public Vector3PlaceHolder m_position = Vector3PlaceHolder.Zero();

	public void SetPosition(Vector3PlaceHolder position){
		//Debug.Log("Position Changed!");
		m_position = position;
	}
	
	public void Die(){
		Debug.Log("Entity( " + this.GetViewID() + ") died!");
	}
}
