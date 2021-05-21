
import java.io.File;


public class Scouter implements Runnable {

    private SynchronizedQueue<File> directoryQueue;
    private File root;
    private int id;
    private SynchronizedQueue<String> milestonesQueue;
    private boolean isMilestones;


    public Scouter(int id, SynchronizedQueue<File> directoryQueue, File root, SynchronizedQueue<String> milestonesQueue, boolean isMilestones) {
        this.directoryQueue = directoryQueue;
        this.root = root;
        this.id = id;
        this.milestonesQueue = milestonesQueue;
        this.isMilestones = isMilestones;

    }

    public void run() {
        if (root.isDirectory()) {
            directoryQueue.registerProducer();
            directoryQueue.enqueue(root);
            try {
                addDir(root);
            } catch (IllegalArgumentException e) {
            }
            directoryQueue.unregisterProducer();

        } else throw new IllegalArgumentException("The Root is Illegal");

    }

    private void addDir(File path) {
        File[] files;
        if (path.isDirectory()) {
            files = path.listFiles();
            for (File file : files) {
                if (isMilestones) {
                    milestonesQueue.enqueue("Scouter on thread id " + id + ": file named " + file.getName() + " was found”");
                }
                if (file.isDirectory()) {
                    directoryQueue.enqueue(file);
                    addDir(file);
                }
            }
        } else return;
    }
}