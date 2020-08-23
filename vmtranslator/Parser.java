package vmtranslator;

import java.io.*;
import java.util.*;

public class Parser {

    public String commandType(String line) {
        System.out.println("Parsing: " + line);
        if (isA(line)) return "A";
        if (line.equals("return")) return "D";
        String identifier = line.substring(0, line.indexOf(" "));
        if (isB(identifier)) return "B";
        else if (isC(identifier)) return "C";
        else if (isD(identifier)) return "D";
        return "ERROR";
    }

    private boolean isA(String id) {
        return  id.equals("add") ||
                id.equals("sub") ||
                id.equals("neg") ||
                id.equals("eq") ||
                id.equals("gt") ||
                id.equals("lt") ||
                id.equals("and") ||
                id.equals("or") ||
                id.equals("not");
    }

    private boolean isB(String id) {
        return  id.equals("push") ||
                id.equals("pop");
    }

    private boolean isC(String id) {
        return  id.equals("label") ||
                id.equals("if-goto") ||
                id.equals("goto");
    }

    private boolean isD(String id) {
        return  id.equals("function") ||
                id.equals("call");// ||
                //id.equals("return");
    }

    public String arg1(String line) {
      int i = line.indexOf(" ");
      if (i==-1) return line;
      return line.substring(0, i);
    }

    public String arg2(String line) {
      int i = line.indexOf(" ");
      if (i==-1) return null;
      int j = line.indexOf(" ", i+1);
      if (j==-1) return line.substring(i+1, line.length()).replaceAll(" ", "");
      return line.substring(i+1, j).replaceAll(" ", "");
    }

    public String arg3(String line) {
      int i = line.indexOf(" ");
      if (i==-1) return null;
      int j = line.indexOf(" ", i+1);
      return line.substring(j+1, line.length()).replaceAll(" ", "");
    }

}
