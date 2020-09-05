package hackComputer.logicGates;

public class Dmux8way {

    public boolean a(boolean in, boolean [] sel) {
        Dmux dmux = new Dmux();
        Dmux4way dmux4way = new Dmux4way();
        boolean abcd = dmux.a(in, sel[0]);
        boolean [] sel12 = {sel[1], sel[2]};
        return dmux4way.a(abcd, sel12);
    }

    public boolean b(boolean in, boolean [] sel) {
        Dmux dmux = new Dmux();
        Dmux4way dmux4way = new Dmux4way();
        boolean abcd = dmux.a(in, sel[0]);
        boolean [] sel12 = {sel[1], sel[2]};
        return dmux4way.b(abcd, sel12);
    }

    public boolean c(boolean in, boolean [] sel) {
        Dmux dmux = new Dmux();
        Dmux4way dmux4way = new Dmux4way();
        boolean abcd = dmux.a(in, sel[0]);
        boolean [] sel12 = {sel[1], sel[2]};
        return dmux4way.c(abcd, sel12);
    }

    public boolean d(boolean in, boolean [] sel) {
        Dmux dmux = new Dmux();
        Dmux4way dmux4way = new Dmux4way();
        boolean abcd = dmux.a(in, sel[0]);
        boolean [] sel12 = {sel[1], sel[2]};
        return dmux4way.d(abcd, sel12);
    }

    public boolean e(boolean in, boolean [] sel) {
        Dmux dmux = new Dmux();
        Dmux4way dmux4way = new Dmux4way();
        boolean efgh = dmux.b(in, sel[0]);
        boolean [] sel12 = {sel[1], sel[2]};
        return dmux4way.a(efgh, sel12);
    }

    public boolean f(boolean in, boolean [] sel) {
        Dmux dmux = new Dmux();
        Dmux4way dmux4way = new Dmux4way();
        boolean efgh = dmux.b(in, sel[0]);
        boolean [] sel12 = {sel[1], sel[2]};
        return dmux4way.b(efgh, sel12);
    }

    public boolean g(boolean in, boolean [] sel) {
        Dmux dmux = new Dmux();
        Dmux4way dmux4way = new Dmux4way();
        boolean efgh = dmux.b(in, sel[0]);
        boolean [] sel12 = {sel[1], sel[2]};
        return dmux4way.c(efgh, sel12);
    }

    public boolean h(boolean in, boolean [] sel) {
        Dmux dmux = new Dmux();
        Dmux4way dmux4way = new Dmux4way();
        boolean efgh = dmux.b(in, sel[0]);
        boolean [] sel12 = {sel[1], sel[2]};
        return dmux4way.d(efgh, sel12);
    }

}
