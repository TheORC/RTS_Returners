package com.thirdtake.au.rts_returners.main;

import java.util.Scanner;

import com.thirdtake.au.rts_returners.main.Server.ServerSocket;
import com.thirdtake.au.rts_returners.utils.Debug;

/*
 * @Author: Oliver Clarke
 * @Date: 17/09/2017
 * @Discription:  This is the main method for the Returners RTS game.  It handles the initialization of the applications.
 */
public class Returners {
	
	public static final String BUILD_ID = "v0.1";
	public static final String BUILD_NAME = "Returners";
	
	public static final Boolean DEBUG = false;

	
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		Debug.Log(BUILD_NAME + " Version: " + BUILD_ID + " starting...");
		
		ServerSocket socket = new ServerSocket("localhost", 8000);
		
		String input = "";
		Scanner scanner = new Scanner(System.in);
		while(!input.equalsIgnoreCase("q")){	
			
			input = scanner.nextLine().toLowerCase();
			
//			socket.SendTestRPC(input);
		}
		
		scanner.close();
		socket.StopServer();
		Stop();	
	}
	
	public static void Stop(){
		Debug.Log(BUILD_NAME + " finished.");
	}
}
