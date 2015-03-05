package bank;

public class Bank implements BankInterface {

    private int money;

    public Bank(int money) {
        this.money = money;
    }

    public Bank() {
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
