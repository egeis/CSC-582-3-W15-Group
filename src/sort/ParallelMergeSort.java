/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sort;

import com.sun.glass.ui.Application;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author Richard Coan
 */
public class ParallelMergeSort<T> extends MergeSort<T> {

    private static ExecutorService executor;
    private int depth = 0;
    private int max_depth = 0;
      
    public ParallelMergeSort(List<T> list, Comparator<T> compare, int max_depth) {
        this(list, compare);
        this.max_depth = max_depth;
    }
    
    public ParallelMergeSort(List<T> list, Comparator<T> compare, int depth, int max_depth) {
        this(list, compare);
        this.depth = depth;
        this.max_depth = max_depth;
    }
    
    public ParallelMergeSort(List<T> list, Comparator<T> compare) {
        super(list, compare);
        if(executor == null) executor = Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        if(list.size() < 2) return;
        
        int threads = Runtime.getRuntime().availableProcessors() - 1;
        
        //Dont over do the number of threads, limit parallel to upper parts of recussion.
        if(depth < 2) {
            ParallelMergeSort<T> pmsa = new ParallelMergeSort( list.subList(0, list.size()/2), compare, (depth+1), max_depth );
            ParallelMergeSort<T> pmsb = new ParallelMergeSort( list.subList(list.size()/2, list.size()), compare, (depth+1), max_depth );

            Future<?> fa = executor.submit(pmsa);
            Future<?> fb = executor.submit(pmsb);

            try {
                fa.get();
                fb.get();
            } catch (ExecutionException | InterruptedException e) {
                System.out.println("[Error] An Exception took place while sorting.  Defaulting to manual run().");
                e.printStackTrace();
                pmsa.run();
                pmsb.run();
            }

            merge(pmsa.get(), pmsb.get());
        } else {
            if(depth >= max_depth && max_depth != 0) {
                //Do some other sort
            } else {
                ParallelMergeSort<T> pmsa = new ParallelMergeSort( list.subList(0, list.size()/2), compare, (depth+1), max_depth );
                ParallelMergeSort<T> pmsb = new ParallelMergeSort( list.subList(list.size()/2, list.size()), compare, (depth+1), max_depth );

                pmsa.run();
                pmsb.run();
                merge(pmsa.get(), pmsb.get());
            }
        }
    }
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {        
        long start, end;
        final int length = 1000000;
        List<Integer> test = new ArrayList();
        Random rand = new Random();
        
        Comparator<Integer> compare = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b){
               return a.compareTo(b);
            }
            
            @Override
            public boolean equals(Object a){
               return this == a;
            }
           };
        
        //Create the Random Array.
        for(int i = 0; i < length; i++) {
            test.add( (int) (rand.nextDouble() * 10) );
        }
        
        ParallelMergeSort pms = new ParallelMergeSort(test, compare);
        
        System.out.println("[Starting] Is Sorted? "+ pms.isSorted(test, compare));
        System.out.println("[Starting] Array Size:"+test.size());
        start = System.currentTimeMillis();
        pms.run();
        end = System.currentTimeMillis();
        System.out.println("[Completed] The algorithm took: " + (end - start) + " ms");
        System.out.println("[Completed] Is Sorted? "+ pms.isSorted(test, compare));
        System.out.println("[Completed] Array Size:"+test.size());
        
        executor.shutdown();
    }
}