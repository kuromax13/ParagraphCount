package Paragraph;

/**
 * Created by Acer on 11.01.2017.
 */
public class Paragraph {

    protected final int numberParagraph;
    protected String paragraph;

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

    public int compareTo(Paragraph obj) {

        return Integer.compare(numberParagraph, obj.getNumberParagraph());
    }
}
