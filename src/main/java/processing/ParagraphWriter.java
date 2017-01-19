package processing;

import org.apache.log4j.Logger;
import paragraph.Paragraph;
import paragraph.ParagraphBuffer;
import util.FileUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
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
    Path newFile1;
    static int paragraphCounter = 0;                            // counter for Paragraphs
    String informationForWrite = "";
    String isEmptyString = "";
    List<Paragraph> writerBuffer = new LinkedList<>();
    public static final Logger logger = Logger.getLogger(ParagraphWriter.class);


    public ParagraphWriter(Object syncWriter, ParagraphBuffer<Paragraph> buffer) {
        this.syncWriter = syncWriter;
        this.buffer = buffer;
    }

    public void run() {
        logger.info("Paragraph writer is started");

//        String newFileName = FileUtil.getNewFileName();
//        newFile = new File(newFileName);
        newFile1 = FileUtil.getFileToWrite();
        File newFile = new File(newFile1.toAbsolutePath().toString());
        try(FileWriter writer = new FileWriter(newFile)){

            while (!buffer.getDataQueue().isEmpty() || !buffer.isEndOfFile()) { //check if buffer not empty
                if (buffer.getDataQueue().isEmpty()) {

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
                    while (!buffer.getDataQueue().isEmpty() && counter < 2){
                        writerBuffer.add((Paragraph) buffer.getDataQueue().remove());
                        counter++;
                    }

                    if (!writerBuffer.isEmpty()){
//                        for (paragraph pr : writerBuffer){
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
            writer.write(System.lineSeparator() + " FileUtil has: " + System.lineSeparator() + (paragraphCounter - 1) + " paragraphs, " + buffer.toString());

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info(" ********************Thread Writer ended work.");

    }
    /**
     * The method is taking paragraphs and their information from writerBuffer, and writing its in the string
     *
     * @param writerBuffer  collection with paragraph's objects
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
     * @param writerBuffer  collection paragraph's objects
     * @return              sorted collection paragraph's objects
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
