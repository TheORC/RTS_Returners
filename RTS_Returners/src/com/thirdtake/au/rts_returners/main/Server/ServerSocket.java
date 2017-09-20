package com.thirdtake.au.rts_returners.main.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.thirdtake.au.rts_returners.main.Server.NetMessage.ErrorType;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.Message;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.MessageType;
import com.thirdtake.au.rts_returners.utils.Debug;

/**
 * @author Oliver
 * @Description
 * <b>This class is used for the bare bones communication to the server</b>
 */
public class ServerSocket extends Thread{
	
	private String m_IP = "localhost"; //Ip of the server
	private int m_PORT = 8000;  //The port the server used.
	
	public static boolean CONNECTED = false;
	private boolean m_ServerRunning = false;
	
	private DatagramSocket m_Socket = null; //The socket to the server.
	private InetAddress m_Host = null;
		
	/**
	 * @param ip - The IP of the server being connected to.
	 * @param port - The port which the server is listening on.
	 */
	public ServerSocket(String ip, int port){
		this.m_IP = ip;
		this.m_PORT = port;
		
		
		Debug.Log("Starting up internal server.");
				
		try {
						
			this.m_Socket = new DatagramSocket();
			this.m_Host = InetAddress.getByName(m_IP);
			
			Debug.Log("Begining handshake...");
			HandShake();
			
			Debug.Log("Waiting for a response...");
			start();

		} catch (IOException e) {
			Debug.LogError(e.getMessage());
		}
	}
	
	/**
	 * This starts a thread, which will listen for messages from the server.
	 * @throws IOException 
	 */
	@Override
	public void run() {
		m_ServerRunning = true;
		while(m_ServerRunning){	
			try{
				//Now receive reply
	            //buffer to receive incoming data
				byte[] buffer = new byte[1024];
	            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
	            m_Socket.receive(reply);  //The code pauses here until a message is received.
	            
	            NetworkMessage message = new NetworkMessage(buffer); //Get the encoded messages.
	            
	            //Debug.Log("Received message from the server!");
	            //Debug.Log("Send by: " + message.SenderID() + " Message type: " + message.MessageType());
	            
	            //The only time login gets called is when the server send us back information.
	            //This is must be trying to send important information.
	            
	            switch(message.MessageType()){      
	            case LOGIN:
	            	if(message.ErrorType() == ErrorType.SUCCESS){
		            	
	            		int clientID = message.GetMessage().getIntVars(0);  //Get the id the server sent
	            		int worldSeed = message.GetMessage().getIntVars(1); //Get the world seed
	            	
	            		LocalClient.ID = clientID;
	            	
	            		Debug.Log("Received login detials from the server!");
	            		Debug.Log("Client ID: " + clientID);
	            		Debug.Log("World seed: " + worldSeed);

	            	}else{
	            		Debug.LogError("Failed to connect to the server.  Server is full!");
	            		m_ServerRunning = false;
	            	}
	            	break;
	            case RPC:
	            	
	            	Debug.Log("Received: " + message.GetMessage().getStrigVars(0));
	            	break;
				default:
					break;
	            }
	            
			}catch(IOException e){
				Debug.LogError("Something went wrong in the listening thread!");
        		m_ServerRunning = false;
			}
		}
	}

	/**
	 * This method is used to start communication with the server.
	 * @throws IOException 
	 */
	private void HandShake() throws IOException{
		/*
		 * Use ProtoBuf to compile a login message.
		 */
		byte[] _HandShakeBytes = MessageCreator.CreateMessage(MessageType.LOGIN, null);                       //Create a handshake message.
		DatagramPacket _packet = new DatagramPacket(_HandShakeBytes, _HandShakeBytes.length, m_Host, m_PORT); //Construct the message to be sent.
		m_Socket.send(_packet);                                                                               //Send the packet.
	}
	
	public void stopServer()
    {
        m_ServerRunning = false;
        this.interrupt();
    }
	
	public void SendTestRPC(String message){	
		Message.Builder ms = Message.newBuilder();
		ms.addStrigVars(message);
		
		byte[] information = ms.build().toByteArray();
		byte[] _packetBytes  = MessageCreator.CreateMessage(MessageType.RPC, information);
		
		DatagramPacket _packet = new DatagramPacket(_packetBytes, _packetBytes.length, m_Host, m_PORT);
		try {
			m_Socket.send(_packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}