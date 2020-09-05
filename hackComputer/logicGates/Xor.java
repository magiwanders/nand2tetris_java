package hackComputer.logicGates;

public class Xor {
    public boolean out(boolean a, boolean b) {
        boolean notA = !a;
        boolean notB = !b;
        boolean out1 = a & notB;
        boolean out2 = b & notA;
        return out1|out2;
    }

}
