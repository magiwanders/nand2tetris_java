package utilities;

public class Log {

    public static String stringify(boolean [] a) {
        String aString = "";
        for(int i=0; i<a.length; i++) {
            if (!a[i]) aString += "0";
            else aString += "1";
        }
        return aString;
    }

}
