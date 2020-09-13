package utilities;

import compiler.SymbolTable;
import hackComputer.utility.Print;

import java.io.*;
import java.util.Vector;

public class Util {

    public static Vector<String> loadFile(String fileAddress) {
        Vector<String> program = new Vector<>();
        BufferedReader r;
        try {
            r = new BufferedReader(new FileReader(new File(fileAddress)));
            while(true) {
                String line = r.readLine();
                if(line==null) break;
                if(!line.equals("")) program.add(line.trim());
            }
            r.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return program;
    }

    public static Vector<String> cleanFile(Vector<String> program) {
        int i=0;
        while (i<program.size()) {
            String currentLine = program.elementAt(i);
            if (currentLine.contains("//")) {
                int indexOfSingleLineComment = currentLine.indexOf("//");
                String newLine = currentLine.substring(0, indexOfSingleLineComment);
                if (newLine.isEmpty()) {
                    program.removeElementAt(i); i--;
                } else{
                    program.add(i, newLine);
                    program.removeElementAt(i+1);
                }
            } else if (currentLine.contains("/**")) {
                if (currentLine.contains("*/")) {
                    program.removeElementAt(i); i--; // Removes starting line.
                } else {
                    program.removeElementAt(i);
                    currentLine = program.elementAt(i);
                    while (!currentLine.contains("*/") && currentLine.contains("*")) {
                        program.removeElementAt(i);
                        currentLine = program.elementAt(i);
                    }
                    program.removeElementAt(i); i--;
                }
            }
            i++;
        }
        return program;
    }

    public static Vector<String> getFiles(String directory, String extension) {
        String [] allFiles = new File(directory).list();
        Vector<String> fileList = new Vector<>();
        for (String file : allFiles) if (file.endsWith(extension)) fileList.addElement(directory + File.separator + file);
        return fileList;
    }

    public static String getSimpleFileName(String file) {
        String nameWithExtension = new File(file).getName();
        int indexOfExtension = nameWithExtension.indexOf(".");
        return nameWithExtension.substring(0, indexOfExtension);
    }

}
