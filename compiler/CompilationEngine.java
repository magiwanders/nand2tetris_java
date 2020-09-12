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

    String simpleLine; // Non-parsed xml token.

    int index=-1;

    public CompilationEngine(String jackDirectory) {
        listOfXMLFiles = Util.getFiles(jackDirectory, ".xml");
        for(String xmlFile : listOfXMLFiles) {
            resetFileData(xmlFile);

            programLines = Util.loadFile(xmlFile);

            compile();
        }
        Log.console("Done compiling XML.");
        w.close();
    }

    private void resetFileData(String xmlFile) {
        index = -1;
        currentVMFile = new String();
        programLines = new Vector<>();
        currentVMFile = xmlFile.replaceAll(".xml", ".vm");
        initializeIO();
    }

    private void initializeIO() {
        try {
            w = new PrintWriter(new BufferedWriter(new FileWriter(new File(currentVMFile))));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void compile() {
        switch(peekNext()) {
            case "class" : compileClass(); break;
            case "function" : compileSubroutine(); break;
            case "constructor" : compileSubroutine(); break;
            case "method" : compileSubroutine(); break;
            case "static" : compileClassVarDec(); break;
            case "field" : compileClassVarDec(); break;
        }
    }

    private void compileClass() {
        write("<class>");
        index++; // "class"
        index++; // className
        index++; // {
        compile();
        index++; // }
        write("</class>");
    }

    private void compileClassVarDec() {
        write("<classVarDec>");
        index++; // "static"/"field"
        index++; // type
        index++; // varName
        while(peekNext().equals(",")) {
            index++; // ,
            index++; // Var name
        }
        index++; // ;
        write("</classVarDec>");
        compile();
    }

    private void compileSubroutine() {
        write("<subroutineDec>");
        index++; // "function"/"construtor"/"method"
        index++; // type/"void"
        index++; // subroutineName
        index++; // (
        compileParameterList();
        index++; // )
        write("<subroutineBody>");
        index++; // {
        while(peekNext().equals("var")) compileVarDec();
        compileStatements();
        index++; // }
        write("</subroutineBody>");
        write("</subroutineDec>");
        compile();
    }

    private void compileParameterList() {
        write("<parameterList>");
        while(!peekNext().equals(")")) {
            index++; // Parameter type
            index++; // Parameter name
            if(!peekFurther().equals(")")) index++; // ,
        }
        write("</parameterList>");
    }

    private void compileVarDec() {
        write("<varDec>");
        index++; // "var"
        index++; // varType
        index++; // varName
        while(peekNext().equals(",")) {
            index++; // ,
            index++; // varName
        }
        index++; // ;
        write("</varDec>");
    }

    private void compileStatements() {
        write("<statements>");
        while(!peekNext().equals("}")) {
            switch (peekNext()) {
                case "let" : compileLet(); break;
                case "if" : compileIf(); break;
                case "while" : compileWhile(); break;
                case "do" : compileDo(); break;
                case "return" : compileReturn(); break;
            }
        }
        write("</statements>");
    }

    private void compileLet() {
        write("<letStatement>");
        index++; // "let"
        index++; // varName
        if(peekNext().equals("[")) {
            index++; // [
            compileExpression();
            index++; // ]
        }
        index++; // =
        compileExpression();
        index++; // ;
        write("</letStatement>");
    }

    private void compileIf() {
        write("<ifStatement>");
        index++; // "if"
        index++; // (
        compileExpression();
        index++; // )
        index++; // {
        compileStatements();
        index++; // }
        if (peekNext().equals("else")) {
            index++; // else
            index++; // {
            compileStatements();
            index++; // }
        }
        write("</ifStatement>");
    }

    private void compileWhile() {
        write("<whileStatement>");
        index++; // "while"
        index++; // (
        compileExpression();
        index++; // )
        index++; // {
        compileStatements();
        index++; // }
        write("</whileStatement>");
    }

    private void compileReturn() {
        write("<returnStatement>");
        index++; // "return"
        if(!peekNext().equals(";")) {
            compileExpression();
        }
        index++; // ;
        write("</returnStatement>");
    }

    private void compileDo() {
        write("<doStatement>");
        index++; // "do"
        compileSubroutineCall();
        index++; // ;
        write("</doStatement>");
    }

    private void compileSubroutineCall() {
        index++; // Subroutine name OR Class name OR Var name
        if(peekNext().equals(".")) { // Means last piece was NOT a subroutine of the same class
            index++; // .
            index++; // subroutineName
            index++; // (
            compileExpressionList();
            index++; // )
            return;
        }
        index++; // (
        compileExpressionList();
        index++; // )
    }

    private void compileExpressionList() {
        write("<expressionList>");
        while (!peekNext().equals(")")) {
            compileExpression();
            if(!peekNext().equals(")")) index++; // ,
        }
        write("</expressionList>");
    }

    private void compileExpression() {
        write("<expression>");
        compileTerm(); // index++; // Dummy term
        if (isOp(peekNext())) {
            index++; // Op
            compileTerm();  // Dummy term
        }
        write("</expression>");
    }

    private void compileTerm() {
        write("<term>");
        if (peekNextType().equals("integerConstant")) {
            index++; // Integer
        }
        else if (peekNextType().equals("stringConstant")) index++; // String
        else if (isKeywordConstant()) {
            index++; // KeyboardConstant
        }
        else if (peekNext().length()==1 && (peekNext().charAt(0)=='~' || peekNext().charAt(0)=='-')) {
            index++; // UnaryOp
            compileTerm();
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
            index++; // varName
        }
        write("</term>");
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


    // Writing XML file methods

    private void write(String toWrite) {
        w.println(toWrite);
        w.flush();
    }

    private String [] peekNextComplete() { // Returns type and name of token being analyzed. Does NOT affect index.
        index++;
        if (index==programLines.size()) {
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
