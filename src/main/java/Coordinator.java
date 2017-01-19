import org.apache.log4j.Logger;
import paragraph.Paragraph;
import paragraph.ParagraphBuffer;
import paragraph.ParagraphBufferReader;
import util.FileUtil;
import util.PropertiesHolder;

import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Acer on 12.01.2017.
 */
public class Coordinator implements Runnable{
    private final Object syncReader = new Object();    // object for synchronization
    private final Object syncWriter = new Object();    // object for synchronization
    private Path oldFile;                      // path file for load
    private String nameNewFile;                      // name file for saved result file's load
    ParagraphBuffer<Paragraph> bufferReader;
    PropertiesHolder propertiesHolder = new PropertiesHolder();
    public static final Logger logger = Logger.getLogger(Coordinator.class);
    static int amountThreadsWorker;


    /**
     * Constructor for object Coordinator
     *
     * @param oldFile           // path file for load
     * @param nameNewFile           // name file for saved result file's load
     * @param amountThreadsWorker   // amount threads worker
     */
    public Coordinator(int amountThreadsWorker) {
//        this.oldFile = FileUtil.getFileToRead();
//        this.nameNewFile = FileUtil.getFileToWrite();
//        Coordinator.amountDataForOperation = amountDataForOperation;
        Coordinator.amountThreadsWorker = amountThreadsWorker;
    }

    public void run() {
        int workerThreadsNumber = propertiesHolder.getWorkerThreadsNumber();
        int paragraphsNumberInBuffer = propertiesHolder.getParagraphsNumberInBuffer();
        oldFile = FileUtil.getFileToRead();
        nameNewFile = FileUtil.getNewFileName();
        // Created reader's and writer's buffers
        ParagraphBufferReader<Paragraph> readerBuffer = new ParagraphBuffer<>(new LinkedBlockingQueue<Paragraph>(amountThreadsWorker));
        ParagraphBuffer<Paragraph> writerBuffer = new ParagraphBuffer<>(new LinkedBlockingQueue<Paragraph>());
//        logger.info("Created reader and writer buffers");

        // Created ans started thread reader
        new Thread(new ParagraphReader(syncReader, oldFile, readerBuffer)).start();
//        logger.info(" Created and started thread reader");

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


    /**
     * The method created and started threads worker
     *
     * @param workerThreadsNumber  // amount threads worker
     * @param readerBuffer         // buffer for reade data which need load
     * @param writerBuffer         // buffer for write data after load
     * @param syncReader           // object for synchronization
     * @param syncWriter           // object for synchronization
     */
    public static void createAndStartedThreadsWorker(int workerThreadsNumber, ParagraphBufferReader<Paragraph> readerBuffer, ParagraphBuffer<Paragraph> writerBuffer, Object syncReader, Object syncWriter){

        int counter = 0;
        while (counter < workerThreadsNumber){

            new Thread(new ParagraphProcessing( syncReader, syncWriter,  writerBuffer, readerBuffer)).start();
            counter++;
            logger.info(" Created and started thread Worker " + counter + " from " + workerThreadsNumber);

        }

    }
}
