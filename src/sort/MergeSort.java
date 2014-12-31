package sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Richard Coan
 * @param <T>
 */
public class MergeSort<T> implements Runnable {
    
    protected List list;
    protected Comparator<T> compare;
   
    /**
     * 
     * @param list 
     */
    public MergeSort(List<T> list, Comparator<T> compare) 
    {
        this.compare = compare;
        this.list = list;
    }

    @Override
    public void run() {
        if(list.size() < 2) return;        

        MergeSort msa = new MergeSort( list.subList(0, list.size()/2), compare );
        MergeSort msb = new MergeSort( list.subList(list.size()/2, list.size()), compare );

        msa.run();        
        msb.run();

        merge(msa.get(), msb.get());
    }    
    
    /**
     * 
     * @param a
     * @param b 
     */
    protected void merge(List<T> a, List<T> b) {
        int i = 0, j = 0, k = 0;
        
         while(i < a.size() && j < b.size()){
            if(compare.compare(a.get(i), b.get(j)) < 0){
                list.set(k, a.get(i));
                i++;
            } else {
                list.set(k, b.get(j));
                j++;
            }
            k++;
        }
        
        while(i < a.size()) {
            list.set(k, a.get(i));
            i++;
            k++;
        }
        
        while(j < b.size()) {
            list.set(k, b.get(j));
            j++;
            k++;
        }
    }

    /**
     * 
     * @return 
     */
    public List<T> get() {
        return list;
    }
    
    protected boolean isSorted(List<T> a, Comparator<T> compare) {
        for (int i = 1; i < a.size(); i++)
            if(compare.compare(a.get(i), a.get(i - 1)) < 0) return false;
        return true;
    }
    
    /**
     * 
     * @param args 
     */
    public static void main(String[] args) {        
        long start, end;
        final int length = 1000000;
        List<Integer> test = new ArrayList();
        Random rand = new Random();
        
        Comparator<Integer> compare = new Comparator<Integer>() {
            @Override
            public int compare(Integer a, Integer b){
               return a.compareTo(b);
            }
            
            @Override
            public boolean equals(Object a){
               return this == a;
            }
           };
        
        //Create the Random Array.
        for(int i = 0; i < length; i++) {
            test.add( (int) (rand.nextDouble() * 10) );
        }
        
        MergeSort ms = new MergeSort(test, compare);
        
        System.out.println("[Starting] Is Sorted? "+ ms.isSorted(test, compare));
        System.out.println("[Starting] Array Size:"+test.size());
        start = System.currentTimeMillis();
        ms.run();
        end = System.currentTimeMillis();
        System.out.println("[Completed] The algorithm took: " + (end - start) + " ms");
        System.out.println("[Completed] Is Sorted? "+ ms.isSorted(test, compare));
        System.out.println("[Completed] Array Size:"+test.size());        
    }
}
