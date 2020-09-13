package hackComputer.Memory;

import hackComputer.logicGates.*;
import hackComputer.utility.Gen;

public class RAM {

    Ram16k ram16k = new Ram16k();
    Ram8k SCREEN = new Ram8k();
    Register KBD = new Register();

    boolean [] lastInM;

    public boolean [] write(boolean [] in, boolean [] address, boolean load) {
        Dmux dmux = new Dmux();
        Mux16 mux16 = new Mux16();
        boolean [] address215 = {address[2], address[3], address[4], address[5], address[6], address[7], address[8], address[9], address[10], address[11], address[12], address[13], address[14], address[15]};
        boolean [] address315 = {address[3], address[4], address[5], address[6], address[7], address[8], address[9], address[10], address[11], address[12], address[13], address[14], address[15]};
        boolean load16k = dmux.a(load, address[1]);
        boolean loadSCR = dmux.b(load, address[1]);
        boolean [] out16k = ram16k.write(in, address215, load16k);
        boolean [] outSCR = SCREEN.write(in, address315, loadSCR);
        boolean [] outKBDSCR = mux16.out(outSCR, KBD.read(), address[1]);
        lastInM = mux16.out(out16k, outKBDSCR, address[0]);
        System.out.println("inM set to " + Gen.boolToString(lastInM));
        return lastInM;
    }

    public boolean [] read(boolean [] address) {
        boolean [] out = Gen.false16();
        boolean [] address215 = {address[2], address[3], address[4], address[5], address[6], address[7], address[8], address[9], address[10], address[11], address[12], address[13], address[14], address[15]};
        boolean [] address315 = {address[3], address[4], address[5], address[6], address[7], address[8], address[9], address[10], address[11], address[12], address[13], address[14], address[15]};
        Dmux dmux = new Dmux();
        if(dmux.a(true, address[1])) out = ram16k.read(address215);
        if(dmux.b(true, address[1])) out = SCREEN.read(address315);
        return out;
    }

    public void update() {
        ram16k.update();
        SCREEN.update();
        KBD.update();
    }

    public boolean [] getLastInM() {
        if(lastInM == null)  return Gen.false16();
        else return lastInM;
    }

}
