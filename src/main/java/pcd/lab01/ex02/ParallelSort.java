package pcd.lab01.ex02;
import java.util.*;

public class ParallelSort {
    static final int VECTOR_SIZE = 400_000_000;

    public static void main(String[] args) throws InterruptedException {
        log("Generating array...");
        var v = genArray(VECTOR_SIZE);
        log("Array generated.");

        // Sequential sorting
        log("Sorting sequentially...");
        long t0 = System.nanoTime();
        Arrays.sort(v.clone());
        long t1 = System.nanoTime();
        long sequentialTime = (t1 - t0) / 1_000_000;
        log("Done. Time elapsed: " + sequentialTime + " ms");

        // Parallel sorting
        int cores = Runtime.getRuntime().availableProcessors();
        log("Sorting in parallel using " + cores + " threads...");
        int[] vParallel = v.clone();
        long t2 = System.nanoTime();
        parallelSort(vParallel, cores);
        long t3 = System.nanoTime();
        long parallelTime = (t3 - t2) / 1_000_000;
        log("Done. Time elapsed: " + parallelTime + " ms");
        log("Speedup: " + (double) sequentialTime / parallelTime + "x");
    }

    private static int[] genArray(int n) {
        Random gen = new Random(System.currentTimeMillis());
        int[] v = new int[n];
        for (int i = 0; i < v.length; i++) {
            v[i] = gen.nextInt();
        }
        return v;
    }

    private static void parallelSort(int[] v, int threads) throws InterruptedException {
        int chunkSize = v.length / threads;
        Thread[] threadPool = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            final int start = i * chunkSize;
            final int end = (i == threads - 1) ? v.length : (i + 1) * chunkSize;
            threadPool[i] = new Thread(() -> Arrays.sort(v, start, end));
            threadPool[i].start();
        }

        for (Thread t : threadPool) {
            t.join();
        }

        mergeSortedArrays(v, chunkSize, threads);
    }

    private static void mergeSortedArrays(int[] v, int chunkSize, int threads) {
        int[] temp = v.clone();
        int[] indices = new int[threads];
        int[] starts = new int[threads];
        for (int i = 0; i < threads; i++) {
            starts[i] = i * chunkSize;
        }

        for (int i = 0; i < v.length; i++) {
            int minIndex = -1;
            for (int j = 0; j < threads; j++) {
                int idx = starts[j] + indices[j];
                if (idx < ((j == threads - 1) ? v.length : starts[j] + chunkSize)) {
                    if (minIndex == -1 || temp[idx] < temp[starts[minIndex] + indices[minIndex]]) {
                        minIndex = j;
                    }
                }
            }
            v[i] = temp[starts[minIndex] + indices[minIndex]];
            indices[minIndex]++;
        }
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}
