package hackComputer.logicGates;

public class Not16 {

    public boolean [] out(boolean [] in) {
        boolean [] out = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        for(int i=0; i<16; i++) {
            out[i] = !in[i];
        }
        return out;
    }

}
