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
import methods.*;

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

    @Override
    public int getBalance() {
        BalanceRes r = new BalanceRes(false, -1);
        try {
            Op o = new Balance();
            oos.writeObject(o);
            oos.flush();

            r = ((BalanceRes) ois.readObject());
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(BankStub.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r.isSuccess() ? r.getBalance() : -1;
    }

    @Override
    public boolean move(int ammount) {

        MoveRes r = new MoveRes(false);
        try {
            Op o = new Move(ammount);
            oos.writeObject(o);
            oos.flush();

            r = ((MoveRes) ois.readObject());
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(BankStub.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r.isSuccess();
    }

    public void quit() {
        try {
            Op o = new Quit();
            oos.writeObject(o);
            ois.close();
            oos.close();
            s.close();
        } catch (IOException ex) {
            Logger.getLogger(BankStub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
