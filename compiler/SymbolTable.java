package compiler;

import utilities.Log;

import java.util.Hashtable;
import java.util.Vector;

public class SymbolTable {

    private Hashtable<String, Variable> table;
    private final Vector<String> acceptableKinds;
    int numberOfARGUMENT, numberOfVAR, numberOfSTATIC, numberOfFIELD;

    public SymbolTable(String classOrsubroutine) {
        table = new Hashtable<>();
        acceptableKinds = new Vector<>();
        if(classOrsubroutine.equals("CLASS")) initializeClass();
        if(classOrsubroutine.equals("SUBROUTINE")) initializeSubroutine();
    }

    private void initializeSubroutine() {
        acceptableKinds.addElement("argument");
        acceptableKinds.addElement("local");
        numberOfSTATIC = -1;
        numberOfFIELD = -1;
    }

    private void initializeClass() {
        acceptableKinds.addElement("static");
        acceptableKinds.addElement("field");
        numberOfARGUMENT = -1;
        numberOfVAR = -1;
    }

    public void define(String name, String type, String kind) {
        if(!acceptableKinds.contains(kind)) {
            Log.console("Wrong kind '" + kind + "' being assigned to the symbol table.");
            return;
        }
        int numberOfThatKind=0;
        switch (kind) {
            case "argument": numberOfThatKind = numberOfARGUMENT; numberOfARGUMENT++; break;
            case "local": numberOfThatKind = numberOfVAR; numberOfVAR++; break;
            case "static": numberOfThatKind = numberOfSTATIC; numberOfSTATIC++; break;
            case "field": numberOfThatKind = numberOfFIELD; numberOfFIELD++; break;
        }
        table.put(name, new Variable(type, kind, numberOfThatKind));
    }

    public void clear() {
        table.clear();
        if(numberOfFIELD!=-1) numberOfFIELD=0;
        if(numberOfSTATIC!=-1) numberOfSTATIC=0;
        if(numberOfVAR!=-1) numberOfVAR=0;
        if(numberOfARGUMENT!=-1) numberOfARGUMENT=0;
    }

    public int varCount(String kind) {
        switch (kind) {
            case "argument": return numberOfARGUMENT;
            case "local": return numberOfVAR;
            case "static": return numberOfSTATIC;
            case "field": return numberOfFIELD;
            default: return -1;
        }
    }

    public String kindOf(String name) {
        Variable temp = table.get(name);
        return temp.kind;
    }

    public String typeOf(String name) {
        Variable temp = table.get(name);
        return temp.type;
    }

    public int indexOf(String name) {
        Variable temp = table.get(name);
        return temp.index;
    }

    public boolean contains(String name) {
        return table.containsKey(name);
    }

    public String toString() {
        StringBuilder tableString = new StringBuilder();
        tableString.append("\nCurrent table state:\n");
        for(String name : table.keySet()) {
            tableString.append("{ "+ name + table.get(name).toString());
        }
        return tableString.toString();
    }

}
