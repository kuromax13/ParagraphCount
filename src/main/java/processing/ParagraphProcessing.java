package processing;

import org.apache.log4j.Logger;
import paragraph.Paragraph;
import paragraph.ParagraphBuffer;
import util.PropertiesHolder;

/**
 * Created by mrybalkin on 01.11.2016.
 *
 * Class describes paragraph processing.
 * Processing means take paragraph from reader buffer and place it into writer buffer.
 */
public class ParagraphProcessing implements Runnable {
    final Object monitorReader;
    final Object monitorWriter;
    Paragraph paragraph;
    ParagraphBuffer<Paragraph> bufferReader;
    ParagraphBuffer<Paragraph> bufferWriter;
    private static final Logger logger = Logger.getLogger(ParagraphProcessing.class);
    static PropertiesHolder propertiesHolder = new PropertiesHolder();
    static int amountThreadsWorker = propertiesHolder.getWorkerThreadsNumber();

    public ParagraphProcessing(Object syncReader, Object syncWriter, ParagraphBuffer<Paragraph> bufferWriter, ParagraphBuffer<Paragraph> bufferReader) {
        this.monitorReader = syncReader;
        this.monitorWriter = syncWriter;
        this.bufferWriter = bufferWriter;
        this.bufferReader = bufferReader;
    }

    public void run() {
        logger.info("### Started new thread Processor ###");

        while (!bufferReader.isEndOfFile() || !bufferReader.getDataQueue().isEmpty()) {
            if (bufferReader.getDataQueue().isEmpty()) {

                synchronized (monitorReader){
                    monitorReader.notifyAll();
                    try {
                        monitorReader.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                // take paragraph for work
                synchronized (monitorReader) {
                    if (bufferReader.getDataQueue().isEmpty()){
                        break;
                    }else {
                        paragraph = bufferReader.getDataQueue().remove();
                    }
                }
                bufferWriter.getDataQueue().add(paragraph);
                logger.info(" Save paragraph's data in the reader buffer");

                synchronized (monitorWriter){
                    monitorWriter.notify();
                }
            }
        }

        lastActionsWorkerThreads(bufferWriter);
        logger.info("### Thread Worker ended work ###");

    }

    /**
     * The method is completing last action work thread
     *
     * @param writerBuffer       buffer for save result work
     */
    public synchronized void lastActionsWorkerThreads(ParagraphBuffer<Paragraph> writerBuffer){

        if (amountThreadsWorker == 1){

            logger.info("Change parameter endFile in the writerBuffer");
            writerBuffer.setFlagEndFile(true);

            synchronized (monitorWriter){
                monitorWriter.notify();
            }
        }
        amountThreadsWorker--;
    }
}
