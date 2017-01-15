package Paragraph;

import java.util.Queue;

/**
 * Created by Acer on 13.01.2017.
 */
public class ParagraphBufferReader<T> {
    private volatile boolean isEndOfFile = false;   // flag end file
    public Queue<T> dataQueue;                      // queue for save paragraphs

    /**
     * Constructor for object STSBuffer
     *
     * @param dataQueue   queue for save paragraphs
     */
    public ParagraphBufferReader(Queue<T> dataQueue) {
        this.dataQueue = dataQueue;
    }

    public boolean isEndOfFile() {
        return isEndOfFile;
    }

    public void setFlagEndFile(boolean isEndOfFile) {
        this.isEndOfFile = isEndOfFile;
    }
}
