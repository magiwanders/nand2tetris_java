package hackComputer.logicGates;

public class Or16 {

    public boolean [] out(boolean [] a, boolean [] b) {
        boolean [] out = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        for(int i=0; i<16; i++) {
            out[i] = a[i]|b[i];
        }
        return out;
    }

}
