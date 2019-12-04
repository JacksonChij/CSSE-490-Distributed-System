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
            System.out.println("Reading filename");
            if(line.matches("^[\\w,\\s-]+\\.[A-Za-z0-9]{3}$")){
                dataOutputStream.writeUTF("File name is valid");
                return line;
            }
            dataOutputStream.writeUTF("Invalid Filename");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getFileName();
    }

    public void getFile(String filename){
        try{

            int length = Integer.parseInt(inputStream.readUTF());
            byte[] filebyte = new byte[length];
            FileOutputStream fileOutputStream = new FileOutputStream(filename);
            BufferedOutputStream fileout = new BufferedOutputStream(fileOutputStream);
            int bytes = inputStream.read(filebyte, 0, filebyte.length);
            System.out.println("Connected to file stream");

            fileout.write(filebyte, 0, bytes);
            fileout.flush();
            System.out.println("File read successfully");
            dataOutputStream.writeUTF("File is valid");
         } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                dataOutputStream.writeUTF("File is invalid");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();

        }

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
        Client client = new Client();

        int port;
        String hostname = "";
        if (args.length > 2) {
            hostname = args[1];
            port = Integer.parseInt(args[2]);
        }
        else { //default
            hostname = "192.168.1.134";
            port = 9999;
        }

        client.startConnection(hostname, port);

        System.out.println("***********************************************************");
        System.out.println("\tConnection established, now waiting for user input...");

        String filename = client.getFileName();
        client.getFile("copy_" + filename);
        client.stopConnection();
    }

}
