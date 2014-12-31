package sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Serial Merge Sorts.
 * @author Richard Coan
 * @param <T> generic Type T.
 */
public class MergeSort<T> implements Runnable
{
    //Global Protected Varibles
    protected List list;
    protected Comparator<T> compare;
   
    /**
     * Initialize Merge Sort.
     * @param list the source array.
     * @param compare a comparator.
     */
    public MergeSort(List<T> list, Comparator<T> compare)
    {
        this.compare = compare;
        this.list = list;
    }

    /**
     * @see java.lang.Runnable
     */
    @Override
    public void run() 
    {
        if(list.size() < 2) return; //Recurssion End.        

        //Divides the Array into two Sub Arrays to Merge Sort.
        MergeSort msa = new MergeSort( list.subList(0, list.size()/2), compare );
        MergeSort msb = new MergeSort( list.subList(list.size()/2, list.size()), compare );

        //Runs the Sub Arrays Merge Sort.
        msa.run();        
        msb.run();

        //Merge Sorts the Results.
        merge(msa.get(), msb.get());
    }    
    
    /**
     * Merges the sub ArrayList by setting them back into source ArrayList.
     * @param a a sub ArrayList of the <em>source</em> ArrayList.
     * @param b a sub ArrayList of the <em>source</em> ArrayList.
     */
    protected void merge(List<T> a, List<T> b)
    {
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
     * Retrieves the result.
     * @return the sorted <em>source</em> ArrayList.
     */
    public List<T> get()
    {
        return list;
    }
    
    /**
     * Checks if an ArrayList is sorted.
     * @param a the source array
     * @param compare a comparator.
     * @return True if the list is sorted.
     */
    protected boolean isSorted(List<T> a, Comparator<T> compare)
    {
        for (int i = 1; i < a.size(); i++)
            if(compare.compare(a.get(i), a.get(i - 1)) < 0) return false;
        return true;
    }
    
    /**
     * The Main Java Method.
     * @param args command-line argument. (Unused).
     */
    public static void main(String[] args)
    {        
        //Initialize Local Varibles and Classes.
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
        
        //Initialze Merge Sort.
        MergeSort ms = new MergeSort(test, compare);
        
        //Start Running Merge Sort.
        System.out.println("[Starting] Is Sorted? "+ ms.isSorted(test, compare));
        System.out.println("[Starting] Array Size:"+test.size());
        
        start = System.currentTimeMillis(); //Start Time
        ms.run();
        end = System.currentTimeMillis();   //End Time
        
        System.out.println("[Completed] The algorithm took: " + (end - start) + " ms.");
        System.out.println("[Completed] Is Sorted? "+ ms.isSorted(test, compare));
        System.out.println("[Completed] Array Size:"+test.size());        
    }
}