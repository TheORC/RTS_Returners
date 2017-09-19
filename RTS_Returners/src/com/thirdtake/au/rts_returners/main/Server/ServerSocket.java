package com.thirdtake.au.rts_returners.main.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.thirdtake.au.rts_returners.main.Server.NetMessage.Header;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.Message;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.Message.Builder;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.Message.MessageType;
import com.thirdtake.au.rts_returners.utils.Debug;

/**
 * @author Oliver
 * @Description
 * <b>This class is used for the bare bones communication to the server</b>
 */
public class ServerSocket {
	
	private String m_IP = "localhost"; //Ip of the server
	private int m_PORT = 8000;  //The port the server used.
	
	private DatagramSocket m_Socket = null; //The socket to the server.
	private InetAddress m_Host = null;
	
	/**
	 * @param ip - The ip of the server being connected to.
	 * @param port - The port which the server is listening on.
	 */
	public ServerSocket(String ip, int port){
		this.m_IP = ip;
		this.m_PORT = port;
		
		Debug.Log("Starting up internal server.");
				
		try {
			this.m_Socket = new DatagramSocket();
			this.m_Host = InetAddress.getByName(m_IP);
			
			HandShake();
			Listener();

		} catch (IOException e) {
			Debug.LogError(e.getMessage());
		}
	}

	/**
	 * This method is used to start communication with the server.
	 * @throws IOException 
	 */
	private void HandShake() throws IOException{
		
		Debug.Log("Begining handshake...");
		
		/*
		 * Use protobuf to compile a login message.
		 */		
		
		Builder mB = Message.newBuilder(); //Create a new instance of 'Message' class.
		mB.setSenderID(-1); //Set the sender.
							//Be default this will set to -1, as the sender has no id.
		mB.setMessageType(MessageType.LOGIN); //Set the type of message.
		mB.addIntVars(999999999);
		
		Message _M = mB.build(); //Construct the message. 
		
		byte[] _mMessage = _M.toByteArray(); //Convert the message into a byte[].
		
		if(_mMessage.length > 100)
			Debug.LogError("Message sizes are exceeding 100 bytes!  The server is not built to handle this");
		
		com.thirdtake.au.rts_returners.main.Server.NetMessage.Header.Builder mHB = Header.newBuilder();
		mHB.setMessageLength(_mMessage.length);
				
		Header _H = mHB.build();
		
		byte[] _mHeader = _H.toByteArray();
		
		byte[] _message = new byte[_mHeader.length + _mMessage.length];
		System.arraycopy(_mHeader, 0, _message, 0, _mHeader.length);
		System.arraycopy(_mMessage, 0, _message, _mHeader.length, _mMessage.length);

		
		Debug.Log("Header size: " + _mHeader.length + " Message size: " + _message.length);
		
		DatagramPacket _packet = new DatagramPacket(_message, _message.length, m_Host, m_PORT); //Construct the message to be sent.
		m_Socket.send(_packet); //Send the packet.
	}
	
	/**
	 * This starts a thread, which will listen for messages from the server.
	 * @throws IOException 
	 */
	private void Listener() throws IOException{
		while(true){
			Debug.Log("Waiting for a response...");
			//now receive reply
            //buffer to receive incoming data
            byte[] buffer = new byte[1024];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            m_Socket.receive(reply);
            
            byte[] headerBytes = new byte[2];
            System.arraycopy(buffer, 0, headerBytes, 0, headerBytes.length);
            
            Header header = Header.parseFrom(headerBytes);
            
            int messageLength = header.getMessageLength();
            
            byte[] messageBytes = new byte[messageLength];
            System.arraycopy(buffer, headerBytes.length, messageBytes, 0, messageLength);
                        
            Message _Message = Message.parseFrom(messageBytes);
            Debug.Log("Received message from the server!");
            Debug.Log("Send by: " + _Message.getSenderID() + " Message type: " + _Message.getMessageType());
            
            Debug.Log("Id: " + _Message.getIntVars(0));
            Debug.Log("Seed: " + _Message.getIntVars(1));
		}
	}
	
}
