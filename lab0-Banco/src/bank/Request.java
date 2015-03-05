package bank;

import java.io.Serializable;

public class Request implements Serializable {

    final int ammount;
    final Type type;

    public enum Type {

        MOVE, BALANCE, QUIT
    }

    public Request(Type type, int ammount) {
        this.type = type;
        this.ammount = ammount;
    }

    public Request(Type type) {
        this.type = type;
        this.ammount = 0;
    }

    public Type getType() {
        return type;
    }

    public int getAmmount() {
        return ammount;
    }

}
