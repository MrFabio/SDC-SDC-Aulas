/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package banco;

import java.util.ArrayList;

/**
 *
 * @author Win7
 */
public class Banco implements BancoInterface {

    private int money;

    public Banco(int money) {
        this.money = money;
    }

    public Banco() {
        this.money = 0;
    }

    @Override
    public synchronized boolean move(int ammount) {

        if (ammount > 0 || (ammount < 0 && money > -ammount)) {
            money += ammount;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public synchronized int balance() {
        return money;
    }

}
