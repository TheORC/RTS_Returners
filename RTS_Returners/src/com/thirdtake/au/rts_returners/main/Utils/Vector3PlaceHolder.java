package com.thirdtake.au.rts_returners.main.Utils;

public class Vector3PlaceHolder {
	
	public static Vector3PlaceHolder Zero(){
		return new Vector3PlaceHolder(0, 0, 0);
	}
	
	private int x;
	private int y;
	private int z;
	
	public Vector3PlaceHolder(int _x, int _y, int _z){
		this.x = _x;
		this.y = _y;
		this.z = _z;
	}
	
	public int GetX(){
		return x;
	}
	
	public int GetY(){
		return y;
	}
	
	public int GetZ(){
		return z;
	}
	
	public void SetX(int value){
		this.x = value;
	}
	
	public void SetY(int value){
		this.y = value;
	}
	
	public void SetZ(int value){
		this.z = value;
	}
	
	
}
