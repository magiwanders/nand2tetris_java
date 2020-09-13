package hackComputer.CPU;

import gui.HackComputerGui;
import hackComputer.alu.*;
import hackComputer.Memory.*;
import hackComputer.logicGates.*;
import hackComputer.utility.*;

public class CPU {

    PC pc = new PC();
    Register D = new Register();
    Register A = new Register();

    boolean [] outM = {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
    boolean writeM = false;
    boolean [] addressM = {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
    boolean [] PC = {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};

    boolean [] lastOutALU = {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};

    public void update(boolean [] instruction, boolean [] inM, boolean reset) {
        //System.out.println("Before update: {outM="+Gen.boolToString(outM)+", writeM="+writeM+", addressM="+Gen.boolToString(addressM)+", PC="+Gen.boolToString(PC)+"}");
        Alu alu = new Alu();
        PCcontroller pccontroller = new PCcontroller();
        Mux16 mux16 = new Mux16();
        Mux mux = new Mux();
        And16 and16 = new And16();

        boolean opcode = instruction[0]; // 0 for A-instruction, 1 for C-instruction

        // Set A and D registers
        boolean d1 = instruction[10];
        boolean d2 = instruction[11];
        boolean d3 = instruction[12];
        boolean [] inA = mux16.out(lastOutALU, instruction, !opcode);
        A.write(inA, d1|(!opcode));
        A.update();

        // Alu  operation
        boolean a = instruction[3];
        boolean [] cccccc = {instruction[4],instruction[5],instruction[6],instruction[7],instruction[8],instruction[9]};
        boolean [] x = D.read();
        addressM = A.read();
        boolean [] y = mux16.out(addressM, inM, a);
        System.out.println("Alu. x: " + Gen.boolToString(x) +". y: "+ Gen.boolToString(y) +". cccccc: "+ Gen.boolToString(cccccc));
        lastOutALU = alu.out(x, y, cccccc);
        boolean [] d316;
        System.out.println("d3: " + d3);
        if(d3) d316 = Gen.true16();
        else d316 = Gen.false16();
        outM = and16.out(lastOutALU, d316);

        System.out.println("Write " + Gen.boolToString(lastOutALU)+ " to D: " + mux.out(d2, false, !opcode));
        D.write(lastOutALU, mux.out(d2, false, !opcode));
        D.update();

        // writeM
        writeM = mux.out(d3, false, !opcode);

        // PC increment
        boolean [] jjj = {instruction[13],instruction[14],instruction[15]};
        boolean zr = alu.zr(x, y, cccccc);
        boolean ng = alu.ng(x, y, cccccc);
        boolean PCload = pccontroller.out(jjj, opcode, zr, ng);
        ////System.out.println("PC load: "+PCload+ " Address: " + Gen.boolToString(addressM));
        PC = pc.set(addressM, PCload, !PCload, reset);
        ////System.out.println("After update: {outM="+Gen.boolToString(outM)+", writeM="+writeM+", addressM="+Gen.boolToString(addressM)+", PC="+Gen.boolToString(PC)+"}");
    }

    public boolean [] getOutM() {
        return outM;
    }

    public boolean getWriteM() {
        return writeM;
    }

    public boolean [] getAddressM() {
        return addressM;
    }

    public boolean [] getPCvalue() {
        return PC;
    }

    public boolean [] getA() {
        return A.read();
    }

    public boolean [] getD() {
        return D.read();
    }

    public void reset() {
        PC = Gen.false16();
        A.write(Gen.false16(), true);
        A.update();
        D.write(Gen.false16(), true);
        D.update();
    }

}
