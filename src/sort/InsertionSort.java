package sort;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import static sort.MergeSort.ANSI_RED;
import static sort.MergeSort.ANSI_RESET;

/**
 *
 * @author Richard Coan
 */
public class InsertionSort implements Runnable {
    
    private Comparable[] a;
    private int start;
    private int end;
    private boolean sorted;
    
    /**
     * 
     * @param a The Source array.
     * @param start The Starting Index.
     * @param end The index of the last element.
     */
    public InsertionSort(Comparable[] a, int start, int end) {
        this.a = a;
        this.start = start;
        this.end = end;
        this.sorted = false;
    }
    
    @Override
    public void run() {
        for (int i = start+1; i < (end + 1); i++) {
            for (int j = i; j > 0; j--) {
                if (a[j-1].compareTo(a[j]) > 0) {
                    exch(a, j-1, j);
                }
                else break;
            }
        }
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
     * @param source The source array.
     * @return Map with a count of each value in source using value as a key.
     */
    public Map<Comparable, Integer> getFrequency() {
        Map<Comparable, Integer> freq = new HashMap<Comparable, Integer>();
        
        for(int i = 0; i < a.length; i++) {
            if(freq.containsKey(a[i])) {
                freq.put(a[i], freq.get(a[i]) + 1);
            } else {
                freq.put(a[i], 1);
            }
        }
        
        return freq;
    }
    
    /**
     * Checks if an ArrayList is sorted.
     * @param source The Source Array.
     * @return True if the list is sorted.
     */
    protected boolean isSorted()
    {
        boolean success = true;
        
        for (int i = 1; i < a.length; i++)
            if(a[i].compareTo(a[i-1]) < 0) {
                System.out.println("Error near Index "+i+" Adjacent Elements: ");
                System.out.println(" Adjacent Elements: "+(i-1)+" "+a[i-1]+" "+(i)+" "+a[i]);
                success = false;
            }
        
        return success;
    }
    
    /**
     * Exchanges a[i] and a[j]
     * @param a The Source array.
     * @param i Index of the element to be swapped.
     * @param j The index of the element that takes the place of i.
     */
    private static void exch(Comparable[] a, int i, int j) {
        Comparable swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    // read in a sequence of words from standard input and print
    // them out in sorted order
    public static void main(String[] args) {
        final int length = 10000;
        Integer[] test = new Integer[length];
        Random rand = new Random();
        Map<Comparable, Integer> freq_start;
        Map<Comparable, Integer> freq_end;
        InsertionSort is = new InsertionSort(test, 0 , (length - 1)); 
        
        for(int i = 0; i < length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }
        
        freq_start = is.getFrequency();
        //sort(test, 45 , 99);
        is.run();
        freq_end = is.getFrequency();
        
        System.out.println("[Completed] Frequency Match: "+ANSI_RED+((freq_start.equals(freq_end))?true:false)+ANSI_RESET);
        System.out.println("[Completed] Is Sorted? "+ANSI_RED+is.isSorted()+ANSI_RESET);
    }
}