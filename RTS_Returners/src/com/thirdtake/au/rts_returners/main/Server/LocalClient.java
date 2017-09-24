package com.thirdtake.au.rts_returners.main.Server;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.thirdtake.au.rts_returners.main.Server.NetMessage.Vector;
import com.thirdtake.au.rts_returners.main.Utils.Debug;
import com.thirdtake.au.rts_returners.main.Utils.Vector3PlaceHolder;
import com.thirdtake.au.rts_returners.main.game.Enums.EntityTypes;

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
	
	/**
	 * @param type      This is the type of networkView being spawned
	 * @param ownerID   This is the ID of this networkView's owner 
	 * @param position  This is the position of the networkView.
	 * @param viewID    This is the unique ID of this networkView.
	 * @return         	Return the newly create networkView or null.
	 */
	public static NetworkView Instantiate(EntityTypes type, int ownerID, Vector3PlaceHolder position, int viewID){
				
		if(!IS_ONLINE){
			Debug.LogWarning("Attempting to spawn a networkView while not online");
			return null;
		}
		
		try {
			
			EntityTypes eType = type; //Find the entity type
			
			//Make sure the entity to be spawned exists.
			if(eType == null){
				Debug.LogError("Attempted to spawn anentity which does not exist!");
				return null;
			}
			
			//Debug.Log("Spawning: " + eType.toString());		
			
			Class<?> object = eType.GetEntityClass();               //Get a reference to its type
			Object obj = object.newInstance();                      //Spawn the networkView.
			NetworkView netView = (NetworkView)obj;
			netView.SetOwnerID(ownerID);                            //Set the id of the networkView.
			netView.SetViewID(viewID);
			
			/*
			 * After spawning the network view, a number of things needs to happen.
			 * First we need to add it to either otherNetViews or myNetViews.
			 * Next we need to set its position
			 */
			
			//This return true, if the local client instantiated the object.
			if(netView.IsMine()){
				//IF the local client spawned the object, we need to alert the server.
				
				//TODO: Assign a unique ID.
				int networkViewID = NetworkView.GetNextNetworkID(); //Get the next ID.
				netView.SetViewID(networkViewID);
				myNetViews.add(netView);
				
				Vector.Builder _position = Vector.newBuilder();
				_position.setX(position.GetX());
				_position.setY(position.GetY());
				_position.setZ(position.GetZ());
				
				//TODO: Send message to server.
				byte[] instantiateBytes = MessageCreator.CreateInstantiateMessage(eType, networkViewID, _position.build());
				ServerSocket.Instance.SendMessage(instantiateBytes);
				
			}else{
				//We don't own this networkView meaning the server sent it to us.
				//Add it to otherNetViews.
				
				List<NetworkView> views = null;
				
				if(!otherNetViews.containsKey(ownerID)){                      //Check to see if this ID already has networkViews
					//Debug.Log("OtherNetViews does not contain owner.  Creating a reference to (" + ownerID + ").");
					otherNetViews.put(ownerID, new ArrayList<NetworkView>()); //If not create a new instance.
				}
					
				views = otherNetViews.get(ownerID);                          //Get the list
				views.add(netView);                                          //Add the networkView
				otherNetViews.put(ownerID, views);                           //Put the list back into the HashTable.
			}
			
			/*
			 * Handle logic regarding the netView, such as positions and stuff.
			 */
			netView.ExecuteRPC("SetPosition", position);
			
			//Everything has finished return the new networkView.
			return netView; //Return the spawned networkView.
			
		} catch (InstantiationException | IllegalAccessException e) {
			Debug.LogError("Attempted to spawn a network object without a NetworkView!");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @param owernID
	 * @param viewID
	 * @return NetworkView
	 * @ Is used to get another clients networkView.
	 */
	public static NetworkView GetOtherNetworkView(int owernID, int viewID){
		
		if(!IS_ONLINE){ //Make sure we are connected to the server before handling any logic.
			Debug.LogWarning("Accessing online networkViews offline!");
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
			Debug.LogWarning("Attempting to access networkViews offline!");
		}
		
		for(NetworkView view : myNetViews)
			if(view.GetViewID() == viewID)
				return view;
		return null;
	}

}
