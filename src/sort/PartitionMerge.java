/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sort;

import java.util.ArrayList;
import java.util.Arrays;
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
    public static ExecutorService executor;
    private List<FutureTask<Integer[]>> future = new ArrayList();
    private int threads;
    
    public PartitionMerge() {
        threads = Runtime.getRuntime().availableProcessors();
        if(executor == null) executor = Executors.newCachedThreadPool();
    }
    
    public Integer[] sort(Integer[] arr) {
        int partition_size = (int) Math.ceil(arr.length/threads);
        
        
        for(int i = 0; i < threads; i++) {
            FutureTask<Integer[]> ft = new FutureTask(new PartitionTask(arr, i*partition_size, Math.min(((i+1)*partition_size - 1),(arr.length - 1))));
            future.add(ft);
            executor.execute(ft);
        }
        
        while(future.size() > 1) {
            FutureTask<Integer[]> fa = future.remove(0);
            FutureTask<Integer[]> fb = future.remove(0);
            
            Integer[] a;
            Integer[] b;
            
            try {
                a = fa.get();
                b = fb.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
                        
            FutureTask<Integer[]> ft = new FutureTask(new MergeTask(fa,fb));
            future.add(ft);
            executor.execute(ft);
        }
        executor.shutdown();
        FutureTask<Integer[]> ft = future.remove(0);
                
        try {
            while(!ft.isDone()) {}
            return ft.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        } 
    }
    
    public static void main(String[] args) {
        long start, end;
        Integer[] test = new Integer[10000];
        Random rand = new Random();
                
        for(int i = 0; i < test.length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }

        System.out.println(Arrays.toString(test));
        
        start = System.currentTimeMillis();
        PartitionMerge pm= new PartitionMerge();
        test = pm.sort(test);
        end = System.currentTimeMillis();
        System.out.println("[DEBUG] The algorithm took: " + (end - start) + " ms");
        System.out.println(Arrays.toString(test));
    }
}