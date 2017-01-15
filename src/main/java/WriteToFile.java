import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by Acer on 24.12.2016.
 */
public class WriteToFile implements Runnable {

    protected static void writeToFile(File outPutFile, Path file) throws IOException {
        FileWriter writer = new FileWriter(outPutFile, false);
        BufferedWriter bfWrite = new BufferedWriter(writer);
        int k = Files.readAllLines(file, StandardCharsets.UTF_8).size();
        BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);

        for (int i = 0; i < k; i++){
            String s = reader.readLine();
            bfWrite.write(s + "  " + ParagraphProcessing.countAverageWordsLengthInParagraph(s) + "\n");
            System.out.println(s + " " + s.length());

        }
        writer.flush();
        bfWrite.flush();

    }

    private static List<String> readFile(Path file) throws IOException {
        //        OutputStream osWrite = new FileOutputStream("C:\\Users\\Acer\\Desktop\\TextOut.txt");
//        long i = str.count();
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }

    public void run() {

    }
}
