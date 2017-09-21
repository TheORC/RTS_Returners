package com.thirdtake.au.rts_returners.main.Server;

import com.thirdtake.au.rts_returners.main.Server.NetMessage.Header;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.Header.Builder;
import com.thirdtake.au.rts_returners.main.utils.Debug;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.MessageHeader;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.MessageType;

public class MessageCreator {
	
	/**
	 * @ This method construct an RPC message which can be sent to the c# server.
	 * @param messageBytes - This can be any byte[] java compiled or not.  The c# server does not handle this information.
	 * @return byte[]
	 */
	public static byte[] CreateMessage(MessageType messageType, byte[] messageBytes){
		
		//if(messageBytes != null && messageBytes.length > 100)
		//	Debug.LogError("Message sizes are exceeding 100 bytes!  The server is not built to handle this");
			
		byte[] _mB = messageBytes;           //Create a copy of the byte array
				
		/*
		 * Create a header containing information about the message.
		 */
		Builder mHB = Header.newBuilder();   //Create a builder
		mHB.setSenderID(LocalClient.MY_ID);     //Set the sender id to this local client
			
		if(messageBytes != null)
			mHB.setMessageLength(_mB.length);//Set the length of the message.
		else
			mHB.setMessageLength(0);         //Set the length of the message to zero.
			
		mHB.setMessageType(messageType);     //Set the message type.  This means the server does not handle any logic.
		                                     //Rather it just passes along the message.		
		Header _H = mHB.build();             //Build the class
		byte[] _mHeader = _H.toByteArray();  //Get the byte array for the header.
			
		/*
		 * Create a header containing information about the message header.
		 */
		MessageHeader mHeader = MessageHeader.newBuilder().setHeaderLength(_mHeader.length).build(); //Create the header.
		byte[] _HeaderBytes = mHeader.toByteArray();                                                 //Convert to byte array.
			
		//Debug.Log("First header length: " + _HeaderBytes.length + " Message header length: " + _mHeader.length);
			
		if(_HeaderBytes.length != NetworkMessage.HEADER_BUFFER_SIZE){           //We have the wrong number of bytes in the header
			Debug.LogError("Wrong number of bytes in a message header! " + _HeaderBytes.length);
		}
			
		byte[] _message = null;
		
		if(messageBytes != null)
			_message = new byte[_HeaderBytes.length + _mHeader.length + _mB.length];              //Create the byte array for the final message.
		else
			_message = new byte[_HeaderBytes.length + _mHeader.length];                           //Create the byte array for the final message.
		
		System.arraycopy(_HeaderBytes, 0, _message, 0, _HeaderBytes.length);
		System.arraycopy(_mHeader, 0, _message, _HeaderBytes.length, _mHeader.length);             //Copy the header to the start of the message
		if(messageBytes != null)
			System.arraycopy(_mB, 0, _message, _HeaderBytes.length + _mHeader.length, _mB.length); //Append the message to the rest of the array.
		return _message;
	}

}
