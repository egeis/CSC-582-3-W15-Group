package sort;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Serial Merge Sorts.
 * @author Richard Coan
 */
public class MergeSort implements Runnable
{
    //Global Protected Varibles
    protected Integer[] source;
   
    /**
     * Initialize Merge Sort.
     * @param source the source array.
     */
    public MergeSort(Integer[] source)
    {
        this.source = source;
    }

    /**
     * @see java.lang.Runnable
     */
    @Override
    public void run() 
    {
        if(source.length < 2) return; //Recurssion End.        

        //Divides the Array into two Sub Arrays to Merge Sort.
        MergeSort msa = new MergeSort( Arrays.copyOfRange(source, 0, source.length/2) );
        MergeSort msb = new MergeSort( Arrays.copyOfRange(source, source.length/2, source.length));

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
    protected void merge(Integer[] a, Integer[] b)
    {
        
        // copy to aux[]
        for (int k = lo; k <= hi; k++) {
            aux[k] = a[k]; 
        }

        // merge back to a[]
        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++) {
            if      (i > mid)              a[k] = aux[j++];  // this copying is unneccessary
            else if (j > hi)               a[k] = aux[i++];
            else if (less(aux[j], aux[i])) a[k] = aux[j++];
            else                           a[k] = aux[i++];
        }
        
        /*int i = 0, j = 0, k = 0;
        
        while(i < a.length && j < b.length){
            if(a[i].compareTo(b[j]) < 0){
                source[k] = a[i];
                ++k;
                ++i;
            }else{
                source[k] = b[j];
                ++k;
                ++j;
            }
        }
        
        while(i < a.length){
            source[k] = a[i];
            ++k;
            ++i;
        }
        
        while(j < b.length){
            source[k] = b[j];
            ++k;
            ++j;
        }*/
    }

    /**
     * Retrieves the result.
     * @return the sorted <em>source</em> ArrayList.
     */
    public Integer[] get()
    {
        return source;
    }
    
    /**
     * Prints out the frequency of each element in the source list.
     * @return Map with a count of each value in source using value as a key.
     */
    public Map<Integer, Integer> getFrequency() {
        Map<Integer, Integer> freq = new HashMap<Integer, Integer>();
        
        for(int i = 0; i < source.length; i++) {
            if(freq.containsKey(source[i])) {
                freq.put(source[i], freq.get(source[i]) + 1);
            } else {
                freq.put(source[i], 1);
            }
        }
        
        return freq;
    }
    
    
    /**
     * Checks if an ArrayList is sorted.
     * @return True if the list is sorted.
     */
    protected boolean isSorted()
    {
        for (int i = 1; i < source.length; i++)
            if(source[i].compareTo(source[i-1]) < 0) return false;
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
        final int length = 10000000;
        Integer[] test = new Integer[length];
        Random rand = new Random();
        Map<Integer, Integer> freq_start;
        Map<Integer, Integer> freq_end;
        
        //Create the Random Array.
        for(int i = 0; i < length; i++) {
            test[i] = Math.abs(rand.nextInt());
        }
        
        //Initialze Merge Sort.
        MergeSort ms = new MergeSort(test);
        
        //Start Running Merge Sort.
        System.out.println("[Starting] Is Sorted? "+ ms.isSorted());
        System.out.println("[Starting] Array Size:"+test.length);
        
        freq_start = ms.getFrequency();
        System.out.println("[Starting] Frequency: "+freq_start.toString());

        System.out.println("Running Sort");
        start = System.currentTimeMillis(); //Start Time
        ms.run();
        end = System.currentTimeMillis();   //End Time
        System.out.println("Done!");
        
        freq_end = ms.getFrequency();
        System.out.println("[Completed] Frequency: "+freq_end.toString());
        
        System.out.println("[Completed] Frequency Match: "+((freq_start.equals(freq_end))?true:false));
        System.out.println("[Completed] The algorithm took: " + (end - start) + " ms.");
        System.out.println("[Completed] Is Sorted? "+ ms.isSorted());
        System.out.println("[Completed] Array Size:"+test.length);        
    }
}