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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import static sort.ParallelMergeSort.executor;

/**
 *
 * @author Richard
 */
public class ParallelMerge<T extends Comparable<T>> implements Callable<T[]> {

    protected T[] values;
    protected List<Future<T[]>> list = new ArrayList<Future<T[]>>();
    public static ExecutorService executor;
    
    public ParallelMerge(T[] values) {
        this.values = values;
        if(executor == null) executor = Executors.newCachedThreadPool();
    }
    
    public T[] call() throws Exception {
    
    }
    
    class Single<T extends Comparable<T>> implements Callable<T[]> {
        private T value;

        public Single(T value) {
            this.value = value;
        }

        public T[] call() throws Exception {
            List<T> results = new ArrayList<T>();
            results.add(value);
            return results.toArray( (T[]) Array.newInstance(results.getClass(), 0) );
        }
    }
}
