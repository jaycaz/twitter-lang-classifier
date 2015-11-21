package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.*;
import java.nio.file.*;

/* class util.Language

    Stores the language codes and plaintext language names
 */
public class Language {
    // The name of the file to open.
    private static final Path filePath = Paths.get("src/util/language-table.txt");

    // Plaintext language name
    private String name;

    // ISO 639-3 language code name
    private String code;

    public static final String UNKNOWN_STRING = "UNKNOWN";
    public static final String UNKNOWN_CODE = "???";
    public static final Language UNKNOWN = new Language(UNKNOWN_CODE, UNKNOWN_STRING);
    private static List<String> allNames;
    private static List<String> allCodes;
    private static boolean initialized = false;

    /**
     * Given a language code, returns the language name if found
     * @param code language code to search for
     * @return name of language if found, null otherwise
     */
    public static String nameOf(String code) {
        int codeIndex = Collections.binarySearch(allCodes, code);

        if(codeIndex < 0) {
            return null;
        }

        return allNames.get(codeIndex);
    }

    // Before using util.Language class, populate global lists of names/codes
    public static void init() {
        if(initialized)
            return;

        allNames = new ArrayList<String>();
        allCodes = new ArrayList<String>();

        String line = null;

        try {
            // Read in all language lines
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath.toString()));

            // Extract language code and name
            while((line = bufferedReader.readLine()) != null) {
                int tab = line.indexOf('\t');
                String code = line.substring(0, tab);
                String name = line.substring(tab + 1);
                allCodes.add(code);
                allNames.add(name);
            }

            bufferedReader.close();
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open language code file '" +
                            filePath.getFileName() + "'");
            ex.printStackTrace();
            return;
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading language code file '"
                            + filePath.getFileName() + "'");
            ex.printStackTrace();
            return;
        }

        initialized = true;
    }

    public Language(String code, String name) {
        if(!initialized)
            init();

        if(code == null || name == null) {
            System.out.println("Code: " + code + " | Name: " + name);
            throw new IllegalArgumentException("Language code or name cannot be null");
        }

        this.code = code;
        this.name = name;
    }

    public Language(String code) {
        if(!initialized)
            init();

        if(code == null) {
            throw new IllegalArgumentException("Language code cannot be null");
        }

        // Find language name
        int codeIndex = Collections.binarySearch(allCodes, code);
        if(codeIndex < 0) {
            throw new IllegalArgumentException("Name for language code '" + code + "' could not be found");
        }

        this.code = code;
        this.name = allNames.get(codeIndex);
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String toString() {
        return "(" + name + ", " + code + ")";
    }

    public boolean equals(Object obj) {
        Language otherLang = (Language) obj;
        if(otherLang == null)
            return false;
        return (name.equals(otherLang.name) && code.equals(otherLang.code));
    }
}
