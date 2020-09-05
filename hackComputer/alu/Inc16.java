package hackComputer.alu;

import hackComputer.utility.Gen;

public class Inc16 {

    public boolean [] out(boolean [] in) {
        Add16 add16 = new Add16();
        boolean [] one = Gen.one16();
        return add16.out(in, one);
    }

}
