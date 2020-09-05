package hackComputer.logicGates;

public class Dmux4way {

    public boolean a(boolean in, boolean [] sel) {
        Dmux dmux = new Dmux();
        boolean isAorB = dmux.a(in, sel[0]);
        return dmux.a(isAorB, sel[1]);
    }

    public boolean b(boolean in, boolean [] sel) {
        Dmux dmux = new Dmux();
        boolean isAorB = dmux.a(in, sel[0]);
        return dmux.b(isAorB, sel[1]);
    }

    public boolean c(boolean in, boolean [] sel) {
        Dmux dmux = new Dmux();
        boolean isCorD = dmux.b(in, sel[0]);
        return dmux.a(isCorD, sel[1]);
    }

    public boolean d(boolean in, boolean [] sel) {
        Dmux dmux = new Dmux();
        boolean isCorD = dmux.b(in, sel[0]);
        return dmux.b(isCorD, sel[1]);
    }

}
