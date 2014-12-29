/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sort;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 *
 * @author Richard
 */
//TODO: Convert back to using Generics.
public class MergeTask implements Callable<Integer[]> {
    private Integer[] a;
    private Integer[] b;
    private Integer[] results; 
    
    public MergeTask(Integer a, Integer b) {
        this.a = new Integer[1];
        this.b = new Integer[1];
        this.a[0] = a;
        this.b[0] = b;
    }
    
    public MergeTask(Integer[] a, Integer b) {
        this.a = a;
        this.b = new Integer[1];
        this.b[0] = b;
    }
    
    public MergeTask(Integer[] a, Integer b[]) {
        this.a = a;
        this.b = b;
    }

    @Override
    public Integer[] call() throws Exception {        
        int i = 0, j = 0,k = 0;
                
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
        
        return results;
    }
}