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
    private static final Path filePath = Paths.get("data/language-table.txt");

    // Plaintext language name
    public String name;

    // ISO 639-3 language code name
    public String code;

    public static final String UNKNOWN_STRING = "UNKNOWN";
    public static final String UNKNOWN_CODE = "???";
    public static final Language UNKNOWN = new Language(UNKNOWN_CODE, UNKNOWN_STRING);
    private static List<String> allNames;
    private static List<String> allCodes;
    private static boolean initialized = false;

    public static List<String> getAllNames() {
        if(!initialized) {
            init();
        }
        return allNames;
    }

    public static List<String> getAllCodes() {
        if(!initialized) {
            init();
        }
        return allCodes;
    }

    // Before using util.Language class, populate global lists of names/codes
    public static void init() {
        if(initialized == true)
            return;
        initialized = true;

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
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading language code file '"
                            + filePath.getFileName() + "'");
            ex.printStackTrace();
        }
    }

    public Language(String cd, String nm) {
        code = cd;
        name = nm;
    }

    public Language(String cd) throws Exception {
        if(!initialized)
            init();

        if(cd == null) {
            throw new IllegalArgumentException("Illegal argument for language code: '" + cd + "'");
        }

        code = cd;

        // Find language name
        int codeIndex = Collections.binarySearch(allCodes, code);
        if(codeIndex < 0) {
            throw new Exception("Code for language name '" + name + "' could not be found");
        }

        name = allNames.get(codeIndex);
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public boolean isUnknown() {
        return this.equals(Language.UNKNOWN);
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
