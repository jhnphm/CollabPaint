package cx.ath.feck.jchat.server;


import cx.ath.feck.jchat.shared.ClientData;
import cx.ath.feck.jchat.shared.ControlMessage;

import cx.ath.feck.jchat.shared.Message;
import cx.ath.feck.jchat.shared.PaintDataMessage;
import cx.ath.feck.jchat.shared.PaintMessage;
import cx.ath.feck.jchat.shared.Status;
import cx.ath.feck.jchat.shared.TextMessage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Class for processing data sent to and from clientData.
 * @author John Pham
 *
 */
public class ClientWorker {
	private ServerCore server;
	private Socket socket;
	private InputThread inputThread;
	private OutputThread outputThread;
	private BlockingQueue<Message> messageQueue;
	private ClientData clientData;
	private boolean loggedIn = false;
	private boolean ignoringMessages = false;
	private boolean stopped = false;
	
	/**
	 * Constructor for ClientConnection. Requires a ServerCore to exist in objRegistry named "server".
	 *
	 * @param socket Socket to client
	 * @param server ServerCore instance.
	 */
	public ClientWorker(Socket socket, ServerCore server) {
		this.server = server;
		this.socket = socket;
	}
	/**
	 * Starts I/O threads for communicating w/ client
	 */
	public void start(){
		inputThread = new InputThread();
		outputThread = new OutputThread();
		messageQueue = new LinkedBlockingQueue<Message>();
		inputThread.start();
		outputThread.start();
	}
	/**
	 * Cleans up, closes sockets, and kills threads for client
	 */
	public void stop(){
		if(!stopped){	//Check if stop already called
			stopped = true;
			if(loggedIn){
				server.removeClientWorker(this);
			}
			inputThread.quit();
			outputThread.quit();
			try{
				socket.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Sends a Message to all other clients
	 * @param message Message to send to other clients
	 */
	public void sendMessage(Message message){
		messageQueue.add(message);
	}
	/**
	 * Runnable to handle input from client to server
	 * @author John Pham
	 *
	 */
	private class InputThread extends Thread{
		boolean stop = false ;
		ObjectInputStream in;
		/**
		 * Waits for clientData input and writes to server thread.
		 */
		public void run()  {
			Message message;
			ControlMessage cmessage;
			try {
				in = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
				
			} catch (IOException e) {
				e.printStackTrace();
				stop = true;
			}
			
			//Loop, wait for input
			while(!stop){
				if(socket.isClosed()){
					break;
				}
				try {
					message = (Message)in.readObject();
				} catch (IOException e) {
					//e.printStackTrace();
					break;
				} catch (ClassNotFoundException e) {
					//e.printStackTrace();
					break;
				}
				if(message.getClass() == ControlMessage.class){
					cmessage = (ControlMessage)message;
					if(!cmessage.isValid()){
						continue;
					}
					controlMessageHandler(cmessage);
					
				}else{
					if(message.getClass() == TextMessage.class || message.getClass() == PaintDataMessage.class){
						server.sendMessage(message);
					}
				}
			}
			quit();
			return;
		}
		/**
		 * Deals w/ control messages.
		 * @param cmessage
		 */
		private void controlMessageHandler(ControlMessage cmessage) {
			switch(cmessage.getMessage()){
				case ControlMessage.LOGIN:
					if(!loggedIn){ //first check if already logged in
						setIgnoringMessages(true);
						ClientWorker.this.clientData = (ClientData)cmessage.getData();
						
						//Return appropriate login confirmation
						
						//Duplicate username
						if( ClientWorker.this.clientData.getUsername().equals("")||
								!server.checkUsername(ClientWorker.this.clientData.getUsername())){
							ClientWorker.this.sendMessage(new ControlMessage(ControlMessage.LOGINCONFIRM,
									Status.FAILINVALID,ServerCore.SERVER_CLIENTID));
							break;
							
							//Successful
						}else if(ClientWorker.this.clientData.getPassword() == null){
							ClientWorker.this.sendMessage(new ControlMessage(ControlMessage.LOGINCONFIRM,
									Status.SUCCESS,ServerCore.SERVER_CLIENTID));
							
							//Success w/ admin
						}else if(server.checkPassword(ClientWorker.this.clientData.getPassword())){
							ClientWorker.this.sendMessage(new ControlMessage(ControlMessage.LOGINCONFIRM,
									Status.SUCCESSWITHADMIN,ServerCore.SERVER_CLIENTID));
							
							//Fail to gain admin
						}else{
							ClientWorker.this.sendMessage(new ControlMessage(ControlMessage.LOGINCONFIRM,
									Status.FAILGAINADMIN,ServerCore.SERVER_CLIENTID));
							break;
						}
						
						//Erase password
						ClientWorker.this.clientData.killPassword();
						
						
						
						//Add server to list of logged in users.
						server.addClientWorker(ClientWorker.this);
						
						//Assign a clientID
						ClientWorker.this.sendMessage(new ControlMessage(ControlMessage.ASSIGNCLIENTID,
								ClientWorker.this.clientData.getClientID(), ServerCore.SERVER_CLIENTID));
						
						for (TextMessage i :server.getTextMessageHist()){
							ClientWorker.this.sendMessage(i);
						}
						//TODO: Option for batch/merge paint messages, for fast loading 
						for (PaintMessage i :server.getPaintMessageHist()){
							ClientWorker.this.sendMessage(i);
						}
						
						
						setIgnoringMessages(false);
						loggedIn = true;
					}
					break;
				case ControlMessage.QUIT:
					//TODO Perform a clean D/C
				default:
					
			}
		}
		/**
		 * Stops thread and cleans up open streams.
		 *
		 */
		public void quit(){
			if(stop){
				return;
			}
			stop = true;
			try{
				in.close();
			}catch (IOException e){
				System.err.println("Could not close input stream");
				e.printStackTrace();
			}
			ClientWorker.this.stop();
		
		}
		
	}
	/**
	 * Runnable to handle output from server to client
	 * @author John Pham
	 *
	 */
	private class OutputThread extends Thread{
		boolean stop = false;
		ObjectOutputStream out;
		
		/**
		 * Waits for input from server, and sends data to clientData
		 */
		@Override
		public void run() {
			
			Message msg;
			
			try {
				out = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
				stop = true;
			}
			while(!stop){
				if(socket.isClosed()){
					break;
				}
				try {
					msg = messageQueue.take();
				} catch (InterruptedException e) {
					//e.printStackTrace();
					break;
				}
				try {
					out.writeObject(msg);
					out.flush();
				} catch (IOException e) {
					//e.printStackTrace();
					break;
				}
			}
			quit();
			return;
		}
		/**
		 * Stops thread and cleans up open streams.
		 *
		 */
		public void quit(){
			//Exit if previously called, to prevent infinite recursion from clientWorker() stop method
			if(stop){
				return;
			}
			stop = true;
			try {
				out.close();
			} catch (IOException e) {
				//e.printStackTrace();
			}
			ClientWorker.this.stop();
		}
		
	}
	/**
	 * Returns ClientData belonging to the worker.
	 * @return ClientData attached to this worker.
	 */
	public ClientData getClientData(){
		return this.clientData;
	}
	
	/**
	 * Returns true if ClientWorker is ignoring all noncontrol messages.
	 * @return true if ClientWorker is ignoring all noncontrol messages.
	 */
	public boolean isIgnoringMessages() {
		return ignoringMessages;
	}
	
	/**
	 * Set to true if ClientWorker is ignoring non ControlMessage (s).
	 * @param ignoringMessages True if ClientWorker is ignoring non ControlMessage (s).
	 */
	public void setIgnoringMessages(boolean ignoringMessages) {
		this.ignoringMessages = ignoringMessages;
	}
	
	
	
}
