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
        
        FutureTask<T[]> fa = new FutureTask<T[]>(new MergeSort<T>(Arrays.copyOfRange(list, 0, (list.length/2))));
        FutureTask<T[]> fb = new FutureTask<T[]>(new MergeSort<T>(Arrays.copyOfRange(list, (list.length/2), list.length)));            
        
        executor.execute(fa);
        executor.execute(fb);
        
        T[] a = fa.get();
        T[] b = fb.get();
            
        int i = 0, j = 0, k = 0;
        /*
        System.out.println("Merge: List.length="+list.length+", a.length"+a.length+",b.length="+b.length);
        System.out.println(Arrays.toString(list));
        System.out.println(Arrays.toString(a));
        System.out.println(Arrays.toString(b));
        */
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

    /**
     * 
     */
    public void print() {
        for(int i = 0; i < list.length; i++) {
            System.out.print(list[i].toString() +((i < list.length - 1)?",":""));
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        
        long start, end;
        Integer[] test = new Integer[10000000];
        Random rand = new Random();
        
        for(int i = 0; i < test.length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }
         System.out.println(Arrays.toString(test));
        
        FutureTask<Integer[]> fa = new FutureTask<Integer[]>(new ParallelMergeSort(test));
        start = System.currentTimeMillis();
        executor.execute(fa);
        
        try {
            test = fa.get();
        } catch(Exception e) {
            
        }
        end = System.currentTimeMillis();
        System.out.println("The algorithm took: " + (end - start) + " ms");
        
        if (executor != null) executor.shutdown();
        System.out.println(Arrays.toString(test));
    }
}
