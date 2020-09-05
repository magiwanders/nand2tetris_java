package hackComputer.Memory;

import hackComputer.logicGates.Dmux8way;
import hackComputer.logicGates.Mux8way16;
import hackComputer.utility.Gen;

public class Ram4k {

    Ram512 ram5120 = new Ram512();
    Ram512 ram5121 = new Ram512();
    Ram512 ram5122 = new Ram512();
    Ram512 ram5123 = new Ram512();
    Ram512 ram5124 = new Ram512();
    Ram512 ram5125 = new Ram512();
    Ram512 ram5126 = new Ram512();
    Ram512 ram5127 = new Ram512();


    public boolean [] write(boolean [] in, boolean [] address, boolean load) {
        Dmux8way dmux8way = new Dmux8way();
        Mux8way16 mux8way16 = new Mux8way16();
        boolean [] address012 = {address[0], address[1], address[2]};
        boolean [] address311 = {address[3], address[4], address[5], address[6], address[7], address[8], address[9], address[10], address[11]};
        boolean load000 = dmux8way.a(load, address012);
        boolean load001 = dmux8way.b(load, address012);
        boolean load010 = dmux8way.c(load, address012);
        boolean load011 = dmux8way.d(load, address012);
        boolean load100 = dmux8way.e(load, address012);
        boolean load101 = dmux8way.f(load, address012);
        boolean load110 = dmux8way.g(load, address012);
        boolean load111 = dmux8way.h(load, address012);
        boolean [] out000 = ram5120.write(in, address311, load000);
        boolean [] out001 = ram5121.write(in, address311, load001);
        boolean [] out010 = ram5122.write(in, address311, load010);
        boolean [] out011 = ram5123.write(in, address311, load011);
        boolean [] out100 = ram5124.write(in, address311, load100);
        boolean [] out101 = ram5125.write(in, address311, load101);
        boolean [] out110 = ram5126.write(in, address311, load110);
        boolean [] out111 = ram5127.write(in, address311, load111);
        return mux8way16.out(out000, out001, out010, out011, out100, out101, out110, out111, address012);
    }

    public boolean [] read(boolean [] address) {
        return write(Gen.false16(), address, false);
    }

    public void update() {
        ram5120.update();
        ram5121.update();
        ram5122.update();
        ram5123.update();
        ram5124.update();
        ram5125.update();
        ram5126.update();
        ram5127.update();
    }

}
