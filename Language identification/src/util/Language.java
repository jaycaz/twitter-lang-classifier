package util;

import java.util.ArrayList;
import java.util.List;

/* class util.Language

    Stores the language codes and plaintext language names
 */
public class Language {

    // Plaintext language name
    public String name;

    // ISO 639-3 language code name
    public String code;

    public static final Language UNKNOWN = new Language("UNKNOWN");
    public static List<String> allNames;
    public static List<String> allCodes;
    private static boolean initialized = false;

    // Before using util.Language class, populate global lists of names/codes
    private static void init() {
        initialized = true;

        // TODO: Read in names and codes
        allNames = new ArrayList<>();
        allCodes = new ArrayList<>();
    }

    public Language(String nm) {
        if(!initialized)
            init();

        name = nm;
        code = nm; // TODO: Change to code

        // TODO: Validate language string
    }

    public String toString() {
        return name;
    }

    public boolean equals(Object obj) {
        Language otherLang = (Language) obj;
        if(otherLang == null)
            return false;
        return name.equals(otherLang.name);
    }
}
