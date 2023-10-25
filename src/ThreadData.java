import java.util.concurrent.BlockingQueue;

public class ThreadData {
    private final BlockingQueue<String> queue;
    private final char criteria;
    private int maxCount;

    public ThreadData(BlockingQueue<String> queue, char criteria, int maxCount) {
        this.queue = queue;
        this.criteria = criteria;
        this.maxCount = maxCount;
    }

    public BlockingQueue<String> getQueue() {
        return queue;
    }

    public char getCriteria() {
        return criteria;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
}