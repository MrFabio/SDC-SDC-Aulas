package bank;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import methods.*;
import net.sf.jgcs.DataSession;
import net.sf.jgcs.Message;
import net.sf.jgcs.MessageListener;
import net.sf.jgcs.Protocol;
import net.sf.jgcs.ip.IpGroup;
import net.sf.jgcs.ip.IpProtocolFactory;
import net.sf.jgcs.ip.IpService;

public class BankStub implements BankInterface, MessageListener {

    IpProtocolFactory pf = null;
    IpGroup gc = null;

    Message msgres;
    Protocol p;
    private DataSession ds;
    ByteArrayOutputStream os;
    ObjectOutputStream oos;

    ByteArrayInputStream is;
    ObjectInputStream ois;

    public BankStub() {

        try {

            pf = new IpProtocolFactory();
            gc = new IpGroup("225.1.2.3:12345");
            p = pf.createProtocol();

            ds = p.openDataSession(gc);
            ds.setMessageListener(this);

            os = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(os);

        } catch (IOException ex) {
            Logger.getLogger(BankStub.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public synchronized int getBalance() {
        BalanceRes r = new BalanceRes(false, -1);
        try {
            Op o = new Balance();

            Message msg = ds.createMessage();
            oos.writeObject(o);
            oos.flush();

            msg.setPayload(os.toByteArray());
            ds.multicast(msg, new IpService(), null);
            msg = null;
            while (msg == null) {
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
            Op o = new Move(ammount);
            Message msg = ds.createMessage();
            oos.writeObject(o);
            oos.flush();

            msg.setPayload(os.toByteArray());
            ds.multicast(msg, new IpService(), null);
            msg = null;
            while (msg == null) {
                wait();
            }

            is = new ByteArrayInputStream(msgres.getPayload());
            ois = new ObjectInputStream(is);
            r = ((MoveRes) ois.readObject());
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            Logger.getLogger(BankStub.class.getName()).log(Level.SEVERE, null, ex);
        }
        return r.isSuccess();
    }

    public synchronized void quit() {
        try {
            Op o = new Quit();
            Message msg = ds.createMessage();
            oos.writeObject(o);
            msg.setPayload(os.toByteArray());
            ds.multicast(msg, null, null);
            msg = null;
            while (msg == null) {
                wait();
            }

            ois.close();
            oos.close();

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(BankStub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Object onMessage(Message msg) {

        msgres = msg;

        notify();

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
