package sort;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Serial (Non-concurrent) recursive Merge Sort. The run() method initiates the sort by recursively dividing the
 * array until the start and end indexs match then merging each half.
 * 
 * * Recurssive Merge Sort works as followed:
 * IF start AND end MATCH RETURN
 * mid = start + (end - start) / 2
 * CREATE A NEW Merge Sort FROM start TO mid AS a
 * CREATE A NEW Merge Sort FROM mid + 1 TO end AS b
 * 
 * RUN a
 * RUN b
 * 
 * Merge a AND b 
 * 
 * USAGE EXAMPLE:
 * * Create the initial sort with the array to be sorted.
 * MergeSort ms = new MergeSort(test);
 * * Run the Sort
 * ms.run()
 * * test will then be Merge Sorted.
 * 
 * Merge Sort, sorting is based on the algorthim at:
 * http://algs4.cs.princeton.edu/22mergesort/Merge.java.html
 * 
 * @author Richard Coan
 */
public class MergeSort implements Runnable
{
    //Global Protected Varibles
    protected Comparable[] source;
    protected int start;
    protected int mid;
    protected int end;
    
    //Global Private Varibles
    private boolean sorted = false;
   
    /**
     * Initialize Merge Sort.
     * @param source the source array.
     */
    public MergeSort(Comparable[] source)
    {
        this.source = source;
        this.start = 0;
        this.end = source.length - 1;
    }
    
    /**
     * Initialize Merge Sort.
     * @param source The source array.
     * @param start The starting index.
     * @param end The last index.
     */
    public MergeSort(Comparable[] source, int start, int end)
    {
        this.source = source;
        this.start = start;
        this.end = end;
    }

    /**
     * @see java.lang.Runnable
     */
    @Override
    public void run() 
    {
        if(end <= start) return;
        int mid = start + (end - start) / 2;
        
        MergeSort msa = new MergeSort( source, start, mid);
        MergeSort msb = new MergeSort( source, mid+1, end);
        
        msa.run();        
        msb.run();
        
        merge(source, start, mid, end);
    }    
    
    /**
     * Merges the sub ArrayList by setting them back into source ArrayList.
     * 
     * @param a The Comparable array.
     * @param lo The starting index.
     * @param mid The middle index.
     * @param hi The ending index.
     */
    protected void merge(Comparable[] a, int lo, int mid, int hi)
    {
        Comparable [] aux = new Comparable [a.length];
        
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
        
        this.sorted = true;
    }
    
    /**
     * Compares two comparables returning the results.
     * @param v Comparable
     * @param w Comparable
     * @return -1 0 or 1
     */
    private static boolean less(Comparable v, Comparable w) {
        return (v.compareTo(w) < 0);
    }

    /**
     * Retrieves the result.
     * @return the sorted <em>source</em> ArrayList.
     */
    public boolean get()
    {
        return sorted;
    }
    
    /**
     * Prints out the frequency of each element in the source list.
     * @return Map with a count of each value in source using value as a key.
     */
    public Map<Comparable, Integer> getFrequency() {
        Map<Comparable, Integer> freq = new HashMap<Comparable, Integer>();
        
        for (Comparable source1 : source) {
            if (freq.containsKey(source1)) {
                freq.put(source1, freq.get(source1) + 1);
            } else {
                freq.put(source1, 1);
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
        boolean success = true;
        
        for (int i = 1; i < source.length; i++)
            if(source[i].compareTo(source[i-1]) < 0) {
                System.out.println("Error near Index "+i+" Adjacent Elements: ");
                System.out.println(" Adjacent Elements: ["+(i-1)+"] "+source[i-1]+" ["+(i)+"] "+source[i]);
                success = false;
            }
        
        return success;
    }
    
    /**
     * The Main Java Method.
     * @param args Command-line argument. (Unused).
     */
    public static void main(String[] args)
    {        
        //Initialize Local Varibles and Classes.
        long start, end;
        final int length = 100000;
        Integer[] test = new Integer[length];
        Random rand = new Random();
        Map<Comparable, Integer> freq_start;
        Map<Comparable, Integer> freq_end;
        
        //Create the Random Array.
        for(int i = 0; i < length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }
        
        //Initialze Merge Sort.
        MergeSort ms = new MergeSort(test);
        freq_start = ms.getFrequency();
                
        //Start Running Merge Sort.
        System.out.print(ANSI_BLUE+"Starting Sort..."+ANSI_RESET);
        start = System.currentTimeMillis(); //Start Time
        ms.run();
        end = System.currentTimeMillis();   //End Time
        System.out.println(ANSI_BLUE+"Done!"+ANSI_RESET);
        freq_end = ms.getFrequency();

        System.out.println("[Completed] The algorithm took: " + ANSI_RED + (end - start) + ANSI_RESET  +" ms");
        System.out.println("[Completed] Frequency Match: "+ANSI_RED+((freq_start.equals(freq_end))?true:false)+ANSI_RESET);
        System.out.println("[Completed] Is Sorted? "+ANSI_RED+ms.isSorted()+ANSI_RESET);
    }
    
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_BLUE = "\u001B[34m";
}
