package utilities;

import java.util.Vector;

public class Log {

    public static String stringify(boolean [] a) {
        String aString = "";
        for(int i=0; i<a.length; i++) {
            if (!a[i]) aString += "0";
            else aString += "1";
        }
        return aString;
    }

    public static void console(String message) {
        System.out.println(message);
    }

    public static void console(Vector<String> messages) {
        for(String message : messages) System.out.println(message);
    }

}
