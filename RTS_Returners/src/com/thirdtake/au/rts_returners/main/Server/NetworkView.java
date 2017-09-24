package com.thirdtake.au.rts_returners.main.Server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Hashtable;

import com.thirdtake.au.rts_returners.main.Utils.Debug;
import com.thirdtake.au.rts_returners.main.Utils.Stack;

/**
 * @author Oliver Clarke
 *
 * @ Every object that needs to be synced over the network has a network view.
 *   The network view contains information about who owns it.  It also has its
 *   own unique id for identification.
 *   
 *   Every client is responsible for setting the networkID for each of their
 *   networkViews.
 */
public class NetworkView {
	
	public static Stack<Integer> newNetworkIDS = null; //Set the limit to only 100 networkViews.
	
	private int networkID = -1; //By default all network ids are -1.
	private int ownerID   = -1; //The id of the client which owns this newworkView.
	
	private Hashtable<String, Method> rpcMethods = new Hashtable<>(); //Table containing references to previously called rpcs.
																	  //This is done to minimalize the use of reflections.

	public NetworkView(){ }
	
	/**
	 * @param methodName  The name of the method being called
	 * @param parameter   The parameters the method takes
	 * @return            This method return true if the method is called successfully, and false it it failed.
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public boolean ExecuteRPC(String methodName, Object parameter) {
		try{
			if(!IsMine()){ //If this is not mine, call RPC.
				if(rpcMethods.containsKey(methodName)){                  //Check to see if the method has already been hashed.
					Method m = rpcMethods.get(methodName);               //If so grab it.
					m.invoke(this, parameter);                           //Call the method, on this class and pass in the parameters.
				
				}else{   
					                                                                                        //This method has not previously been called.  It needs to be found.
					Method m = null;
					
					if(parameter == null){
						m = NetworkView.GetMethod(this.getClass(), methodName);    //Find a reference to it.
					}else{
						m = NetworkView.GetMethod(this.getClass(), methodName, parameter.getClass());
					}	
							
					if(m == null){                                                                          //Make sure a method was found.
						Debug.LogError("RPC tryed to call a method that does not exist");
						return false;
					}
					
					//The method was successfully found.  Store it.
					rpcMethods.put(methodName, m);                 //Add the method to the HashTable.
					if(parameter != null)
						m.invoke(this, parameter);                     //Call the method.
					else
						m.invoke(this);
				}
			}else{  //This is mine.  Send it to the server.
				byte[] rpcMessage = MessageCreator.CreateRPCMessage(methodName, this.GetViewID(), parameter);
				ServerSocket.Instance.SendMessage(rpcMessage);
			}
		}catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * @param ID - The id of the client checking for ownership.
	 * @return
	 */
	public boolean IsOwner(int ID){
		return ID == ownerID;
	}
	
	/**
	 * @return ID - Check if the local client controls this networkView.
	 */
	public boolean IsMine(){
		return LocalClient.MY_ID == this.ownerID;
	}
	
	/**
	 * @param ID - Set the networkView's ID
	 */
	public void SetViewID(int ID){
		this.networkID = ID;
	}
	
	/**
	 * @return  Gets this networkViews ID.
	 */
	public int GetViewID(){
		return this.networkID;
	}
	
	/**
	 * @param ID - Set the owners ID.
	 */
	public void SetOwnerID(int ID){
		this.ownerID = ID;
	}
	
	/**
	 * @return  Gets the networkViews owner ID.
	 */
	public int GetOwnerID(){
		return this.ownerID;
	}
	
	/**
	 * @return  Gets a new id for a networkView ID.
	 */
	public static int GetNextNetworkID(){
		
		if(NetworkView.newNetworkIDS == null){
			NetworkView.newNetworkIDS = new Stack<>(Integer.class, 100);
			for(int i = 0; i < NetworkView.newNetworkIDS.Capacity(); i++)
				NetworkView.newNetworkIDS.Push(i);
		}
		
		return NetworkView.newNetworkIDS.Pop();
	}
	
	/**
	 * @param instanceClass
	 * @param name
	 * @param parameterTypes
	 * @return
	 */
	public static Method GetMethod(Class<?> instanceClass, String methodName, Class<?>... parameterTypes) {
		Class<?> searchType = instanceClass;
		
		while (searchType != null) {
			Method[] methods = (searchType.isInterface() ? searchType.getMethods() : searchType.getDeclaredMethods());

	        for (Method method : methods) {
	        	if (methodName.equals(method.getName()) && (parameterTypes == null || Arrays.equals(parameterTypes, method.getParameterTypes()))) {
	        		return method;
	            }
	        }
	        searchType = searchType.getSuperclass();
		}
		
	    return null;
	}

}
