package hackComputer.utility;

import java.util.Arrays;

public class Gen {

    public static boolean [] true16() {
        boolean [] a = {true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true,
                true};
        return a;
    }

    public static boolean [] false16() {
        boolean [] a = {false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false};
        return a;
    }

    public static boolean [] false15() {
        boolean [] a = {false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false,
                false};
        return a;
    }

    public static boolean [] custom16(String binary) {
        boolean [] out = true16();
        for(int i=0; i<16; i++) {
            if (binary.charAt(i) == '0') out[i] = false;
        }
        return out;
    }

    public static boolean [] custom2(String binary) {
        boolean [] out = {true, true};
        for(int i=0; i<2; i++) {
            if (binary.charAt(i) == '0') out[i] = false;
        }
        return out;
    }

    public static boolean [] custom3(String binary) {
        boolean [] out = {true, true, true};
        for(int i=0; i<3; i++) {
            if (binary.charAt(i) == '0') out[i] = false;
        }
        return out;
    }

    public static boolean [] custom6(String binary) {
        boolean [] out = {true, true, true, true, true, true};
        for(int i=0; i<6; i++) {
            if (binary.charAt(i) == '0') out[i] = false;
        }
        return out;
    }
    public static boolean [] custom9(String binary) {
        boolean [] out = {true, true, true, true, true, true, true, true, true};
        for(int i=0; i<9; i++) {
            if (binary.charAt(i) == '0') out[i] = false;
        }
        return out;
    }

    public static boolean [] custom12(String binary) {
        boolean [] out = {true, true, true, true, true, true, true, true, true, true, true, true};
        for(int i=0; i<12; i++) {
            if (binary.charAt(i) == '0') out[i] = false;
        }
        return out;
    }

    public static boolean [] custom13(String binary) {
        boolean [] out = {true, true, true, true, true, true, true, true, true, true, true, true, true};
        for(int i=0; i<13; i++) {
            if (binary.charAt(i) == '0') out[i] = false;
        }
        return out;
    }

    public static boolean [] custom14(String binary) {
        boolean [] out = {true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        for(int i=0; i<14; i++) {
            if (binary.charAt(i) == '0') out[i] = false;
        }
        return out;
    }

    public static boolean [] custom15(String binary) {
        boolean [] out = {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true};
        for(int i=0; i<15; i++) {
            if (binary.charAt(i) == '0') out[i] = false;
        }
        return out;
    }

    public static boolean [] one16() {
        boolean [] one = {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true};
        return one;
    }

        public static boolean [] toBoolean(String line) {
            boolean [] out = false16();
            for(int i=0; i<16; i++) {
                if(line.charAt(i) == '1') out[i] = true;
            }
            return out;
        }

        public static String boolToString(boolean [] line) {
            char [] a = {'0','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0'};
            for(int i=0; i<line.length; i++) {
                if(line[i]) a[i] = '1';
            }
            return Arrays.toString(a).replaceAll(",", "").replaceAll("]", "").replaceAll(" ", "").replaceAll("\\[", "");
        }

}
