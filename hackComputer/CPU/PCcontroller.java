package hackComputer.CPU;

import hackComputer.logicGates.Mux;

public class PCcontroller {

    public boolean out(boolean [] jjj, boolean opcode, boolean zr, boolean ng) {
        Mux mux = new Mux();
        boolean j1 = jjj[0];
        boolean j2 = jjj[1];
        boolean j3 = jjj[2];
        boolean nz = !zr;
        boolean nn = !ng;
        boolean and123 = j1&j2&j3;
        boolean and1nznn = j1&zr&ng;
        boolean andnzng3 = j3&nz&ng;
        boolean andnn2zr = j2&nn&zr;
        boolean or1 = and123|and1nznn;
        boolean or2 = or1|andnzng3;
        boolean or3 = or2|andnn2zr;
        return mux.out(or3, false, opcode);
    }

}
