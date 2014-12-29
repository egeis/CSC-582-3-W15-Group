/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sort;

import com.sun.glass.ui.Application;
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
public class ParallelMerge {
    protected Integer[] values;
    protected List<FutureTask<Integer[]>> list = new ArrayList();
    public static ExecutorService executor;
    
    public ParallelMerge() {
        if(executor == null) executor = Executors.newCachedThreadPool();
    }
    
    public Integer[] merge(Integer[] values) {
        this.values = values;
        
        for(int i = 0; i < values.length; i=i+2) {            
            FutureTask<Integer[]> ft = new FutureTask(new MergeTask(values[i], values[i+1]));
            list.add(ft);
            executor.execute(ft);
        }
        
        if(values.length % 2 == 1) {
            System.out.println("[DEBUG] Odd Values");
            FutureTask<Integer[]> fa = list.remove(0);
            Integer[] a;
            
            try {
                a = fa.get();
            } catch (ExecutionException | InterruptedException e) {
                System.out.println("[Error]:"+e.getCause());
                e.printStackTrace();
                return null;
            }
            
            FutureTask<Integer[]> ft = new FutureTask(new MergeTask(a, values[values.length - 1]));
            list.add(ft);
            executor.execute(ft);
        }
        
        //System.out.println("[DEBUG] Starting with " + list.size() + " items.");
        while(list.size() > 1) {
            //System.out.println("[DEBUG] Looping with " + list.size() + " items.");
            FutureTask<Integer[]> fa = list.remove(0);
            FutureTask<Integer[]> fb = list.remove(0);
            FutureTask<Integer[]> ft = new FutureTask(new MergeTask(fa,fb));
            list.add(ft);
            executor.execute(ft);
        }
        executor.shutdown();
        //System.out.println("[DEBUG] Ending with " + list.size() + " items.");
        FutureTask<Integer[]> ft = list.remove(0);
                
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
        Integer[] test = new Integer[100000];
        Random rand = new Random();
                
        for(int i = 0; i < test.length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }

        System.out.println(Arrays.toString(test));
        
        start = System.currentTimeMillis();
        ParallelMerge pm= new ParallelMerge();
        test = pm.merge(test);
        end = System.currentTimeMillis();
        System.out.println("[DEBUG] The algorithm took: " + (end - start) + " ms");
        System.out.println(Arrays.toString(test));
    }
}