package processing;

import org.apache.log4j.Logger;
import paragraph.Paragraph;
import paragraph.ParagraphBufferReader;
import util.PropertiesHolder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Created by Acer on 11.01.2017.
 */
public class ParagraphReader implements Runnable {
    private Path fileToRead;
    static int paragraphsAmountToRead;
    ParagraphBufferReader<Paragraph> buffer;
    final Object syncReader;
     static final Logger logger = Logger.getLogger(ParagraphReader.class);
    static PropertiesHolder propertiesHolder = new PropertiesHolder();
    volatile int paragraphsCount;

    public ParagraphReader(Object syncReader, Path fileToRead, ParagraphBufferReader<Paragraph> buffer) {
        this.syncReader = syncReader;
        this.fileToRead = fileToRead;
        this.buffer = buffer;
    }

    public void run() {

        try{
                        BufferedReader reader = Files.newBufferedReader(fileToRead, StandardCharsets.UTF_8);
            paragraphsCount = Files.readAllLines(fileToRead, StandardCharsets.UTF_8).size();

            boolean indicator = true;
            Scanner scanner = new Scanner(fileToRead);
            StringBuilder entry = new StringBuilder();
            String fir;
//            for (int i = 1; i < paragraphsCount; i++){
            while (scanner.hasNextLine()) {
//            while ((fir = reader.readLine()) !=null) {
                if (buffer.getDataQueue().size() < propertiesHolder.getParagraphsNumberInBuffer()){

                    while(indicator){
//                        entry.append(reader.readLine());
//                        entry.append(fir);
                        entry.append(scanner.nextLine());
                        if (entry.indexOf("/r/n") < 0) {
                            indicator = false;
                        }
                    }

                    if(entry.length() > 0){
                        buffer.getDataQueue().add(new Paragraph(paragraphsAmountToRead++, entry.toString()));
                        entry.delete(0, entry.length());
                    }
                    indicator = true;

                    synchronized (syncReader){
                        syncReader.notify();
                    }

                }else {
                    synchronized (syncReader){
                        syncReader.wait();
                    }
                }
            }
            buffer.setFlagEndFile(true);
            logger.info(" Change parameter flagEndFile. FileUtil read all.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // notify all Worker threads
        synchronized (syncReader) {
            syncReader.notifyAll();
        }
        logger.info(" ********************Thread Reader ended work.");

    }
}
