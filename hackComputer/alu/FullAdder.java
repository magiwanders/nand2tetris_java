package hackComputer.alu;

public class FullAdder {

    public boolean sum(boolean a, boolean b, boolean c) {
        HalfAdder halfAdder = new HalfAdder();
        boolean ab = halfAdder.sum(a, b);
        boolean cab = halfAdder.carry(a, b);
        return halfAdder.sum(ab, c);
    }

    public boolean carry(boolean a, boolean b, boolean c) {
        HalfAdder halfAdder = new HalfAdder();
        boolean ab = halfAdder.sum(a, b);
        boolean cab = halfAdder.carry(a, b);
        boolean sum = halfAdder.sum(ab, c);
        boolean cabc = halfAdder.carry(ab, c);
        return cab|cabc;
    }

}
