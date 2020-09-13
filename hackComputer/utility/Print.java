package hackComputer.utility;

public class Print {

    public static void out(boolean a) {
        if(a) System.out.println("1");
        else System.out.println("0");
    }

    public static void out(boolean [] a) {
        for(int i=0; i<16; i++) {
            if(a[i]) System.out.print("1");
            else System.out.print("0");
        }
        System.out.println("");
    }

}
