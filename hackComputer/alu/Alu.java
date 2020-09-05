package hackComputer.alu;

import hackComputer.logicGates.*;
import hackComputer.utility.*;

public class Alu {

    public boolean [] out(boolean [] x, boolean [] y, boolean [] cccccc) {
        return out1(x, y, cccccc);
    }

    public boolean zr(boolean [] x, boolean [] y, boolean [] cccccc) {
        Or8way or8way = new Or8way();
        Mux mux = new Mux();
        boolean [] out1 = out1(x, y, cccccc);
        boolean [] out107 = {out1[0],out1[1],out1[2],out1[3],out1[4],out1[5],out1[6],out1[7]};
        boolean [] out1815 = {out1[8],out1[9],out1[10],out1[11],out1[12],out1[13],out1[14],out1[15]};
        boolean origway1 = or8way.out(out107);
        boolean origway2 = or8way.out(out1815);
        boolean isnotzero = origway1|origway2;
        return mux.out(true, false, isnotzero);
    }

    public boolean ng(boolean [] x, boolean [] y, boolean [] cccccc) {
        Mux mux = new Mux();
        boolean [] out1 = out1(x, y, cccccc);
        return mux.out(false, true, out1[0]);
    }

    private boolean [] out1(boolean [] x, boolean [] y, boolean [] cccccc) {
        Mux16 mux16 = new Mux16();
        Not16 not16 = new Not16();
        And16 and16 = new And16();
        Add16 add16 = new Add16();
        boolean [] false16 = Gen.false16();
        boolean [] true16 = Gen.true16();
        boolean zx = cccccc[0];
        boolean nx = cccccc[1];
        boolean zy = cccccc[2];
        boolean ny = cccccc[3];
        boolean f = cccccc[4];
        boolean no = cccccc[5];
        boolean [] zxout = mux16.out(x, false16, zx);
        boolean [] zyout = mux16.out(y, false16, zy);
        boolean [] notzxout = not16.out(zxout);
        boolean [] notzyout = not16.out(zyout);
        boolean [] zxnxout = mux16.out(zxout, notzxout, nx);
        boolean [] zynyout = mux16.out(zyout, notzyout, ny);
        boolean [] xandy = and16.out(zxnxout, zynyout);
        boolean [] xplusy = add16.out(zxnxout, zynyout);
        boolean [] outf = mux16.out(xandy, xplusy, f);
        boolean [] notoutf = not16.out(outf);
        return mux16.out(outf, notoutf, no);
    }

}
