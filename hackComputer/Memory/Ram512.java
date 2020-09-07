package hackComputer.Memory;

import hackComputer.logicGates.Dmux8way;
import hackComputer.logicGates.Mux8way16;
import hackComputer.utility.Gen;

public class Ram512 {

    Ram64 ram640 = new Ram64();
    Ram64 ram641 = new Ram64();
    Ram64 ram642 = new Ram64();
    Ram64 ram643 = new Ram64();
    Ram64 ram644 = new Ram64();
    Ram64 ram645 = new Ram64();
    Ram64 ram646 = new Ram64();
    Ram64 ram647 = new Ram64();


    public boolean [] write(boolean [] in, boolean [] address, boolean load) {
        Dmux8way dmux8way = new Dmux8way();
        Mux8way16 mux8way16 = new Mux8way16();
        boolean [] address012 = {address[0], address[1], address[2]};
        boolean [] address345678 = {address[3], address[4], address[5], address[6], address[7], address[8]};
        boolean load000 = dmux8way.a(load, address012);
        boolean load001 = dmux8way.b(load, address012);
        boolean load010 = dmux8way.c(load, address012);
        boolean load011 = dmux8way.d(load, address012);
        boolean load100 = dmux8way.e(load, address012);
        boolean load101 = dmux8way.f(load, address012);
        boolean load110 = dmux8way.g(load, address012);
        boolean load111 = dmux8way.h(load, address012);
        boolean [] out000 = ram640.write(in, address345678, load000);
        boolean [] out001 = ram641.write(in, address345678, load001);
        boolean [] out010 = ram642.write(in, address345678, load010);
        boolean [] out011 = ram643.write(in, address345678, load011);
        boolean [] out100 = ram644.write(in, address345678, load100);
        boolean [] out101 = ram645.write(in, address345678, load101);
        boolean [] out110 = ram646.write(in, address345678, load110);
        boolean [] out111 = ram647.write(in, address345678, load111);
        return mux8way16.out(out000, out001, out010, out011, out100, out101, out110, out111, address012);
    }

    public boolean [] read(boolean [] address) {
        boolean [] out = Gen.false16();
        boolean [] address012 = {address[0], address[1], address[2]};
        boolean [] address345678 = {address[3], address[4], address[5], address[6], address[7], address[8]};
        Dmux8way dmux8way = new Dmux8way();
        if(dmux8way.a(true, address012)) out = ram640.read(address345678);
        if(dmux8way.b(true, address012)) out = ram641.read(address345678);
        if(dmux8way.c(true, address012)) out = ram642.read(address345678);
        if(dmux8way.d(true, address012)) out = ram643.read(address345678);
        if(dmux8way.e(true, address012)) out = ram644.read(address345678);
        if(dmux8way.f(true, address012)) out = ram645.read(address345678);
        if(dmux8way.g(true, address012)) out = ram646.read(address345678);
        if(dmux8way.h(true, address012)) out = ram647.read(address345678);
        return out;
    }

    public void update() {
        ram640.update();
        ram641.update();
        ram642.update();
        ram643.update();
        ram644.update();
        ram645.update();
        ram646.update();
        ram647.update();
    }
}
