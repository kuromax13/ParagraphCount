package paragraph;

/**
 * Created by mrybalkin on 11.01.2017.
 *
 * Class to describe paragraph
 */
public class Paragraph implements Comparable<Paragraph> {
    private final int numberParagraph;
    private String paragraph;
    private String paragraphHash;
    private int paragraphLength;
    private int wordsInParagraph;
    private int averageWordLength;
    private int punctuationSymbolsAmount;

    public Paragraph(int numberParagraph, String paragraph) {
        this.numberParagraph = numberParagraph;
        this.paragraph = paragraph;
    }

    public int getNumberParagraph() {
        return numberParagraph;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
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

    @Override
    public String toString() {
        return  "paragraphNumber=" + numberParagraph +
                ", paragraphText='" + paragraph + '\'' +
                ", paragraphHash='" + paragraphHash + '\'' +
                ", paragraphLength=" + paragraphLength +
                ", wordsInParagraph=" + wordsInParagraph +
                ", averageWordLength=" + averageWordLength +
                ", punctuationSymbolsAmount=" + punctuationSymbolsAmount +
                '}';
    }

    @Override
    public int compareTo(Paragraph obj) {
        return Integer.compare(numberParagraph, obj.getNumberParagraph());
    }
}
