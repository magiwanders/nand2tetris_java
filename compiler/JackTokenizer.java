package compiler;

import java.util.Vector;

import utilities.Util;
import utilities.Util.*;

public class JackTokenizer {

    private Vector<String> listOfJackFiles;
    private Vector<String> programLines;
    private String program;
    private String currentToken;
    private int index=0;

    public JackTokenizer(String jackDirectory) {
        listOfJackFiles = Util.getFiles(jackDirectory, ".jack");
        for(String jackFile : listOfJackFiles) {
            programLines = Util.loadFile(jackFile, ".jack", "_tokens.xml");
            program = stringify();
            Util.write("<tokens>");
            XMLize();
            Util.write("</tokens>");
        }
    }

    private void XMLize() {
        while(hasMoreTokens()) {
            if(program.charAt(index) == ' ') {
                index ++;
            } else if(program.charAt(index) == '"') {
                int secondVirgolett = program.indexOf("\"", index);
                currentToken = program.substring(index + 1, secondVirgolett);
                handle("stringConstant");
                index = secondVirgolett + 1;
            } else if(isNumber(program.charAt(index))) {
                int nextSpace = program.indexOf(" ", index);
                currentToken = program.substring(index, nextSpace);
                handle("integerConstant");
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
                        if(isLetter(currentTokenChar[i])) {
                            word.append(currentTokenChar[i]);
                        } else if (isSymbol(currentTokenChar[i])) {
                            if(isKeyword(word.toString())) {
                                handle("keyword");
                            } else if (!containsMoreTokens(word.toString())) {
                                handle("identifier");
                            }
                            handle("symbol", String.valueOf(currentTokenChar[i]));
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
        Util.write("<"+ type +">" + token + "</"+ type +">");
    }

    private String stringify() {
        StringBuilder programBuilder = new StringBuilder();
        for(String line : programLines) {
            programBuilder.append(line);
        }
        return programBuilder.toString();
    }

    public boolean hasMoreTokens() {
        return (index+1) < program.length();
    }

    public void advance() {

    }

}
