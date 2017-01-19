package processing;

import paragraph.Paragraph;
import paragraph.ParagraphBufferReader;
import org.apache.log4j.Logger;

import java.io.IOException;
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
    public static final Logger logger = Logger.getLogger(ParagraphReader.class);


    public ParagraphReader(Object syncReader, Path fileToRead, ParagraphBufferReader<Paragraph> buffer) {
        this.syncReader = syncReader;
        this.fileToRead = fileToRead;
        this.buffer = buffer;
    }

    public void run() {

        try{
//            BufferedReader reader = Files.newBufferedReader(fileToRead, StandardCharsets.UTF_8);
            Scanner scanner = new Scanner(fileToRead);
//            int paragraphsCount = Files.readAllLines(fileToRead, StandardCharsets.UTF_8).size();
            boolean indicator = true;
            StringBuilder entry = new StringBuilder();

//            for (int i = 0; i < paragraphsCount; i++){
            while (scanner.hasNextLine()) {
                if (buffer.getDataQueue().size() < 2){

                    while(indicator){
//                        entry.append(reader.readLine());
                        entry.append(scanner.nextLine());
//                        System.out.println(scanner.nextLine());
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
