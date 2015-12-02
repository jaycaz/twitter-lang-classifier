package util;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by jacaz_000 on 11/29/2015.
 */
public class FilePaths {

    public static final String DATA_PATH = Paths.get("Language Identification/data").toAbsolutePath().toString();
    public static final String UTIL_PATH = Paths.get("Language Identification/src/util").toAbsolutePath().toString();
}
