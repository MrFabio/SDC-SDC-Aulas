/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banco;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Win7
 */
public class Main {

    private static Banco banco;
    private static int somaT;

    public static void main(String[] args) {
        banco = new Banco();
        somaT = 0;
        test();
    }

    private static void test() {
        int i = 1000;
        ArrayList<TestThread> listT = new ArrayList<TestThread>(i);
        i--;
        while (i >= 0) {
            TestThread t = new TestThread();
            listT.add(t);
            t.start();
            i--;
        }
        /*
         while (i >= 0) {
         try {
         if (listT.get(i).isAlive()) {
         listT.get(i).join();
         System.out.println("WAIT");
         }
         } catch (InterruptedException ex) {
         Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
         }
         }*/
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("saldo final:" + banco.balance());
        if (banco.balance() == Main.somaT) {
            System.out.println("Igual");
        } else {
            System.out.println("Diferente: " + banco.balance() + " | " + Main.somaT);
        }
    }

    public static class TestThread extends Thread {

        public void run() {
            int saldo = 0;
            int i = 0, x;
            boolean b;
            Random r = new Random();
            while (i < 10000) {
                x = r.nextInt(1000) - 500;
                b = banco.move(x);
                if (b) {
                    saldo += x;
                }
                i++;
            }
            somaThrads(saldo);
            //System.out.println(saldo);

        }
    }

    private static synchronized void somaThrads(int s) {
        Main.somaT += s;
    }

}
