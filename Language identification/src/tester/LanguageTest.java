package tester;

import util.Language;

/**
 * LanguageTest: Test Language class
 */
public class LanguageTest {

    public static void main(String args[]) throws Exception {
        System.out.println("Working Directory = " +
                System.getProperty("user.dir"));
        System.out.println("Initializing languages...");
        Language.init();

        System.out.println("Creating language 'French'");
        Language french = new Language("fra");
        System.out.println(french);
        System.out.println("Language successfully created!");

        System.out.println("Testing unknown language");
        Language unk = Language.UNKNOWN;
        System.out.println(unk);
    }
}
