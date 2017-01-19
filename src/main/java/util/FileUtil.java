package util;

import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by mrybalkin on 1/17/17.
 *
 *Class to access file to read/write
 */
public class FileUtil {
    static PropertiesHolder propertiesHolder = new PropertiesHolder();

    //returns Path to file which need to read. File name described in property file
    public static Path getFileToRead(){
        return FileSystems.getDefault().getPath(getResourceFolder(), propertiesHolder.getFileToReadName());
    }

    //returns Path to file in which need to write. File name described in property file
    public static Path getFileToWrite() {
        return FileSystems.getDefault().getPath(getResourceFolder(), propertiesHolder.getFileToWriteName());
    }

    //returns path to project as string
    private static String getResourceFolder(){
        return System.getProperty("user.dir");
    }
}
