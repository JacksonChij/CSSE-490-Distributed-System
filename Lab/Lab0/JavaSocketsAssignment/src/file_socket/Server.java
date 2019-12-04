package file_socket;

import java.io.*;
import java.net.*;

public class Server {
    private int port;
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream inputStream = null;
    private DataOutputStream outputStream = null;

    public Server(int port) {
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
                inputStream = new DataInputStream(System.in);
                outputStream = new DataOutputStream(socket.getOutputStream());
                String line = "";

                while(!line.equals("File name is valid")){
                    System.out.println("Please input a valid file name:");
                    System.out.print("\tprompt> ");
                    outputStream.writeUTF(inputStream.readUTF());
                    line = inputStream.readUTF();
                }

                while(!line.equals("File is valid")){
                    System.out.println("Please input a valid file path");
                    System.out.print("\tprompt> ");
                    File file = new File(inputStream.readUTF());
                    byte[] bytes = new byte[(int) file.length()];
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    bis.read(bytes, 0, bytes.length);
                    outputStream.write(bytes,0, bytes.length);
                    outputStream.flush();

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
}
