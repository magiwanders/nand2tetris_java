package compiler;

import utilities.Log;
import utilities.Util;

import java.util.Vector;

public class CompilationEngine {

    Vector<String> listOfTXMLFiles;
    String currentFile;
    Vector<String> programLines;
    String [] line = {"type", "name"};
    String simpleLine;

    int index;

    public CompilationEngine(String jackDirectory) {
        listOfTXMLFiles = Util.getFiles(jackDirectory, "T.xml");
        for(String txmlFile : listOfTXMLFiles) {
            index=0;
            currentFile = new String();
            programLines = new Vector<>();
            currentFile = txmlFile.replaceAll("T.xml", ".xml");
            Log.console("Processing file: " + currentFile);
            programLines = Util.loadFile(txmlFile);
            programLines.removeElementAt(0); // <tokens>
            programLines.removeElementAt(programLines.size()-1); // </tokens>
            Log.console(programLines);
            compile();
        }
    }

    private void compile() {
        line = peekNext();
        switch(line[1]) {
            case "class" : compileClass(); break;
            case "function" : compileSubroutine(); break;
            case "constructor" : compileSubroutine(); break;
            case "method" : compileSubroutine(); break;
            case "static" : compileClassVarDec(); break;
            case "field" : compileClassVarDec(); break;
        }
    }

    private void compileClassVarDec() {
        Util.append(currentFile, "<classVarDec>");
        dontTouchNext(); // static/field
        dontTouchNext(); // Var type
        dontTouchNext(); // Var name
        line = peekNext();
        while(line[1].equals(",")) {
            dontTouchNext(); // ,
            dontTouchNext(); // Var name
            line = peekNext();
        }
        dontTouchNext(); // ;
        Util.append(currentFile, "</classVarDec>");
        compile();
    }

    private void compileClass() {
        Util.append(currentFile, "<class>");
        dontTouchNext(); // class
        dontTouchNext(); // Class name
        dontTouchNext(); // {
        compile();
        dontTouchNext(); // }
        Util.append(currentFile, "</class>");
    }

    private void compileSubroutine() {
        Util.append(currentFile, "<subroutineDec>");
        dontTouchNext(); // function/construtor/method
        dontTouchNext(); // Return type
        dontTouchNext(); // Subroutine name
        dontTouchNext(); // (
        compileParameterList();
        dontTouchNext(); // )
        Util.append(currentFile, "<subroutineBody>");
        dontTouchNext(); // {
        line = peekNext();
        while(line[1].equals("var")) {
            line = parseNext();
            if (line[1].equals("var")) compileVarDec();
            line = peekNext();
        }
        compileStatements();
        dontTouchNext(); // }
        Util.append(currentFile, "</subroutineBody>");
        Util.append(currentFile, "</subroutineDec>");
        compile();
    }

    private void compileVarDec() {
        Util.append(currentFile, "<varDec>");
        Util.append(currentFile, simpleLine);
        dontTouchNext(); // Var type
        dontTouchNext(); // Var name
        line = peekNext();
        while(line[1].equals(",")) {
            dontTouchNext(); // ,
            dontTouchNext(); // Var name
            line = peekNext();
        }
        dontTouchNext(); // ;
        Util.append(currentFile, "</varDec>");
    }

    private void compileStatements() {
        Util.append(currentFile, "<statements>");
        line = peekNext();
        while(!line[1].equals("}")) {
            line = parseNext();
            switch (line[1]) {
                case "var" : compileVarDec(); break;
                case "let" : compileLet(); break;
                case "if" : compileIf(); break;
                case "while" : compileWhile(); break;
                case "do" : compileDo(); break;
                case "return" : compileReturn(); break;
            }
            line = peekNext();
        }
        Util.append(currentFile, "</statements>");
    }

    private void compileReturn() {
        Util.append(currentFile, "<returnStatement>");
        Util.append(currentFile, simpleLine);
        line = peekNext();
        if(!line[1].equals(";")) {
            compileExpression();
        }
        dontTouchNext(); // ;
        Util.append(currentFile, "</returnStatement>");
    }

    private void compileDo() {
        Util.append(currentFile, "<doStatement>");
        Util.append(currentFile, simpleLine);
        compileSubroutineCall();
        dontTouchNext(); // ;
        Util.append(currentFile, "</doStatement>");
    }

