package cx.ath.feck.jchat.client;


import cx.ath.feck.jchat.shared.ClientData;
import cx.ath.feck.jchat.shared.ControlMessage;
import cx.ath.feck.jchat.shared.Message;
import cx.ath.feck.jchat.shared.PaintDataMessage;
import cx.ath.feck.jchat.shared.Status;
import cx.ath.feck.jchat.shared.Status;
import cx.ath.feck.jchat.shared.Status;
import cx.ath.feck.jchat.shared.Status;
import cx.ath.feck.jchat.shared.TextMessage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.Socket;

import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.SwingUtilities;


/**
 * Handles network I/O for clientCore, sending and recieving messages to/from server.
 *
 * @author John Pham
 */
public class NetworkIO {
	private String host;
	private int port;
    private Socket socket;
    private InputThread inputThread;
    private OutputThread outputThread;
    private BlockingQueue<Message> messageQueue;
	private ClientData clientData;
	private Map<Integer, ClientData> currentClientDataMap; //current clients connected to server
	private Map<Integer, ClientData> histClientDataMap; //all clients ever connected to server
	

    /**
     * Connects to server, and spawns network I/O threads.
     *
     */
    public NetworkIO(String host, int port){
		this.port = port;
		this.host = host;
    }

    /**
     * Cleans up, closes sockets, and kills threads for clientCore.
     */
    public void stop() {
        inputThread.quit();
        outputThread.quit();

        try { socket.close();
        } catch (IOException e) {
            //System.err.println("Could not close socket");
            //e.printStackTrace();
        }
    }

    /**
     * Runnable to handle input from clientCore to server
     *
     * @author John Pham
     */
    private class InputThread extends Thread {
        boolean stop = false;
        ObjectInputStream in;

