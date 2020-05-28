package coms.casper.io;

import java.io.InputStream;

public class Resources {
    public static InputStream getResourceAdStream(String path) {
        return Resources.class.getClassLoader().getResourceAsStream(path);
    }
}