    private void compileSubroutineCall() {
        dontTouchNext(); // Subroutine name OR Class name OR Var name
        line = peekNext();
        if(line[1].equals(".")) {
            dontTouchNext(); // .
            dontTouchNext(); // Subroutine name
        }
        dontTouchNext(); // (
        compileExpressionList();
        dontTouchNext(); // )
    }

    private void compileExpressionList() {
        Util.append(currentFile, "<expressionList>");
        line = peekNext();
        if(!line[1].equals(")")) {
            compileExpression();
            line = peekNext();
            while (line[1].equals(",")) {
                dontTouchNext(); // ,
                compileExpression();
                line = peekNext();
            }
        }
        Util.append(currentFile, "</expressionList>");
    }

    private void compileWhile() {
        Util.append(currentFile, "<whileStatement>");
        Util.append(currentFile, simpleLine);
        dontTouchNext(); // (
        compileExpression();
        dontTouchNext(); // )
        dontTouchNext(); // {
        compileStatements();
        dontTouchNext(); // }
        Util.append(currentFile, "</whileStatement>");
    }

    private void compileIf() {
        Util.append(currentFile, "<ifStatement>");
        Util.append(currentFile, simpleLine);
        dontTouchNext(); // (
        compileExpression();
        dontTouchNext(); // )
        dontTouchNext(); // {
        compileStatements();
        dontTouchNext(); // }
        line = peekNext();
        if (line[1].equals("else")) {
            dontTouchNext(); // else
            dontTouchNext(); // {
            compileStatements();
            dontTouchNext(); // }
        }
        Util.append(currentFile, "</ifStatement>");
    }

    private void compileLet() {
        Util.append(currentFile, "<letStatement>");
        Util.append(currentFile, simpleLine);
        dontTouchNext(); // var name
        line = peekNext();
        if(line[1].equals("[")) {
            dontTouchNext(); // [
            compileExpression();
            dontTouchNext(); // ]
        }
        dontTouchNext(); // =
        compileExpression();
        dontTouchNext(); // ;
        Util.append(currentFile, "</letStatement>");
    }

    private void compileExpression() {
        Util.append(currentFile, "<expression>");
        Util.append(currentFile, "<term>");
        dontTouchNext(); // Dummy term
        Util.append(currentFile, "</term>");
        line = peekNext();
        if (isOp(line[1])) {
            dontTouchNext(); // Op
            Util.append(currentFile, "<term>");
            dontTouchNext(); // Dummy term
            Util.append(currentFile, "</term>");
        }
        Util.append(currentFile, "</expression>");
    }

    private boolean isOp(String line) {
        return line.equals("+") ||
                line.equals("-") ||
                line.equals("*") ||
                line.equals("/") ||
                line.equals("&") ||
                line.equals("|") ||
                line.equals("<") ||
                line.equals(">") ||
                line.equals("=") ||
                line.equals("~");


    }

    private void compileParameterList() {
        Util.append(currentFile, "<parameterList>");
        String [] peekedLine = peekNext();
        while(!peekedLine[1].equals(")")) {
            dontTouchNext(); // Parameter type
            dontTouchNext(); // Parameter name
            peekedLine = peekNext();
        }
        Util.append(currentFile, "</parameterList>");
    }

    private void dontTouchNext() {
        Util.append(currentFile, programLines.elementAt(index));
        index++;
    }

    private String [] parseNext() {
        simpleLine = programLines.elementAt(index);
        String [] complexLine = {"type", "name"};
        int indexOfClosing = simpleLine.indexOf("</");
        if(indexOfClosing==-1) {
            complexLine[0] = simpleLine.substring(simpleLine.indexOf("<")+1, simpleLine.indexOf(">"));
            complexLine[1] = "";
            index++;
            return complexLine;
        }
        int indexLimit = simpleLine.indexOf(">", indexOfClosing);
        complexLine[0] = simpleLine.substring(indexOfClosing+2, indexLimit);
        int indexOfFirstSpace = simpleLine.indexOf(" ");
        int indexOfSecondSpace = simpleLine.indexOf(" ", indexOfFirstSpace+1);
        complexLine[1] = simpleLine.substring(indexOfFirstSpace+1, indexOfSecondSpace);
        index++;
        return complexLine;
    }

    private String [] peekNext() {
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
        return complexLine;
    }
}
