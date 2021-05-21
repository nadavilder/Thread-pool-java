import java.io.*;
import java.io.File;

public class Copier implements Runnable {
    public static final int COPY_BUFFER_SIZE = 4000;
    private int id;
    private java.io.File destination;
    private SynchronizedQueue<java.io.File> resultsQueue;
    private SynchronizedQueue<String> milestonesQueue;
    private boolean isMilestones;


    public Copier(int id, java.io.File destination, SynchronizedQueue<java.io.File> resultsQueue, SynchronizedQueue<String> milestonesQueue, boolean isMilestones) {
        this.id = id;
        this.destination = destination;
        this.resultsQueue = resultsQueue;
        this.milestonesQueue = milestonesQueue;
        this.isMilestones = isMilestones;
    }

    public void run() {
        int length;
        resultsQueue.registerProducer();
        File file;
        byte[] buffer = new byte[COPY_BUFFER_SIZE];

        while (this.resultsQueue.getSize() > 0) {

            file = resultsQueue.dequeue();
            if (isMilestones) {
                milestonesQueue.enqueue("Copier from thread id " + id + ": file named " + file.getName() + " was copied”");
            }
            try {
                File newFile = new File(destination, file.getName());
                FileInputStream fileStream = new FileInputStream(file);
                FileOutputStream newFileStream = new FileOutputStream(newFile);
                while ((length = fileStream.read(buffer)) > 0) {
                    newFileStream.write(buffer, 0, length);
                }
                fileStream.close();
                newFileStream.close();
            } catch (IOException e) {
                System.out.println(e.toString());
            }
        }
        resultsQueue.unregisterProducer();
    }
}
