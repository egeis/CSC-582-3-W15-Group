package sort.tasks;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author egeis
 */
public class Base {    
    protected static final String ANSI_RESET = "\u001B[0m";
    protected static final String ANSI_BLACK = "\u001B[30m";
    protected static final String ANSI_RED = "\u001B[31m";
    protected static final String ANSI_BLUE = "\u001B[34m";
    
   /**
     * Prints out the frequency of each element in the source list.
     * @return Map with a count of each value in source using value as a key.
     */
    public Map<Comparable, Integer> getFrequency(Comparable[] source) {
        Map<Comparable, Integer> freq = new HashMap<Comparable, Integer>();
        
        for (Comparable item : source) {
            if (freq.containsKey(item)) {
                freq.put(item, freq.get(item) + 1);
            } else {
                freq.put(item, 1);
            }
        }
        
        return freq;
    }
    
    /**
     * Checks if an ArrayList is sorted.
     * @return True if the list is sorted.
     */
    public boolean isSorted(Comparable[] source)
    {
        boolean success = true;
        
        for (int i = 1; i < source.length; i++)
            if(source[i].compareTo(source[i-1]) < 0) {
                System.out.println("Error near Index "+i+" Adjacent Elements: ");
                System.out.println(" Adjacent Elements: "+(i-1)+" "+source[i-1]+" "+(i)+" "+source[i]);
                success = false;
            }
        
        return success;
    }
    
}
