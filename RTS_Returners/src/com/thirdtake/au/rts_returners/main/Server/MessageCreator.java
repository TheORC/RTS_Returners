package com.thirdtake.au.rts_returners.main.Server;

import com.thirdtake.au.rts_returners.main.Server.NetMessage.ErrorType;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.Header;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.Header.Builder;
import com.thirdtake.au.rts_returners.main.Utils.Debug;
import com.thirdtake.au.rts_returners.main.Utils.Vector3PlaceHolder;
import com.thirdtake.au.rts_returners.main.game.Entities.EntityTypes;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.Message;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.MessageHeader;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.MessageType;
import com.thirdtake.au.rts_returners.main.Server.NetMessage.Vector;

public class MessageCreator {

	/**
	 * @ This method construct an RPC message which can be sent to the c#
	 * server.
	 * 
	 * @param messageBytes
	 *            - This can be any byte[] java compiled or not. The c# server
	 *            does not handle this information.
	 * @return byte[]
	 */
	public static byte[] CreateMessage(MessageType messageType, byte[] messageBytes) {

		// if(messageBytes != null && messageBytes.length > 100)
		// Debug.LogError("Message sizes are exceeding 100 bytes! The server is
		// not built to handle this");

		byte[] _mB = messageBytes; // Create a copy of the byte array

		/*
		 * Create a header containing information about the message.
		 */
		Builder mHB = Header.newBuilder(); // Create a builder
		mHB.setSenderID(LocalClient.MY_ID); // Set the sender id to this local
											// client

		if (messageBytes != null)
			mHB.setMessageLength(_mB.length);// Set the length of the message.
		else
			mHB.setMessageLength(0); // Set the length of the message to zero.

		mHB.setMessageType(messageType); // Set the message type. This means the
											// server does not handle any logic.
											// Rather it just passes along the
											// message.
		Header _H = mHB.build(); // Build the class
		byte[] _mHeader = _H.toByteArray(); // Get the byte array for the
											// header.

		/*
		 * Create a header containing information about the message header.
		 */
		MessageHeader mHeader = MessageHeader.newBuilder().setHeaderLength(_mHeader.length).build(); // Create
																										// the
																										// header.
		byte[] _HeaderBytes = mHeader.toByteArray(); // Convert to byte array.

		// Debug.Log("First header length: " + _HeaderBytes.length + " Message
		// header length: " + _mHeader.length);

		if (_HeaderBytes.length != NetworkMessage.HEADER_BUFFER_SIZE) { // We
																		// have
																		// the
																		// wrong
																		// number
																		// of
																		// bytes
																		// in
																		// the
																		// header
			Debug.LogError("Wrong number of bytes in a message header! " + _HeaderBytes.length);
		}

		byte[] _message = null;

		if (messageBytes != null)
			_message = new byte[_HeaderBytes.length + _mHeader.length + _mB.length]; // Create
																						// the
																						// byte
																						// array
																						// for
																						// the
																						// final
																						// message.
		else
			_message = new byte[_HeaderBytes.length + _mHeader.length]; // Create
																		// the
																		// byte
																		// array
																		// for
																		// the
																		// final
																		// message.

		System.arraycopy(_HeaderBytes, 0, _message, 0, _HeaderBytes.length);
		System.arraycopy(_mHeader, 0, _message, _HeaderBytes.length, _mHeader.length); // Copy
																						// the
																						// header
																						// to
																						// the
																						// start
																						// of
																						// the
																						// message
		if (messageBytes != null)
			System.arraycopy(_mB, 0, _message, _HeaderBytes.length + _mHeader.length, _mB.length); // Append
																									// the
																									// message
																									// to
																									// the
																									// rest
																									// of
																									// the
																									// array.
		return _message;
	}

