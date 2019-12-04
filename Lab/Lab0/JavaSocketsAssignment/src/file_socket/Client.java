package file_socket;

import java.io.*;
import java.net.*;

public class Client {
    private Socket socket;
    private DataOutputStream dataOutputStream = null;
    private DataInputStream inputStream = null;

    public void startConnection(String ip, int port) {
        System.out.println("Client has requested to start connection with host " +
                ip + " on port " + port);
        try{
            socket = new Socket(ip, port);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            System.out.println("Connection established, now waiting for user input...");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileName(){
        String line = "";
        try{
            line = inputStream.readUTF();
            if(line.matches("/[^\\\\]*\\.(\\w+)$/")){
                dataOutputStream.writeUTF("File name is valid");
                return line;
            }
            dataOutputStream.writeUTF("Invalid Filename");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getFileName();
    }



    public void stopConnection() {
        //TODO: close everything down
        try {
            inputStream.close();
            dataOutputStream.close();
            socket.close();
            System.out.println("Attempting to shut down client sockets and other streams");
            System.out.println("Shut down successful... goodbye");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){

    }

}
