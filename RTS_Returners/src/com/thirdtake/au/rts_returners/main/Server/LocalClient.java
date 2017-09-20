package com.thirdtake.au.rts_returners.main.Server;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.thirdtake.au.rts_returners.utils.Debug;

/**
 * @author Oliver Clarke
 * 
 * @ The LocalClient class contains information about THIS client.
 *   It has references to all networkViews in the game.
 *
 */
public class LocalClient {
	
	public static boolean IS_ONLINE = false; // Check to see if we are online.
	
	public static int MY_ID = 1; //The default id is always 1. (Negative numbers when compiled are large than 10 bytes)
							     //When a positive number is at a max 2 bytes.
	
	public static LocalClient Instance;
	
	/*
	 * This has table contains references to every other clients networkViews.
	 * These references are stored so that RPCs can be called on them later.
	 */
	private static Hashtable<Integer, List<NetworkView>> otherNetViews = new Hashtable<Integer, List<NetworkView>>();
	
	/*
	 * This list contains references to all of MY networkViews.
	 */
	private static List<NetworkView> myNetViews = new ArrayList<NetworkView>();
	
	/*
	 * Create an instance of the LocalClient.
	 * This only gets called when the client has connected to the server.
	 */
	public static void StartLocalClient(){
		Instance = new LocalClient();
	}
	
	/*
	 * Instantiate the LocalClient.
	 */
	public LocalClient(){
		
		if(Instance != null)
			Debug.LogError("Attempting to create multiple LocalClients, which is not allowed!");
		else
			Instance = this;
	}
	
	/*
	 * 
	 */
	public static void Instantiate(Class<?> object){
		try {
			NetworkView netView = (NetworkView) object.newInstance();
			
			
			
			
			
		} catch (InstantiationException | IllegalAccessException e) {
			Debug.LogError("Attemtped to spawn a network object without a NetworkView!");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param owernID
	 * @param viewID
	 * @return NetworkView
	 * @ Is used to get another clients networkView.
	 */
	public static NetworkView GetOtherNetworkView(int owernID, int viewID){
		
		if(!IS_ONLINE){ //Make sure we are connected to the server before handelig any logic.
			Debug.LogWarning("Attempting to acces networkViews offline!");
			return null;
		}
		
		List<NetworkView> otherViews = otherNetViews.get(owernID);
		
		for(NetworkView view : otherViews) {
			if(view.GetViewID() == viewID)
				return view;
		}
		
		return null;
	}
	
	/**
	 * @param viewID
	 * @return NetworkView
	 * @ Used to find MY networkViews
	 */
	public static NetworkView GetMyNetworkView(int viewID){
		
		if(!IS_ONLINE){
			Debug.LogWarning("Attempting to acces networkViews offline!");
			return null;
		}
		
		for(NetworkView view : myNetViews)
			if(view.GetViewID() == viewID)
				return view;
		return null;
	}

}
