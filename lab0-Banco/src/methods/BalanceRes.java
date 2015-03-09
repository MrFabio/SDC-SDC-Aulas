package methods;

public class BalanceRes extends Res {

    int balance;

    public BalanceRes(boolean success, int balance) {
        super(success);
        this.balance = balance;

    }

    public int getBalance() {
        return balance;
    }

}
