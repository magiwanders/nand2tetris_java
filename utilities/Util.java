package utilities;

import java.io.*;
import java.util.Vector;

public class Util {

    private static BufferedReader r;
    private static PrintWriter w;

    private static Vector<String> program;

    public static Vector<String> loadFile(String fileAddress, String toReplace, String substitute) {
        initializeIO(fileAddress, toReplace, substitute);
        readFile();
        return program;
    }

    private static void readFile() {
        try {
            while(true) {
                String line = r.readLine();
                if(line==null) break;
                clean(line);
                if(!line.equals("")) program.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clean(String line) {
        line = removeComments(line); // Removes comments.
        line = line.trim();
    }

    private static String removeComments(String line) {
        if(line.contains("//")) line = line.substring(0, line.indexOf("//")); // In case there is a commment i removes it.
        return line;
    }

    public static void write(String toWrite) {
        w.println(toWrite);
    }

    private static void initializeIO(String fileAddress, String toReplace, String substitute) {
        try {
            r = new BufferedReader(new FileReader(new File(fileAddress)));
            w = new PrintWriter(new FileWriter(new File(fileAddress.replaceAll(toReplace, substitute))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Vector<String> getFiles(String directory, String extension) {
        String [] allFiles = new File(directory).list();
        Vector<String> fileList = new Vector<>();
        for (String file : allFiles) if (file.endsWith(extension)) fileList.addElement(file);
        return fileList;
    }

}
