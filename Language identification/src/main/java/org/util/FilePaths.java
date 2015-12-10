package org.util;

import java.nio.file.Paths;

/**
 * Created by jacaz_000 on 11/29/2015.
 */
public class FilePaths {

    public static final String DATA_PATH = Paths.get("data").toAbsolutePath().toString();
    public static final String UTIL_PATH = Paths.get("src/main/java/org/util").toAbsolutePath().toString();
}
