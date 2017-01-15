import Paragraph.Paragraph;
import Paragraph.ParagraphBuffer;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Acer on 11.01.2017.
 */
public class ParagraphWriter implements Runnable {
    final Object syncWriter;
    ParagraphBuffer<Paragraph> buffer;
    File newFile;
    String newFileName;
    static int paragraphCounter = 1;                            // counter for Paragraphs
    String informationForWrite = "";
    String isEmptyString = "";
    List<Paragraph> writerBuffer = new LinkedList<>();
    public static final Logger logger = Logger.getLogger(ParagraphWriter.class);


    public ParagraphWriter(Object syncWriter, ParagraphBuffer<Paragraph> buffer, String newFileName) {
        this.syncWriter = syncWriter;
        this.buffer = buffer;
        this.newFileName = newFileName;
    }

    public void run() {
        logger.info("********************* Started thread STSFileWriter");

        newFile = new File(newFileName);
        try(FileWriter writer = new FileWriter(newFileName)){

            while (!buffer.dataQueue.isEmpty() || !buffer.isEndOfFile()) { //check if buffer not empty
                if (buffer.dataQueue.isEmpty()) {

                    synchronized (syncWriter) {
                        try {
                            syncWriter.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }else{
                    //add read paragraphs to the list
                    int counter = 0;
                    while (!buffer.dataQueue.isEmpty() && counter < 2){
                        writerBuffer.add((Paragraph) buffer.dataQueue.remove());
                        counter++;
                    }

                    if (!writerBuffer.isEmpty()){
//                        for (Paragraph pr : writerBuffer){
//                            writer.write(pr.toString());
//                        }

                        writerBuffer = sortedInformationForWrite(writerBuffer);

                        informationForWrite = getInformationForWrite(writerBuffer);
                        if (!informationForWrite.equals(isEmptyString)){

                            writer.write(informationForWrite);
                            logger.info(" Wrote paragraph and his data. Amount wrote paragraphs  = " + (paragraphCounter - 1));
                            informationForWrite = isEmptyString;
                        }

                    }
                }
            }
            logger.info(" Wrote data about all paragraphs.");
            writer.write(System.lineSeparator() + " File has: " + System.lineSeparator() + (paragraphCounter - 1) + " paragraphs, " + buffer.toString());

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info(" ********************Thread Writer ended work.");

    }
    /**
     * The method is taking paragraphs and their information from writerBuffer, and writing its in the string
     *
     * @param writerBuffer  collection with Paragraph's objects
     * @return              paragraphs with their information
     */
    protected String getInformationForWrite(List<Paragraph> writerBuffer){

        StringBuilder stringBuilder = new StringBuilder();
        Iterator<Paragraph> paragraphIterator = writerBuffer.iterator();
        while (paragraphIterator.hasNext()){
            Paragraph paragraph = paragraphIterator.next();
            if (paragraph.getNumberParagraph() == paragraphCounter){
                stringBuilder.append(paragraph.getParagraph());
                paragraphIterator.remove();
                paragraphCounter++;
            }else break;
        }

        return stringBuilder.toString();
    }

    /**
     * The method is sorting objects in the writerBuffer
     *
     * @param writerBuffer  collection Paragraph's objects
     * @return              sorted collection Paragraph's objects
     */
    protected List<Paragraph> sortedInformationForWrite(List<Paragraph> writerBuffer){

        Paragraph[] arrayParagraphs = writerBuffer.toArray(new Paragraph[writerBuffer.size()]);

        for (int i = 0; i < arrayParagraphs.length; i++){
            Paragraph minValueCounter = arrayParagraphs[i];
            int index = i;
            for (int j = i+1; j < arrayParagraphs.length; j++){
                if (minValueCounter.compareTo(arrayParagraphs[j]) > 0){
                    minValueCounter = arrayParagraphs[j];
                    index = j;
                }
            }
            if (index != i){
                System.arraycopy(arrayParagraphs, i, arrayParagraphs, i+1, index-i);
                arrayParagraphs[i] = minValueCounter;
            }
        }
        writerBuffer.clear();
        Collections.addAll(writerBuffer, arrayParagraphs);

        return writerBuffer;
    }
}
