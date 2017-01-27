package processing;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import org.apache.log4j.Logger;
import paragraph.Paragraph;
import paragraph.ParagraphBuffer;
import util.FileUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by mrybalkin on 11.01.2017.
 *
 * Class to get paragraphs from buffer, calculate paragraph's parameters and write into file.
 */
public class ParagraphWriter implements Runnable {
    final String SYMBOLS_PUNCTUATION = ".,:;!?()[]{}<>/|@#$%^&*-+=_~`\"";
    final Object monitorWriter;
    static int paragraphCounter = 0;
    String informationForWrite = "";
    String isEmptyString = "";
    Path newFile1;
    ParagraphBuffer<Paragraph> buffer;
    List<Paragraph> writerBuffer = new LinkedList<>();
    private static final Logger logger = Logger.getLogger(ParagraphWriter.class);

    public ParagraphWriter(Object monitorWriter, ParagraphBuffer<Paragraph> buffer) {
        this.monitorWriter = monitorWriter;
        this.buffer = buffer;
    }

    public void run() {
        logger.info("Paragraph writer is started");

        newFile1 = FileUtil.getFileToWrite();
        File newFile = new File(newFile1.toAbsolutePath().toString());

        try(FileWriter writer = new FileWriter(newFile)){

            while (!buffer.getDataQueue().isEmpty() || !buffer.isEndOfFile()) { //check if buffer not empty
                if (buffer.getDataQueue().isEmpty()) {

                    synchronized (monitorWriter) {
                        try {
                            monitorWriter.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    //add read paragraphs to the list
                    while (!buffer.getDataQueue().isEmpty() ){
                        writerBuffer.add(buffer.getDataQueue().remove());
                    }

                    if (!writerBuffer.isEmpty()){
                        writerBuffer = sortParagraphs(writerBuffer);
                        informationForWrite = getInformationForWrite(writerBuffer);

                        if (!informationForWrite.equals(isEmptyString)){
                            writer.write(informationForWrite);
                            informationForWrite = isEmptyString;
                        }
                    }
                }
            }

            logger.info("Finish writing paragraphs data.");
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("### Thread Writer ended work. ###");

    }

    /**
     * Take paragraph from buffer, calc info, and converts to the string
     *
     * Java 8 may be used. See http://codereview.stackexchange.com/questions/64011/removing-elements-on-a-list-while-iterating-through-it
     *
     * @param writerBuffer  collection with paragraph's objects
     * @return              paragraphs with their information
     */
    protected String getInformationForWrite(List<Paragraph> writerBuffer){
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Paragraph> paragraphIterator = writerBuffer.iterator();
        while (paragraphIterator.hasNext()){
            Paragraph paragraph = paragraphIterator.next();
            if (paragraph.getNumberParagraph() == paragraphCounter) { //filter out duplicated paragraphs in buffer
                calcParagraph(paragraph);
                stringBuilder.append(paragraph.toString()).append(System.lineSeparator());
                paragraphIterator.remove();
                paragraphCounter++;
            }else break;
        }

        return stringBuilder.toString();
    }

    /**
     * Sorting paragraphs from buffer
     */
    protected List<Paragraph> sortParagraphs(List<Paragraph> writerBuffer){
        Collections.sort(writerBuffer);
        return writerBuffer;
    }

    /**
     * Get paragraph and calculate parameters
     *
     * @param paragraph - to get info for calculating
     */
    public void calcParagraph(Paragraph paragraph){
        String text = paragraph.getParagraph();

        paragraph.setParagraph(text);
        paragraph.setParagraphLength(countParagraphLength(text));
        paragraph.setAverageWordLength(countAverageWordsLengthInParagraph(text));
        paragraph.setWordsInParagraph(countWordsInParagraph(text));
        paragraph.setParagraphHash(Hashing.sha1().hashString(text, Charsets.UTF_8).toString());
        paragraph.setPunctuationSymbolsAmount(countPunctuationSymbolsInParagraph(text));
    }

    public int countParagraphLength(String paragraph){
        return paragraph.length();
    }

    public int countWordsInParagraph(String paragraph){
        String[] wordsInParagraph = paragraph.split(" ");

        return wordsInParagraph.length;
    }

    public int countPunctuationSymbolsInParagraph(String paragraph){
        int amountSymbol = 0;
        char[] text = paragraph.toCharArray();

        for (char ch : text){
            if (SYMBOLS_PUNCTUATION.contains(String.valueOf(ch))){
                amountSymbol++;
            }
        }

        return amountSymbol;
    }

    public static int countAverageWordsLengthInParagraph(String paragraph){
        List<Integer> wordsCount = new ArrayList<Integer>();
        String[] wordsInParagraph = paragraph.split(" ");
        int t = 0;

        for (String str : wordsInParagraph){
            wordsCount.add(str.length());
        }

        for (Integer aWordsCount : wordsCount) {
            t += aWordsCount;
        }

        return t / wordsCount.size();
    }
}
