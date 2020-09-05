package hackComputer.logicGates;

public class Mux4way16 {

    public boolean [] out(boolean [] a, boolean [] b, boolean[] c, boolean [] d, boolean [] sel) {
        Mux16 mux16 = new Mux16();
        boolean [] out1 = mux16.out(a, b, sel[0]);
        boolean [] out2 = mux16.out(c, d, sel[0]);
        return mux16.out(out1, out2, sel[1]);
    }

}
