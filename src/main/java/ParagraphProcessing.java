import java.util.ArrayList;
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
public class ParagraphProcessing {
    private static List <Integer> wordsCount;

    public int countParagraphLength(String paragraph){
        return paragraph.length();
    }

    public int countWordsInParagraph(String paragraph){
        String[] wordsInParagraph = paragraph.split(" ");

        return wordsInParagraph.length;
    }

    public static int countAverageWordsLengthInParagraph(String paragraph){
        wordsCount = new ArrayList<Integer>();
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

}
