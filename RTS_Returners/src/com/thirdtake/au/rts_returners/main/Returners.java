package com.thirdtake.au.rts_returners.main;

import java.util.Scanner;

import com.thirdtake.au.rts_returners.main.Server.LocalClient;
import com.thirdtake.au.rts_returners.main.Server.ServerSocket;
import com.thirdtake.au.rts_returners.main.Utils.Debug;
import com.thirdtake.au.rts_returners.main.Utils.Vector3PlaceHolder;
import com.thirdtake.au.rts_returners.main.game.Enums.EntityTypes;
import com.thirdtake.au.rts_returners.main.game.Units.Basic;

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
		
		Basic bas = null;
		
		String input = "";
		String[] commandArgs = new String[] {""};
		Scanner scanner = new Scanner(System.in);
		while(!commandArgs[0].equalsIgnoreCase("q")){	
			
			input = scanner.nextLine();
			commandArgs = input.split(" ");
			
			if(commandArgs[0].equalsIgnoreCase("rpc")){
				if(commandArgs.length > 1){
					
					String methodName = commandArgs[1];
					
					if(bas == null)
						bas = (Basic)LocalClient.Instantiate(EntityTypes.BASIC, LocalClient.MY_ID, Vector3PlaceHolder.Zero(), -1);
					
					if(commandArgs.length > 2){
						
						String message = "";
						for(int i = 2; i < commandArgs.length; i++)
							message += commandArgs[i] + " ";
												
						bas.ExecuteRPC(methodName, message);
					}
					else
						bas.ExecuteRPC(commandArgs[1], null);
					
				}else{
					Debug.LogWarning("RPC command takes 'rpc <method name> [optional parameter]'");
				}
			}else if(commandArgs[0].equalsIgnoreCase("new")){
				bas = (Basic)LocalClient.Instantiate(EntityTypes.BASIC, LocalClient.MY_ID, Vector3PlaceHolder.Zero(), -1);
			}
		}
		
		scanner.close();	
		socket.StopServer();
		Stop();	
	}
	
	public static void Stop(){
		Debug.Log(BUILD_NAME + " finished.");
	}
}
