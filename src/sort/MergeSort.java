package sort;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Richard Coan
 * @param <T>
 */
public class MergeSort implements Runnable {
    
    private Integer[] list;
    private Integer[] a;
    private Integer[] b;
    private boolean isSorted = false;
    
    /**
     * 
     * @param list 
     */
    public MergeSort(Integer[] list) 
    {
        this.list = list;
    }
    
    public MergeSort(Integer[] a, Integer[] b) {
        this.a = a;
        this.b = b;
        this.isSorted = true;
    }

    @Override
    public void run() {
        if(!isSorted) {
            if(list.length < 2) return;        

            MergeSort msa = new MergeSort( Arrays.copyOfRange(list, 0, (list.length/2)) );
            MergeSort msb = new MergeSort( Arrays.copyOfRange(list, (list.length/2), list.length) );

            msa.run();        
            msb.run();

            merge(msa.get(),msb.get());
        } else {
            merge(a,b);
        }
    }    
    
    /**
     * 
     * @param a
     * @param b 
     */
    public void merge(Integer[] a, Integer[] b) {
        int i = 0, j = 0, k = 0;
        list = new Integer[(a.length+b.length)];
        
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
    public Integer[] get() {
        return list;
    }
    
    /**
     * 
     */
    public void show() {
        System.out.println(Arrays.toString(list));
    }
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {        
        long start, end;
        Integer[] test = new Integer[100];
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
