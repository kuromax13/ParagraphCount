import Paragraph.Paragraph;
import Paragraph.ParagraphReader;
import Paragraph.ParagraphBuffer;
import Paragraph.ParagraphBufferReader;

import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Acer on 12.01.2017.
 */
public class Coordinator implements Runnable{
    private final Object syncReader = new Object();    // object for synchronization
    private final Object syncWriter = new Object();    // object for synchronization
    static int amountThreadsWorker;                 // amount threads worker
    static int amountDataForOperation;           // max amount paragraphs in the buffer in one time
    protected Path oldFile;                      // path file for load
    protected String nameNewFile;                      // name file for saved result file's load
    ParagraphBuffer<Paragraph> bufferReader;


    /**
     * Constructor for object Coordinator
     *
     * @param oldFile           // path file for load
     * @param nameNewFile           // name file for saved result file's load
     * @param amountThreadsWorker   // amount threads worker
     */
    public Coordinator(Path oldFile, String nameNewFile, int amountThreadsWorker, int amountDataForOperation) {
        this.oldFile = oldFile;
        this.nameNewFile = nameNewFile;
        Coordinator.amountDataForOperation = amountDataForOperation;
        Coordinator.amountThreadsWorker = amountThreadsWorker;
    }

    public void run() {

        // Created reader's and writer's buffers
        ParagraphBufferReader<Paragraph> readerBuffer = new ParagraphBuffer<>(new LinkedBlockingQueue<Paragraph>(amountDataForOperation));
        ParagraphBuffer<Paragraph> writerBuffer = new ParagraphBuffer<>(new LinkedBlockingQueue<Paragraph>());
        System.out.println("Created reader and writer buffers");
//        logger.info("Created reader and writer buffers");

        // Created ans started thread reader
        new Thread(new ParagraphReader(syncReader, oldFile, readerBuffer)).start();
        System.out.println(" Created and started thread reader");
//        logger.info(" Created and started thread reader");

        // Created and started threads worker
        createAndStartedThreadsWorker(amountThreadsWorker, readerBuffer, writerBuffer, syncReader, syncWriter);
        System.out.println(" Created and started thread worker");
        // Created and started thread writer
        Thread paragraphWriter = new Thread(new ParagraphWriter(syncWriter, writerBuffer, nameNewFile));
        paragraphWriter.start();
        System.out.println(" Created and started thread writer");
        try {
            paragraphWriter.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    /**
     * The method created and started threads worker
     *
     * @param amountThreadsWorker  // amount threads worker
     * @param readerBuffer         // buffer for reade data which need load
     * @param writerBuffer         // buffer for write data after load
     * @param syncReader           // object for synchronization
     * @param syncWriter           // object for synchronization
     */
    public void createAndStartedThreadsWorker(int amountThreadsWorker, ParagraphBufferReader<Paragraph> readerBuffer, ParagraphBuffer<Paragraph> writerBuffer, Object syncReader, Object syncWriter){

        int counter = 0;
        while (counter < amountThreadsWorker){

            new Thread(new ParagraphProcessing( syncReader, syncWriter,  writerBuffer, readerBuffer)).start();
            counter++;
            System.out.println(" Created and started thread Worker " + counter + " from " + amountThreadsWorker);
//            logger.info(" Created and started thread Worker " + counter + " from " + amountThreadsWorker);

        }

    }
}
