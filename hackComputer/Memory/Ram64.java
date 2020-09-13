package hackComputer.Memory;

import hackComputer.logicGates.Dmux8way;
import hackComputer.logicGates.Mux8way16;
import hackComputer.utility.Gen;

public class Ram64 {

    Ram8 ram80 = new Ram8();
    Ram8 ram81 = new Ram8();
    Ram8 ram82 = new Ram8();
    Ram8 ram83 = new Ram8();
    Ram8 ram84 = new Ram8();
    Ram8 ram85 = new Ram8();
    Ram8 ram86 = new Ram8();
    Ram8 ram87 = new Ram8();


    public boolean [] write(boolean [] in, boolean [] address, boolean load) {
        Dmux8way dmux8way = new Dmux8way();
        Mux8way16 mux8way16 = new Mux8way16();
        boolean [] address012 = {address[0], address[1], address[2]};
        boolean [] address345 = {address[3], address[4], address[5]};
        boolean load000 = dmux8way.a(load, address012);
        boolean load001 = dmux8way.b(load, address012);
        boolean load010 = dmux8way.c(load, address012);
        boolean load011 = dmux8way.d(load, address012);
        boolean load100 = dmux8way.e(load, address012);
        boolean load101 = dmux8way.f(load, address012);
        boolean load110 = dmux8way.g(load, address012);
        boolean load111 = dmux8way.h(load, address012);
        boolean [] out000 = ram80.write(in, address345, load000);
        boolean [] out001 = ram81.write(in, address345, load001);
        boolean [] out010 = ram82.write(in, address345, load010);
        boolean [] out011 = ram83.write(in, address345, load011);
        boolean [] out100 = ram84.write(in, address345, load100);
        boolean [] out101 = ram85.write(in, address345, load101);
        boolean [] out110 = ram86.write(in, address345, load110);
        boolean [] out111 = ram87.write(in, address345, load111);
        return mux8way16.out(out000, out001, out010, out011, out100, out101, out110, out111, address012);
    }

    public boolean [] read(boolean [] address) {
        boolean [] out = Gen.false16();
        boolean [] address012 = {address[0], address[1], address[2]};
        boolean [] address345 = {address[3], address[4], address[5]};
        Dmux8way dmux8way = new Dmux8way();
        if(dmux8way.a(true, address012)) out = ram80.read(address345);
        if(dmux8way.b(true, address012)) out = ram81.read(address345);
        if(dmux8way.c(true, address012)) out = ram82.read(address345);
        if(dmux8way.d(true, address012)) out = ram83.read(address345);
        if(dmux8way.e(true, address012)) out = ram84.read(address345);
        if(dmux8way.f(true, address012)) out = ram85.read(address345);
        if(dmux8way.g(true, address012)) out = ram86.read(address345);
        if(dmux8way.h(true, address012)) out = ram87.read(address345);
        return out;
    }

    public void update() {
        ram80.update();
        ram81.update();
        ram82.update();
        ram83.update();
        ram84.update();
        ram85.update();
        ram86.update();
        ram87.update();
    }
}
