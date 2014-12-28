package sort;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 *
 * @author Richard Coan
 * @param <T>
 */
public class ParallelMergeSort<T extends Comparable<T>> implements Callable<T[]> {
        
    protected T[] list;

    public static ExecutorService executor;
    
    public ParallelMergeSort(T[] list) 
    {
        this.list = list;
        if(executor == null) executor = Executors.newCachedThreadPool();
    }

    @Override
    public T[] call() throws Exception {
        if(list.length <= 1) return list;
                
        
        /**
        for (int n = 1; n < N; n = n+n) {
            for (int i = 0; i < N-n; i += n+n) {
                int lo = i;
                int m  = i+n-1;
                int hi = Math.min(i+n+n-1, N-1);
                merge(a, aux, lo, m, hi);
            }
        }**/
        
        
        /**FutureTask<T[]> fa = new FutureTask<T[]>(new ParallelMergeSort<T>(Arrays.copyOfRange(list, 0, (list.length/2))));
        FutureTask<T[]> fb = new FutureTask<T[]>(new ParallelMergeSort<T>(Arrays.copyOfRange(list, (list.length/2), list.length)));            
        
        executor.execute(fa);
        executor.execute(fb);
        
        T[] a = fa.get();
        T[] b = fb.get();
        
        return merge(a,b);**/
    }
     
    private T[] merge(FutureTask<T[]> fa, FutureTask<T[]>fb) {
        int i = 0, j = 0, k = 0;
        
        T[] a = fa.get();
        T[] b = fb.get();

        while(i < a.length && j < b.length) {
            if(b[j].compareTo(a[i]) < 0) {
                list[k] = b[j];
                j++;
            } else {
                list[k] = a[i];
                i++;
            }

            k++;
        }

        while(i < a.length) {
            list[k] = a[i];
            k++;
            i++;
        }

        while(j < b.length) {
            list[k] = b[j];
            k++;
            j++;
        }
        
        return list;
    }
        
    public static void main(String[] args) {
        
        long start, end;
        Integer[] test = new Integer[10000];
        Random rand = new Random();
        
        for(int i = 0; i < test.length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }
        
        //System.out.println(Arrays.toString(test));
        
        FutureTask<Integer[]> fa = new FutureTask<Integer[]>(new ParallelMergeSort(test));
        start = System.currentTimeMillis();
        executor.execute(fa);
        
        try {
            test = fa.get();
        } catch(Exception e) {
            
        }
        executor.shutdown();
        
        end = System.currentTimeMillis();
        System.out.println("The algorithm took: " + (end - start) + " ms");
        System.out.println(Arrays.toString(test));
    }
}