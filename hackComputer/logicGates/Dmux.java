package hackComputer.logicGates;

public class Dmux {

    public boolean a(boolean in, boolean sel) {
        boolean notSel = !sel;
        return in&notSel;
    }

    public boolean b(boolean in, boolean sel) {
        return in&sel;
    }





}
