package single_threaded;

import java.io.*;
import java.net.*;

public class Client {

	//TODO: You probably want some instance variables...
 
    public void startConnection(String ip, int port) {
    	System.out.println("Client has requested to start connection with host " + 
    						ip + " on port " + port);
        //TODO: stsrt a connection with the server
        
    }
 
    public String sendMessage(String msg) {
    	//TODO: write code here to send a message to the server and then wait for the reply
        return "";
    }
 
    public void stopConnection() {
    	//TODO: close everything down
    }

    public static void main(String[] args) {
    	Client client = new Client();
    	
    	int port;
    	String hostname = "";
        if (args.length > 2) {
        	hostname = args[1];
        	port = Integer.parseInt(args[2]);
        }
        else { //default
        	hostname = "127.0.0.1";
        	port = 9999;
        }
        
        client.startConnection(hostname, port);
        
        System.out.println("***********************************************************");
        System.out.println("\tConnection established, now waiting for user input...");
        
        BufferedReader reader =  new BufferedReader(new InputStreamReader(System.in)); 
        
        String input = "", response = "";
        while(!";;;".contentEquals(input)) {
            try {
            	System.out.print("\tprompt> ");
				input = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
            
            //now send this string to the server
            System.out.println("\tSending message to Server...");
            response = client.sendMessage(input);
            if(!("".contentEquals(response) || ";;;".contentEquals(input)))
            	System.out.println("\tReceived response from server of\n\t\t\"" + response + "\"");
        }
        System.out.println("User entered sentinel of \";;;\", now stopping client");
        System.out.println("***********************************************************");
        client.stopConnection();
    }
}
