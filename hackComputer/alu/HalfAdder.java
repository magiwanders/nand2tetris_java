package hackComputer.alu;

import hackComputer.logicGates.*;

public class HalfAdder {

    public boolean sum(boolean a, boolean b) {
        Xor xor = new Xor();
        return xor.out(a, b);
    }

    public boolean carry(boolean a, boolean b) {
        return a&b;
    }

}
