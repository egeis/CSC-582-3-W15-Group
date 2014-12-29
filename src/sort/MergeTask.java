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
 * @param <T>
 */
public class MergeTask<T extends Comparable<T>> implements Callable<T[]> {
    
    private Class<T> tClass;
    
    private T[] a;
    private T[] b;
    private FutureTask<T[]> fa;
    private FutureTask<T[]> fb;
    private List<T> results = new ArrayList<>(); 
    private boolean isDone = false;
    
    public MergeTask(T values) {
        results.add(values);
        isDone = true;
    }
    
    public MergeTask(FutureTask<T[]> fa, FutureTask<T[]> fb) {
        this.fa = fa;
        this.fb = fb;
    }
    
    @Override
    public T[] call() throws Exception {        
        int i = 0, j = 0;
     
        if(!isDone) {
            try {
                a = fa.get();
                b = fb.get();
            } catch (ExecutionException e) {
                System.out.println("[Error]:"+e.getCause());
                e.printStackTrace();
                System.out.println("[Status] Futures are done:"+fa.isDone()+" and "+fb.isDone());
            }
                        
            while(i < a.length && j < b.length) {
                if(b[j].compareTo(a[i]) < 0) {
                    results.add(b[j]);
                    j++;
                } else {
                    results.add(a[i]);
                    i++;
                }
            }

            while(i < a.length) {
                results.add(a[i]);
                i++;
            }

            while(j < b.length) {
                results.add(b[j]);
                j++;
            }
        }
        
        System.out.println("[Debug]: Merge Length "+results.size());
        
        return results.toArray( (T[]) Array.newInstance(tClass.getClass(),0) );
    }
}