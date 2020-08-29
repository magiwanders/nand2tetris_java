package assembler;

public class AssemblyParser {

    public String AInstructionInt(String line) { // @value -> retrieves 'value' as a string
        line = line.replaceAll("@", "");
        return line;
     }

    public String dest(String line) { // dest = comp ; jump   ->   retrieves dest
        int indexOfEqual = line.indexOf("=");
        if(indexOfEqual == -1) return "NULL";
        return line.substring(0, indexOfEqual);
    }

    public String comp(String line) { // dest = comp ; jump   ->   retrieves comp
        int indexOfEqual = line.indexOf("=");
        int indexOfEnd = line.indexOf(";");
        if(indexOfEnd == -1) return line.substring(indexOfEqual+1);
        else return line.substring(indexOfEqual+1, indexOfEnd);
    }

    public String jump(String line) { // dest = comp ; jump   ->   retrieves jump
        int indexOfEnd = line.indexOf(";");
        if((indexOfEnd+1) == line.length() || indexOfEnd == -1) return "NULL";
        else return line.substring(indexOfEnd+1);
    }

}
