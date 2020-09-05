package hackComputer.Memory;

import hackComputer.logicGates.*;

public class Bit {

    boolean DFF = false;
    boolean mem = false;

    public boolean write(boolean in, boolean load) {
        Mux mux = new Mux();
        mem = mux.out(DFF, in, load);
        return DFF;
    }

    public boolean read() {
        return write(false, false);
    }

    public void update() {
        DFF = mem;
    }

}
