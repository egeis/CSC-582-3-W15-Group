package sort;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author Richard Coan
 */
public class ParallelMergeSort<T extends Comparable<T>> extends MergeSort {
    private int depth;
    private static ExecutorService executor;
    
    public ParallelMergeSort(T[] list) {
        super(list);
        this.depth = depth;
        if(executor == null) executor = Executors.newCachedThreadPool();
    }
    
    public ParallelMergeSort(T[] list, int depth) {
        super(list);
        this.depth = depth;
    }
    
    @Override
    public void run() {
        if(list.length < 2) return; //Too Small.
        
        T[] a = (T[]) Arrays.copyOfRange(list, 0, (list.length/2));
        T[] b = (T[]) Arrays.copyOfRange(list, (list.length/2), list.length);
        
        ParallelMergeSort<T> msa = new ParallelMergeSort(a); 
        ParallelMergeSort<T> msb = new ParallelMergeSort(b); 

        if(depth < 2) {
            Future<?> fa = executor.submit(msa);
            Future<?> fb = executor.submit(msb);

            try {
                fa.get();
                fb.get();
            } catch (Exception e) {
                //Do Nothing
            }
        } else {
            msa.run();
            msb.run();
        }
        
        merge(msa.get(), msb.get());
    }
    
    public static void main(String[] args) {
        long start, end;
        Integer[] test = new Integer[1000000];
        Random rand = new Random();
        
        for(int i = 0; i < test.length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }
        
        ParallelMergeSort<Integer> pms = new ParallelMergeSort(test); 
        pms.show();
        start = System.currentTimeMillis();
        pms.run();
        end = System.currentTimeMillis();
        pms.show();
        System.out.println("The algorithm took: " + (end - start) + " ms");
        
        if (executor != null) executor.shutdown();
    }
}
