package hackComputer.CPU;

import hackComputer.alu.Inc16;
import hackComputer.logicGates.*;
import hackComputer.Memory.*;
import hackComputer.utility.*;

public class PC {

    Register register = new Register();

    public boolean [] set(boolean [] in, boolean load, boolean inc, boolean reset) {
        Mux16 mux16 = new Mux16();
        Mux4way16 mux4way16 = new Mux4way16();
        Inc16 inc16 = new Inc16();
        boolean [] out1 = register.read();
        boolean [] incremented = inc16.out(out1);
        boolean [] loadMux = {inc, load};
        boolean [] preinput = mux4way16.out(out1, incremented, in, in, loadMux);
        boolean [] input = mux16.out(preinput, Gen.false16(), reset);
        register.write(input, true);
        register.update();
        return out1;
    }

    public boolean [] increment() {
        return set(Gen.false16(), false, true, false);
    }

    public boolean [] reset() {
        return set(Gen.false16(), false, false, true);
    }

}
