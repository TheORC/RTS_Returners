package com.thirdtake.au.rts_returners.main.Server;

import java.lang.reflect.InvocationTargetException;

import com.thirdtake.au.rts_returners.main.utils.Debug;

public class MessageHandler {
	
	public MessageHandler(){ }
	
	
	/*
	 * The message class contains the following
	 * 
	 * 1)  List of integers.
	 * 2)  List of strings.
	 * 3)  List of vectors.
	 * 
	 * An RPC call finds a networkView from it's ID and calls a method from it.
	 * 
	 * The method to be called is always the first string in GetStringVars.
	 * The id of the networkView is always the first int in GetIntVars.
	 */
	public void ProccessRPCMessage(NetworkMessage message){
		
		int senderID = message.SenderID();                        //The id of the RPC sender.
		
		int netViewID = message.GetMessage().getIntVars(0);
		String methodName = message.GetMessage().getStrigVars(0); //This is the name of the method we are trying to call.
		
		NetworkView view = LocalClient.GetOtherNetworkView(senderID, netViewID);
		if(view == null){
			Debug.LogWarning("Reeived an RPC for an networkView that does not exist!");
			return; //Exit.  We want nothing else to do with this.
			        //There are a number of reasons that this might occure.
			        //1) The server has not yet told us about this networkView.
			        //2) The networkView has been destoryed.
		}
		
		/*
		 * Using this information, attempt to call the method.
		 */
		try {
			view.CallRPC(methodName, null);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	

}
