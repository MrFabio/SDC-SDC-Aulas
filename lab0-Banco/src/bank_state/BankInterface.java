/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bank_state;

import bank.*;

/**
 *
 * @author Win7
 */
public interface BankInterface {

    int getBalance();

    boolean move(int ammount);

}
