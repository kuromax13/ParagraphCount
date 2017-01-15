import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by Acer on 24.10.2016.
 */
public class Main {
    protected static String nameFile = "Text.txt";
    protected static String nameNewFile = "TextOut.txt";
    protected static int amountThreadsWorker = 2;
    protected static int amountDataForOperation = 2;

    public static void main(String[] args) throws IOException {
        Path file = FileSystems.getDefault().getPath("C:\\Users\\Acer\\Desktop", "Text.txt");
//        File outPutFile = new File("C:\\Users\\Acer\\Desktop\\TextOut.txt");
//        BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
//
//        paragraphProcessing = new ParagraphProcessing(reader.readLine());
//        Thread myThread = new Thread(paragraphProcessing);
//        myThread.start();
//
//        WriteToFile.writeToFile(outPutFile, file);

        new Thread(new Coordinator(file, nameNewFile, amountThreadsWorker, amountDataForOperation)).start();
    }
}
