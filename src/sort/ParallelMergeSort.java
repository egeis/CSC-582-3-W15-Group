package sort;

import java.util.Arrays;
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
        
        ParallelMergeSort<T> msa = new ParallelMergeSort(Arrays.copyOfRange(list, 0, list.length / 2 - 1), depth + 1); 
        ParallelMergeSort<T> msb = new ParallelMergeSort(Arrays.copyOfRange(list, list.length / 2, list.length - 1), depth + 1); 

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
    
    
}
