package hackComputer.Memory;

import hackComputer.logicGates.Dmux;
import hackComputer.logicGates.Mux16;
import hackComputer.utility.Gen;

public class ROM {

    Ram16k ram16k0 = new Ram16k();
    Ram16k ram16k1 = new Ram16k();

    public boolean [] write(boolean [] in, boolean [] address, boolean load) {
        Dmux dmux = new Dmux();
        Mux16 mux16 = new Mux16();
        boolean address1 = address[1];
        boolean [] address215 = {address[2], address[3], address[4], address[5], address[6], address[7], address[8], address[9], address[10], address[11], address[12], address[13], address[14], address[15]};
        boolean load0 = dmux.a(load, address1);
        boolean load1 = dmux.b(load, address1);
        boolean [] out0 = ram16k0.write(in, address215, load0);
        boolean [] out1 = ram16k1.write(in, address215, load1);
        return mux16.out(out0, out1, address1);
    }

    public boolean [] read(boolean [] address) {
        boolean [] out = Gen.false16();
        boolean address1 = address[1];
        boolean [] address215 = {address[2], address[3], address[4], address[5], address[6], address[7], address[8], address[9], address[10], address[11], address[12], address[13], address[14], address[15]};
        Dmux dmux = new Dmux();
        if(dmux.a(true, address1)) out = ram16k0.read(address215);
        if(dmux.b(true, address1)) out = ram16k1.read(address215);
        return out;
    }

    public void update() {
        ram16k0.update();
        ram16k1.update();
    }
}
