package cx.ath.feck.jchat.server;

import cx.ath.feck.jchat.shared.PaintMessage;
import cx.ath.feck.jchat.shared.TextMessage;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cx.ath.feck.jchat.shared.ClientData;
import cx.ath.feck.jchat.shared.ControlMessage;
import cx.ath.feck.jchat.shared.Message;




/**
 * Does heavy lifting of the server, and also stores data on all connected clientWorkers.
 * @author John Pham
 *
 */
public class ServerCore implements Runnable {
	/**
	 * Client ID of server, which should be 0
	 */
	public static final int SERVER_CLIENTID = 0;
	private Map<Integer,ClientWorker> clientWorkers;
	private BlockingQueue<Message> messageQueue;
	private String	password;
	private List<TextMessage> textMessageHistList;
	private List<PaintMessage> paintMessageHistList;
	private Map<String, ClientData> clientDataHistMap;
	int id = 1;
	
	/**
	 * Admin password to set
	 * @param password Password to set
	 */
	public ServerCore(String password){
		this.password = password;
		clientWorkers = Collections.synchronizedMap(new HashMap<Integer,ClientWorker>());
		messageQueue = new LinkedBlockingQueue<Message>();
		textMessageHistList = Collections.synchronizedList(new LinkedList<TextMessage>());
		paintMessageHistList = Collections.synchronizedList(new LinkedList<PaintMessage>());
		clientDataHistMap = Collections.synchronizedMap(new HashMap<String, ClientData>());
	}
	/**
	 * Empties queue filled by the ClientWorker instances and sends them back to all other ClientWorkers
	 */
	public void run(){
		while(true){
			Message message = null;
			try {
				message = messageQueue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for(ClientWorker c :clientWorkers.values()){
				if(!c.isIgnoringMessages()){
					//Don't send message if it comes from ourself, unless text 
					//(since text is small and we want to know if it actually arrived as a disconnect indicator. 
					if(message.getClientID() != c.getClientData().getClientID() ||
							message.getClass() == TextMessage.class){
						c.sendMessage(message);
					}
				}
			}
		}
	}
	/**
	 * Adds client to list of connected clientWorkers
	 * @param clientWorker ClientWorker to add
	 */
	public void addClientWorker(ClientWorker clientWorker){
		//Check if already in system, if so assign old client ID
		ClientData data = clientDataHistMap.get(clientWorker.getClientData().getUsername());
		if(data == null){
			clientWorker.getClientData().setClientID(this.getNextID());
		}else{
			clientWorker.getClientData().setClientID(data.getClientID());
		}
		
		clientWorkers.put(clientWorker.getClientData().getClientID(),clientWorker);
		clientDataHistMap.put(clientWorker.getClientData().getUsername(),clientWorker.getClientData());
		//Send list of clients to added client first (required before dumping all old messages).
		sendClientData(clientWorker);
		//Sends list of clients to all clients using server thread
		sendClientData();
	}
	/**
	 * Removes client from list of connected clientWorkers
	 * @param clientWorker ClientWorker to remove
	 */
	public void removeClientWorker(ClientWorker clientWorker){
		clientWorkers.remove(clientWorker.getClientData().getClientID());
		//Send list of all connected clients.
		sendClientData();
	}
	/**
	 * Sends message to server thread.
	 * @param message Message to send to server.
	 */
	public void sendMessage(Message message){
		messageQueue.add(message);
		//If text message, add to textMessagehistory
		if(message.getClass() == TextMessage.class){
			getTextMessageHist().add((TextMessage)message);
		}
		//Use instanceof since paint message clases are really 2 different types w/ a superclass
		if(message instanceof PaintMessage){
			getPaintMessageHist().add((PaintMessage)message);
		}
	}
	/**
	 * Returns true if password is valid
	 * @param password Password to check
	 * @return True if valid password
	 */
	public boolean checkPassword(String password){
		if(this.password.equals(password)){
			return true;
		}
		return false;
	}
	/**
	 * Returns an available ID number.
	 * @return
	 */
	private int getNextID() {
		return id++;
	}
	/**
	 * Returns true if valid unused username.
	 * @param username Username to test if used
	 * @return True if allowable username.
	 */
	public boolean checkUsername(String username) {
		for(ClientWorker c: clientWorkers.values()){
			if(c.getClientData().getUsername().equals(username)){
				return false;
			}
		}
		return true;
	}
	/**
	 * Sends list of connected clients and all clients ever connected since last reset to all clients.
	 */
	public void sendClientData() {
		
		//Lazy, just duplicated code
		Map<Integer, ClientData> map = new HashMap<Integer,ClientData>();
		Map<Integer, ClientData> map2 = new HashMap<Integer,ClientData>();
		for(ClientWorker i : clientWorkers.values()){
			map.put(i.getClientData().getClientID(), i.getClientData());
		}
		sendMessage(new ControlMessage(ControlMessage.SHOWOTHERCURRENTCLIENTS,
				map,
				SERVER_CLIENTID));
		for(ClientData i : clientDataHistMap.values()){
			map2.put(i.getClientID(), i);
		}
		sendMessage(new ControlMessage(ControlMessage.SHOWALLCLIENTS,
				map2,
				SERVER_CLIENTID));
	}
		/**
	 * Sends list of connected clients and all clients ever connected since last reset to specified worker
	* @param clientWorker  ClientWorker to send list of connected clients to. 
	 */
	public void sendClientData(ClientWorker clientWorker) {
		Map<Integer, ClientData> map = new HashMap<Integer,ClientData>();
		Map<Integer, ClientData> map2 = new HashMap<Integer,ClientData>();
		for(ClientWorker i : clientWorkers.values()){
			map.put(i.getClientData().getClientID(), i.getClientData());
		}
		clientWorker.sendMessage(new ControlMessage(ControlMessage.SHOWOTHERCURRENTCLIENTS,
				map,
				SERVER_CLIENTID));
		for(ClientData i : clientDataHistMap.values()){
			map2.put(i.getClientID(), i);
		}
		clientWorker.sendMessage(new ControlMessage(ControlMessage.SHOWALLCLIENTS,
				map2,
				SERVER_CLIENTID));
	}

	/**
	 * Retrieves all TextMessage messages ever recieved since last reset.
	 * @return List of TextMessage messages recieved.
	 */
	public List<TextMessage> getTextMessageHist() {
		return textMessageHistList;
	}
	/**
	 * Retrieves all PaintDataMessage messages ever recieved since last reset.
	 * 
	 * @return List of PaintDataMessage messages recieved.
	 */
	public List<PaintMessage> getPaintMessageHist() {
		return paintMessageHistList;
	}
	
}

