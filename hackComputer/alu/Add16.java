package hackComputer.alu;

import hackComputer.utility.*;

public class Add16 {

    public boolean [] out(boolean [] a, boolean [] b) {
        HalfAdder halfAdder = new HalfAdder();
        FullAdder fullAdder = new FullAdder();
        boolean [] out = Gen.true16();
        boolean carry;
        out[15] = halfAdder.sum(a[15], b[15]);
        carry = halfAdder.carry(a[15], b[15]);
        for(int i=14; i>=0; i--) {
            out[i] = fullAdder.sum(carry, a[i], b[i]);
            carry = fullAdder.carry(carry, a[i], b[i]);
        }
        return out;
    }

}
