package sort;

import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author Richard Coan
 */
public class InsertionSort {
    
    /**
     * 
     * @param a 
     * @param start 
     * @param end 
     */
    public static void sort(Comparable[] a, int start, int end) {
        
        for (int i = start+1; i <= end; i++) {
            for (int j = i; j > 0; j--) {
                if (a[j-1].compareTo(a[j]) > 0) {
                    exch(a, j-1, j);
                }
                else break;
            }
        }
    }

    // exchange a[i] and a[j]
    private static void exch(Comparable[] a, int i, int j) {
        Comparable swap = a[i];
        a[i] = a[j];
        a[j] = swap;
    }

    // read in a sequence of words from standard input and print
    // them out in sorted order
    public static void main(String[] args) {
        final int length = 10;
        Integer[] test = new Integer[length];
        Random rand = new Random();
        
        for(int i = 0; i < length; i++) {
            test[i] = Math.abs(rand.nextInt());
        }
        
        System.out.println(Arrays.toString(test));
        sort(test, 0 , 4);
        
        sort(test, 5 , 4);
        System.out.println(Arrays.toString(test));
    }
}