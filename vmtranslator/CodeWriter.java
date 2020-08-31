/*

 CODE WRITER - translates VM Code (foo.vm) into assembly language (foo.asm). It can translate more than one .vm file into one .asm file.

*/

package vmtranslator;

import java.io.*;
import java.util.*;

public class CodeWriter {

    // In order to read from the .vm file(s) and write to the .asm file.
    private BufferedReader r;
    private PrintWriter w;

    // The other component of the CodeWriter
    private final VMParser parse = new VMParser(); // Decomposes each VM command and retrieves its various parts.

    // Loaded file(s) internal representation.
    private final File directory;                                    // The directory containing the .vm file(s).
    private final Vector<String> fileList = new Vector<>(); // List of .vm files to be transcoded
    private String currentFileName;                            // Name of the single .vm file currently being processed (no extension).
    private Vector<String> program;                      // Contains line by line the currently to be translated .vm file.
    private String line;                                       // Current line being processed.

    private int lineNumber = 0;       // Used to create unique labels for A-commands that need them. Gets incremented each new line being processed. NOT resetted for each file.
    private int nestedCallNumber = 0; // Used to create unique labels for same-function nested calls.


    public CodeWriter(File directory) {
        this.directory = directory;

        populateProgramList(directory); // Creates list of .vm files contained in 'directory'.
        initializeDirW(directory);      // Creates PrintWriter w for the directoryName.asm file.
        writeBootstrap();               // Writes BOOTSTRAP code to the directoryName.asm file.
        translateDirectory();           // Actually adds the translation of all .vm files to directoryName.asm.
        exit();                         // Closes writer and reader.
    }


    private void populateProgramList(File path) {
        String [] allFiles = path.list();                                                    // All files contained in the directory.
        for (String file : allFiles) if (file.endsWith(".vm")) fileList.addElement(file); // Only .vm files get added to fileList.
    }

