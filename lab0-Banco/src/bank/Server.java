package bank;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static Bank myBank;

    public static void main(String[] args) throws IOException {
        myBank = new Bank();
        int port = 4567;//Integer.parseInt(args[0]);
        System.out.println("Binding to port " + port + ", please wait  ...");
        ServerSocket ss;

        ss = new ServerSocket(port);
        System.out.println("Server started: " + ss);
        int i = 0;
        while (true) {
            System.out.println("Waiting for a client ...");
            Socket soc = ss.accept();
            System.out.println("Client accepted: " + soc + "\n");
            ClientHandler sc = new ClientHandler(soc);

            sc.start();
            i++;

        }
    }

}
