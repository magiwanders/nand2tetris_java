package compiler;

import utilities.Log;
import utilities.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;

public class CompilationEngine {

    Vector<String> listOfXMLFiles;
    String currentVMFile;
    PrintWriter w;
    Vector<String> programLines;

    SymbolTable classTable = new SymbolTable("CLASS");
    SymbolTable subroutineTable = new SymbolTable("SUBROUTINE");

    String simpleLine; // Non-parsed xml token.
    String simpleFileName; // Extensionless VM file name.

    int index=-1;
    String className;

    // Counters
    int alwaysIncrementingConstant=0;

    public CompilationEngine(String jackDirectory) {
        listOfXMLFiles = Util.getFiles(jackDirectory, "T.xml");
        for(String xmlFile : listOfXMLFiles) {
            resetFileData(xmlFile);

            programLines = Util.loadFile(xmlFile);
            programLines.removeElementAt(0); // <tokens>
            programLines.removeElementAt(programLines.size()-1); // </tokens>
            simpleFileName = Util.getSimpleFileName(currentVMFile);

            compileClass();
        }
        w.close();
    }

    private void resetFileData(String xmlFile) {
        index = -1;
        classTable.clear();
        subroutineTable.clear();
        currentVMFile = new String();
        programLines = new Vector<>();
        currentVMFile = xmlFile.replaceAll("T.xml", ".vm");
        initializeIO();
    }

