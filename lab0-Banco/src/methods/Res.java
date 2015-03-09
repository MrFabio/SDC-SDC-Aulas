package methods;

import java.io.Serializable;

public abstract class Res implements Serializable {

    boolean success;

    public Res(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

}
