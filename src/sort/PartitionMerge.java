/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sort;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author Richard
 */
public class PartitionMerge {
    public ExecutorService executor;
    public CompletionService pool;
    private final int threads;
    
    public PartitionMerge() {
        threads = Runtime.getRuntime().availableProcessors();
        executor = Executors.newCachedThreadPool();
        pool = new ExecutorCompletionService(executor);
    }
    
    public Integer[] sort(Integer[] arr) {
        int partition_size = (int) Math.ceil(arr.length/threads);
        
        for(int i = 0; i < threads; i++) {
            pool.submit(new PartitionTask(arr, i*partition_size, Math.min(((i+1)*partition_size - 1),(arr.length - 1))));
        }
        
        //Initalized to null Suppresses an Warrning.
        Integer[] results = null;
        Future<Integer[]> fb = null; 
        
        while(true) { 
            Integer[] b = null;
           
            try {
                results = (Integer[]) pool.take().get();
                fb = pool.poll();
                if(fb == null) break;  //All Done!
                b = fb.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                pool.submit(new MergeTask(results,b));
            }
        }
        
        if (executor != null) { executor.shutdown(); }
        return results;
    }
    
    public static void main(String[] args) {
        long start, end;
        Integer[] test = new Integer[10000];
        Random rand = new Random();
                
        for(int i = 0; i < test.length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }

        System.out.println(Arrays.toString(test));
        PartitionMerge pm= new PartitionMerge();
        
        start = System.currentTimeMillis();
        test = pm.sort(test);
        end = System.currentTimeMillis();
        
        System.out.println("[DEBUG] The algorithm took: " + (end - start) + " ms");
        System.out.println(Arrays.toString(test));
    }
}