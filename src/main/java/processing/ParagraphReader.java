package processing;

import org.apache.log4j.Logger;
import paragraph.Paragraph;
import paragraph.ParagraphBuffer;
import util.PropertiesHolder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Created by mrybalkin on 11.01.2017.
 *
 * Class to read paragraphs from file to buffer and add them into writer buffer in queue
 */
public class ParagraphReader implements Runnable {
    final Object monitorReader;
    static int paragraphsAmountToRead;
    Path fileToRead;
    ParagraphBuffer<Paragraph> buffer;
    static final Logger logger = Logger.getLogger(ParagraphReader.class);
    static PropertiesHolder propertiesHolder = new PropertiesHolder();

    public ParagraphReader(Object syncReader, Path fileToRead, ParagraphBuffer<Paragraph> buffer) {
        this.monitorReader = syncReader;
        this.fileToRead = fileToRead;
        this.buffer = buffer;
    }

    public void run() {

        try{
            boolean indicator = true;
            Scanner scanner = new Scanner(fileToRead);
            StringBuilder entry = new StringBuilder();

            while (scanner.hasNextLine()) {
                if (buffer.getDataQueue().size() < propertiesHolder.getParagraphsNumberInBuffer()){
                    while(indicator){
                        entry.append(scanner.nextLine());
                        if (entry.indexOf("/r/n") < 0) {
                            indicator = false;
                        }
                    }

                    if(entry.length() > 0){
                        buffer.getDataQueue().add(new Paragraph(paragraphsAmountToRead++, entry.toString()));
                        entry.delete(0, entry.length());
                    }
                    indicator = true;

                    synchronized (monitorReader){
                        monitorReader.notify();
                    }

                }else {
                    synchronized (monitorReader){
                        monitorReader.wait();
                    }
                }
            }
            buffer.setFlagEndFile(true);
            logger.info(" Change parameter flagEndFile. FileUtil read all.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // notify all Worker threads
        synchronized (monitorReader) {
            monitorReader.notifyAll();
        }
        logger.info(" ********************Thread Reader ended work.");

    }
}
