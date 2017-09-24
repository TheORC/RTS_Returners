package com.thirdtake.au.rts_returners.main.Utils;

public class Debug {
	
	/**
	 * @param message
	 * @ Send a message to the System.
	 */
	public static void Log(String message){
		System.out.println("[LOG] " + message);
	}
	
	/**
	 * @param errorMessage
	 * @ Send an error message to the System.
	 */
	public static void LogError(String errorMessage){
		System.out.println("[ERROR] " + errorMessage);
	}
	
	/**
	 * @param warningMessage
	 * @ Send a warning message to the System.
	 */
	public static void LogWarning(String warningMessage){
		System.out.println("[WARNING] " + warningMessage);
	}
}