    private void initializeDirW(File path) {
        String pathFile = path.getAbsolutePath() + File.separator + path.getName() + ".asm";
        try {
            w = new PrintWriter(new FileWriter(new File(pathFile)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeBootstrap() {
        w.println("@256"); // SP=256
        w.println("D=A");
        w.println("@SP");
        w.println("M=D");////////////////////////Non fovrebbe esserci un return address e nuovi ARG/LCL

        w.println("@SP"); // Leave space for return address.
        w.println("M=M+1");

        w.println("@LCL"); // push LCL
        simplePush();

        w.println("@ARG"); // push ARG
        simplePush();

        w.println("@THIS"); // push THIS
        simplePush();

        w.println("@THAT"); // push THAT
        simplePush();

        w.println("@Sys.init");
        w.println("0;JEQ");

        w.println("(Sys.init$returnAddress)"); // Writes label name
    }

    private void translateDirectory() {
        for(int i = 0; i< fileList.size(); i++) { // Cycles all files.
            currentFileName = fileList.elementAt(i).replaceAll(".vm", "");             // Retrieves the name (no extension) of the .vm file currently being translated.
            initializeDirR(directory.getAbsolutePath() + File.separator + currentFileName + ".vm"); // Creates BufferedReader r to read the .vm file currently being translated.
            loadFile();                                                                                  // Loads .vm file in program. Also removes spaces and comments.
            translateFile();                                                                             // Actually does the translating.
        }
    }

    private void initializeDirR(String file) {
        try {
            r = new BufferedReader(new FileReader(new File(file)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFile() {
        program = new Vector<>(); // Re-initializes each time the program vector.
        try {
            while(true) {
                line = r.readLine();
                if (line == null) break;               // File ended.
                clean();                               // Removes comments from each line.
                if(!line.isEmpty()) program.add(line); // Adds line to program, unless it is an empty line.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clean() {
        line = removeComments(line); // Removes comments.
        line = line.trim();
    }

    private String removeComments(String line) {
        if(line.contains("//")) line = line.substring(0, line.indexOf("//")); // In case there is a commment i removes it.
        return line;
    }

    private void translateFile() {
        for(int i=0; i<program.size(); i++) { // Cycles all lines in program and translate them.
            line = program.elementAt(i);
            translateLine();
            lineNumber++; // Used in order to create unique labels in A-commands that require labels.
        }
    }

    private void translateLine() {
        w.println("//" + line); // Useful to inspect the output to see what VM command produced what translation.
        switch(parse.commandType(line)) {
            case "A": writeA(); break; // A is ARITHMETIC/LOGIC command
            case "B": writeB(); break; // B is MEMORY SEGMENT command
            case "C": writeC(); break; // C is BRANCHING command
            case "D": writeD(); break; // D is FUNCTION command
            default: System.out.println("ERROR"); break;
        }
    }

    private void writeA() { // ARITHMETIC/LOGIC command.
        switch(line) { // 'line' content coincides with the command.
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
      w.println("@SP");   // Stack: |...|x|y|SP|
      w.println("M=M-1"); // Decrement SP   ->   |...|x|ySP|
      w.println("A=M");   // Select address of last element of the stack (y) (SP which has just been decremented).
      w.println("D=M");   // Memorize its content in D (D=y).
      w.println("A=A-1"); // Select address of second-to-last element of the stack (x).
    }

    private void writeApost() {
      w.println("@SP");                        // In case it doesn't jump, it has to w.println false.
      w.println("A=M-1");                      // Last element of stack.
      w.println("M=0");                        // Sets it to FALSE.
      w.println("@END_" + lineNumber);         // Points to the end.
      w.println("0;JMP");                      // Goes to the end.
      w.println("(LABEL_" + lineNumber + ")"); // In case it jumps.
      w.println("@SP");
      w.println("A=M-1");                      // Last element of stack.
      w.println("M=-1");                       // Sets it to TRUE.
      w.println("(END_" + lineNumber + ")");
    }

    private void writeAadd() {
      writeAprimer();
      w.println("M=D+M"); // Sum its content to D (which contains the other factor y) and replaces it (x = x+y).
    }

    private void writeAsub() {
      writeAprimer();
      w.println("M=M-D"); // Subtracts D (which contains the other factor y) to x and replaces it (x = x-y).
    }

    private void writeAneg() {
      w.println("@SP");   // *(SP-1)   ->   -(SP-1)
      w.println("A=M-1"); // Select address of last element of the stack (y) (SP decremented).
      w.println("M=-M");  // Substitute its content with the opposite of it (y=-y).
    }

    private void writeAeq() {
      writeAprimer();
      w.println("D=M-D");                // Subtracts D (which contains the other factor y) to x and puts it in D (D = x-y).
      w.println("@LABEL_" + lineNumber); // Points LABEL.
      w.println("D;JEQ");                // if x==y, then x-y==0 and the program jumps to (LABEL).
      writeApost();
    }

    private void writeAgt() {
      writeAprimer();
      w.println("D=M-D");                // Subtracts D (which contains the other factor y) to x and puts it in D (D = x-y).
      w.println("@LABEL_" + lineNumber); // Points LABEL.
      w.println("D;JGT");                // if x>y, then x-y>0 and the program jumps to (LABEL).
      writeApost();
    }

    private void writeAlt() {
      writeAprimer();
      w.println("D=M-D");                // Subtracts D (which contains the other factor y) to x and puts it in D (D = x-y).
      w.println("@LABEL_" + lineNumber); // Points LABEL.
      w.println("D;JLT");                // if x==y, then x-y==0 and the program jumps to (LABEL).
      writeApost();
    }

    private void writeAand() {
      writeAprimer();
      w.println("M=D&M"); // ANDs D (which contains the other factor y) and x and puts it in x (x = x&y).
    }

    private void writeAor() {
      writeAprimer();
      w.println("M=D|M"); // ORs D (which contains the other factor y) and x and puts it in x (x = x|y).
    }

    private void writeAnot() {
      w.println("@SP");
      w.println("A=M-1"); // Select address of last element of the stack (y) (SP decremented).
      w.println("M=!M");  // Negates the content and substitutes it.
    }

    private void writeB() { // MEMORY SEGMENT command.
      switch(parse.arg1(line)) { // Whether is a PUSH or POP.
        case "push": writeBpush(); break;
        case "pop": writeBpop(); break;
        default: break;
      }
    }

    private void writeBpush() {
      String arg2 = parse.arg2(line);
      switch(arg2) { // Whether is local, argument, this, that, constant, static, pointer, temp.
        case "local": w.println("@LCL"); break;
        case "argument": w.println("@ARG"); break;
        case "this": w.println("@THIS"); break;
        case "that": w.println("@THAT"); break;
        case "constant": writeBpushConstant(); return;
        case "static": writeBpushStatic(); return;
        case "pointer": writeBpushPointer(); return;
        case "temp": writeBpushTemp(); return;
        default: break;
      }
      writeBfinal();
    }

    private void writeBpushConstant() {
        w.println("@" + parse.arg3(line));
        w.println("D=A");
        simplerPush();
    }

    private void writeBpushStatic() {
        w.println("@" + currentFileName + "." + parse.arg3(line));
        simplePush();
    }

    private void simplePush() {
        w.println("D=M");
        simplerPush();
    }

    private void simplerPush() {
        w.println("@SP");
        w.println("M=M+1");
        w.println("A=M-1");
        w.println("M=D");
    }

    private void writeBpushPointer() {
      switch (parse.arg3(line)) {
        case "0": w.println("@3"); break;
        case "1": w.println("@4"); break;
        default: break;
      }
      simplePush();
    }

    private void writeBpushTemp() {
        w.println("@5");
        w.println("D=A");
        writeBend();
    }

    private void writeBfinal() {
        w.println("D=M");
        writeBend();
    }

    private void writeBend() {
        w.println("@" + parse.arg3(line));
        w.println("D=D+A"); // D = LOCAL + offset
        w.println("A=D"); // Point to LOCAL + offset
        simplePush();
    }

    private void writeBpop() {
      switch(parse.arg2(line)) { // Whether is local, argument, this, that, constant, static, pointer, temp.
        case "local": w.println("@LCL");  break;
        case "argument": w.println("@ARG"); break;
        case "this": w.println("@THIS"); break;
        case "that": w.println("@THAT"); break;
        case "static": writeBpopStatic(); return;
        case "pointer": writeBpopPointer(); return;
        case "temp": writeBpopTemp(); return;
        default: break;
      }
      writeBpopFinal();
    }

    private void writeBpopStatic() {
        w.println("@" + currentFileName + "." + parse.arg3(line));
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

    private void writeBpopTemp() {
        w.println("@5");
        w.println("D=A");
        writeBpopEnd();
    }

    private void writeBpopFinalPrimer() {
      w.println("@" + parse.arg3(line));
      w.println("D=D+A"); // Points the address to which the value will be popped.
      w.println("@R13");
      w.println("M=D");
    }

    private void writeBpopFinal() {
        w.println("D=M");
        writeBpopEnd();
    }

    private void writeBpopEnd() {
        writeBpopFinalPrimer(); // Puts address to pop to into R13.
        w.println("@SP");
        w.println("M=M-1");
        w.println("A=M");
        w.println("D=M");
        w.println("@R13");
        w.println("A=M");
        w.println("M=D");
    }

    private void writeC() {
        System.out.println("Command type: C");
        switch(parse.arg1(line)) { // Whether is a LABEL, IF-GOTO, GOTO.
            case "label": writeClabel(); break;
            case "if-goto": writeCifgoto(); break;
            case "goto": writeCgoto(); break;
            default: break;
        }
    }

    private void writeClabel() {
        w.println("(" + parse.arg2(line) + ")"); // Writes label name.
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

    private void writeD() { // Whether is a LABEL, IF-GOTO, GOTO.
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

        w.println("(" +  parse.arg2(line) + "_call." + nestedCallNumber + "_" + "$returnAddress" + ")"); // Writes label name.
        nestedCallNumber++;
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
        w.println("(" + parse.arg2(line) + ")");
        //String originalLine = line;
        int args = Integer.parseInt(parse.arg3(line));
        for(int i=0; i<args; i++) {
            line = "push constant 0";
            translateLine();
        }
        //line = originalLine;
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
