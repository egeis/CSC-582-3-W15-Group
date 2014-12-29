package sort;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;

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
        
        T[] a = Arrays.copyOfRange(list, 0, (list.length/2));
        T[] b = Arrays.copyOfRange(list, (list.length/2), list.length);
        
        MergeSort<T> msa = new MergeSort(a);
        MergeSort<T> msb = new MergeSort(b);
                  
        msa.run();        
        msb.run();
        
        a = msa.get();
        b = msb.get();
        
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
                list[k] = b[j];
                j++;
            } else {
                list[k] = a[i];
                i++;
            }
            
            k++;
        }
        
        while(i < a.length) {
            list[k] = a[i];
            k++;
            i++;
        }
        
        while(j < b.length) {
            list[k] = b[j];
            k++;
            j++;
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
        /*for(int i = 0; i < list.length; i++) {
            System.out.print(list[i].toString() +((i < list.length - 1)?",":""));
        }*/
        System.out.println(Arrays.toString(list));
    }
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {        
        long start, end;
        Integer[] test = new Integer[10000];
        Random rand = new Random();
        
        for(int i = 0; i < test.length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }
        
        MergeSort ms = new MergeSort(test);
        
        ms.show();
        start = System.currentTimeMillis();
        ms.run();
        end = System.currentTimeMillis();
        ms.show();
                
        System.out.println("The algorithm took: " + (end - start) + " ms");
    }
}
