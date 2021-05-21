import java.io.File;


public class DiskSearcher {
    public static final int DIRECTORY_QUEUE_CAPACITY = 50;
    public static final int RESULTS_QUEUE_CAPACITY = 50;
    public static final int MSQ_QUEUE_CAPACITY = DIRECTORY_QUEUE_CAPACITY * RESULTS_QUEUE_CAPACITY;


    public static void main(java.lang.String[] args) {
        long startTime = System.currentTimeMillis();
        boolean flag = Boolean.parseBoolean(args[0]);
        String extension = args[1];
        File root = new File(args[2]);
        File destDir = new File(args[3]);
        int searches = Integer.parseInt(args[4]);
        int copiers = Integer.parseInt(args[5]);

        SynchronizedQueue<File> directoryQueue = new SynchronizedQueue<File>(DIRECTORY_QUEUE_CAPACITY);
        SynchronizedQueue<File> resultsQueue = new SynchronizedQueue<File>(RESULTS_QUEUE_CAPACITY);
        SynchronizedQueue<String> milestonesQueue = new SynchronizedQueue<String>(MSQ_QUEUE_CAPACITY);

        if (flag) {
            milestonesQueue.enqueue("General, program has started the search");
        }

        if (!root.isDirectory()) {
            System.out.println("Illegal root");
            return;
        }

        if (!destDir.isDirectory()) {
            System.out.println("Illegal destination");
            return;
        }

        if (searches <= 0 || copiers <= 0) {
            System.out.println("invalid number of searchArrayThread or copyThread");
            return;
        }


        Scouter scout = new Scouter(0, directoryQueue, root, milestonesQueue, flag);
        Thread scouter = new Thread(scout);
        try {
            scouter.start();
        } catch (RuntimeException e) {
            Thread.currentThread().interrupt();
        }


        try {
            scouter.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        Thread[] searchArrayThread = new Thread[searches];
        for (int i = 1; i <= searches; i++) {
            Searcher searcher = new Searcher(i, extension, directoryQueue, resultsQueue, milestonesQueue, flag);
            searchArrayThread[i - 1] = new Thread(searcher);
            searchArrayThread[i - 1].start();
        }

        Thread[] copyArrayThread = new Thread[copiers];
        for (int i = searches + 1; i <= searches + copiers; i++) {
            Copier copier = new Copier(i, destDir, resultsQueue, milestonesQueue, flag);
            copyArrayThread[i - searches - 1] = new Thread(copier);
            copyArrayThread[i - searches - 1].start();
        }

        for (int i = 0; i < copiers; i++) {
            try {
                if (copyArrayThread[i] != null)
                    copyArrayThread[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        for (int i = 0; i < searches; i++) {
            try {
                if (searchArrayThread[i] != null)
                    searchArrayThread[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }


        int msqSize = milestonesQueue.getSize();
        for (int i = 0; i < msqSize; i++) {
            System.out.println(milestonesQueue.dequeue());
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        //System.out.println(estimatedTime);
        //System.out.println(Runtime.getRuntime().availableProcessors());

    }


}


