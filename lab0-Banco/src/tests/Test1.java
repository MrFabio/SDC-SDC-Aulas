/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import bank.Bank;
import bank.Bank;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Win7
 */
public class Test1 {

    static bank.Bank myBank;
    static int sumT;

    public static void main(String[] args) {

        myBank = new Bank();
        int i = 1000;
        //ArrayList<TestThread> listT = new ArrayList<TestThread>(i);
        i--;
        while (i >= 0) {
            TestThread t = new TestThread();
            //listT.add(t);
            t.start();
            i--;
        }
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Test1.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("saldo final:" + banco.balance());
        if (myBank.balance() == Test1.sumT) {
            System.out.println("OK");
        } else {
            System.out.println("NOT OK: " + myBank.balance() + " | " + Test1.sumT);
        }
    }

    public static class TestThread extends Thread {

        @Override
        public void run() {
            int ammount = 0;
            int i = 0, x;
            boolean b;
            Random r = new Random();
            while (i < 10000) {
                x = r.nextInt(1000) - 500;
                b = myBank.move(x);
                if (b) {
                    ammount += x;
                }
                i++;
            }
            sumThrads(ammount);
            //System.out.println(saldo);

        }
    }

    private static synchronized void sumThrads(int s) {
        Test1.sumT += s;
    }
}
