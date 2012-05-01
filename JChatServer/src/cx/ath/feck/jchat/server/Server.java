package cx.ath.feck.jchat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 * Server class for server app. Waits and spawns threads for connections and launches the server thread
 * 
 * @author John Pham
 */
public class Server {

	
	/**
	 * Server function for server part of JChat.
	 * <code>
	 * --port Port to listen on
	 * --password Admin password, defaults to "password", cannot be blank.
	 * </code>
	 * Spawns main thread and thread for client connections.
	 * 
	 * 
	 * @param args Command line arguments to server
	 */
	public static void main(String[] args) {
		int port;
		String password = "password";
		ServerSocket serverSocket = null;
		ServerCore server;
		// Sets defaults
		port = 9999;
		
		// Loop through args and set params
		for(int i = 0; i < args.length; i++){
			if(args[i].equals("--port")){
				port = Integer.parseInt(args[i+1]);
			}
			if(args[i].equals("--adminpassword")){
				password = args[i+1];
				if(password.equals("")){
					return;
				}
			}else{
				password = "password";
			}
		}
		try{
			serverSocket = new ServerSocket(port);
		}catch(IOException ioe){
			System.err.println("Could not open port " + port + ".");
			System.exit(1);
		}
		
		

		
		//Start off main server thread that sends data off to other clients.
		server = new ServerCore(password);

		(new Thread(server)).start();
		//TODO Create heartbeat thread that fires off control messages every 20 seconds to clients to check for aliveness.
		
		// Loop and spawn new threads for connections
		while(true){
			Socket clientSocket = null; 
			try {
				clientSocket = serverSocket.accept();
				clientSocket.setKeepAlive(true);
				ClientWorker clientWorker = new ClientWorker(clientSocket,server);
				clientWorker.start();
			} catch (IOException e) {
				System.err.println("Accept failed: " + port);
			}
			
		}
	}

}
