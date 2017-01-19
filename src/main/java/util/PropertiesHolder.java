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

    public PropertiesHolder() {
        loadPropertiesFile();
    }

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

    public String getFileToReadName() {
        return properties.getProperty("file.name.to.read");
    }

    public String getFileToWriteName() {
        return properties.getProperty("file.name.to.write");
    }

    public int getWorkerThreadsNumber() {
        return Integer.parseInt(properties.getProperty("worker.threads.number"));
    }

    public int getParagraphsNumberInBuffer() {
        return Integer.parseInt(properties.getProperty("paragraphs.number.in.buffer"));
    }
}
