package vmtranslator;

import javax.print.DocFlavor;
import java.io.*;
import java.util.*;

public class CodeWriter {

    private BufferedReader r;
    private PrintWriter w;
    private Parser parse;
    private Vector<String> program;
    private int programLength;
    private String line; // Current line.

    private int lineNumber = 0;

    public CodeWriter(File path, boolean isDirectory) {
        if (isDirectory) {
            initializeDirectory(path);
            for(String file : path.list()) {
                String absoluteFilePath = path.getAbsolutePath() + File.separator + file;
                execute(absoluteFilePath);
            }
        } else {
            String file = path.getAbsolutePath();
            initializeFile(file);
            execute(file);
        }
        translateFile();
        exit();
    }

    private void execute(String file) {
        parse = new Parser();
        program = new Vector<String>();
        loadFile(); // Also removes spaces and comments.
    }

    private void initializeFile(String file) {
        try {
            r = new BufferedReader(new FileReader(new File(file)));
            w = new PrintWriter(new FileWriter(new File(file.replaceAll(".vm", ".asm"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeDirectory(File path) {
        String cumulativeFileName = path.getAbsolutePath() + File.separator + path.getName() + ".asm";
        initializeFile(cumulativeFileName);
    }

    private void loadFile() {
        try {
            while(true) {
                line = r.readLine();
                if (line == null) break; // File ended.
                clean(); // Removes spaces and comments from each line.
                if(!line.isEmpty()) {
                    program.add(line);
                }
            }

            programLength = program.size();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clean() {
        //line = line.replaceAll(" ", ""); // Removes spaces
        line = removeComments(line); // Removes comments
    }

    private String removeComments(String line) {
        int index = line.indexOf("//");

        if(index != -1) { // In case there is a commment.
            line = line.substring(0, index);
        }

        return line;
    }

    private void translateFile() {
        for(int i=0; i<programLength; i++) {
            line = program.elementAt(i);
            translateLine();
            lineNumber++;
        }
    }

    private void translateLine() {
        w.println("//" + line);
        switch(parse.commandType(line)) {
            case "A": writeA(); break; // A is ARITHMETIC/LOGIC command
            case "B": writeB(); break; // B is MEMORY SEGMENT command
            case "C": writeC(); break; // C is BRANCHING command
            case "D": writeD(); break; // D is FUNCTION command
            default: break;
        }
    }

    private void writeA() { // ARITHMETIC/LOGIC command
        System.out.println("Command type: A");
        switch(line) { // line content coincides with the command
          case "add": writeAadd(); break;
          case "sub": writeAsub(); break;
          case "neg": writeAneg(); break;
          case "eq": writeAeq(); break;
          case "gt": writeAgt(); break;
          case "lt": writeAlt(); break;
          case "and": writeAand(); break;
          case "or": writeAor(); break;
          case "not": writeAnot(); break;
          default: break;
        }
    }

    private void writeAprimer() {
      w.println("@SP"); // (SP-1) + (SP-2)
      w.println("M=M-1"); // decrements SP
      w.println("A=M"); // select address of last element of the stack (y) (SP which has just been decremented)
      w.println("D=M"); // memorize its content in D (D=y)
      w.println("A=A-1"); // select address of second-to-last element of the stack (x)
    }

    private void writeApost() {
      w.println("@SP"); // in case it doesn't jump, it has to write false
      w.println("A=M-1"); // last element of stack
      w.println("M=0"); // sets it to FALSE
      w.println("@END_" + lineNumber); // points to the end
      w.println("0;JMP"); // goes to the end
      w.println("(LABEL_" + lineNumber + ")"); // in case it jumps
      w.println("@SP");
      w.println("A=M-1"); // last element of stack
      w.println("M=-1"); // sets it to TRUE
      w.println("(END_" + lineNumber + ")");
    }

    private void writeAadd() {
      writeAprimer();
      w.println("M=D+M"); // sum its content to D (which contains the other factor y) and replaces it (x = x+y)
    }

    private void writeAsub() {
      writeAprimer();
      w.println("M=M-D"); // subtracts D (which contains the other factor y) to x and replaces it (x = x-y)
    }

    private void writeAneg() {
      w.println("@SP"); // -(SP-1)
      w.println("A=M-1"); // select address of last element of the stack (y) (SP decremented)
      w.println("M=-M"); // substitute its content with the opposite of it (y=-y)
    }

    private void writeAeq() {
      writeAprimer();
      w.println("D=M-D"); // subtracts D (which contains the other factor y) to x and puts it in D (D = x-y)
      w.println("@LABEL_" + lineNumber); // points LABEL
      w.println("D;JEQ"); // if x==y, then x-y==0 and the program jumps to (LABEL)
      writeApost();
    }

    private void writeAgt() {
      writeAprimer();
      w.println("D=M-D"); // subtracts D (which contains the other factor y) to x and puts it in D (D = x-y)
      w.println("@LABEL_" + lineNumber); // points LABEL
      w.println("D;JGT"); // if x>y, then x-y>0 and the program jumps to (LABEL)
      writeApost();
    }

    private void writeAlt() {
      writeAprimer();
      w.println("D=M-D"); // subtracts D (which contains the other factor y) to x and puts it in D (D = x-y)
      w.println("@LABEL_" + lineNumber); // points LABEL
      w.println("D;JLT"); // if x==y, then x-y==0 and the program jumps to (LABEL)
      writeApost();
    }

    private void writeAand() {
      writeAprimer();
      w.println("M=D&M"); // ANDs D (which contains the other factor y) and x and puts it in x (x = x&y)
    }

    private void writeAor() {
      writeAprimer();
      w.println("M=D|M"); // ORs D (which contains the other factor y) and x and puts it in x (x = x|y)
    }

    private void writeAnot() {
      w.println("@SP"); // -(SP-1)
      w.println("A=M-1"); // select address of last element of the stack (y) (SP decremented)
      w.println("M=!M"); // negates the content and substitutes it
    }

    private void writeB() { // MEMORY SEGMENT command
        System.out.println("Command type: B");
      switch(parse.arg1(line)) { // wether is a PUSH or POP
        case "push": writeBpush(); break;
        case "pop": writeBpop(); break;
        default: break;
      }
    }

    private void writeBpush() {
      String arg2 = parse.arg2(line);
      switch(arg2) { // wether is local, argument, this, that, constant, static, pointer, temp
        case "local": w.println("@LCL"); w.println("D=M");break;
        case "argument": w.println("@ARG"); w.println("D=M");break;
        case "this": w.println("@THIS"); w.println("D=M");break;
        case "that": w.println("@THAT"); w.println("D=M");break;
        case "constant": writeBpushConstant(); break;
        case "static": w.println("@16"); w.println("D=A");break;
        case "pointer": writeBpushPointer(); break;
        case "temp": w.println("@5"); w.println("D=A");break;
        default: break;
      }
      if (arg2.equals("constant") || arg2.equals("pointer")) return;
      else writeBfinal();
    }

    private void writeBfinal() {
      w.println("@" + parse.arg3(line));
      w.println("D=D+A"); // D = LOCAL + offset
      w.println("A=D"); // Point to LOCAL + offset
      w.println("D=M"); // Put local @offset in D
      w.println("@SP");
      w.println("M=M+1"); // Increments SP
      w.println("A=M-1");
      w.println("M=D"); // D contains the value to be pushed
    }

    private void  writeBpushConstant() {
      w.println("@" + parse.arg3(line));
      w.println("D=A");
      w.println("@SP");
      w.println("A=M");
      w.println("M=D");
      w.println("@SP");
      w.println("M=M+1"); // Increments SP
    }

    private void  writeBpushPointer() {
      switch (parse.arg3(line)) {
        case "0": w.println("@3"); break;
        case "1": w.println("@4"); break;
        default: break;
      }
      w.println("D=M");
      w.println("@SP");
      w.println("M=M+1");
      w.println("A=M-1");
      w.println("M=D");
    }

    private void writeBpop() {
      boolean pointer = false;
      switch(parse.arg2(line)) { // wether is local, argument, this, that, constant, static, pointer, temp
        case "local": w.println("@LCL"); w.println("D=M"); break;
        case "argument": w.println("@ARG"); w.println("D=M"); break;
        case "this": w.println("@THIS"); w.println("D=M"); break;
        case "that": w.println("@THAT"); w.println("D=M"); break;
        case "static": w.println("@16"); w.println("D=A"); break;
        case "pointer": { writeBpopPointer(); pointer = true; break;}
        case "temp": w.println("@5"); w.println("D=A"); break;
        default: break;
      }
      if (pointer) return;
      else writeBpopFinal();
    }

    private void writeBpopFinalPrimer() {
      w.println("@" + parse.arg3(line));
      w.println("D=D+A"); // points the address to which the value will be popped
      w.println("@R13");
      w.println("M=D");
    }

    private void writeBpopFinal() {
      writeBpopFinalPrimer(); // puts address to pop to into R13
      w.println("@SP");
      w.println("M=M-1");
      w.println("A=M");
      w.println("D=M");
      w.println("@R13");
      w.println("A=M");
      w.println("M=D");
    }

    private void writeBpopPointer() {
        w.println("@SP");
        w.println("M=M-1");
        w.println("A=M");
        w.println("D=M");
        switch (parse.arg3(line)) {
            case "0": w.println("@THIS"); break;
            case "1": w.println("@THAT"); break;
            default: break;
        }
      w.println("M=D");
    }

    private void writeC() {
        System.out.println("Command type: C");
        switch(parse.arg1(line)) { // wether is a PUSH or POP
            case "label": writeClabel(); break;
            case "if-goto": writeCifgoto(); break;
            case "goto": writeCgoto(); break;
            default: break;
        }
    }

    private void writeClabel() {
        w.println("(" + parse.arg2(line) + ")"); // Writes label name
    }

    private void writeCifgoto() {
        w.println("@SP");
        w.println("M=M-1");
        w.println("A=M");
        w.println("D=M");
        w.println("@" + parse.arg2(line));
        w.println("D;JNE");
    }

    private void writeCgoto() {
        w.println("@" + parse.arg2(line));
        w.println("0;JEQ");
    }

    private void writeD() {
        System.out.println("Command type: D");
        switch(parse.arg1(line)) {
            case "function": writeDfunction(); break;
            case "call": writeDcall(); break;
            case "return": writeDreturn(); break;
            default: break;
        }
    }

    private void writeDcall() {
        w.println("@SP"); // push SP(returnAddress) and save SP (future SP-5) in general purpose register R14 and nArgs in R15
        w.println("M=M+1");
        w.println("AD=M-1");
        w.println("M=D");
        w.println("@R14");
        w.println("M=D");
        w.println("@" + parse.arg3(line));
        w.println("D=A");
        w.println("@R15");
        w.println("M=D");

        w.println("@LCL"); // push LCL
        simplePush();

        w.println("@ARG"); // push ARG
        simplePush();

        w.println("@THIS"); // push THIS
        simplePush();

        w.println("@THAT"); // push THAT
        simplePush();

        w.println("@LCL"); // push LCL
        simplePush();

        w.println("@R14"); // ARG = SP-5-nArgs ("SP-5" is in gen.pur.register R14 and nArgs in R15)
        w.println("D=M");
        w.println("@R15");
        w.println("D=D-M"); // (SP-5)-nArgs
        w.println("@ARG");
        w.println("M=D");

        w.println("@SP"); // LCL = SP
        w.println("D=M");
        w.println("@LCL");
        w.println("M=D");

        String originalLine = line;
        String functionName = parse.arg2(line); // goto functionName
        line = "goto " + functionName;
        translateLine();

        line = "label " + functionName + "$returnAddress";
        translateLine();

        line = originalLine;
    }

    private void simplePush() {
        w.println("D=M");
        w.println("@SP");
        w.println("M=M+1");
        w.println("A=M-1");
        w.println("M=D");
    }

    private void writeDreturn() {
        w.println("@LCL"); // LCL saved to endframe (gen.pur.register R15)
        w.println("D=M");
        w.println("@R15");
        w.println("M=D");

        w.println("M=M-1"); // enframe-5
        w.println("M=M-1");
        w.println("M=M-1");
        w.println("M=M-1");
        w.println("MD=M-1");

        // get returnAddress as endframe-5 = LCL-5
        w.println("@R14"); // save it to R14
        w.println("M=D-1");

        w.println("@SP"); // pop returnValue(last element of the stack) to *ARG (arg zero)
        w.println("M=M-1");
        w.println("A=M");
        w.println("D=M");
        w.println("@ARG");
        w.println("A=M");
        w.println("M=D");

        w.println("@R15"); // LCL = endframe-4
        w.println("AMD=M+1");
        w.println("D=M");
        w.println("@LCL");
        w.println("M=D");

        w.println("@R15"); // ARG = endframe-3
        w.println("AMD=M+1");
        w.println("D=M");
        w.println("@ARG");
        w.println("M=D");

        w.println("@R15"); // THIS = endframe-2
        w.println("AMD=M+1");
        w.println("D=M");
        w.println("@THIS");
        w.println("M=D");

        w.println("@R15"); // THAT = endframe-1
        w.println("AMD=M+1");
        w.println("D=M");
        w.println("@THAT");
        w.println("M=D");

        w.println("@R14"); // goto return address stored in R14
        w.println("D=M");
        w.println("@SP");
        w.println("M=D");
    }

    private void writeDfunction() {
        w.println("(" + parse.arg2(line) + ")");
        String originalLine = line;
        int args = Integer.parseInt(parse.arg3(line));
        for(int i=0; i<args; i++) {
            line = "push constant 0";
            translateLine();
        }
        line = originalLine;
    }



    public void exit() {
        try{
            w.close();
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
