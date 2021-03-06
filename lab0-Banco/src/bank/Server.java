package bank;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
import net.sf.jgcs.GroupException;
import net.sf.jgcs.Message;
import net.sf.jgcs.MessageListener;
import net.sf.jgcs.Protocol;
import net.sf.jgcs.annotation.PointToPoint;
import net.sf.jgcs.ip.IpGroup;
import net.sf.jgcs.jgroups.JGroupsGroup;
import net.sf.jgcs.jgroups.JGroupsProtocolFactory;
import net.sf.jgcs.jgroups.JGroupsService;

public class Server implements MessageListener {

    public static Bank myBank;

    private Protocol p;

    DataSession ds;
    IpGroup gc = null;
    JGroupsGroup gg = null;

    public Server(IpGroup gc, Protocol p) {
        try {
            this.gc = gc;
            this.p = p;
            this.ds = p.openDataSession(gc);
            this.ds.setMessageListener(this);
            this.p.openControlSession(gc).join();
        } catch (GroupException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Server(JGroupsGroup gg, Protocol p) {
        try {
            this.gg = gg;
            this.p = p;
            this.ds = p.openDataSession(gg);
            this.ds.setMessageListener(this);
            this.p.openControlSession(gg).join();
        } catch (GroupException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws IOException {
        myBank = new Bank();

        //IpProtocolFactory pf = new IpProtocolFactory();
        //IpGroup gc = new IpGroup("225.1.2.3:12345");
        JGroupsProtocolFactory gf = new JGroupsProtocolFactory();
        JGroupsGroup gg = new JGroupsGroup("banco");
        Protocol p = gf.createProtocol();
        Server serv = new Server(gg, p);

        System.out.println("Server started: ");

        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public Object onMessage(Message msg) {

        try {

            System.out.println("=====   Message Received");
            ByteArrayOutputStream os = null;
            ObjectOutputStream oos = null;

            int i = 0;
            ByteArrayInputStream is = new ByteArrayInputStream(msg.getPayload());
            ObjectInputStream ois = new ObjectInputStream(is);

            try {
                Object obj = ois.readObject();
                Op o = null;
                Res ro = null;
                try {
                    o = (Op) obj;
                } catch (Exception e) {
                    //System.out.println("not");
                }

                try {
                    ro = (Res) obj;
                } catch (Exception e) {
                    //System.out.println("not2");
                }
                os = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(os);
                Message response = null;
                Res r = null;
                if (ro != null) {

                } else if (o instanceof Quit) {
                    oos.close();
                    ois.close();
                    System.out.println("Client exited");
                } else if (o instanceof Balance) {
                    int balance = myBank.getBalance();
                    r = new BalanceRes(true, balance);

                } else if (o instanceof Move) {
                    int ammount = ((Move) o).getAmmount();
                    boolean moved = myBank.move(ammount);
                    r = new MoveRes(moved);
                }

                if (r != null) {
                    oos.writeObject(r);
                    oos.flush();
                    byte[] data = os.toByteArray();
                    response = ds.createMessage();
                    response.setPayload(data);
                    ds.multicast(response, new JGroupsService(), null, new PointToPoint(msg.getSenderAddress()));
                }
            } catch (IOException | ClassNotFoundException ex) {

                if (oos != null) {
                    oos.close();
                }
                if (ois != null) {
                    ois.close();
                }
                System.out.println("Client exited abruptly");

            } finally {
                if (oos != null) {
                    oos.close();
                }
                if (ois != null) {
                    ois.close();
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
