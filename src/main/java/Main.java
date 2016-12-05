import sun.nio.cs.ISO_8859_2;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Acer on 24.10.2016.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        Path file = FileSystems.getDefault().getPath("C:\\Users\\Acer\\Desktop", "Text.txt");
//        BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);


        File outPutFile = new File("C:\\Users\\Acer\\Desktop\\TextOut.txt");
        wrtieToFile(outPutFile, readFile(file));
    }

    private static void wrtieToFile(File outPutFile, List<String> readedFile) throws IOException {
        FileWriter writer = new FileWriter(outPutFile, false);
        BufferedWriter bfWrite = new BufferedWriter(writer);
        for (String gt : readedFile){
            bfWrite.write(gt+ "  " + ParagraphProcessing.countAverageWordsLengthInParagraph(gt) + "\n");
            System.out.println(gt + " " + gt.length());

        }
        writer.flush();
        bfWrite.flush();

    }

    private static List<String> readFile(Path file) throws IOException {
        //        OutputStream osWrite = new FileOutputStream("C:\\Users\\Acer\\Desktop\\TextOut.txt");
//        long i = str.count();
        return Files.readAllLines(file, StandardCharsets.UTF_8);
    }
}
