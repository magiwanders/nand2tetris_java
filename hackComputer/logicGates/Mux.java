package hackComputer.logicGates;

public class Mux {

    public boolean out (boolean a, boolean b, boolean sel) {
        boolean notSel = !sel;
        boolean out1 = a & notSel;
        boolean out2 = b & sel;
        return out1|out2;
    }

}
