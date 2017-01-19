import paragraph.Paragraph;
import paragraph.ParagraphBuffer;
import paragraph.ParagraphBufferReader;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import org.apache.log4j.Logger;
import util.PropertiesHolder;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
    PropertiesHolder propertiesHolder = new PropertiesHolder();
    final String SYMBOLS_PUNCTUATION = ".,:;!?()[]{}<>/|@#$%^&*-+=_~`\"";
    final char SPACE = ' ';
    final Object syncReader;
    final Object syncWriter;
    ParagraphBufferReader<Paragraph> bufferReader;
    ParagraphBuffer<Paragraph> bufferWriter;
    Paragraph paragraph;
    ParagraphBuffer<String> workerBuffer = new ParagraphBuffer<String>(new LinkedList<String>());
    public static final Logger logger = Logger.getLogger(ParagraphProcessing.class);



    public ParagraphProcessing(Object syncReader, Object syncWriter, ParagraphBuffer<Paragraph> bufferWriter, ParagraphBufferReader<Paragraph> bufferReader) {
        this.syncReader = syncReader;
        this.syncWriter = syncWriter;
        this.bufferWriter = bufferWriter;
        this.bufferReader = bufferReader;
    }

    public void run() {
        logger.info(" Started new thread Worker");

        while (!bufferReader.isEndOfFile() || !bufferReader.dataQueue.isEmpty()) {
            if (bufferReader.dataQueue.isEmpty()) {

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
                    if (bufferReader.dataQueue.isEmpty()){
                        break;
                    }else {
                        paragraph = bufferReader.dataQueue.remove();
                    }
                }

                String text = paragraph.getParagraph();

                workerBuffer.setParagraphText(text);
                workerBuffer.setParagraphLength(countParagraphLength(text));
                workerBuffer.setAverageWordLength(countAverageWordsLengthInParagraph(text));
                workerBuffer.setWordsInParagraph(countWordsInParagraph(text));
                workerBuffer.setParagraphHash(Hashing.sha1().hashString(text, Charsets.UTF_8).toString());
                workerBuffer.setPunctuationSymbolsAmount(countPunctuationSymbolsInParagraph(text));
                logger.info(" Save paragraph's data in the STSWriterBuffer");

                addParagraphIntoWriterBuffer();

                synchronized (syncWriter){
                    syncWriter.notify();
                }
                workerBuffer = cleanParametersInWorkerBuffer(workerBuffer);
            }
        }
        lastActionsWorkerThreads(bufferWriter);
        logger.info(" Thread Worker ended work.");

    }

    public synchronized void addParagraphIntoWriterBuffer() {

        bufferWriter.dataQueue.add(paragraph);

        bufferWriter.setParagraphText(workerBuffer.getParagraphText());
        bufferWriter.setParagraphLength(workerBuffer.getParagraphLength());
        bufferWriter.setAverageWordLength(workerBuffer.getAverageWordLength());
        bufferWriter.setWordsInParagraph(workerBuffer.getWordsInParagraph());
        bufferWriter.setParagraphHash(workerBuffer.getParagraphHash());
        bufferWriter.setPunctuationSymbolsAmount(workerBuffer.getPunctuationSymbolsAmount());
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

        for (int i = 0; i < wordsCount.size(); i++){
            t += wordsCount.get(i);
        }

        return t / wordsCount.size();
    }
    /**
     * The method is completing last action work thread
     *
     * @param writerBuffer       buffer for save result work
     */
    public synchronized void lastActionsWorkerThreads(ParagraphBuffer<Paragraph> writerBuffer){

        if (Main.amountThreadsWorker == 1){

            logger.info("  Change parameter flagAndFile in the writerBuffer.");
            writerBuffer.setFlagEndFile(true);

            synchronized (syncWriter){
                syncWriter.notify();
            }
        }
        Main.amountThreadsWorker--;
//        Coordinator.amountThreadsWorker--;

    }
    /**
     * The method is mount all parameters in the start value
     *
     * @param workerBuffer     buffer for clean
     * @return                 buffer after clean
     */
    public ParagraphBuffer<String> cleanParametersInWorkerBuffer(ParagraphBuffer<String> workerBuffer){

        workerBuffer.setParagraphHash("");
        workerBuffer.setPunctuationSymbolsAmount(0);
        workerBuffer.setWordsInParagraph(0);
        workerBuffer.setAverageWordLength(0);
        workerBuffer.setParagraphText("");
        workerBuffer.setParagraphLength(0);

        return workerBuffer;
    }
}
