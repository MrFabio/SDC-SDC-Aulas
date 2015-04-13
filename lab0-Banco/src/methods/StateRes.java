package methods;

public class StateRes extends Res {

    int balance;

    public StateRes(boolean success, int balance) {
        super(success);
        this.balance = balance;

    }

    public int getBalance() {
        return balance;
    }

}
