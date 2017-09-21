package com.thirdtake.au.rts_returners.main.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.thirdtake.au.rts_returners.main.Server.NetMessage.ErrorType;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.MessageType;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.Vector;
import com.thirdtake.au.rts_returners.main.entities.EntityTypes;
import com.thirdtake.au.rts_returners.main.utils.Debug;
import com.thirdtake.au.rts_returners.main.utils.Vector3PlaceHolder;

/**
 * @author Oliver
 * @Description
 * <b>This class is used for the bare bones communication to the server</b>
 */
public class ServerSocket extends Thread{
	
	public static ServerSocket Instance = null;
	
	private String m_IP = "localhost"; //Ip of the server
	private int m_PORT = 8000;  //The port the server used.
	
	private boolean m_ServerRunning = false;
	
	private DatagramSocket m_Socket = null; //The socket to the server.
	private InetAddress m_Host = null;
	
	private MessageHandler m_MessageHandler = null;
		
	/**
	 * @param ip - The IP of the server being connected to.
	 * @param port - The port which the server is listening on.
	 */
	public ServerSocket(String ip, int port){
		
		Instance = this;
		
		this.m_IP = ip;
		this.m_PORT = port;
		this.m_MessageHandler = new MessageHandler();
		
		
		Debug.Log("Starting up internal server.");
				
		try {
						
			this.m_Socket = new DatagramSocket();
			this.m_Host = InetAddress.getByName(m_IP);
			
			Debug.Log("Begining handshake...");
			BeginHandShake();
			
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
	            	
	            		Debug.Log("Received login detials from the server!");
	            		Debug.Log("Client ID: " + clientID);
	            		Debug.Log("World seed: " + worldSeed);
	            		
	            		/*
	            		 * Start the localClient and go online.
	            		 */
	            		LocalClient.MY_ID = clientID;   //Set MY clients ID.
	            		LocalClient.IS_ONLINE = true;   //Set the client into online mode.
	            		
	            		LocalClient.StartLocalClient();
	            		
	            		//TODO: Implement game logic.

	            	}else{
	            		Debug.LogError("Failed to connect to the server.  Server is full!");
	            		m_ServerRunning = false;
	            	}
	            	break;
	            case RPC:
	            	m_MessageHandler.ProccessRPCMessage(message);
	            	break;
	            case INSTANTIATE:
	            	
	            	int ownerID       = message.SenderID();
	            	int networkViewID = message.GetMessage().getIntVars(0);  //Get the id the server sent
	            	EntityTypes type  = EntityTypes.GetFromID(message.GetMessage().getIntVars(1));
	            	Vector vector     = message.GetMessage().getVectorVars(0);
	            	Vector3PlaceHolder position = new Vector3PlaceHolder(vector.getX(), vector.getY(), vector.getZ()); 
	            	
	            	//This is called when we needs to instantiate something on the local client.
	            	LocalClient.Instantiate(type, ownerID, position, networkViewID);
	            	
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
	private void BeginHandShake() throws IOException{
		/*
		 * Use ProtoBuf to compile a login message.
		 */
		byte[] _HandShakeBytes = MessageCreator.CreateMessage(MessageType.LOGIN, null);                       //Create a handshake message.
		DatagramPacket _packet = new DatagramPacket(_HandShakeBytes, _HandShakeBytes.length, m_Host, m_PORT); //Construct the message to be sent.
		m_Socket.send(_packet);                                                                               //Send the packet.
	}
	
	/**
	 * This method is used to send messages to the server.
	 * @param messagebytes
	 */
	public void SendMessage(byte[] messagebytes){
		DatagramPacket _packet = new DatagramPacket(messagebytes, messagebytes.length, m_Host, m_PORT); //Construct the message to be sent.
		try {
			m_Socket.send(_packet);
		} catch (IOException e) {
			e.printStackTrace();
		}     
	}
	
	/**
	 *  Stop the server listening thread.
	 */
	public void StopServer()
    {
        m_ServerRunning = false;
        this.interrupt();
    }
}