	/**
	 * @param messageType
	 *            Sets what type of RPC this is.
	 * @param methodName
	 *            Sets the method to be called. This is left blank for
	 *            Instantiate calls.
	 * @param viewID
	 *            Sets the ID of the networkView.
	 * @param parameter
	 *            Allows the attachment of any value.
	 * @return
	 */
	public static byte[] CreateRPCMessage(String methodName, int viewID, Object parameter) {

		// Create the basic message.
		Message.Builder mBuilder = Message.newBuilder();
		mBuilder.addStringVars(methodName);// We do not need to include the
		mBuilder.addIntVars(viewID); // Add the networkView ID so it can be found on the other clients.

		if (parameter != null) { // We are sending a variable.
			if (parameter instanceof Integer) { // See if the parameter is a
												// integer.
				mBuilder.addIntVars((int) parameter);
			} else if (parameter instanceof String) { // See if the parameter is
														// a string.
				mBuilder.addStringVars((String) parameter);
			} else if (parameter instanceof Vector3PlaceHolder) { // Set if the parameter is a vector.
				
				Vector.Builder vBuilder = Vector.newBuilder();
				vBuilder.setX(((Vector3PlaceHolder)parameter).GetX());
				vBuilder.setY(((Vector3PlaceHolder)parameter).GetY());
				vBuilder.setZ(((Vector3PlaceHolder)parameter).GetZ());

				mBuilder.addVectorVars(vBuilder.build());
			} else {
				Debug.LogError("Attempting to send an RPC with a parameter type that is not allowed!");
				return null;
			}
		}

		byte[] messageBytes = mBuilder.build().toByteArray();

		Header.Builder hBuilder = Header.newBuilder();
		hBuilder.setMessageLength(messageBytes.length);
		hBuilder.setMessageType(MessageType.RPC);
		hBuilder.setSenderID(LocalClient.MY_ID);
		hBuilder.setErrorType(ErrorType.SUCCESS);

		byte[] headerBytes = hBuilder.build().toByteArray();

		MessageHeader.Builder mhBuilder = MessageHeader.newBuilder();
		mhBuilder.setHeaderLength(headerBytes.length);

		byte[] messageHeaderBytes = mhBuilder.build().toByteArray();

		byte[] finalMessage = new byte[messageBytes.length + headerBytes.length + messageHeaderBytes.length];
		System.arraycopy(messageHeaderBytes, 0, finalMessage, 0, messageHeaderBytes.length);
		System.arraycopy(headerBytes, 0, finalMessage, messageHeaderBytes.length, headerBytes.length);
		System.arraycopy(messageBytes, 0, finalMessage, messageHeaderBytes.length + headerBytes.length, messageBytes.length);

		return finalMessage;
	}

	public static byte[] CreateInstantiateMessage(EntityTypes entity, int viewID, Vector position) {
		/*
		 * Instance message format.
		 * 
		 * Integer(0)    = the networkView ID 
		 * Integer(1)    = the entity ID 
		 * vector(0)     = the networkView Position
		 */

		Message.Builder mBuilder = Message.newBuilder();
		mBuilder.addIntVars(viewID);
		mBuilder.addIntVars(entity.GetID());
		mBuilder.addVectorVars(position);
		byte[] messageBytes = mBuilder.build().toByteArray();

		Header.Builder hBuilder = Header.newBuilder();
		hBuilder.setMessageType(MessageType.INSTANTIATE);
		hBuilder.setMessageLength(messageBytes.length);
		hBuilder.setSenderID(LocalClient.MY_ID);
		hBuilder.setErrorType(ErrorType.SUCCESS);

		byte[] headerBytes = hBuilder.build().toByteArray();

		MessageHeader.Builder mhBuilder = MessageHeader.newBuilder();
		mhBuilder.setHeaderLength(headerBytes.length);

		byte[] messageHeaderBytes = mhBuilder.build().toByteArray();

		byte[] finalMessage = new byte[messageBytes.length + headerBytes.length + messageHeaderBytes.length];
		
		System.arraycopy(messageHeaderBytes, 0, finalMessage, 0, messageHeaderBytes.length);
		System.arraycopy(headerBytes, 0, finalMessage, messageHeaderBytes.length, headerBytes.length);
		System.arraycopy(messageBytes, 0, finalMessage, messageHeaderBytes.length + messageBytes.length,
				messageBytes.length);

		return finalMessage;
	}

}
