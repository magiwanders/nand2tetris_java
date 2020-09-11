package compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Vector;

import utilities.Log;
import utilities.Util;
import utilities.Util.*;

import javax.print.DocFlavor;

public class JackTokenizer {

    private Vector<String> listOfJackFiles;
    private String currentFile;
    private Vector<String> programLines;
    private String program;
    private PrintWriter w;
    private String currentToken;
    private int index=0;

    public JackTokenizer(String jackDirectory) {
        listOfJackFiles = Util.getFiles(jackDirectory, ".jack");
        for(String jackFile : listOfJackFiles) {
            currentFile = new String();
            programLines = new Vector<>();
            program = new String();
            currentToken = new String();
            index = 0;
            currentFile = jackFile.replaceAll(".jack", "T.xml");
            //Log.console("Processing file: " + currentXMLFile);
            programLines = Util.loadFile(jackFile);
            //Log.console(programLines);
            program = stringify();
            initializeIO(currentFile);
            w.println("<tokens>");
            XMLize();
            w.println("</tokens>");
        }

        w.close();
    }

    private void initializeIO(String currentXMLFile) {
        try {
            w = new PrintWriter(new BufferedWriter(new FileWriter(new File(currentXMLFile))));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void XMLize() {
        while(hasMoreTokens()) {
            if(program.charAt(index) == ' ') {
                index ++;
            } else if(program.charAt(index) == '"') {
                int secondVirgolett = program.indexOf("\"", index+1);
                currentToken = program.substring(index + 1, secondVirgolett);
                handle("stringConstant");
                index = secondVirgolett + 1;
            } else if(isNumber(program.charAt(index))) {
                int nextSpace = program.indexOf(" ", index);
                currentToken = program.substring(index, nextSpace);
                if (!containsMoreTokens(currentToken)) {
                    handle("integerConstant");
                } else {
                    char [] currentTokenChar = currentToken.toCharArray();
                    StringBuilder integer = new StringBuilder();
                    for(int i=0; i<currentToken.length(); i++) {
                        if (isSymbol(currentTokenChar[i]) || (i+1) == currentToken.length()) {
                            handle("integerConstant", integer.toString());
                            handle("symbol", String.valueOf(currentTokenChar[i]));
                            integer = new StringBuilder();
                        } else {
                            integer.append(currentTokenChar[i]);
                        }
                    }
                }
                index = nextSpace+1;
            } else if (isSymbol(program.charAt(index))) {
                currentToken = program.substring(index, index+1);
                handle("symbol");
                index++;
            } else if(isLetter(program.charAt(index))) {
                int nextSpace = program.indexOf(" ", index);
                currentToken = program.substring(index, nextSpace);
                if(isKeyword(currentToken)) {
                    handle("keyword");
                } else if (!containsMoreTokens(currentToken)) {
                    handle("identifier");
                } else {
                    char [] currentTokenChar = currentToken.toCharArray();
                    StringBuilder word = new StringBuilder();
                    for(int i=0; i<currentToken.length(); i++) {
                        if(isLetter(currentTokenChar[i])||isNumber(currentTokenChar[i])) {
                            word.append(currentTokenChar[i]);
                        }
                        if (isSymbol(currentTokenChar[i]) || (i+1) == currentToken.length()) {
                            if(isKeyword(word.toString())) {
                                handle("keyword", word.toString());
                            } else if (isNumber(word.toString())) {
                                handle("integerConstant", word.toString());
                            } else if (!containsMoreTokens(word.toString())) {
                                handle("identifier", word.toString());
                            }
                            if (isSymbol(currentTokenChar[i])) handle("symbol", String.valueOf(currentTokenChar[i]));
                            word = new StringBuilder();
                        }
                    }
                }
                index = nextSpace+1;
            }
        }
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

    private boolean isSymbol(char candidate) {
        return candidate == '{' ||
                candidate == '}' ||
                candidate == '(' ||
                candidate == ')' ||
                candidate == '[' ||
                candidate == ']' ||
                candidate == '.' ||
                candidate == ',' ||
                candidate == ';' ||
                candidate == '+' ||
                candidate == '-' ||
                candidate == '*' ||
                candidate == '/' ||
                candidate == '&' ||
                candidate == '|' ||
                candidate == '>' ||
                candidate == '<' ||
                candidate == '=' ||
                candidate == '~';
    }

    private boolean isNumber(char candidate) {
        return candidate == '0' ||
                candidate == '1' ||
                candidate == '2' ||
                candidate == '3' ||
                candidate == '4' ||
                candidate == '5' ||
                candidate == '6' ||
                candidate == '7' ||
                candidate == '8' ||
                candidate == '9';
    }

    private boolean isNumber(String candidate) {
        try {
            int candidateInt = Integer.parseInt(candidate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean containsMoreTokens(String candidate) {
        return candidate.contains("{") ||
                candidate.contains("}") ||
                candidate.contains("(") ||
                candidate.contains(")") ||
                candidate.contains("[") ||
                candidate.contains("]") ||
                candidate.contains(",") ||
                candidate.contains(".") ||
                candidate.contains(";") ||
                candidate.contains("+") ||
                candidate.contains("-") ||
                candidate.contains("*") ||
                candidate.contains("/") ||
                candidate.contains("&") ||
                candidate.contains("|") ||
                candidate.contains("<") ||
                candidate.contains(">") ||
                candidate.contains("=") ||
                candidate.contains("~");
    }

    private boolean isKeyword(String candidate) {
        return candidate.equals("class") ||
                candidate.equals("constructor") ||
                candidate.equals("function") ||
                candidate.equals("method") ||
                candidate.equals("field") ||
                candidate.equals("static") ||
                candidate.equals("var") ||
                candidate.equals("int") ||
                candidate.equals("char") ||
                candidate.equals("boolean") ||
                candidate.equals("void") ||
                candidate.equals("true") ||
                candidate.equals("false") ||
                candidate.equals("null") ||
                candidate.equals("this") ||
                candidate.equals("let") ||
                candidate.equals("do") ||
                candidate.equals("if") ||
                candidate.equals("else") ||
                candidate.equals("while") ||
                candidate.equals("return");
    }

    private void handle(String type) {
        handle(type, currentToken);
    }

    private void handle(String type, String token) {
        if(!token.equals("")) {
            if(type.equals("symbol")) {
                if(token.equals("<")) token = "&lt;";
                if(token.equals(">")) token = "&gt;";
                if(token.equals("\"")) token = "&quot;";
                if(token.equals("&")) token = "&amp;";
            }
            w.println("<"+ type +"> " + token + " </"+ type +">");
        }
    }

    private String stringify() {
        StringBuilder programBuilder = new StringBuilder();
        for(String line : programLines) {
            programBuilder.append(strategicSpace(line));
        }
        return programBuilder.toString();
    }

    private String strategicSpace(String line) { // Puts spaces after and before ; and before the first "
        StringBuilder strategicLine = new StringBuilder();
        char [] lineChar = line.toCharArray();
        boolean foundFirstVirgolett = false;
        for(int i=0; i<line.length(); i++) {
            if(lineChar[i] == ';') {
                strategicLine.append(" ");
                strategicLine.append(String.valueOf(lineChar[i]));
                strategicLine.append(" ");
            } else if (lineChar[i] == '\"' && foundFirstVirgolett) {
                strategicLine.append(String.valueOf(lineChar[i]));
                foundFirstVirgolett = false;
            } else if (lineChar[i] == '\"' && !foundFirstVirgolett) {
                strategicLine.append(" ");
                strategicLine.append(String.valueOf(lineChar[i]));
                foundFirstVirgolett = true;
            } else {
                strategicLine.append(String.valueOf(lineChar[i]));
            }
        }
        return strategicLine.toString();
    }

    public boolean hasMoreTokens() {
        return index < program.length();
    }

}
