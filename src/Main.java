import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    private final static String SOURCE_STRING = "abc";
    private final static int TEXT_LENGTH = 100_000;
    private final static int TEXT_COUNT = 10_000;
    private final static int QUEUE_SIZE = 100;
    private static final List<ThreadData> threadData = new ArrayList<>();

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static int countLetter(String str, char letter) {
        return (int) str.chars().filter(x -> x == letter).count();
    }

    public static void initThreadData() {
        threadData.add(new ThreadData(new ArrayBlockingQueue<>(QUEUE_SIZE), 'a', 0));
        threadData.add(new ThreadData(new ArrayBlockingQueue<>(QUEUE_SIZE), 'b', 0));
        threadData.add(new ThreadData(new ArrayBlockingQueue<>(QUEUE_SIZE), 'c', 0));
    }

    public static void main(String[] args) {
        initThreadData();
        Thread generateTexts = new Thread(
                () -> {
                    for (int i = 0; i < TEXT_COUNT; i++) {
                        String curText = generateText(SOURCE_STRING, TEXT_LENGTH);
                        threadData.forEach(x -> {
                            try {
                                x.getQueue().put(curText);
                            } catch (InterruptedException e) {
                                System.out.println(e.getMessage());
                            }
                        });
                        if ((i + 1) % 5000 == 0) {
                            System.out.printf("Generated %d texts. Thread current status: %s.\n", i + 1, Thread.currentThread().getState());
                        }
                    }
                }
        );
        generateTexts.start();
        ThreadGroup searchMax = new ThreadGroup("searchMax");
        threadData.forEach(x ->
                new Thread(
                        searchMax,
                        () -> {
                            for (int i = 0; i < TEXT_COUNT; i++) {
                                try {
                                    String curText = x.getQueue().take();
                                    int curCount = countLetter(curText, x.getCriteria());
                                    if (x.getMaxCount() < curCount) {
                                        x.setMaxCount(curCount);
                                    }
                                } catch (InterruptedException e) {
                                    System.out.println(e.getMessage());
                                }
                                if ((i + 1) % 5000 == 0) {
                                    System.out.printf("Processed %d texts. Thread current status: %s.\n", i + 1, Thread.currentThread().getState());
                                }
                            }
                        }
                ).start()
        );
        try {
            Thread[] threads = new Thread[1];
            while (searchMax.activeCount() > 0) {
                searchMax.enumerate(threads, false);
                threads[0].join();
            }
        } catch (InterruptedException e) {
            return;
        }
        threadData.forEach(x -> System.out.printf("Max count \"%c\": %d\n", x.getCriteria(), x.getMaxCount()));
    }
}