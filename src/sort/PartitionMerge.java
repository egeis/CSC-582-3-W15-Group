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
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Richard
 */
public class PartitionMerge {
    public ExecutorService executor;
    public CompletionService pool;
    private final int threads;
    private final int partition_size;
    private List<FutureTask<Integer[]>> flist;
    
    private final Integer[] arr;
    
    /**
     * 
     */
    public PartitionMerge(Integer[] arr) {
        threads = Runtime.getRuntime().availableProcessors();
        executor = Executors.newCachedThreadPool();
        flist = new ArrayList();
        //executor = Executors.newFixedThreadPool(1);
        //pool = new ExecutorCompletionService(executor);
        
        this.arr = arr;
        
        partition_size = (int) Math.ceil(((1.0 * arr.length)/threads));
    }
    
    /**
     * Initialize the MergeSort
     * @return Integer[]
     */
    public Integer[] sort() {        
        //Create Parts
        for(int i = 0; i < threads; i++) {
            //System.out.println("[Partition]"+i*partition_size+" "+Math.min(((i+1)*partition_size - 1),(arr.length - 1)));
            Integer[] part = Arrays.copyOfRange(arr, i*partition_size, Math.min(((i+1)*partition_size - 1),(arr.length - 1)));
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
                //results = (Integer[]) pool.take().get();
                //if(run == 1) break;
                //b = (Integer[]) pool.take().get();
                System.out.println("[Merge Parts]" + a.length+" | "+b.length);
                //fb = pool.poll(threads, TimeUnit.DAYS);
                //if(fb == null) break;  //All Done!
                //b = fb.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            } finally {
                //pool.submit(new MergeTask(a,b));
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
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {
        long start, end;
        Integer[] test = new Integer[10000];
        Random rand = new Random();
                
        for(int i = 0; i < test.length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }

        System.out.println(Arrays.toString(test));
        PartitionMerge pm= new PartitionMerge(test);
        
        start = System.currentTimeMillis();
        test = pm.sort();
        end = System.currentTimeMillis();
        
        System.out.println("[DEBUG] The algorithm took: " + (end - start) + " ms");
        System.out.println("[Debug] Array Length "+test.length);
        System.out.println(Arrays.toString(test));
    }
}