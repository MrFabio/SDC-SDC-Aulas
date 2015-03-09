/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Win7
 */
public class ClientHandler extends Thread {

    Socket cli = null;
    boolean online = false;
    Bank bn = null;

    ObjectInputStream ois;
    ObjectOutputStream oos;

    public ClientHandler(Socket cli, Bank bn) {

        this.cli = cli;
        this.bn = bn;
    }

    @Override
    public void run() {
        Request req;
        try {
            ois = new ObjectInputStream(this.cli.getInputStream());
            oos = new ObjectOutputStream(this.cli.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            online = true;

            while (online) {

                req = (Request) ois.readObject();

                switch (req.getType()) {

                    case QUIT:
                        oos.close();
                        ois.close();
                        online = false;
                        System.out.println("Client exited");
                        break;

                    case BALANCE:
                        int balance = this.bn.getBalance();
                        oos.writeInt(balance);
                        oos.flush();
                        break;

                    case MOVE:
                        int ammount = req.getAmmount();
                        boolean moved = this.bn.move(ammount);
                        oos.writeBoolean(moved);
                        oos.flush();
                        break;

                    default:
                        break;

                }

            }
        } catch (IOException | ClassNotFoundException ex) {
            try {
                oos.close();
                ois.close();
                System.out.println("Client exited abruptly");
            } catch (IOException ex1) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex1);
            }

        } finally {
            try {
                oos.close();
                ois.close();
            } catch (IOException ex) {
                Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
