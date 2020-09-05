package hackComputer.Memory;

import hackComputer.logicGates.*;
import hackComputer.utility.Gen;

public class Ram16k {

    Ram4k ram4k0 = new Ram4k();
    Ram4k ram4k1 = new Ram4k();
    Ram4k ram4k2 = new Ram4k();
    Ram4k ram4k3 = new Ram4k();


    public boolean [] write(boolean [] in, boolean [] address, boolean load) {
        Dmux4way dmux4way = new Dmux4way();
        Mux4way16 mux4way16 = new Mux4way16();
        boolean [] address01 = {address[0], address[1]};
        boolean [] address213 = {address[2], address[3], address[4], address[5], address[6], address[7], address[8], address[9], address[10], address[11], address[12], address[13]};
        boolean load00 = dmux4way.a(load, address01);
        boolean load01 = dmux4way.b(load, address01);
        boolean load10 = dmux4way.c(load, address01);
        boolean load11 = dmux4way.d(load, address01);
        boolean [] out00 = ram4k0.write(in, address213, load00);
        boolean [] out01 = ram4k1.write(in, address213, load01);
        boolean [] out10 = ram4k2.write(in, address213, load10);
        boolean [] out11 = ram4k3.write(in, address213, load11);
        return mux4way16.out(out00, out01, out10, out11, address01);
    }

    public boolean [] read(boolean [] address) {
        return write(Gen.false16(), address, false);
    }

    public void update() {
        ram4k0.update();
        ram4k1.update();
        ram4k2.update();
        ram4k3.update();
    }

}
