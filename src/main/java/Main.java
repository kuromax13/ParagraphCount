import org.apache.log4j.Logger;
import paragraph.Paragraph;
import paragraph.ParagraphBuffer;
import processing.ParagraphProcessing;
import processing.ParagraphReader;
import processing.ParagraphWriter;
import util.FileUtil;
import util.PropertiesHolder;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mrybalkin on 24.10.2016.
 *
 * Main class for test processor.
 *
 * Creates and starts threads for reader, processor, writer.
 * Creates reader and writer buffers.
 *
 * File to read location, number of processor threads, number of paragraphs in buffer gets from property file.
 */
public class Main {
    static final Object monitorReader = new Object();
    static final Object syncWriter = new Object();
    static final Logger logger = Logger.getLogger(Main.class);
    static PropertiesHolder propertiesHolder = new PropertiesHolder();

    public static void main(String[] args) throws IOException {
        int processorThreadsNumber = propertiesHolder.getWorkerThreadsNumber();
        int paragraphNumberInBuffer = propertiesHolder.getParagraphsNumberInBuffer();

        ParagraphBuffer<Paragraph> readerBuffer = new ParagraphBuffer<>(new LinkedBlockingQueue<Paragraph>(paragraphNumberInBuffer));
        ParagraphBuffer<Paragraph> writerBuffer = new ParagraphBuffer<>(new LinkedBlockingQueue<Paragraph>());
        logger.info("### Created reader and writer buffers ###");

        new Thread(new ParagraphReader(monitorReader, FileUtil.getFileToRead(), readerBuffer)).start();
        logger.info("### Created and started thread reader ###");

        startParagraphProcessing(processorThreadsNumber, readerBuffer, writerBuffer, monitorReader, syncWriter);

        Thread paragraphWriter = new Thread(new ParagraphWriter(syncWriter, writerBuffer));
        paragraphWriter.start();
        logger.info("### Created and started thread writer ###");

        try {
            paragraphWriter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void startParagraphProcessing(int processingThreadsNumber, ParagraphBuffer<Paragraph> readerBuffer, ParagraphBuffer<Paragraph> writerBuffer, Object monitorReader, Object monitorWriter){
        for (int i = 0; i < processingThreadsNumber; i++){
            new Thread(new ParagraphProcessing( monitorReader, monitorWriter,  writerBuffer, readerBuffer)).start();
            logger.info("### Created and started thread Worker " + i + " from " + processingThreadsNumber + " ###");
        }
    }
}
