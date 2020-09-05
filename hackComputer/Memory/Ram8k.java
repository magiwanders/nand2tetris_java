package hackComputer.Memory;

import hackComputer.logicGates.*;
import hackComputer.utility.Gen;

public class Ram8k {

    Ram4k ram4k0 = new Ram4k();
    Ram4k ram4k1 = new Ram4k();

    public boolean [] write(boolean [] in, boolean [] address, boolean load) {
        Dmux dmux = new Dmux();
        Mux16 mux16 = new Mux16();
        boolean address0 = address[0];
        boolean [] address112 = {address[1], address[2], address[3], address[4], address[5], address[6], address[7], address[8], address[9], address[10], address[11], address[12]};
        boolean load0 = dmux.a(load, address0);
        boolean load1 = dmux.b(load, address0);
        boolean [] out0 = ram4k0.write(in, address112, load0);
        boolean [] out1 = ram4k1.write(in, address112, load1);
        return mux16.out(out0, out1, address0);
    }

    public boolean [] read(boolean [] address) {
        return write(Gen.false16(), address, false);
    }

    public void update() {
        ram4k0.update();
        ram4k1.update();
    }
}
