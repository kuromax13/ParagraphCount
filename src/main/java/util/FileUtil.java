package util;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

/**
 * Created by root on 1/17/17.
 */
public class FileUtil {
    static PropertiesHolder propertiesHolder = new PropertiesHolder();
    private static FileUtil fileToWrite;
    private File newFile;
    static String nameFile = propertiesHolder.getFileToReadName();

//    public FileUtil(PropertiesHolder propertiesHolder) {
//        this.propertiesHolder = propertiesHolder;
//    }

    public static Path getFileToRead(){
        return FileSystems.getDefault().getPath(getResourceFolder(), nameFile);
    }

    public static Path getFileToWrite() {
        return FileSystems.getDefault().getPath(getResourceFolder(), getNewFileName());
    }

    public static String getNewFileName() {
//        newFile = new File(propertiesHolder.getFileToWriteName());
        return propertiesHolder.getFileToWriteName();
    }

    public static String getResourceFolder(){
        return System.getProperty("user.dir") + "/src/main/resources";
    }
}
