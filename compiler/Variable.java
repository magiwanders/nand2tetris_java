package compiler;

public class Variable {

    final int index;
    final String type;
    final String kind;

    public Variable(String type, String kind, int index) {
        this.index = index;
        this.type = type;
        this.kind = kind;
    }

    public String toString() {
        return " | " + type + " | " + kind + " | " + index + " }\n";
    }

}
