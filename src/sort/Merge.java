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
import java.util.concurrent.FutureTask;

/**
 *
 * @author Richard
 */
public class Merge<T extends Comparable<T>> implements Callable<T[]> {
    
    private T[] a;
    private T[] b;
    private FutureTask<T[]> fa;
    private FutureTask<T[]> fb;
    
    public Merge(FutureTask<T[]> fa, FutureTask<T[]>fb) {
        this.fa = fa;
        this.fb = fb;
    }
    
    public T[] call() throws Exception {        
        int i = 0, j = 0, k = 0;
        
        T[] a = fa.get();
        T[] b = fb.get();
        
        List<T> results = new ArrayList<T>(); 

        while(i < a.length && j < b.length) {
            if(b[j].compareTo(a[i]) < 0) {
                results.add(b[j]);
                j++;
            } else {
                results.add(a[i]);
                i++;
            }

            k++;
        }

        while(i < a.length) {
            results.add(a[i]);
            k++;
            i++;
        }

        while(j < b.length) {
            results.add(b[j]);
            k++;
            j++;
        }
        
        return results.toArray( (T[]) Array.newInstance(results.getClass(), 0) );
    }
}