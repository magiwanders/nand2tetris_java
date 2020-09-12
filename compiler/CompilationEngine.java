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
        index++; // className
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
        if(subroutine.equals("constructor")) {
            write("push constant " + classTable.numberOfFIELD);
            write("call Memory.alloc 1"); // Allocates memory for the
            write("pop pointer 0");       // Sets THIS
        }
        peekNext(); index++; // type/"void"
        String subroutineName = peekNext(); index++; // subroutineName
        index++; // (
        compileParameterList();
        index++; // )
        index++; // {
        int nVars=0;
        while(peekNext().equals("var")) {
            nVars += compileVarDec();
        }
        if (subroutine.equals("method")) nVars++;
        write("function " + simpleFileName + "." + subroutineName + " " + nVars);
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

    private int compileVarDec() {
        int nVars=0;
        index++; // "var"
        String type = peekNext(); index++; // varType
        String name = peekNext(); index++; // varName
        nVars++;
        subroutineTable.define(name, type, "local");
        while(peekNext().equals(",")) {
            index++; // ,
            name = peekNext(); index++; // varName
            nVars++;
            subroutineTable.define(name, type, "local");
        }
        index++; // ;
        return nVars;
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
            index++; // [
            compileExpression();
            index++; // ]
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
            index++; // (
            int nArgs = compileExpressionList();
            index++; // )
            write("call "+ firstName + "." + secondName + " " + nArgs);
            return;
        }
        index++; // (
        int nArgs = compileExpressionList();
        index++; // )
        write("call "+ simpleFileName + "." + firstName + nArgs);
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
        }
        else if (peekNextType().equals("stringConstant")) index++; // String
        else if (isKeywordConstant()) {
            write(keywordConstantValue()); index++; // KeyboardConstant
        }
        else if (peekNext().length()==1 && (peekNext().charAt(0)=='~' || peekNext().charAt(0)=='-')) {
            String op = peekNext(); index++; // UnaryOp
            compileTerm();
            write(unaryOpToVM(op));
        }
        else if (peekNext().length()==1 && peekNext().charAt(0)=='(') {
            index++; // (
            compileExpression();
            index++; // )
        } else if (isLetter(peekNext().charAt(0)) && peekFurther().equals("[")) {
            index++; // Var name
            index++; // [
            compileExpression();
            index++; // ]
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
        int indexOfSecondSpace = simpleLine.indexOf(" ", indexOfFirstSpace+1);
        complexLine[1] = simpleLine.substring(indexOfFirstSpace+1, indexOfSecondSpace);
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
