import org.testng.Assert;
import org.testng.annotations.Test;
import paragraph.Paragraph;
import paragraph.ParagraphBuffer;
import processing.ParagraphWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by mrybalkin on 30.01.2017.
 *
 * Tests:
 *      1.countParagraphLength;
 *      2.countAverageWordsLengthInParagraph;
 *      3.countWordsInParagraph
 *      4.countPunctuationSymbolsInParagraph
 *      5.calcParagraph
 *      6.getInformation to write
 *      7.sortParagraphs
 */
public class ParagraphWriter_Test {

    @Test
    public void testCountParagraphLength(){
        String paragraph = "123 456";

        Assert.assertEquals(ParagraphWriter.countParagraphLength(paragraph), 7);
    }

    @Test
    public void testCountAverageWordsLengthInParagraph(){
        String paragraph = "123 456";

        Assert.assertEquals(ParagraphWriter.countAverageWordsLengthInParagraph(paragraph), 3);
    }

    @Test
    public void testCountWordsInParagraph(){
        String paragraph = "123 456";

        Assert.assertEquals(ParagraphWriter.countWordsInParagraph(paragraph), 2);
    }

    @Test
    public void testPunctuationSymbolsInParagraph(){
        String paragraph =  ".,:;!?()[]{}<>/|@#$%^&*-+=_~`\"";

        Assert.assertEquals(ParagraphWriter.countPunctuationSymbolsInParagraph(paragraph), 30);
    }

    @Test
    public void testCalcParagraph() {
        Paragraph paragraph = new Paragraph(0, "123 456");
        ParagraphWriter.calcParagraph(paragraph);

        Assert.assertTrue(paragraph.getParagraph().equals("123 456") &&
                            paragraph.getAverageWordLength() == 3 &&
                            paragraph.getParagraphLength() == 7 &&
                            paragraph.getWordsInParagraph() == 2 &&
                            paragraph.getPunctuationSymbolsAmount() == 0);
    }

    @Test
    public void testGetInfoToWrite() {
        String expectedString = "paragraphNumber=0, paragraphText='1', paragraphHash='356a192b7913b04c54574d18c28d46e6395428ab', paragraphLength=1, wordsInParagraph=1, averageWordLength=1, punctuationSymbolsAmount=0}\n" +
                "paragraphNumber=1, paragraphText='2', paragraphHash='da4b9237bacccdf19c0760cab7aec4a8359010b0', paragraphLength=1, wordsInParagraph=1, averageWordLength=1, punctuationSymbolsAmount=0}\n";
        List<Paragraph> paragraphList = new ArrayList<>();
        paragraphList.add(0, new Paragraph(0, "1"));
        paragraphList.add(1, new Paragraph(1, "2"));

        Assert.assertEquals(ParagraphWriter.getInformationForWrite(paragraphList), expectedString);

    }

    @Test
    public void testSortParagraphs(){
        List<Paragraph> paragraphList = new ArrayList<>();
        paragraphList.add(0, new Paragraph(1, "1"));
        paragraphList.add(1, new Paragraph(0, "2"));

        Assert.assertEquals(ParagraphWriter.sortParagraphs(paragraphList).get(0).getNumberParagraph(), 0);
    }

    @Test
    public void testParagraphWriter(){
        final Object syncWriter = new Object();
        ParagraphBuffer<Paragraph> writerBuffer = new ParagraphBuffer<>(new LinkedBlockingQueue<Paragraph>());

        writerBuffer.getDataQueue().add(new Paragraph(0, "1"));
        new Thread(new ParagraphWriter(syncWriter, writerBuffer)).start();

        Assert.assertTrue(!writerBuffer.getDataQueue().isEmpty());
        Assert.assertEquals(writerBuffer.getDataQueue().size(), 1);
    }

}
