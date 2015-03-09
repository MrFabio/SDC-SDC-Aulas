package bank;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import methods.*;

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
        Op o;
        try {
            ois = new ObjectInputStream(this.cli.getInputStream());
            oos = new ObjectOutputStream(this.cli.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            online = true;

            while (online) {

                o = (Op) ois.readObject();

                if (o instanceof Quit) {
                    oos.close();
                    ois.close();
                    online = false;
                    System.out.println("Client exited");
                } else if (o instanceof Balance) {
                    int balance = this.bn.getBalance();
                    Res r = new BalanceRes(true, balance);
                    oos.writeObject(r);
                    oos.flush();
                } else if (o instanceof Move) {
                    int ammount = ((Move) o).getAmmount();
                    boolean moved = this.bn.move(ammount);
                    Res r = new MoveRes(moved);
                    oos.writeObject(r);
                    oos.flush();
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
