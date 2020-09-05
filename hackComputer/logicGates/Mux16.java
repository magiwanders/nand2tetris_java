package hackComputer.logicGates;

import hackComputer.utility.*;

public class Mux16 {

    public boolean [] out(boolean [] a, boolean [] b, boolean sel) {
        Mux mux = new Mux();
        boolean [] out = Gen.true16();
        for(int i=0; i<16; i++) {
            out[i] = mux.out(a[i], b[i], sel);
        }
        return out;
    }
}
