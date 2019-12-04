package single_threaded;

import java.io.*;
import java.net.*;

public class Server {
	
	//TODO: You probsbly need more instance variables...
    private int port;
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;
    public Server(int port) {
    	//TODO: set things up

        try{
            InetAddress ip = InetAddress.getLocalHost();
            System.out.println("Serial Server on host " + ip + " is listening on port " + port);
            server = new ServerSocket(port);
            this.port = port;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    public void start() {
    	System.out.println("Serial Server starting, listening on port " + this.port);
        try {
            socket = server.accept();
            while(true){
                String client = socket.getRemoteSocketAddress().toString();
                System.out.println("Received connection request from " + client);
                System.out.println("*********************************************");
                System.out.println("Now listening for incoming messages...");
                inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                outputStream = new DataOutputStream(socket.getOutputStream());
                String line = "";
                while(!line.equals(";;;")){
                    try{
                        line = inputStream.readUTF();
                        System.out.println(line);
                        outputStream.writeUTF(line.toUpperCase());
                    } catch (IOException e) {

                    }
                }
                System.out.println("Client finished, now waiting to service another client...");
                System.out.println("*********************************************");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //TODO: Loop forever, connecting to one client at a time and echoing the
    	//message received in all upper case
        
    }
    
    public void stop() {
        try {
            inputStream.close();
            outputStream.close();
            server.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
