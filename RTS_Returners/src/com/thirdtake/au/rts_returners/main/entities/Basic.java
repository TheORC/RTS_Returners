package com.thirdtake.au.rts_returners.main.entities;

import com.thirdtake.au.rts_returners.main.Server.NetworkView;
import com.thirdtake.au.rts_returners.main.utils.Vector3PlaceHolder;

public class Basic extends NetworkView {

	public Vector3PlaceHolder m_position = Vector3PlaceHolder.Zero();

	public void SetPosition(Vector3PlaceHolder position){
		m_position = position;
	}
}