        /**
         * Waits for clientCore input and writes to server thread. Reqyures a ClientData named "clientCore" existing in the object registry
         */
        @SuppressWarnings("unchecked")
		@Override
        public void run() {


            try {
                in = new ObjectInputStream(new BufferedInputStream(
                            socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
                stop = true;
            }

            //Loop, wait for input
            while (!stop) {
				Message message;
                if (socket.isClosed()) {
                    break;
                }

                try {
                    message = (Message) in.readObject();
                } catch (IOException e) {
                    //e.printStackTrace();
                    //TODO Deal w/ this somehow
                    return;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return;
                }

                if (message.getClass() == PaintDataMessage.class) {
                        //clientCore.displayPaintDataMessage((cx.ath.feck.jchat.PaintDataMessage) message);
                } else if (message.getClass() == TextMessage.class) {
                    //clientCore.displayTextMessage((TextMessage) message);
                } else if (message.getClass() == ControlMessage.class) {
                    //clientCore.controlMessageHandler((ControlMessage) message);
                }
            }

            quit();

            return;
        }

        /**
         * Stops thread and cleans up open streams.
         *
         */
        public void quit() {
            //			Exit if previously called, to prevent infinite recursion from clientWorker() stop method
            if (stop) {
                return;
            }

            stop = true;

            try {
                in.close();
            } catch (IOException e) {
                System.err.println("Could not close input stream");
                e.printStackTrace();
            }
        }
    }

    /**
     * Runnable to handle output from server to clientCore
     *
     * @author John Pham
     */
    private class OutputThread extends Thread {
        boolean stop = false;
        private ObjectOutputStream out;

        /**
         * Waits for input from server, and sends data to clientCore.
         */
        public void run() {
            Message message;

            try {
                out = new ObjectOutputStream(new BufferedOutputStream(
                            socket.getOutputStream()));
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
                stop = true;
            }

            while (!stop) {
                if (socket.isClosed()) {
                    break;
                }

                try {
                    message = messageQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
                try {
                    out.writeObject(message);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();

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
        public void quit() {
            //Exit if previously called, to prevent infinite recursion from clientWorker() stop method
            if (stop) {
                return;
            }

            stop = true;

            try {
                out.close();
            } catch (IOException e) {
				//TODO: Log close
                //e.printStackTrace();
            }

            NetworkIO.this.stop();
        }
    }

    /**
     * Sends message to server on other side of socket
     * @param message Sends a Message to the server.
     */
    public void sendMessage(Message message) {
        //TODO Throw error if socket closed or threads dead while sending message
        messageQueue.add(message);
    }


	/**
	 * Sends supplied text message to server.
	 * @param text Text to send to server.
	 */
	public void sendTextMessage(String text) {
		TextMessage message = new TextMessage(text, clientData.getClientID());
		this.sendMessage(message);
	}
	
//	public void sendPaintDataMessage(BufferedImage img, Point location,
//			int layer) {
//		PaintDataMessage message = new PaintDataMessage(img, location, layer,
//				clientData.getClientID());
//		this.sendMessage(message);
//	}
	
	/**
	 * Displays given TextMessage on GUI.
	 * @param textMessage TextMessage to display on GUI.
	 */
//	public void displayTextMessage(TextMessage textMessage) {
//		String username = histClientDataMap.get(textMessage.getClientID())
//				.getUsername();
//		String message = textMessage.getText();
//		Singletons.getMainPanel().getTextChatPanel()
//				.displayMessage(username, message);
//	}
//	
//	public void displayPaintDataMessage(PaintDataMessage paintDataMessage) {
//		        Singletons.getPaintCore()
//		                  .mergeImage(paintDataMessage.getImage(),
//		            paintDataMessage.getLocation(), paintDataMessage.getLayer());
//	}
	
	public void connect() throws NumberFormatException, UnknownHostException, IOException {
        socket = new Socket(host, port);
        messageQueue = new LinkedBlockingQueue<Message>();
        inputThread = new InputThread();
        outputThread = new OutputThread();
        inputThread.start();
        outputThread.start();
	}
	
	/**
	 * Logs client into server with given username and password.
	 * @param username Self explanatory.
	 * @param password Self explanatory.
	 */
	public void login(String username, String password) {
//		Singletons.getLoginConfirmPanel()
//				.setStatus(StatusConsts.LOGGINGIN);
//		Singletons.getBasePanel().setState(BasePanel.DISPLAYSTATUS);
//		//Create ControlMessage and login- do after setting status, to prevent
//		//network data confirming login arriving before status is displayed- when status is displayed,
//		//logged in state would be overwritten
//		clientData = new ClientData(username, password);
//		
//		ControlMessage message = new ControlMessage(ControlMessage.LOGIN,
//				clientData, clientData.getClientID());
//		Singletons.getNetworkIO().sendMessage(message);
	}
	
	/**
	 * Interprets ControlMessage, performs actions such as loading client list, switching panels, etc.
	 * @param controlMessage ControlMessage interpret.
	 */
	@SuppressWarnings("unchecked")
	public void controlMessageHandler(final ControlMessage controlMessage) {
		try{
			switch (controlMessage.getMessage()) {
			case ControlMessage.LOGINCONFIRM:
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try{
							switch ((Status)controlMessage.getData()) {
							case SUCCESS:
						//		Singletons.getBasePanel().setState(BasePanel.LOGGEDIN);
								break;
							case SUCCESSWITHADMIN:
						//		Singletons.getBasePanel().setState(BasePanel.LOGGEDINASADMIN);
								break;
							case FAILINVALID:
						//		Singletons.getBasePanel().setState(BasePanel.DISPLAYSTATUS);
						//		Singletons.getLoginConfirmPanel()
						//				.setStatus(Status.FAILINVALID);
								break;
							case FAILGAINADMIN:
						//		Singletons.getBasePanel().setState(BasePanel.DISPLAYSTATUS);
						//		Singletons.getLoginConfirmPanel()
						//				.setStatus(Status.FAILGAINADMIN);
								break;
							default:
							}
						}catch(ClassCastException cce){
							System.err.println("Recieved invalid ControlMessage");
							cce.printStackTrace();
						}
					}
				});
				break;
				
			case ControlMessage.ASSIGNCLIENTID:
				clientData.setClientID((Integer) controlMessage.getData());
				
				break;
				
			case ControlMessage.SHOWOTHERCURRENTCLIENTS:
				currentClientDataMap = (Map<Integer, ClientData>) controlMessage.getData();
//				Singletons.getMainPanel().getTextChatPanel()
//						.updateClientList(currentClientDataMap);
				
				break;
				
			case ControlMessage.SHOWALLCLIENTS:
				histClientDataMap = (Map<Integer, ClientData>) controlMessage.getData();
				
				break;
				
			default:
			}
		}catch(ClassCastException cce){
			System.err.println("Recieved invalid ControlMessage");
			cce.printStackTrace();
		}
	}
}
