package assembler;

import java.io.*;
import java.util.*;

public class Parser {

    public String AInstructionInt(String line) {
        line = line.replaceAll("@", "");
        return line;
     }

    public String dest(String line) {
        int indexOfEqual = line.indexOf("=");
        if(indexOfEqual == -1) return "NULL";
        return line.substring(0, indexOfEqual);
    }

    public String comp(String line) {
        int indexOfEqual = line.indexOf("=");
        int indexOfEnd = line.indexOf(";");
        if(indexOfEnd == -1) return line.substring(indexOfEqual+1, line.length());
        else return line.substring(indexOfEqual+1, indexOfEnd);
    }

    public String jump(String line) {
        int indexOfEnd = line.indexOf(";");
        if((indexOfEnd+1) == line.length() || indexOfEnd == -1) return "NULL";
        else return line.substring(indexOfEnd+1, line.length());
    }

}
