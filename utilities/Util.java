package utilities;

import hackComputer.utility.Print;

import java.io.*;
import java.util.Vector;

public class Util {

    public static Vector<String> loadFile(String fileAddress, String toReplace, String substitute) {
        Vector<String> program = new Vector<>();
        PrintWriter w;
        BufferedReader r;
        try {
            r = new BufferedReader(new FileReader(new File(fileAddress)));
            w = new PrintWriter(new FileWriter(new File(fileAddress.replaceAll(toReplace, substitute))));
            while(true) {
                String line = r.readLine();
                if(line==null) break;
                if(line.contains("/**")) {
                    while(!line.contains(" */")) {
                        line = r.readLine();
                    }
                }
                line = clean(line);
                if(!line.equals("")) program.add(line);
            }
            r.close();
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return program;
    }

    public static String clean(String line) {
        line = removeComments(line); // Removes comments.
        return line.trim();
    }

    private static String removeComments(String line) {
        if(line.contains("//")) line = line.substring(0, line.indexOf("//")); // In case there is a commment i removes it.
        if(line.contains("/**")) line = line.substring(0, line.indexOf("/**"));
        if(line.contains(" */")) line = line.substring(0, line.indexOf(" */"));
        return line;
    }

    public static void append(String fileToAppendTo, String toWrite) {
        try {
            PrintWriter appender = new PrintWriter(new FileWriter(new File(fileToAppendTo), true));
            appender.println(toWrite);
            appender.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Vector<String> getFiles(String directory, String extension) {
        String [] allFiles = new File(directory).list();
        Vector<String> fileList = new Vector<>();
        for (String file : allFiles) if (file.endsWith(extension)) fileList.addElement(directory + File.separator + file);
        return fileList;
    }

}
