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
public class InsertionSort {
    
    /**
     * 
     * @param a The Source array.
     * @param start The Starting Index.
     * @param end The index of the last element.
     */
    public static void sort(Comparable[] a, int start, int end) {
        
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
     * Prints out the frequency of each element in the source list.
     * @param source The source array.
     * @return Map with a count of each value in source using value as a key.
     */
    public static Map<Comparable, Integer> getFrequency(Comparable[] source) {
        Map<Comparable, Integer> freq = new HashMap<Comparable, Integer>();
        
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
     * @param source The Source Array.
     * @return True if the list is sorted.
     */
    protected static boolean isSorted(Comparable[] source)
    {
        for (int i = 1; i < source.length; i++)
            if(source[i].compareTo(source[i-1]) < 0) {
                System.out.println("Error near Index "+i);
                return false;
            }
        return true;
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
        final int length = 100;
        Integer[] test = new Integer[length];
        Random rand = new Random();
        Map<Comparable, Integer> freq_start;
        Map<Comparable, Integer> freq_end;
                
        for(int i = 0; i < length; i++) {
            test[i] = (int) (rand.nextDouble() * 10);
        }
        
        freq_start = getFrequency(test);
        sort(test, 0 , 44);
        sort(test, 45 , 99);
        sort(test, 0 , 99);
        freq_end = getFrequency(test);
        
        System.out.println("[Completed] Frequency Match: "+ANSI_RED+((freq_start.equals(freq_end))?true:false)+ANSI_RESET);
        System.out.println("[Completed] Is Sorted? "+ANSI_RED+isSorted(test)+ANSI_RESET);
        freq_start = getFrequency(test);
    }
}