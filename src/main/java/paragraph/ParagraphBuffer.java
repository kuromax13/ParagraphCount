package paragraph;

import java.util.Queue;

/**
 * Created by mrybalkin on 11.01.2017.
 *
 * Class to describe paragraph to write into file after proccessing
 */

public class ParagraphBuffer<T> extends ParagraphBufferReader<T> {
    private int paragraphNumber;
    private String paragraphText;
    private String paragraphHash;
    private int paragraphLength;
    private int wordsInParagraph;
    private int averageWordLength;
    private int punctuationSymbolsAmount;

    public ParagraphBuffer(Queue<T> dataQueue) {
        super(dataQueue);
    }

    public String getParagraphText() {
        return paragraphText;
    }

    public void setParagraphText(String paragraphText) {
        this.paragraphText = paragraphText;
    }

    public String getParagraphHash() {
        return paragraphHash;
    }

    public void setParagraphHash(String paragraphHash) {
        this.paragraphHash = paragraphHash;
    }

    public int getParagraphLength() {
        return paragraphLength;
    }

    public void setParagraphLength(int paragraphLength) {
        this.paragraphLength = paragraphLength;
    }

    public int getWordsInParagraph() {
        return wordsInParagraph;
    }

    public void setWordsInParagraph(int wordsInParagraph) {
        this.wordsInParagraph = wordsInParagraph;
    }

    public int getAverageWordLength() {
        return averageWordLength;
    }

    public void setAverageWordLength(int averageWordLength) {
        this.averageWordLength = averageWordLength;
    }

    public int getPunctuationSymbolsAmount() {
        return punctuationSymbolsAmount;
    }

    public void setPunctuationSymbolsAmount(int punctuationSymbolsAmount) {
        this.punctuationSymbolsAmount = punctuationSymbolsAmount;
    }

    public int getParagraphNumber() {
        return paragraphNumber;
    }

    public void setParagraphNumber(int paragraphNumber) {
        this.paragraphNumber = paragraphNumber;
    }

    @Override
    public String toString() {
        return  "paragraphNumber=" + paragraphNumber +
                ", paragraphText='" + paragraphText + '\'' +
                ", paragraphHash='" + paragraphHash + '\'' +
                ", paragraphLength=" + paragraphLength +
                ", wordsInParagraph=" + wordsInParagraph +
                ", averageWordLength=" + averageWordLength +
                ", punctuationSymbolsAmount=" + punctuationSymbolsAmount +
                '}';
    }
    public int compareTo(ParagraphBuffer obj) {
        return Integer.compare(paragraphNumber, obj.getParagraphNumber());
    }

}
