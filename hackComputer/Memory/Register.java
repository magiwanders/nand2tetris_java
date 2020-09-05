package hackComputer.Memory;

import hackComputer.utility.*;

public class Register {

    Bit bit0 = new Bit();
    Bit bit1 = new Bit();
    Bit bit2 = new Bit();
    Bit bit3 = new Bit();
    Bit bit4 = new Bit();
    Bit bit5 = new Bit();
    Bit bit6 = new Bit();
    Bit bit7 = new Bit();
    Bit bit8 = new Bit();
    Bit bit9 = new Bit();
    Bit bit10 = new Bit();
    Bit bit11 = new Bit();
    Bit bit12 = new Bit();
    Bit bit13 = new Bit();
    Bit bit14 = new Bit();
    Bit bit15 = new Bit();

    public boolean [] write(boolean [] in, boolean load) {
        boolean [] out = {bit0.write(in[0], load),bit1.write(in[1], load),bit2.write(in[2], load),bit3.write(in[3], load),bit4.write(in[4], load),bit5.write(in[5], load),bit6.write(in[6], load),bit7.write(in[7], load),bit8.write(in[8], load),bit9.write(in[9], load),bit10.write(in[10], load),bit11.write(in[11], load),bit12.write(in[12], load),bit13.write(in[13], load),bit14.write(in[14], load),bit15.write(in[15], load)};
        return out;
    }

    public boolean [] read() {
        return write(Gen.false16(), false);
    }

    public void update() {
        bit0.update();
        bit1.update();
        bit2.update();
        bit3.update();
        bit4.update();
        bit5.update();
        bit6.update();
        bit7.update();
        bit8.update();
        bit9.update();
        bit10.update();
        bit11.update();
        bit12.update();
        bit13.update();
        bit14.update();
        bit15.update();
    }


}
