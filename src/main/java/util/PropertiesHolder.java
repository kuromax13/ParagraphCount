package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by mrybalkin on 1/17/17.
 *
 * Class interaction with config properties file
 */
public class PropertiesHolder {
    Properties properties = new Properties();
    InputStream input = null;
    String fileName = "config.properties";

    /**
     * Constructor for properties holder.
     *
     * Loads properties when object is created
     */
    public PropertiesHolder() {
        loadPropertiesFile();
    }

    /**
     * Loads properties file
     */
    public void loadPropertiesFile() {
        try {
            input = getClass().getClassLoader().getResourceAsStream(fileName);

            if (input != null){
                properties.load(input);
            } else {
                throw new FileNotFoundException("Property file not found in the classpath");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //returns file's to read name value as string
    public String getFileToReadName() {
        return properties.getProperty("file.name.to.read");
    }

    //returns file's to write name value as string
    public String getFileToWriteName() {
        return properties.getProperty("file.name.to.write");
    }

    //returns number of threads for worker
    public int getWorkerThreadsNumber() {
        return Integer.parseInt(properties.getProperty("worker.threads.number"));
    }

    //returns number of paragraphs
    public int getParagraphsNumberInBuffer() {
        return Integer.parseInt(properties.getProperty("paragraphs.number.in.buffer"));
    }
}
