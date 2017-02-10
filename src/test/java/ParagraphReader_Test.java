import org.testng.Assert;
import org.testng.annotations.Test;
import paragraph.Paragraph;
import paragraph.ParagraphBuffer;
import processing.ParagraphReader;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by root on 2/6/17.
 */
public class ParagraphReader_Test {

    @Test
    public void testParagraphReader(){
        final Object monitorReader = new Object();
        ParagraphBuffer<Paragraph> readerBuffer = new ParagraphBuffer<>(new LinkedBlockingQueue<Paragraph>());
        File file = new File(System.getProperty("user.dir") +
                File.separator + "src" +
                File.separator + "test" +
                File.separator + "java" +
                File.separator + "resources", "Test.txt");
        Thread paragraphReader = new Thread(new ParagraphReader(monitorReader, file.toPath(), readerBuffer));
        paragraphReader.run();

        Assert.assertTrue(!readerBuffer.getDataQueue().isEmpty());
    }
}
