package processing;

import org.apache.log4j.Logger;
import paragraph.Paragraph;
import paragraph.ParagraphBuffer;
import util.PropertiesHolder;

/**
 * Created by Acer on 01.11.2016.
 *
 * Processing means that you'll calculate:
 * length,
 * words count,
 * average words length,
 * dots
 * commas
 * fingerprint (hash, SHA-1)
 */
public class ParagraphProcessing implements Runnable {
    final Object syncReader;
    final Object syncWriter;
    ParagraphBuffer<Paragraph> bufferReader;
    ParagraphBuffer<Paragraph> bufferWriter;
    Paragraph paragraph;
    public static final Logger logger = Logger.getLogger(ParagraphProcessing.class);
    static PropertiesHolder propertiesHolder = new PropertiesHolder();
    static int amountThreadsWorker = propertiesHolder.getWorkerThreadsNumber();


    public ParagraphProcessing(Object syncReader, Object syncWriter, ParagraphBuffer<Paragraph> bufferWriter, ParagraphBuffer<Paragraph> bufferReader) {
        this.syncReader = syncReader;
        this.syncWriter = syncWriter;
        this.bufferWriter = bufferWriter;
        this.bufferReader = bufferReader;
    }

    public void run() {
        logger.info(" Started new thread Worker");

        while (!bufferReader.isEndOfFile() || !bufferReader.getDataQueue().isEmpty()) {
            if (bufferReader.getDataQueue().isEmpty()) {

                synchronized (syncReader){
                    syncReader.notifyAll();
                    try {
                        syncReader.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                // take paragraph for work
                synchronized (syncReader) {
                    if (bufferReader.getDataQueue().isEmpty()){
                        break;
                    }else {
                        paragraph = bufferReader.getDataQueue().remove();
                    }
                }
                bufferWriter.getDataQueue().add(paragraph);
                logger.info(" Save paragraph's data in the reader buffer");

                synchronized (syncWriter){
                    syncWriter.notify();
                }
            }
        }
        lastActionsWorkerThreads(bufferWriter);
        logger.info(" Thread Worker ended work.");

    }

    /**
     * The method is completing last action work thread
     *
     * @param writerBuffer       buffer for save result work
     */
    public synchronized void lastActionsWorkerThreads(ParagraphBuffer<Paragraph> writerBuffer){

        if (amountThreadsWorker == 1){

            logger.info("  Change parameter flagAndFile in the writerBuffer.");
            writerBuffer.setFlagEndFile(true);

            synchronized (syncWriter){
                syncWriter.notify();
            }
        }
        amountThreadsWorker--;
    }
}
