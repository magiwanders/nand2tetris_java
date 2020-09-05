package hackComputer.logicGates;

public class Or8way {

    public boolean out(boolean [] in) {
        boolean out = in[0];
        for(int i=1; i<8; i++) {
            out = out|in[i];
        }
        return out;
    }

}
