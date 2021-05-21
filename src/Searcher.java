import java.io.File;

public class Searcher implements Runnable {
    private int id;
    private java.lang.String extension;
    private SynchronizedQueue<java.io.File> directoryQueue;
    private SynchronizedQueue<java.io.File> resultsQueue;
    private SynchronizedQueue<String> milestonesQueue;
    private boolean isMilestones;


    public Searcher(int id, java.lang.String extension, SynchronizedQueue<java.io.File> directoryQueue, SynchronizedQueue<java.io.File> resultsQueue, SynchronizedQueue<String> milestonesQueue, boolean isMilestones) {
        this.id = id;
        this.extension = extension;
        this.directoryQueue = directoryQueue;
        this.resultsQueue = resultsQueue;
        this.milestonesQueue = milestonesQueue;
        this.isMilestones = isMilestones;
    }

    public void run() {
        resultsQueue.registerProducer();
        File[] files;
        for (int i = 0; i < directoryQueue.getSize(); i++) {
            File dir = directoryQueue.dequeue();
            files = dir.listFiles();
            for (File file : files) {
                if (isMilestones) {
                    milestonesQueue.enqueue("Searcher on thread id " + id + ": file named " + file.getName() + " was found”");
                }
                if (file.getName().substring(file.getName().lastIndexOf('.') + 1).equals(extension)) {
                    resultsQueue.enqueue(file);

                }
            }
        }
        resultsQueue.unregisterProducer();
    }
}
