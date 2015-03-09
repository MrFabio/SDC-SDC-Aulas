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
public class BankStub implements BankInterface {

    Socket s;
    ObjectInputStream ois;
    ObjectOutputStream oos;

    public BankStub() {
        try {
            //Start Connection
            String host = "localhost";
            int port = 4567;
            this.s = new Socket(host, port);
            oos = new ObjectOutputStream(this.s.getOutputStream());
            ois = new ObjectInputStream(this.s.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(BankStub.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int getBalance() {
        int balance = -1;
        try {
            Request r = new Request(Request.Type.BALANCE);
            oos.writeObject(r);
            oos.flush();

            balance = ois.readInt();
        } catch (IOException ex) {
            Logger.getLogger(BankStub.class.getName()).log(Level.SEVERE, null, ex);
        }
        return balance;
    }

    public boolean move(int ammount) {
        boolean result = false;
        try {
            Request r = new Request(Request.Type.MOVE, ammount);
            oos.writeObject(r);
            oos.flush();

            result = ois.readBoolean();
        } catch (IOException ex) {
            Logger.getLogger(BankStub.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public void quit() {
        try {
            oos.writeObject(new Request(Request.Type.QUIT));
            ois.close();
            oos.close();
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(BankStub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
