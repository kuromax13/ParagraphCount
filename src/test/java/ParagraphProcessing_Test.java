import org.testng.Assert;
import org.testng.annotations.Test;
import paragraph.Paragraph;
import paragraph.ParagraphBuffer;
import processing.ParagraphProcessing;
import util.PropertiesHolder;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mrybalkin on 2/6/17.
 */
public class ParagraphProcessing_Test {
    static PropertiesHolder propertiesHolder = new PropertiesHolder();
    static int amountThreadsWorker = propertiesHolder.getWorkerThreadsNumber();

    @Test
    public void testLastActionsWorkerThreads(){
        int paragraphNumberInBuffer = 1;
        final Object monitorReader = new Object();
        final Object monitorWriter = new Object();
        ParagraphBuffer<Paragraph> readerBuffer = new ParagraphBuffer<>(new LinkedBlockingQueue<Paragraph>(paragraphNumberInBuffer));
        ParagraphBuffer<Paragraph> writerBuffer = new ParagraphBuffer<>(new LinkedBlockingQueue<Paragraph>());
        ParagraphProcessing pr = new ParagraphProcessing(monitorReader, monitorWriter, writerBuffer, readerBuffer);

        readerBuffer.getDataQueue().add(new Paragraph(0, "123"));
        pr.run();
        writerBuffer.getDataQueue();

        pr.lastActionsWorkerThreads(writerBuffer);
        writerBuffer.getDataQueue();
        Assert.assertEquals(amountThreadsWorker, 5);

    }

    @Test
    public void testParagraphProcessing(){

    }
}
