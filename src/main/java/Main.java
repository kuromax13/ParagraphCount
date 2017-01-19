import org.apache.log4j.Logger;
import paragraph.Paragraph;
import paragraph.ParagraphBuffer;
import paragraph.ParagraphBufferReader;
import processing.ParagraphProcessing;
import processing.ParagraphReader;
import processing.ParagraphWriter;
import util.FileUtil;
import util.PropertiesHolder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Acer on 24.10.2016.
 */
public class Main {
    private static final Object syncReader = new Object();    // object for synchronization
    private static final Object syncWriter = new Object();    // object for synchronization
    private static Path oldFile;                      // path file for load
    private static String nameNewFile;                      // name file for saved result file's load
    ParagraphBuffer<Paragraph> bufferReader;
    static PropertiesHolder propertiesHolder = new PropertiesHolder();
    public static final Logger logger = Logger.getLogger(Main.class);


    public static void main(String[] args) throws IOException {

        int workerThreadsNumber = propertiesHolder.getWorkerThreadsNumber();
        int paragraphsNumberInBuffer = propertiesHolder.getParagraphsNumberInBuffer();
        oldFile = FileUtil.getFileToRead();

        // Created reader's and writer's buffers
        ParagraphBufferReader<Paragraph> readerBuffer = new ParagraphBufferReader<>(new LinkedBlockingQueue<Paragraph>(workerThreadsNumber));
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

    public static void createAndStartedThreadsWorker(int workerThreadsNumber, ParagraphBufferReader<Paragraph> readerBuffer, ParagraphBuffer<Paragraph> writerBuffer, Object syncReader, Object syncWriter){

        int counter = 0;
        while (counter < workerThreadsNumber){

            new Thread(new ParagraphProcessing( syncReader, syncWriter,  writerBuffer, readerBuffer)).start();
            counter++;
            logger.info(" Created and started thread Worker " + counter + " from " + workerThreadsNumber);

        }

    }
}
