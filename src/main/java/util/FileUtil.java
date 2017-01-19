package util;

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by root on 1/17/17.
 */
public class FileUtil {
    static PropertiesHolder propertiesHolder = new PropertiesHolder();

    public static Path getFileToRead(){
        return FileSystems.getDefault().getPath(getResourceFolder(), propertiesHolder.getFileToReadName());
    }

    public static Path getFileToWrite() {
        return FileSystems.getDefault().getPath(getResourceFolder(), propertiesHolder.getFileToWriteName());
    }

    private static String getResourceFolder(){
        return System.getProperty("user.dir") + "/src/main/resources";
    }
}
