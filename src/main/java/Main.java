import org.apache.log4j.Logger;
import paragraph.Paragraph;
import paragraph.ParagraphBuffer;
import processing.ParagraphProcessing;
import processing.ParagraphReader;
import processing.ParagraphWriter;
import util.FileUtil;
import util.PropertiesHolder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mrybalkin on 24.10.2016.
 */
public class Main {
    private static final Object syncReader = new Object();    // object for synchronization
    private static final Object syncWriter = new Object();    // object for synchronization
    static PropertiesHolder propertiesHolder = new PropertiesHolder();
    public static final Logger logger = Logger.getLogger(Main.class);


    public static void main(String[] args) throws IOException {

        int workerThreadsNumber = propertiesHolder.getWorkerThreadsNumber();
        int paragraphNumberInBuffer = propertiesHolder.getParagraphsNumberInBuffer();
        Path oldFile = FileUtil.getFileToRead();

        // Created reader's and writer's buffers
        ParagraphBuffer<Paragraph> readerBuffer = new ParagraphBuffer<>(new LinkedBlockingQueue<Paragraph>(paragraphNumberInBuffer));
        ParagraphBuffer<Paragraph> writerBuffer = new ParagraphBuffer<>(new LinkedBlockingQueue<Paragraph>());
        logger.info("Created reader and writer buffers");

        // Created ans started thread reader
        new Thread(new ParagraphReader(syncReader, oldFile, readerBuffer)).start();
        logger.info(" Created and started thread reader");

        // Created and started threads worker
       createAndStartedThreadsWorker(workerThreadsNumber, readerBuffer, writerBuffer, syncReader, syncWriter);
        // Created and started thread writer
        Thread paragraphWriter = new Thread(new ParagraphWriter(syncWriter, writerBuffer));
        paragraphWriter.start();
        try {
            paragraphWriter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void createAndStartedThreadsWorker(int workerThreadsNumber, ParagraphBuffer<Paragraph> readerBuffer, ParagraphBuffer<Paragraph> writerBuffer, Object syncReader, Object syncWriter){

        int counter = 0;
        while (counter < workerThreadsNumber){

            new Thread(new ParagraphProcessing( syncReader, syncWriter,  writerBuffer, readerBuffer)).start();
            counter++;
            logger.info(" Created and started thread Worker " + counter + " from " + workerThreadsNumber);

        }

    }
}
