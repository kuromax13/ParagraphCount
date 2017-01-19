package paragraph;

import java.util.Queue;

/**
 * Created by mrybalkin on 13.01.2017.
 *
 * Class describes buffer to start process paragraph after reading from file and writing into file
 */
public class ParagraphBufferReader<T> {
    private volatile boolean isEndOfFile = false;
    private Queue<T> dataQueue;

    public ParagraphBufferReader(Queue<T> dataQueue) {
        this.dataQueue = dataQueue;
    }

    public boolean isEndOfFile() {
        return isEndOfFile;
    }

    public void setFlagEndFile(boolean isEndOfFile) {
        this.isEndOfFile = isEndOfFile;
    }

    public Queue<T> getDataQueue() {
        return dataQueue;
    }
}
