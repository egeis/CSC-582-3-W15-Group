/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 *
 * @author Richard
 */
public class PartitionMerge {
    public ExecutorService executor;
    private final int threads;
    private final int partition_size;
    private List<FutureTask<Integer[]>> flist;
    
    private final Integer[] arr;
    
    /**
     * 
     * @param arr Integer[]
     */
    public PartitionMerge(Integer[] arr) {
        threads = Runtime.getRuntime().availableProcessors();
        executor = Executors.newCachedThreadPool();
        //executor = Executors.newFixedThreadPool(threads);
        flist = new ArrayList();
        
        this.arr = arr;
        partition_size = (int) Math.ceil(((1.0 * arr.length)/threads));
    }
    
    /**
     * Initialize the MergeSort
     * @return Integer[]
     */
    public Integer[] sort() {        
        
        //Create Parts
        int size = arr.length;
        
        for(int i = 0; i < threads; i++) { 
            Integer[] part = new Integer[ Math.min(partition_size,size) ]; 
            System.arraycopy(arr, i*partition_size, part, 0, Math.min(partition_size, size));
            size -= partition_size;
            FutureTask ft = new FutureTask(new PartitionTask(part));
            executor.submit(ft);
            flist.add(ft);
        }

        //Merge Parts
        while(flist.size() > 1) {            
            Integer[] a = null;
            Integer[] b = null;
            
            try {
                a = flist.remove(0).get();
                b = flist.remove(0).get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                FutureTask ft = new FutureTask(new MergeTask(a,b));
                executor.submit(ft);
                flist.add(ft);
            }
        }
        
        Integer[] results = null;
        try {
            results = flist.remove(0).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        
        executor.shutdown();
        return results;
    }
    
    private static boolean isSorted(Integer[] a) {
        for (int i = 1; i < a.length; i++)
            if (a[i].compareTo(a[i-1]) < 0) return false;
        return true;
    }
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {
        long start, end;
        Integer[] test = new Integer[100000];
        Random rand = new Random();
                
        for(int i = 0; i < test.length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }

        //System.out.println(Arrays.toString(test));
        PartitionMerge pm= new PartitionMerge(test);
        
        start = System.currentTimeMillis();
        test = pm.sort();
        end = System.currentTimeMillis();
        
        //System.out.println(Arrays.toString(test));
        System.out.println("[Results] The algorithm took: " + (end - start) + " ms");
        System.out.println("[Results] Array Length "+test.length);
        System.out.println("[Results] is sorted? "+isSorted(test));
    }
}