package vmtranslator;

import java.io.*;
import java.util.*;

public class Parser {

    public String commandType(String line) {
        if (line.indexOf(" ")==-1) return "A";
        return "B";
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
      return line.substring(i+1, j);
    }

    public String arg3(String line) {
      int i = line.indexOf(" ");
      if (i==-1) return null;
      int j = line.indexOf(" ", i+1);
      return line.substring(j+1, line.length());
    }

}
