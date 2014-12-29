/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sort;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 *
 * @author Richard
 */
//TODO: Convert back to using Generics.
public class MergeTask implements Callable<Integer[]> {
    private Integer[] a;
    private Integer[] b;
    private FutureTask<Integer[]> fa;
    private FutureTask<Integer[]> fb;
    private Integer[] results = new Integer[1]; 
    private boolean first = false;
    
    public MergeTask(Integer a, Integer b) {
        this.a = new Integer[1];
        this.b = new Integer[1];
        this.a[0] = a;
        this.b[0] = b;
        first = true;
    }
    
     public MergeTask(Integer[] a, Integer b) {
        this.a = a;
        this.b = new Integer[1];
        this.b[0] = b;
        first = true;
    }
    
    public MergeTask(FutureTask<Integer[]> fa, FutureTask<Integer[]> fb) {
        this.fa = fa;
        this.fb = fb;
    }
    
    @Override
    public Integer[] call() throws Exception {        
        int i = 0, j = 0,k = 0;
        
        
        if(!first) {
            try {
                a = fa.get();
                b = fb.get();
            } catch (ExecutionException e) {
                System.out.println("[Error]:"+e.getCause());
                e.printStackTrace();
                System.out.println("[Status] Futures are done:"+fa.isDone()+" and "+fb.isDone());
            }
        }
        
        results = new Integer[a.length+b.length]; 
        
        while(i < a.length && j < b.length) {
            if(b[j].compareTo(a[i]) < 0) {
                results[k] = (b[j]);
                j++;
                k++;
            } else {
                results[k] = (a[i]);
                i++;
                k++;
            }
        }

        while(i < a.length) {
            results[k] = (a[i]);
            i++;
            k++;
        }

        while(j < b.length) {
            results[k] = (b[j]);
            j++;
            k++;
        }
        
        //System.out.println("[Debug]: Merge Length "+results.size());
        return results;
    }
}