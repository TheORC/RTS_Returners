package com.thirdtake.au.rts_returners.main.Server;

import com.google.protobuf.InvalidProtocolBufferException;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.ErrorType;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.Header;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.MessageType;
import com.thirdtake.au.rts_returners.main.utils.Debug;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.Message;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.MessageHeader;

/**
 * @author Oliver Clarke
 * @ This class is a place holder for all messages that come back from the server.
 */
public class NetworkMessage {
	
	public static final int HEADER_BUFFER_SIZE = 2;  //An integer that hold the information about the size of the header.
	
	private Header m_Header = null;
	private Message m_Message = null;
	
	/**
	 * @param _data - The byte[] returned from the server socket.
	 */
	public NetworkMessage(byte[] _data) {
		
		try{
			byte[] mBytes = _data; //Byte array containing the message.
			
			byte[] _messageHeaderBytes = new byte[HEADER_BUFFER_SIZE];                       //Byte array containing information about this message.  
	        System.arraycopy(mBytes, 0, _messageHeaderBytes, 0, _messageHeaderBytes.length); //Get the header information from mBytes.
	        MessageHeader _MHeader = MessageHeader.parseFrom(_messageHeaderBytes);            //Get a reference to the header object

	        /*
	         * Using the MessageHeader, it is possible to get
	         * the information about the incoming message.
	         */
	        int _HeaderLength = _MHeader.getHeaderLength();  								      //Get the length of the message information.
	        byte[] _HeaderBytes = new byte[_HeaderLength];  									  //Create an array to hold the message info bytes.
	        System.arraycopy(mBytes, _messageHeaderBytes.length, _HeaderBytes, 0, _HeaderLength); //Copy the information into the array.
	        m_Header = Header.parseFrom(_HeaderBytes);
	        
	        /*
	         * Using the Header, it is possible to find
	         * the information regarding the message
	         */
	        
	        int _MessageLength = m_Header.getMessageLength();      //Get the length of the incoming message.
	        
	        if(_MessageLength != 0){                               //Make sure the message size is not zero.
	        	byte[] _MessageBytes = new byte[_MessageLength];                                                              //Construct an array to hold the message.
		        System.arraycopy(mBytes, _messageHeaderBytes.length + _HeaderBytes.length, _MessageBytes, 0, _MessageLength); //Cope the message to the message array.
		                    
		        m_Message = Message.parseFrom(_MessageBytes);  //Create the message.
	        }
	        
		} catch (InvalidProtocolBufferException e) {
			Debug.LogError(e.getMessage());
		}
	}
	
	/**
	 * @return  The ID of the person who sent this message.
	 */
	public int SenderID(){
		return m_Header.getSenderID();
	}
	
	/**
	 * @return  Returns the type of message this is.
	 */
	public MessageType MessageType(){
		return m_Header.getMessageType();
	}
	
	public ErrorType ErrorType(){
		return m_Header.getErrorType();
	}
	
	/**
	 * @return  Returns a reference to the Message class, so that variables can be grabed from it.
	 */
	public Message GetMessage(){
		return m_Message;
	}
}
