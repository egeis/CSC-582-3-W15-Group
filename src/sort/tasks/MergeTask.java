/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sort.tasks;

import java.util.concurrent.Callable;

/**
 *
 * @author egeis
 */
public class MergeTask extends Base implements Callable<Comparable[]> {
    private Comparable[] a;
    private Comparable[] b;
    
    public MergeTask(Comparable[] a, Comparable[] b) {
        this.a = a;
        this.b = b;
    }
    
    public MergeTask(Comparable a, Comparable b) {
        this.a = new Comparable[1];
        this.b = new Comparable[1];
        this.a[0] = a;
        this.b[0] = b;
    }
    
    @Override
    public Comparable[] call() throws Exception {
        Comparable[] results = new Comparable[a.length+b.length];
        int k = 0, i = 0, j = 0;
        
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