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
import net.sf.jgcs.ControlSession;
import net.sf.jgcs.DataSession;
import net.sf.jgcs.Message;
import net.sf.jgcs.MessageListener;
import net.sf.jgcs.Protocol;
import net.sf.jgcs.jgroups.JGroupsGroup;
import net.sf.jgcs.jgroups.JGroupsProtocolFactory;
import net.sf.jgcs.jgroups.JGroupsService;

public class BankStub implements BankInterface, MessageListener {

    JGroupsProtocolFactory pf = null;
    JGroupsGroup gc = null;
    ControlSession cs = null;

    Message msgres;
    private DataSession ds;
    ByteArrayOutputStream os;
    ObjectOutputStream oos;

    ByteArrayInputStream is;
    ObjectInputStream ois;

    public BankStub() {

        try {

            JGroupsProtocolFactory pf = new JGroupsProtocolFactory();
            JGroupsGroup gg = new JGroupsGroup("banco");
            Protocol p = pf.createProtocol();
            this.ds = p.openDataSession(gg);
            this.ds.setMessageListener(this);
            cs = p.openControlSession(gg);
            cs.join();

        } catch (IOException ex) {
            Logger.getLogger(BankStub.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public synchronized int getBalance() {
        BalanceRes r = new BalanceRes(false, -1);
        try {
            Op o = new Balance();

            os = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(os);

            Message msg = ds.createMessage();
            oos.writeObject(o);
            oos.flush();

            msg.setPayload(os.toByteArray());
            ds.multicast(msg, new JGroupsService(), null);
            msgres = null;
            while (msgres == null) {
                wait();
            }
            is = new ByteArrayInputStream(msgres.getPayload());
            ois = new ObjectInputStream(is);
            r = ((BalanceRes) ois.readObject());
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            Logger.getLogger(BankStub.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r.isSuccess() ? r.getBalance() : -1;
    }

    @Override
    public synchronized boolean move(int ammount) {

        MoveRes r = new MoveRes(false);
        try {

            os = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(os);
            Op o = new Move(ammount);
            Message msg = ds.createMessage();
            oos.writeObject(o);
            oos.flush();

            msg.setPayload(os.toByteArray());
            ds.multicast(msg, new JGroupsService(), null);
            msgres = null;
            while (msgres == null) {
                wait();
            }

            is = new ByteArrayInputStream(msgres.getPayload());
            ois = new ObjectInputStream(is);
            Object ob = ois.readObject();
            if (ob instanceof MoveRes) {
                r = ((MoveRes) ob);
            } else {
                return false;
            }
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            Logger.getLogger(BankStub.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r.isSuccess();
    }

    public synchronized void quit() {
        try {

            os = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(os);
            Op o = new Quit();
            Message msg = ds.createMessage();
            oos.writeObject(o);
            msg.setPayload(os.toByteArray());
            ds.multicast(msg, new JGroupsService(), null);

        } catch (IOException ex) {
            Logger.getLogger(BankStub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized Object onMessage(Message msg) {

        if (msg.getSenderAddress().equals(cs.getLocalAddress())) {
            //System.out.println("===== Discarded Message");
        } else {
            //System.out.println("===== Received Response from " + msg.getSenderAddress());
            msgres = msg;
            notify();
        }

        return null;
    }

}
