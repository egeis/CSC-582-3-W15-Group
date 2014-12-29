/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sort;

import java.util.concurrent.Callable;
/**
 *
 * @author Richard
 */
public class PartitionTask implements Callable<Integer[]> {

    private Integer[] part;
    private final int beg;
    private final int end;

    public PartitionTask(Integer[] a, int beg, int end) {
        this.part = a;
        this.beg = beg;
        this.end = end;
    }

    @Override
    public Integer[] call() throws Exception {
        int N = part.length;

        for (int n = 1; n < N; n = n+n) {
            for (int i = 0; i < N-n; i += n+n) {
                int lo = i;
                int m  = i+n-1;
                int hi = Math.min(i+n+n-1, N-1);
                this.merge(lo, m, hi);
            }
        }

        return this.part;
    }

    private void merge(int lo, int mid, int hi) {            
        Integer[] aux = new Integer[part.length];

        // copy to aux[]
        for (int k = lo; k <= hi; k++) {
            aux[k] = part[k]; 
        }

        // merge back to a[]
        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++) {
            if      (i > mid)                       part[k] = aux[j++];
            else if (j > hi)                        part[k] = aux[i++];
            else if (aux[j].compareTo(aux[i]) < 0)  part[k] = aux[j++];
            else                                    part[k] = aux[i++];
        }
    }
}