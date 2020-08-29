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

    private String currentFile;
    private String line; // Current line.

    private String lastLabel;

    private int lineNumber = 0;
    private int nestedCallNumber = 0;
    private int lineWritten; // LAST RAM ADDRESS WRITTEN (does not count labels of course)

    private Vector<String> programList; // List of .vm files to be transcoded

    public CodeWriter(File path, boolean isDirectory) {
        if (isDirectory) {
            System.out.println("Directory found! It is: " + path);
            populateProgramList(path);
            initializeDirW(path);
            lineWritten = -1;
            writeBootstrap();
            for(int i = 0; i<programList.size(); i++) {
                System.out.println("Starting to translate program " + (i+1) + "of" + programList.size() );
                currentFile = programList.elementAt(i);
                initializeDirR(path.getAbsolutePath() + File.separator + currentFile);
                // System.out.println("Let's translate!");
                execute();
            }
        } else {
            String file = path.getAbsolutePath();
            initializeFile(file);
            writeBootstrap();
            execute();
        }
        exit();
    }

    private void initializeDirW(File path) {
        String pathFile = path.getAbsolutePath() + File.separator + path.getName() + ".asm";
        try {
            w = new PrintWriter(new FileWriter(new File(pathFile)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeDirR(String file) {
        try {
            r = new BufferedReader(new FileReader(new File(file)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateProgramList(File path) {
        String [] allFiles = path.list();
        programList = new Vector<>();
        for (String file : allFiles) {
            if (file.endsWith(".vm")) {
                programList.addElement(file);
                System.out.println(file + " added.");
            }
        }
    }

    private void execute() {
        parse = new Parser();
        program = new Vector<String>();
        loadFile(); // Also removes spaces and comments.
        translateFile();
    }

    private void initializeFile(String file) {
        try {
            r = new BufferedReader(new FileReader(new File(file)));
            w = new PrintWriter(new FileWriter(new File(file.replaceAll(".vm", ".asm"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFile() {
        // System.out.println("Let's load!");
        try {
            while(true) {
                line = r.readLine();
                if (line == null) break; // File ended.
                clean(); // Removes spaces and comments from each line.
                if(!line.isEmpty()) {
                    // System.out.println("Read:" + line);
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
        line = line.trim();
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

    private void writeBootstrap() {
        w.println("@256"); // SP=256
        w.println("D=A");
        w.println("@SP");
        w.println("M=D");

        //line = "call Sys.0nit 0";
        //translateLine();

        //w.println("@Sys.init$returnAddress");
        //w.println("D=A");

        w.println("@SP");
        w.println("M=M+1");
        //w.println("A=M-1");
        //w.println("M=D");

        w.println("@LCL"); // push LCL
        simplePush();

        w.println("@ARG"); // push ARG
        simplePush();

        w.println("@THIS"); // push THIS
        simplePush();

        w.println("@THAT"); // push THAT
        simplePush();

        //w.println("@SP"); // D contains SP
        //w.println("D=M");
        //w.println("@5");
        //w.println("D=D-A"); // D contains SP-5
        //w.println("@" + parse.arg3(line));
        //w.println("D=D-A"); // D contains (SP-5)-nArgs
        //w.println("@ARG"); // New ARG value
        //w.println("M=D");

        //w.println("@SP"); // LCL = SP
        //w.println("D=M");
        //w.println("@LCL");
        //w.println("M=D");

        w.println("@Sys.init");
        w.println("0;JEQ");

        w.println("(Sys.init$returnAddress)"); // Writes label name
    }

    private void translateLine() {
        w.println("//" + line);
        switch(parse.commandType(line)) {
            case "A": writeA(); break; // A is ARITHMETIC/LOGIC command
            case "B": writeB(); break; // B is MEMORY SEGMENT command
            case "C": writeC(); break; // C is BRANCHING command
            case "D": writeD(); break; // D is FUNCTION command
            default: System.out.println("ERROR"); break;
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
      w.println("@SP"); // in case it doesn't jump, it has to w.println false
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
        case "static": writeBpushStatic();break;
        case "pointer": writeBpushPointer(); break;
        case "temp": w.println("@5"); w.println("D=A");break;
        default: break;
      }
      if (arg2.equals("constant") || arg2.equals("pointer") || arg2.equals("static")) return;
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

    private void writeBpushConstant() {
      w.println("@" + parse.arg3(line));
      w.println("D=A");
      w.println("@SP");
      w.println("A=M");
      w.println("M=D");
      w.println("@SP");
      w.println("M=M+1"); // Increments SP
    }

    private void writeBpushPointer() {
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

    private void writeBpushStatic() {
        w.println("@" + currentFile + "." + parse.arg3(line));
        w.println("D=M"); // Put local @offset in D
        w.println("@SP");
        w.println("M=M+1"); // Increments SP
        w.println("A=M-1");
        w.println("M=D"); // D contains the value to be pushed
    }

    private void writeBpop() {
      boolean pointer = false;
      switch(parse.arg2(line)) { // wether is local, argument, this, that, constant, static, pointer, temp
        case "local": w.println("@LCL"); w.println("D=M"); break;
        case "argument": w.println("@ARG"); w.println("D=M"); break;
        case "this": w.println("@THIS"); w.println("D=M"); break;
        case "that": w.println("@THAT"); w.println("D=M"); break;
        case "static": writeBpopStatic(); pointer = true; break;
        case "pointer": { writeBpopPointer(); pointer = true; break;}
        case "temp": w.println("@5"); w.println("D=A"); break;
        default: break;
      }
      if (pointer) return;
      else writeBpopFinal();
    }

    private void writeBpopStatic() {
        w.println("@" + currentFile + "." + parse.arg3(line));
        w.println("D=A");
        w.println("@R13");
        w.println("M=D");
        w.println("@SP");
        w.println("M=M-1");
        w.println("A=M");
        w.println("D=M");
        w.println("@R13");
        w.println("A=M");
        w.println("M=D");
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
        // SOME USE write() and SOME w.println() because labels are removed and do not occupy a part of RAM
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
        w.println("@" + parse.arg2(line) + "_call." + nestedCallNumber + "_" + "$returnAddress");
        w.println("D=A");

        w.println("@SP");
        w.println("M=M+1");
        w.println("A=M-1");
        w.println("M=D");

        w.println("@LCL"); // push LCL
        simplePush();

        w.println("@ARG"); // push ARG
        simplePush();

        w.println("@THIS"); // push THIS
        simplePush();

        w.println("@THAT"); // push THAT
        simplePush();

        w.println("@SP"); // D contains SP
        w.println("D=M");
        w.println("@5");
        w.println("D=D-A"); // D contains SP-5
        w.println("@" + parse.arg3(line));
        w.println("D=D-A"); // D contains (SP-5)-nArgs
        w.println("@ARG"); // New ARG value
        w.println("M=D");

        w.println("@SP"); // LCL = SP
        w.println("D=M");
        w.println("@LCL");
        w.println("M=D");

        w.println("@" + parse.arg2(line));
        w.println("0;JEQ");

        w.println("(" +  parse.arg2(line) + "_call." + nestedCallNumber + "_" + "$returnAddress" + ")"); // Writes label name
        nestedCallNumber++;
    }

    private void simplePush() {
        w.println("D=M");
        w.println("@SP");
        w.println("M=M+1");
        w.println("A=M-1");
        w.println("M=D");
    }

    private void writeDreturn() {
        w.println("@LCL"); // N.B. ReturnAddress is the value of SP after the function has returned
        w.println("D=M"); // D contains pointer to LCL
        w.println("@R15"); // Save LCL to R15(endframe)
        w.println("M=D");
        w.println("@5"); // Actually compute LCL-5
        w.println("D=D-A"); // D points LCL-5
        w.println("A=D");
        w.println("D=M"); // Gets content of returnAddress
        w.println("@R14"); // Saves it to R14
        w.println("M=D");

        w.println("@SP"); // pop returnValue(last element of the stack) to *ARG (arg zero)
        w.println("M=M-1");
        w.println("A=M");
        w.println("D=M");
        w.println("@ARG");
        w.println("A=M");
        w.println("M=D");

        w.println("@ARG"); // SP=ARG+1
        w.println("D=M");
        w.println("@SP");
        w.println("M=D+1");

        w.println("@R15"); // reinstate old THAT
        w.println("M=M-1");
        w.println("A=M"); // points saved THAT
        w.println("D=M"); // D contains saved THAT value
        w.println("@THAT");
        w.println("M=D");

        w.println("@R15"); // reinstate old THIS
        w.println("M=M-1"); // now R15 contains pointer to saved ARG
        w.println("A=M"); // points saved THIS
        w.println("D=M"); // D contains saved THIS value
        w.println("@THIS");
        w.println("M=D");

        w.println("@R15"); // reinstate old ARG
        w.println("M=M-1"); // now R15 contains pointer to saved LCL
        w.println("A=M"); // points saved ARG
        w.println("D=M"); // D contains saved ARG value
        w.println("@ARG");
        w.println("M=D");

        w.println("@R15"); // reinstate old LCL
        w.println("M=M-1");
        w.println("A=M"); // points saved LCL
        w.println("D=M"); // D contains saved LCL value
        w.println("@LCL");
        w.println("M=D");

        w.println("@R14");
        w.println("A=M");
        w.println("0;JEQ");
    }

    private void writeDfunction() {
        lastLabel = parse.arg2(line);
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
