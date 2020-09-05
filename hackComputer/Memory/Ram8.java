package hackComputer.Memory;

import hackComputer.logicGates.Dmux8way;
import hackComputer.logicGates.Mux8way16;
import hackComputer.utility.Gen;

public class Ram8 {

    Register register0 = new Register();
    Register register1 = new Register();
    Register register2 = new Register();
    Register register3 = new Register();
    Register register4 = new Register();
    Register register5 = new Register();
    Register register6 = new Register();
    Register register7 = new Register();

    public boolean [] write(boolean [] in, boolean [] address, boolean load) {
        Dmux8way dmux8way = new Dmux8way();
        Mux8way16 mux8way16 = new Mux8way16();
        boolean load000 = dmux8way.a(load, address);
        boolean load001 = dmux8way.b(load, address);
        boolean load010 = dmux8way.c(load, address);
        boolean load011 = dmux8way.d(load, address);
        boolean load100 = dmux8way.e(load, address);
        boolean load101 = dmux8way.f(load, address);
        boolean load110 = dmux8way.g(load, address);
        boolean load111 = dmux8way.h(load, address);
        boolean [] out000 = register0.write(in, load000);
        boolean [] out001 = register1.write(in, load001);
        boolean [] out010 = register2.write(in, load010);
        boolean [] out011 = register3.write(in, load011);
        boolean [] out100 = register4.write(in, load100);
        boolean [] out101 = register5.write(in, load101);
        boolean [] out110 = register6.write(in, load110);
        boolean [] out111 = register7.write(in, load111);
        return mux8way16.out(out000, out001, out010, out011, out100, out101, out110, out111, address);
    }

    public boolean [] read(boolean [] address) {
        return write(Gen.false16(), address, false);
    }

    public void update() {
        register0.update();
        register1.update();
        register2.update();
        register3.update();
        register4.update();
        register5.update();
        register6.update();
        register7.update();
    }

}
