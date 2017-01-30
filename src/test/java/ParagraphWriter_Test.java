import org.testng.Assert;
import org.testng.annotations.Test;
import paragraph.Paragraph;
import paragraph.ParagraphBuffer;
import processing.ParagraphWriter;

/**
 * Created by mrybalkin on 30.01.2017.
 */
public class ParagraphWriter_Test {
    Object monitor;
    ParagraphBuffer<Paragraph> pr;

    @Test
    public void test(){
        ParagraphWriter pw = new ParagraphWriter(monitor, pr);
        String str = "123456";
        Assert.assertEquals(ParagraphWriter.countParagraphLength(str), 6);
    }
}
