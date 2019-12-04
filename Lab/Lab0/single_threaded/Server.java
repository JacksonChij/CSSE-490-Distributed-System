package single_threaded;

import java.io.*;
import java.net.*;

public class Server {
	
	//TODO: You probsbly need more instance variables...
    private int port;
    
    public Server(int port) {
    	//TODO: set things up
    }
    
    
    public void start() {
    	System.out.println("Serial Server starting, listeneing on port " + this.port);
    	
    	//TODO: Loop forever, connecting to one client at a time and echoing the 
    	//message received in all upper case
        
    }
    
    public void stop() {
        //TODO: Put code here to shut everything down
    }
	
    public static void main(String args[]) {
        int port;
        
        if (args.length > 1)
        	port = Integer.parseInt(args[1]);
        else
        	port = 9999;

        Server server = new Server(port);
        
        server.start();
    }
}
