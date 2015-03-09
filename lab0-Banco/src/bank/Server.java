package bank;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import methods.Balance;
import methods.BalanceRes;
import methods.Move;
import methods.MoveRes;
import methods.Op;
import methods.Quit;
import methods.Res;
import net.sf.jgcs.DataSession;
import net.sf.jgcs.Message;
import net.sf.jgcs.MessageListener;
import net.sf.jgcs.Protocol;
import net.sf.jgcs.ip.IpGroup;
import net.sf.jgcs.ip.IpProtocolFactory;

public class Server implements MessageListener {

    public static Bank myBank;
    private static IpProtocolFactory pf;
    private static IpGroup gc;
    private static Protocol p;

    private static ByteArrayOutputStream os;
    private static ObjectOutputStream oos;

    private static ByteArrayInputStream is;
    private static ObjectInputStream ois;
    private static Message msgreq;
    private static DataSession ds;

    public static void main(String[] args) throws IOException {
        myBank = new Bank();

        pf = new IpProtocolFactory();
        gc = new IpGroup("225.1.2.3:12345");
        p = pf.createProtocol();

        p.openControlSession(gc).join();

        ds = p.openDataSession(gc);
        ds.setMessageListener(this);

        System.out.println("Server started: ");

        os = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(os);

        int i = 0;

        while (true) {
            msgreq = null;
            while (msgreq == null) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            is = new ByteArrayInputStream(msgreq.getPayload());
            ois = new ObjectInputStream(is);

            try {

                Op o = (Op) ois.readObject();

                if (o instanceof Quit) {
                    oos.close();
                    ois.close();
                    System.out.println("Client exited");
                } else if (o instanceof Balance) {
                    int balance = myBank.getBalance();
                    Res r = new BalanceRes(true, balance);
                    Message msg = ds.createMessage();

                    oos.writeObject(r);
                    oos.flush();

                    msg.setPayload(os.toByteArray());
                    ds.multicast(msg, null, null);

                } else if (o instanceof Move) {
                    int ammount = ((Move) o).getAmmount();
                    boolean moved = myBank.move(ammount);
                    Res r = new MoveRes(moved);
                    Message msg = ds.createMessage();

                    oos.writeObject(r);
                    oos.flush();

                    msg.setPayload(os.toByteArray());
                    ds.multicast(msg, null, null);

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

    @Override
    public Object onMessage(Message msg) {

        msgreq = msg;

        notify();
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
