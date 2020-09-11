package compiler;

import utilities.Log;
import utilities.Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;

public class XMLEngine {

    Vector<String> listOfTXMLFiles;
    String currentXMLFile;
    PrintWriter w;
    Vector<String> programLines;

    String token;
    String simpleLine; // Non-parsed xml token.

    int index=-1;

    public XMLEngine(String jackDirectory) {
        listOfTXMLFiles = Util.getFiles(jackDirectory, "T.xml");
        for(String txmlFile : listOfTXMLFiles) {
            resetFileData(txmlFile);

            programLines = Util.loadFile(txmlFile);
            programLines.removeElementAt(0); // <tokens>
            programLines.removeElementAt(programLines.size()-1); // </tokens>

            compile();
        }
        Log.console("Done compiling XML.");
        w.close();
    }

    private void resetFileData(String txmlFile) {
        index = -1;
        currentXMLFile = new String();
        programLines = new Vector<>();
        currentXMLFile = txmlFile.replaceAll("T.xml", ".xml");
        initializeIO();
    }

    private void initializeIO() {
        try {
            w = new PrintWriter(new BufferedWriter(new FileWriter(new File(currentXMLFile))));
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
        writeAndAdvance(); // "class"
        writeAndAdvance(); // className
        writeAndAdvance(); // {
        compile();
        writeAndAdvance(); // }
        write("</class>");
    }

    private void compileClassVarDec() {
        write("<classVarDec>");
        writeAndAdvance(); // "static"/"field"
        writeAndAdvance(); // type
        writeAndAdvance(); // varName
        while(peekNext().equals(",")) {
            writeAndAdvance(); // ,
            writeAndAdvance(); // Var name
        }
        writeAndAdvance(); // ;
        write("</classVarDec>");
        compile();
    }

    private void compileSubroutine() {
        write("<subroutineDec>");
        writeAndAdvance(); // "function"/"construtor"/"method"
        writeAndAdvance(); // type/"void"
        writeAndAdvance(); // subroutineName
        writeAndAdvance(); // (
        compileParameterList();
        writeAndAdvance(); // )
        write("<subroutineBody>");
        writeAndAdvance(); // {
        while(peekNext().equals("var")) compileVarDec();
        compileStatements();
        writeAndAdvance(); // }
        write("</subroutineBody>");
        write("</subroutineDec>");
        compile();
    }

    private void compileParameterList() {
        write("<parameterList>");
        while(!peekNext().equals(")")) {
            writeAndAdvance(); // Parameter type
            writeAndAdvance(); // Parameter name
            if(!peekFurther().equals(")")) writeAndAdvance(); // ,
        }
        write("</parameterList>");
    }

    private void compileVarDec() {
        write("<varDec>");
        writeAndAdvance(); // "var"
        writeAndAdvance(); // varType
        writeAndAdvance(); // varName
        while(peekNext().equals(",")) {
            writeAndAdvance(); // ,
            writeAndAdvance(); // varName
        }
        writeAndAdvance(); // ;
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
        writeAndAdvance(); // "let"
        writeAndAdvance(); // varName
        if(peekNext().equals("[")) {
            writeAndAdvance(); // [
            compileExpression();
            writeAndAdvance(); // ]
        }
        writeAndAdvance(); // =
        compileExpression();
        writeAndAdvance(); // ;
        write("</letStatement>");
    }

    private void compileIf() {
        write("<ifStatement>");
        writeAndAdvance(); // "if"
        writeAndAdvance(); // (
        compileExpression();
        writeAndAdvance(); // )
        writeAndAdvance(); // {
        compileStatements();
        writeAndAdvance(); // }
        if (peekNext().equals("else")) {
            writeAndAdvance(); // else
            writeAndAdvance(); // {
            compileStatements();
            writeAndAdvance(); // }
        }
        write("</ifStatement>");
    }

    private void compileWhile() {
        write("<whileStatement>");
        writeAndAdvance(); // "while"
        writeAndAdvance(); // (
        compileExpression();
        writeAndAdvance(); // )
        writeAndAdvance(); // {
        compileStatements();
        writeAndAdvance(); // }
        write("</whileStatement>");
    }

    private void compileReturn() {
        write("<returnStatement>");
        writeAndAdvance(); // "return"
        if(!peekNext().equals(";")) {
            compileExpression();
        }
        writeAndAdvance(); // ;
        write("</returnStatement>");
    }

    private void compileDo() {
        write("<doStatement>");
        writeAndAdvance(); // "do"
        compileSubroutineCall();
        writeAndAdvance(); // ;
        write("</doStatement>");
    }

    private void compileSubroutineCall() {
        writeAndAdvance(); // Subroutine name OR Class name OR Var name
        if(peekNext().equals(".")) { // Means last piece was NOT a subroutine of the same class
            writeAndAdvance(); // .
            writeAndAdvance(); // subroutineName
            writeAndAdvance(); // (
            compileExpressionList();
            writeAndAdvance(); // )
            return;
        }
        writeAndAdvance(); // (
        compileExpressionList();
        writeAndAdvance(); // )
    }

    private void compileExpressionList() {
        write("<expressionList>");
        while (!peekNext().equals(")")) {
            compileExpression();
            if(!peekNext().equals(")")) writeAndAdvance(); // ,
        }
        write("</expressionList>");
    }

    private void compileExpression() {
        write("<expression>");
        compileTerm(); // writeAndAdvance(); // Dummy term
        if (isOp(peekNext())) {
            writeAndAdvance(); // Op
            compileTerm();  // Dummy term
        }
        write("</expression>");
    }

    private void compileTerm() {
        write("<term>");
        if (peekNextType().equals("integerConstant")) {
            writeAndAdvance(); // Integer
        }
        else if (peekNextType().equals("stringConstant")) writeAndAdvance(); // String
        else if (isKeywordConstant()) {
            writeAndAdvance(); // KeyboardConstant
        }
        else if (peekNext().length()==1 && (peekNext().charAt(0)=='~' || peekNext().charAt(0)=='-')) {
            writeAndAdvance(); // UnaryOp
            compileTerm();
        }
        else if (peekNext().length()==1 && peekNext().charAt(0)=='(') {
            writeAndAdvance(); // (
            compileExpression();
            writeAndAdvance(); // )
        } else if (isLetter(peekNext().charAt(0)) && peekFurther().equals("[")) {
            writeAndAdvance(); // Var name
            writeAndAdvance(); // [
            compileExpression();
            writeAndAdvance(); // ]
        } else if (isLetter(peekNext().charAt(0)) && (peekFurther().equals("(") || peekFurther().equals("."))) {
            compileSubroutineCall();
        } else if (!(isLetter(peekNext().charAt(0)) && (peekFurther().equals("(") || peekFurther().equals(".") || peekFurther().equals("[")))) {
            writeAndAdvance(); // varName
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

    private void writeAndAdvance() {
        index++;
        write(programLines.elementAt(index));
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
