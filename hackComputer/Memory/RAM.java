package hackComputer.Memory;

import hackComputer.logicGates.*;
import hackComputer.utility.Gen;

public class RAM {

    Ram16k ram16k = new Ram16k();
    Ram8k SCREEN = new Ram8k();
    Register KBD = new Register();

    public boolean [] write(boolean [] in, boolean [] address, boolean load) {
        Dmux dmux = new Dmux();
        Mux16 mux16 = new Mux16();
        boolean [] address114 = {address[1], address[2], address[3], address[4], address[5], address[6], address[7], address[8], address[9], address[10], address[11], address[12], address[13], address[14]};
        boolean [] address214 = {address[2], address[3], address[4], address[5], address[6], address[7], address[8], address[9], address[10], address[11], address[12], address[13], address[14]};
        boolean load16k = dmux.a(load, address[0]);
        boolean loadSCR = dmux.b(load, address[0]);
        boolean [] out16k = ram16k.write(in, address114, load16k);
        boolean [] outSCR = SCREEN.write(in, address214, loadSCR);
        boolean [] outKBDSCR = mux16.out(outSCR, KBD.read(), address[1]);
        return mux16.out(out16k, outKBDSCR, address[0]);
    }

    public boolean [] read(boolean [] address) {
        return write(Gen.false16(), address, false);
    }

    public void update() {
        ram16k.update();
        SCREEN.update();
        KBD.update();
    }

}
