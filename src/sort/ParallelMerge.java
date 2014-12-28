/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sort;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 *
 * @author Richard
 */
public class ParallelMerge<T extends Comparable<T>> implements Callable<T[]> {

    protected T[] values;
    protected List<FutureTask<T[]>> list = new ArrayList<FutureTask<T[]>>();
    public static ExecutorService executor;
    
    public ParallelMerge(T[] values) {
        this.values = values;
        if(executor == null) executor = Executors.newCachedThreadPool();
        
        for(int i = 0; i < values.length; i++) {
            FutureTask<T[]> ft = new FutureTask(new Merge(values[i]));
            list.add(ft);
            executor.execute(ft);
        }
    }
    
    public T[] call() throws Exception {
        System.out.println("[DEBUG] Starting with " + list.size() + " items.");
        while(list.size() > 1) {
            FutureTask<T[]> fa = list.remove(0);
            FutureTask<T[]> fb = list.remove(0);


            FutureTask<T[]> ft = new FutureTask(new Merge(fa,fb));
            list.add(ft);
            executor.execute(ft);
            System.out.println("[DEBUG] Looping with " + list.size() + " items.");
        }
        
        FutureTask<T[]> ft = list.remove(0);
        return ft.get();
    }
    
    public static void main(String[] args) {
        
        long start, end;
        Integer[] test = new Integer[4];
        Random rand = new Random();
        
        for(int i = 0; i < test.length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }
        
        System.out.println(Arrays.toString(test));
        
        FutureTask<Integer[]> fa = new FutureTask<Integer[]>(new ParallelMerge(test));
        start = System.currentTimeMillis();
        executor.execute(fa);
        
        try {
            test = fa.get();
        } catch(Exception e) {
            
        }
        executor.shutdown();
        
        end = System.currentTimeMillis();
        System.out.println("[DEBUG] The algorithm took: " + (end - start) + " ms");
        System.out.println(Arrays.toString(test));
        
    }
}