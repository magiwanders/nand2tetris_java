package hackComputer.CPU;

import hackComputer.alu.*;
import hackComputer.Memory.*;
import hackComputer.logicGates.*;
import hackComputer.utility.*;

public class CPU {

    Register D = new Register();
    Register A = new Register();

    boolean [] outM;
    boolean writeM;
    boolean [] addressM;
    boolean [] PC;

    public void update(boolean [] instruction, boolean [] inM, boolean reset) {
        Alu alu = new Alu();
        PC pc = new PC();
        PCcontroller pccontroller = new PCcontroller();
        Mux16 mux16 = new Mux16();
        Mux mux = new Mux();
        boolean opcode = instruction[0];
        boolean [] address = {instruction[1],instruction[2],instruction[3],instruction[4],instruction[5],instruction[6],instruction[7],instruction[8],instruction[9],instruction[10],instruction[11],instruction[12],instruction[13],instruction[14],instruction[15]};
        boolean a = instruction[3];
        boolean [] cccccc = {instruction[4],instruction[5],instruction[6],instruction[7],instruction[8],instruction[9]};
        boolean [] ddd = {instruction[10],instruction[11],instruction[12]};
        boolean [] jjj = {instruction[13],instruction[14],instruction[15]};
        boolean [] x = D.read();
        addressM = A.read();
        boolean [] y = mux16.out(addressM, inM, a);
        outM = alu.out(x, y, cccccc);
        boolean zr = alu.zr(x, y, cccccc);
        boolean ng = alu.ng(x, y, cccccc);
        D.write(outM, mux.out(instruction[11], false, !opcode));
        boolean [] inA = mux16.out(outM, instruction, !opcode);
        A.write(inA, instruction[10]|(!opcode));
        boolean PCload = pccontroller.out(jjj, opcode, zr, ng);
        PC = pc.set(addressM, PCload, !PCload, reset);
        writeM = mux.out(instruction[12], false, !opcode);
    }

    public boolean [] outM() {
        return outM;
    }

    public boolean writeM() {
        return writeM;
    }

    public boolean [] addressM() {
        return addressM;
    }

    public boolean [] PC() {
        return PC;
    }

    public void reset() {
        PC = Gen.false16();
        A.write(Gen.false16(), true);
        A.update();
        D.write(Gen.false16(), true);
        D.update();
    }

}
