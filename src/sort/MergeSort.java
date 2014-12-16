package sort;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 *
 * @author Richard Coan
 * @param <T>
 */
public class MergeSort<T extends Comparable<T>> implements Runnable {
    
    protected T[] list;
    
    /**
     * 
     * @param list 
     */
    public MergeSort(T[] list) 
    {
        this.list = list;
    }

    @Override
    public void run() {
        if(list.length < 2) return;        
        
        //TODO: Split Arrays...
        T[] a = Arrays.copyOfRange(list, 0, list.length/2);
        T[] b = Arrays.copyOfRange(list, list.length / 2, list.length);
        
        MergeSort<T> msa = new MergeSort(a);
        MergeSort<T> msb = new MergeSort(b);
        
        msa.show();       
        msb.show();
                  
        msa.run();        
        msb.run();
        
        a = msa.get();
        b = msa.get();
        
        merge(a,b);
    }    
    
    /**
     * 
     * @param a
     * @param b 
     */
    public void merge(T[] a, T[] b) {
        int i = 0, j = 0, k = 0;
        
        while(i < a.length && j < b.length) {
            if(b[j].compareTo(a[i]) < 0) {
                list[k++] = b[j++];
            } else {
                list[k++] = a[i++];
            }
        }
        
        while(i < a.length) {
            list[k++] = a[i++];
        }
        
        while(j < b.length) {
            list[k++] = b[j++];
        }
        
    }

    /**
     * 
     * @return 
     */
    public T[] get() {
        return list;
    }
    
    /**
     * 
     */
    public void show() {
        for(int i = 0; i < list.length; i++) {
            System.out.print(list[i].toString() +((i < list.length - 1)?",":""));
        }
        System.out.println();
    }
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {
        
        Integer[] test = {0,0,0,0};
                
        MergeSort ms = new MergeSort(test);
        
        Integer[] a = {8,3};
        Integer[] b = {2,9};
        
        ms.merge(a, b);
        ms.show();
        
        /*Integer[] test = {7,3,6};        
        
        
        
        ms.show();

        ms.run();
        
        ms.show();*/
    }
}
