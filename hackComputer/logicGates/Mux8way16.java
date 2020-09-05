package hackComputer.logicGates;

public class Mux8way16 {

    public boolean [] out(boolean [] a, boolean [] b, boolean[] c, boolean [] d, boolean [] e, boolean [] f, boolean[] g, boolean [] h, boolean [] sel) {
        Mux16 mux16 = new Mux16();
        Mux4way16 mux4way16 = new Mux4way16();
        boolean [] sel01 = {sel[0], sel[1]};
        boolean [] out1 = mux4way16.out(a, b, c, d, sel01);
        boolean [] out2 = mux4way16.out(e, f, g, h, sel01);
        return mux16.out(out1, out2, sel[2]);
    }
}