    private void initializeIO() {
        try {
            w = new PrintWriter(new BufferedWriter(new FileWriter(new File(currentVMFile))));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void compileClass() {
        index++; // "class"
        className = peekNext(); index++; // className
        index++; // {
        while(peekNext().equals("field")||peekNext().equals("static")) {
            compileClassVarDec();
        }
        while (peekNext().equals("constructor")||peekNext().equals("function")||peekNext().equals("method")) {
            compileSubroutine();
        }
        index++; // }
    }

    private void compileClassVarDec() {
        String kind = peekNext(); index++; // "static"/"field"
        String type = peekNext(); index++; // type
        String name = peekNext(); index++; // varName
        classTable.define(name, type, kind);
        while(peekNext().equals(",")) {
            index++; // ,
            name = peekNext(); index++; // Var name
            classTable.define(name, type, kind);
        }
        index++; // ;
    }

    private void compileSubroutine() {
        // All the class variables are been declared, now classVariablesCounter contains exactly their number.
        subroutineTable.clear();
        String subroutine = peekNext(); index++; // "function"/"construtor"/"method"
        peekNext(); index++; // type/"void"
        String subroutineName = peekNext(); index++; // subroutineName
        index++; // (
        if(subroutine.equals("method")) subroutineTable.define("this", className, "argument");
        compileParameterList();
        index++; // )
        index++; // {
        while(peekNext().equals("var")) {
            compileVarDec();
        }
        int nVars = subroutineTable.varCount("local");
        write("function " + simpleFileName + "." + subroutineName + " " + nVars);
        if(subroutine.equals("constructor")) {
            write("push constant " + classTable.numberOfFIELD);
            write("call Memory.alloc 1"); // Allocates memory for the
            write("pop pointer 0");       // Sets THIS
        } else if (subroutine.equals("method")) {
            write("push argument 0");
            write("pop pointer 0");
        }
        compileStatements();
        index++; // }
    }

    private void compileParameterList() {
        if (peekNext().equals(")")) return;
        else {
            String type = peekNext(); index++; // Parameter type
            String name = peekNext(); index++; // Parameter name
            subroutineTable.define(name, type, "argument");
            while (peekNext().equals(",")) {
                index++; // ,
                type = peekNext(); index++; // Parameter type
                name = peekNext(); index++; // Parameter name
                subroutineTable.define(name, type, "argument");
            }
        }
    }

    private void compileVarDec() {
        index++; // "var"
        String type = peekNext(); index++; // varType
        String name = peekNext(); index++; // varName
        subroutineTable.define(name, type, "local");
        while(peekNext().equals(",")) {
            index++; // ,
            name = peekNext(); index++; // varName
            subroutineTable.define(name, type, "local");
        }
        index++; // ;
    }

    private void compileStatements() {
        while(!peekNext().equals("}")) {
            switch (peekNext()) {
                case "let" : compileLet(); break;
                case "if" : compileIf(); break;
                case "while" : compileWhile(); break;
                case "do" : compileDo(); break;
                case "return" : compileReturn(); break;
            }
        }
    }

    private void compileLet() {
        index++; // "let"
        String name = peekNext(); index++; // varName
        if(peekNext().equals("[")) {
            if (subroutineTable.contains(name)) {
                String kind = subroutineTable.kindOf(name);
                int i = subroutineTable.indexOf(name);
                write("push " + kind + " " + i);
            } else {
                if (classTable.contains(name)) {
                    String kind = classTable.kindOf(name);
                    int i = classTable.indexOf(name);
                    write("push " + kind + " " + i);
                }
            }
            index++; // [
            compileExpression();
            index++; // ]
            write("add");
            index++; // =
            compileExpression();
            index++; // ;
            write("pop temp 0");
            write("pop pointer 1");
            write("push temp 0");
            write("pop that 0");
            return;
        }
        index++; // =
        compileExpression();
        index++; // ;

        if (subroutineTable.contains(name)) {
            String kind = subroutineTable.kindOf(name);
            int i = subroutineTable.indexOf(name);
            write("pop " + kind + " " + i);
        } else {
            if (classTable.contains(name)) {
                String kind = classTable.kindOf(name);
                int i = classTable.indexOf(name);
                write("pop " + kind + " " + i);
            }
        }
    }

    private void compileIf() {
        String L1 = "IF_FALSE_" + alwaysIncrementingConstant;
        String L2 = "IF_END_" + alwaysIncrementingConstant;
        alwaysIncrementingConstant++;
        index++; // "if"
        index++; // (
        compileExpression();
        write("not");
        write("if-goto " + L1);
        index++; // )
        index++; // {
        compileStatements();
        index++; // }
        write("goto " + L2);
        write("label " + L1);
        if (peekNext().equals("else")) {
            index++; // else
            index++; // {
            compileStatements();
            index++; // }
        }
        write("label " + L2);
    }

    private void compileWhile() {
        String L1 = "WHILE_EXP_" + alwaysIncrementingConstant;
        String L2 = "WHILE_END_" + alwaysIncrementingConstant;
        alwaysIncrementingConstant++;
        index++; // "while"
        write("label " + L1);
        index++; // (
        compileExpression();
        write("not");
        write("if-goto "+ L2);
        index++; // )
        index++; // {
        compileStatements();
        index++; // }
        write("goto " + L1);
        write("label " + L2);
    }

    private void compileReturn() {
        index++; // "return"
        if(!peekNext().equals(";")) {
            compileExpression();
            write("return");
            index++; // ;
            return;
        }
        write("push constant 0");
        write("return");
        index++; // ;
    }

    private void compileDo() {
        index++; // "do"
        compileSubroutineCall();
        index++; // ;
        write("pop temp 0");
    }

    private void compileSubroutineCall() {
        String firstName = peekNext(); index++; // Subroutine name OR Class name OR Var name
        if(peekNext().equals(".")) { // Means last piece was NOT a subroutine of the same class
            index++; // .
            String secondName = peekNext(); index++; // subroutineName
            if (!classTable.contains(firstName) && !subroutineTable.contains(firstName)) { // Calling on a class name (constructor of other class or method of current class)
                index++; // (
                int nArgs = compileExpressionList();
                index++; // )
                write("call "+ firstName + "." + secondName + " " + nArgs);
            } else if (classTable.contains(firstName)) {
                String kind = classTable.kindOf(firstName);
                int i = classTable.indexOf(firstName);
                write("push " + kind + " " + i);
                index++; // (
                int nArgs = compileExpressionList(); nArgs++;
                index++; // )
                write("call "+ classTable.typeOf(firstName) + "." + secondName + " " + nArgs);
            } else if (subroutineTable.contains(firstName)) {
                String kind = subroutineTable.kindOf(firstName);
                int i = subroutineTable.indexOf(firstName);
                write("push " + kind + " " + i);
                index++; // (
                int nArgs = compileExpressionList(); nArgs++;
                index++; // )
                write("call "+ subroutineTable.typeOf(firstName) + "." + secondName + " " + nArgs);
            }
            return;
        }
        write("push pointer 0");
        index++; // (
        int nArgs = compileExpressionList();
        index++; // )
        nArgs++;
        write("call "+ simpleFileName + "." + firstName + " " + nArgs);
    }

    private int compileExpressionList() {
        int nArgs = 0;
        while (!peekNext().equals(")")) {
            compileExpression();
            nArgs++;
            if(!peekNext().equals(")")) index++; // ,
        }
        return nArgs;
    }

    private void compileExpression() {
        compileTerm(); // index++; // Dummy term
        if (isOp(peekNext())) {
            String op = peekNext(); index++; // Op
            compileTerm();  // Dummy term
            write(opToVM(op));
        }
    }

    private void compileTerm() {
        if (peekNextType().equals("integerConstant")) {
            int currentInt = Integer.parseInt(peekNext());
            write("push constant " + currentInt);
            index++; // Integer
        } else if (peekNextType().equals("stringConstant")) {
            String string = peekNext(); index++; // String
            write("push constant " + string.length());
            write("call String.new 1");
            for(char c : string.toCharArray()) {
                write("push constant " + charToNumber(c));
                write("call String.appendChar 2");
            }
        } else if (isKeywordConstant()) {
            write(keywordConstantValue()); index++; // KeyboardConstant
        } else if (peekNext().length()==1 && (peekNext().charAt(0)=='~' || peekNext().charAt(0)=='-')) {
            String op = peekNext(); index++; // UnaryOp
            compileTerm();
            write(unaryOpToVM(op));
        } else if (peekNext().length()==1 && peekNext().charAt(0)=='(') {
            index++; // (
            compileExpression();
            index++; // )
        } else if (isLetter(peekNext().charAt(0)) && peekFurther().equals("[")) {
            String name = peekNext(); index++; // Var name
            if (subroutineTable.contains(name)) {
                String kind = subroutineTable.kindOf(name);
                int i = subroutineTable.indexOf(name);
                write("push " + kind + " " + i);
            } else {
                if (classTable.contains(name)) {
                    String kind = classTable.kindOf(name);
                    int i = classTable.indexOf(name);
                    write("push " + kind + " " + i);
                }
            }
            index++; // [
            compileExpression();
            index++; // ]
            write("add");
            write("pop pointer 1");
            write("push that 0");
        } else if (isLetter(peekNext().charAt(0)) && (peekFurther().equals("(") || peekFurther().equals("."))) {
            compileSubroutineCall();
        } else if (!(isLetter(peekNext().charAt(0)) && (peekFurther().equals("(") || peekFurther().equals(".") || peekFurther().equals("[")))) {
            String name = peekNext(); index++; // varName
            if (subroutineTable.contains(name)) {
                String kind = subroutineTable.kindOf(name);
                int i = subroutineTable.indexOf(name);
                write("push " + kind + " " + i);
            } else {
                if (classTable.contains(name)) {
                    String kind = classTable.kindOf(name);
                    int i = classTable.indexOf(name);
                    write("push " + kind + " " + i);
                }
            }
        }
    }

    private String keywordConstantValue() {
        switch (peekNext()) {
            case "true": return "push constant 1\nneg";
            case "false": return "push constant 0";
            case "null": return "push constant 0";
            case "this": return "push pointer 0";
        }
        return "";
    }


    private String opToVM(String op) {
        switch (op) {
            case "+": return "add";
            case "-": return "sub";
            case "*": return "call Math.multiply 2";
            case "/": return "call Math.divide 2";
            case "&amp;": return "and";
            case "|": return "or";
            case "&lt;": return "lt";
            case "&gt;": return "gt";
            case "=": return "eq";
        }
        return "";
    }

    private String unaryOpToVM(String op) {
        switch (op) {
            case "~": return "not";
            case "-": return "neg";
        }
        return "";
    }

    private int charToNumber(char c) {
        if(c=='a') return 97; if(c=='A') return 65;
        if(c=='b') return 98; if(c=='B') return 66;
        if(c=='c') return 99; if(c=='C') return 67;
        if(c=='d') return 100; if(c=='D') return 68;
        if(c=='e') return 101; if(c=='E') return 69;
        if(c=='f') return 102; if(c=='F') return 70;
        if(c=='g') return 103; if(c=='G') return 71;
        if(c=='h') return 104; if(c=='H') return 72;
        if(c=='i') return 105; if(c=='I') return 73;
        if(c=='j') return 106; if(c=='J') return 74;
        if(c=='k') return 107; if(c=='K') return 75;
        if(c=='l') return 108; if(c=='L') return 76;
        if(c=='m') return 109; if(c=='M') return 77;
        if(c=='n') return 110; if(c=='N') return 78;
        if(c=='o') return 111; if(c=='O') return 79;
        if(c=='p') return 112; if(c=='P') return 80;
        if(c=='q') return 113; if(c=='Q') return 81;
        if(c=='r') return 114; if(c=='R') return 82;
        if(c=='s') return 115; if(c=='S') return 83;
        if(c=='t') return 116; if(c=='T') return 84;
        if(c=='u') return 117; if(c=='U') return 85;
        if(c=='v') return 118; if(c=='V') return 86;
        if(c=='w') return 119; if(c=='W') return 87;
        if(c=='x') return 120; if(c=='X') return 88;
        if(c=='y') return 121; if(c=='Y') return 89;
        if(c=='z') return 122; if(c=='Z') return 90;

        if(c=='0') return 48; if(c==':') return 58;
        if(c=='1') return 49; if(c==';') return 59;
        if(c=='2') return 50; if(c=='<') return 60;
        if(c=='3') return 51; if(c=='=') return 61;
        if(c=='4') return 52; if(c=='>') return 62;
        if(c=='5') return 53; if(c=='?') return 63;
        if(c=='6') return 54; if(c=='@') return 64;
        if(c=='7') return 55; if(c=='[') return 91;
        if(c=='8') return 56; if(c=='\\') return 92;
        if(c=='9') return 57; if(c==']') return 93;

        if(c=='^') return 94; if(c=='_') return 95;
        if(c==' ') return 32; if(c=='"') return 90;
        if(c=='#') return 34; if(c=='$') return 36;
        if(c=='%') return 37; if(c=='&') return 38;
        if(c=='\'') return 39; if(c=='(') return 40;
        if(c==')') return 41; if(c=='*') return 42;
        if(c=='+') return 43; if(c==',') return 44;
        if(c=='.') return 46; if(c=='/') return 47;
        if(c=='!') return 35;

        if(c=='\n') return 128;
        return -1;
    }

    private boolean isLetter(char candidate) {
        return candidate == 'a' || candidate == 'A' ||
                candidate == 'b' || candidate == 'B' ||
                candidate == 'c' || candidate == 'C' ||
                candidate == 'd' || candidate == 'D' ||
                candidate == 'e' || candidate == 'E' ||
                candidate == 'f' || candidate == 'F' ||
                candidate == 'g' || candidate == 'G' ||
                candidate == 'h' || candidate == 'H' ||
                candidate == 'i' || candidate == 'I' ||
                candidate == 'j' || candidate == 'J' ||
                candidate == 'k' || candidate == 'K' ||
                candidate == 'l' || candidate == 'L' ||
                candidate == 'm' || candidate == 'M' ||
                candidate == 'n' || candidate == 'N' ||
                candidate == 'o' || candidate == 'O' ||
                candidate == 'p' || candidate == 'P' ||
                candidate == 'q' || candidate == 'Q' ||
                candidate == 'r' || candidate == 'R' ||
                candidate == 's' || candidate == 'S' ||
                candidate == 't' || candidate == 'T' ||
                candidate == 'u' || candidate == 'U' ||
                candidate == 'v' || candidate == 'V' ||
                candidate == 'w' || candidate == 'W' ||
                candidate == 'x' || candidate == 'X' ||
                candidate == 'y' || candidate == 'Y' ||
                candidate == 'z' || candidate == 'Z';
    }

    private boolean isKeywordConstant() {
        return peekNextType().equals("keyword") && (peekNext().equals("true") ||
                peekNext().equals("false") ||
                peekNext().equals("null") ||
                peekNext().equals("this"));
    }

    private boolean isOp(String line) {
        return line.equals("+") ||
                line.equals("-") ||
                line.equals("*") ||
                line.equals("/") ||
                line.equals("&amp;") ||
                line.equals("|") ||
                line.equals("&lt;") ||
                line.equals("&gt;") ||
                line.equals("=");
    }


    // Writing VM file methods

    private void write(String toWrite) {
        w.println(toWrite);
        w.flush();
    }

    private String [] peekNextComplete() { // Returns type and name of token being analyzed. Does NOT affect index.
        index++;
        if (index>=programLines.size()) {
            String [] failLine = {"", ""};
            return failLine;
        }
        simpleLine = programLines.elementAt(index);
        String [] complexLine = {"type", "name"};
        int indexOfClosing = simpleLine.indexOf("</");
        if(indexOfClosing==-1) {
            complexLine[0] = simpleLine.substring(simpleLine.indexOf("<")+1, simpleLine.indexOf(">"));
            complexLine[1] = "";
            return complexLine;
        }
        int indexLimit = simpleLine.indexOf(">", indexOfClosing);
        complexLine[0] = simpleLine.substring(indexOfClosing+2, indexLimit);
        int indexOfFirstSpace = simpleLine.indexOf(" ");
        // int indexOfSecondSpace = simpleLine.indexOf(" ", indexOfFirstSpace+1);
        complexLine[1] = simpleLine.substring(indexOfFirstSpace+1, indexOfClosing-1);
        index--;
        return complexLine;
    }

    private String peekNext() {
        return peekNextComplete()[1];
    }

    private String peekNextType() {
        return peekNextComplete()[0];
    }

    private String peekFurther() {
        index++;
        String temporaryLine = peekNext();
        index--;
        return temporaryLine;
    }

}
