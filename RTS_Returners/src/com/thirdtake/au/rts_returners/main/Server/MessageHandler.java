package com.thirdtake.au.rts_returners.main.Server;

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
	 * The id of the networkView is always the first integer in GetIntVars.
	 */
	public void ProccessRPCMessage(NetworkMessage message){
		
		int senderID = message.SenderID();                        //The id of the RPC sender.
		
		int netViewID = message.GetMessage().getIntVars(0);
		String methodName = message.GetMessage().getStringVars(0); //This is the name of the method we are trying to call.
		
		NetworkView view = LocalClient.GetOtherNetworkView(senderID, netViewID);
		if(view == null){
			Debug.LogWarning("Received an RPC for an networkView that does not exist!");
			return; //Exit.  We want nothing else to do with this.
			        //There are a number of reasons that this might occur.
			        //1) The server has not yet told us about this networkView.
			        //2) The networkView has been destroyed.
		}
		
		/*
		 * Using this information, attempt to call the method.
		 * 
		 * An RPC can only send a single variable at a time.
		 * This makes it easier to find out of a parameter has been sent.
		 */
		Object param = null;
		if(message.GetMessage().getIntVarsCount() > 1){
			param = message.GetMessage().getIntVars(1); //Remember that the viewID value is 0.
		}
		
		if(message.GetMessage().getStringVarsCount() > 1){
			param = message.GetMessage().getStringVars(1); //Remember that the the methodName  value is 0.
		}
		
		//Debug.Log("Proccessing RPC: Sender ID (" + senderID + ") NetworkView ID (" + netViewID + ") Method Name (" + methodName + ").");
		
		//Debug.LogWarning("TODO: (MessagHandler) allow for the parsing of parameters into the RPC call");
		view.ExecuteRPC(methodName, param);
	}
	
	

}
