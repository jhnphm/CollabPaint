/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cx.ath.feck.jchat.client;

/**
 *
 * @author john
 */
public class JChatClient{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       	int width,height;
		String host;
		int port;
		try{
			// Loop through args and set params
			for(int i = 0; i < args.length; i++){
				if(args[i].equals("--host")){
					host = (args[i+1]);
				}
				if(args[i].equals("--port")){
					port = Integer.parseInt(args[i+1]);
				}
				if(args[i].equals("--size")){
					String size = args[i+1];
					final String[] sizep = size.split("x");
					width = Integer.parseInt(sizep[0]);
					height = Integer.parseInt(sizep[1]);
				}
			}
		}catch(Exception e){
			System.err.println("Invalid Syntax.");
		}
    }

}
