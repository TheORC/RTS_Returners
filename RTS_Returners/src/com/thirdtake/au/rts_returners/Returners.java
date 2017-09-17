package com.thirdtake.au.rts_returners;

/*
 * @Author: Oliver Clarke
 * @Date: 17/09/2017
 * @Discription:  This is the main method for the Returners RTS game.  It handles the initialization of the applications.
 */
public class Returners {
	
	public static final String BUILD_ID = "v0.1";
	public static final String BUILD_NAME = "Returners";

	
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		System.out.println(BUILD_NAME + " Version: " + BUILD_ID + " starting...");
		
		Stop();
		
	}
	
	public static void Stop(){
		System.out.println(BUILD_NAME + " finished.");
	}
}
