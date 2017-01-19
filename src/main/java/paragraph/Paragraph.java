package paragraph;

/**
 * Created by mrybalkin on 11.01.2017.
 *
 * Class to describe paragraph to read from file
 */
public class Paragraph {
    private final int numberParagraph;
    private String paragraph;

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
